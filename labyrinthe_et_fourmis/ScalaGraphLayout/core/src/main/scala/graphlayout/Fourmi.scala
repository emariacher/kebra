package graphlayout

import java.awt.{Color, Graphics2D}

import graphlayout.Tableaux.tbx
import kebra.MyLog._
import org.apache.commons.math3.distribution._

import scala.collection.immutable.List
import scala.collection.mutable
import scala.util.Random

// https://www.futura-sciences.com/planete/dossiers/zoologie-fourmi-secrets-fourmiliere-1404/page/8/
/*
Chez la fourmi du pharaon Monomorium pharaonis, on conna�t une ph�romone de piste � longue dur�e d'action qui permet de
dessiner un r�seau de pistes parcourues chaque jour. Une deuxi�me ph�romone est plus volatile, mais attire un plus
grand nombre d'ouvri�res sur la piste. Enfin une troisi�me substance d�pos�e aux croisements fait office de sens interdits
informant les ouvri�res qu'il n'y a aucune nourriture sur la route ainsi signal�e.

Les ph�romones d'alarme alertent, regroupent ou dispersent les ouvri�res mises en difficult� par un �v�nement impr�vu comme l'irruption d'un intrus.
 */

// http://jl.carton.free.fr/fourmis/TPE.pdf
/*
Une  ouvri�re  en  qu�te  de  nourriture  suit  les  odeurs  des  ph�romones-aller  ("je  pars"),  et  une ouvri�re qui
veut retourner � la fourmili�re utilise des ph�romones-retour ("je reviens"). (voir Exp�rience du I : Mise en �vidence du � langage � des fourmis )
 */

class Fourmi(val anode: ANode) {
  var fourmiliere: Fourmiliere = _
  val tribu: Tribu = anode.tribu
  var direction: Double = .0
  var jnode: JNode = _
  var state: FourmiStateMachine = FourmiStateMachine.cherche
  var previousState: FourmiStateMachine = FourmiStateMachine.undefined
  var stateCompteur = 0
  var logxys: List[(Carre, FourmiStateMachine)] = List[(Carre, FourmiStateMachine)]()
  var indexlog: Int = _
  var triggerTrace = false
  var logcarres: List[Carre] = List[Carre]()
  var lcompteurState: mutable.Map[FourmiStateMachine, Int] = scala.collection.mutable.Map[FourmiStateMachine, Int]()
  var lastlogcarre = new Carre(0, 0)
  var cptShortcut: Int = _
  var CEstLaFourmiliere = 20.0
  var influenceDesPheromones = 40.0
  var angleDeReniflage: Double = (Math.PI * 4 / 5)
  var suisLeChemin1 = 10
  var suisLeChemin2 = 20
  var avanceAPeuPresCommeAvantDispersion = .1
  var avanceAPeuPresCommeAvantVitesse = 2
  var avanceDroitVitesse = 3
  var sautsTropGrandsLissage: Int = 15
  var simplifieLissage = 7
  var filtrePattern = 0
  var sautsTropGrandsLissageAlgo = 2
  var raccourci: Double = influenceDesPheromones
  var limiteDetectionNourriture = 500
  var increment = 10
  var plusAssezDEnergie = 1500
  var flabel = ""
  var limiteReniflage = 50
  var listeDesCarresReniflables: List[Carre] = List[Carre]()
  var listeDesCarresPasDejaParcourus: List[Carre] = List[Carre]()

  def carre: Carre = tbx.findCarre(anode.x, anode.y)

  override def toString: String = "[%.0f,%.0f](%d)".format(anode.x, anode.y, logxys.length) + tribu + carre

  def avance(lc: List[Carre]): Unit = {
    val oldnode = new Node(anode.x, anode.y)
    val oldcarre = tbx.findCarre(oldnode.x, oldnode.y)
    if (lc.isEmpty) {
      avanceAPeuPresCommeAvant()
      logcarres = List[Carre]()
    } else {
      listeDesCarresReniflables = lc.filter(c =>
        anode.pasLoin(c.XY) < limiteReniflage & c.hasPheromone(tribu) > ParametresPourFourmi.depotEvaporeFinal)

      listeDesCarresPasDejaParcourus = listeDesCarresReniflables.filter(c => !logcarres.contains(c))

      if (listeDesCarresPasDejaParcourus.isEmpty) {
        avanceAPeuPresCommeAvant()
        if (carre.hasPheromone(tribu) > ParametresPourFourmi.depotEvaporeFinal) {
          state = FourmiStateMachine.surLaTrace
        } else {
          state = FourmiStateMachine.cherche
        }
      } else {
        val lfedges2 = listeDesCarresPasDejaParcourus.map(c => {
          val e = new Edge(c.fn, anode)
          e.attraction = Math.max(c.hasPheromone(tribu),
            suisLeChemin1 +
              (listeDesCarresReniflables.length - listeDesCarresPasDejaParcourus.length) * suisLeChemin2
          ) // quand ca tourne en rond, force la sortie
          e
        })
        //myPrintDln("***********************************")
        lfedges2.foreach(_.rassemble)
        Edge.checkInside("" + (anode, listeDesCarresReniflables.map(_.fn).mkString("{", ",", "}")),
          listeDesCarresReniflables.map(_.fn) :+ oldnode, anode)
        if (carre.hasPheromone(tribu) > ParametresPourFourmi.depotEvaporeFinal) {
          state = FourmiStateMachine.surLaTrace
        } else {
          state = FourmiStateMachine.cherche
        }

      }
      direction = oldnode.getNodeDirection(anode)
      logcarres = (logcarres :+ carre).distinct
    }
  }

  def avanceverslaJaffe(lc: List[Carre]): Unit = {
    val oldnode = new Node(anode.x, anode.y)
    val oldcarre = tbx.findCarre(oldnode.x, oldnode.y)
    avanceDroit()
    direction = oldnode.getNodeDirection(anode)
    logcarres = (logcarres :+ carre).distinct
  }

  def estALaFourmiliere: Boolean = anode.pasLoin(fourmiliere.centre) < CEstLaFourmiliere

  def avanceAPeuPresCommeAvant(): Unit = {
    direction = new NormalDistribution(direction, avanceAPeuPresCommeAvantDispersion).sample
    anode.x += Math.sin(direction) * avanceAPeuPresCommeAvantVitesse
    anode.y += Math.cos(direction) * avanceAPeuPresCommeAvantVitesse
  }

  def avanceDroit(): Unit = {
    anode.x += Math.sin(direction) * avanceDroitVitesse
    anode.y += Math.cos(direction) * avanceDroitVitesse
  }


  def rembobine: Int = {
    logcarres = List[Carre]()
    myAssert3(indexlog < 1, false, toString + " " + indexlog + " " + previousState + "[" + stateCompteur + "]")
    if (indexlog > logxys.length) {
      myErrPrintDln(toString + " " + indexlog + "<" + logxys.length +
        " " + previousState + "[" + stateCompteur + "] fmcentre[" + fourmiliere.c + "] cptShortcut=" + cptShortcut
        + fourmiliere.retoursFourmiliere.mkString("\n  rf(", ", ", ")")
        + "\n  " + logxys.length + logxys.mkString(" - logxys(", ", ", ")\n"))
      indexlog = logxys.length - 2
    }
    indexlog -= 1
    val c = logxys.apply(indexlog)._1
    anode.moveTo(c.fn)
    myAssert3(c == null, false, toString)
    c.updatePheromone(tribu)
    indexlog
  }

  def redirige(largeur: Int, hauteur: Int, border: Int, rnd: Random): Unit = {
    if (anode.remetsDansLeTableau(largeur, hauteur, border)) {
      direction = rnd.nextDouble() * Math.PI * 2
    }
  }

  def AuxAlentoursDeLaFourmiliere: FourmiStateMachine = {
    anode.moveTo(fourmiliere.centre) // teleporte toi au centre de la fourmiliere
    direction = direction * (-1) // essaye de reprendre le meme chemin
    logxys = List((fourmiliere.c, state))
    indexlog = 0
    logcarres = List(fourmiliere.c)
    fourmiliere.retour(state)
    anode.selected = false
    FourmiStateMachine.auBercail
  }

  def lisseLeRetour(): Unit = {
    val jaffeC = logxys.last
    val fourmiliereC = (fourmiliere.c, FourmiStateMachine.cherche)
    if (raccourci > 0) {
      // detecte les raccourcis
      var cptRaccourcis = 0
      val oldlength = logxys.length
      //var logxystemp = List[(Carre, FourmiStateMachine)]()
      var logxystemp = logxys.zipWithIndex
      var indexLogxys = 0
      while (indexLogxys < logxystemp.length) {
        val avantlength = logxystemp.length
        val c = logxystemp.apply(indexLogxys)._1._1
        val lf = logxystemp.filter(_._1._1.dist(c) < raccourci).sortBy(_._2)
        if (lf.last._2 - lf.head._2 > 10) {
          cptRaccourcis += 1
          logxystemp = logxystemp.filter(logitem => logitem._2 <= lf.head._2 | logitem._2 > lf.last._2)
        }
        indexLogxys += 1
      }
      logxys = logxystemp.sortBy(_._2).map(_._1)
      selPrint(toString + " <-- raccourci -- " + oldlength + " cptRaccourcis=" + cptRaccourcis)
    }
    if (simplifieLissage > 0) {
      // d�cime!
      val oldlength = logxys.length
      logxys = logxys.zipWithIndex.filter(_._2 % (simplifieLissage + tbx.rnd.nextInt(2)) == 1).map(_._1)
      selPrint(toString + " <--   trop    -- " + oldlength)
    }
    // remet la fourmiliere au d�but et la jaffe a la fin si jamais on simplifie abusivement
    logxys = fourmiliereC :: logxys
    logxys = logxys :+ jaffeC
    if (sautsTropGrandsLissage > 0) {
      // sauts trop grands / rajoute quand il n'y en a pas assez
      var cptPasAssez = 0
      val oldlength = logxys.length
      var lsauts = logxys.zipWithIndex.sliding(2).toList.map(l => (l.head._1._1.dist(l.last._1._1),
        l.head._1._1, l.last._1._1, l.head._2)).filter(_._1 > sautsTropGrandsLissage)
      while (!lsauts.isEmpty) {
        cptPasAssez += 1
        val llissage = lsauts.map(s => (s._2, s._2.milieu(s._3), s._3, s._4))
        llissage.reverse.foreach(toBeInserted => {
          logxys = insert(logxys, toBeInserted._4 + 1, (toBeInserted._2, FourmiStateMachine.lisse))
        })
        lsauts = logxys.zipWithIndex.sliding(2).toList.map(l => (l.head._1._1.dist(l.last._1._1),
          l.head._1._1, l.last._1._1, l.head._2)).filter(_._1 > sautsTropGrandsLissage)
      }
      selPrint(toString + " <--pas assez-- " + oldlength + " cptPasAssez=" + cptPasAssez + " max=" +
        logxys.zipWithIndex.sliding(2).toList.map(l => (l.head._1._1.dist(l.last._1._1),
          l.head._1._1, l.last._1._1, l.head._2)).map(_._1).max(Ordering.Double.TotalOrdering))
    }
  }

  def cherche(lc: List[Carre]): Unit = {
    state match {
      case FourmiStateMachine.detecte => avanceverslaJaffe(lc)
      case _ => avance(lc)
    }
    redirige(tbx.zp.largeur, tbx.zp.hauteur, 10, tbx.rnd)
    if (aDetecteLaNourriture(limiteDetectionNourriture)) {
      state = FourmiStateMachine.detecte
      direction = anode.getNodeDirection(jnode)
    } else if ((estALaFourmiliere) & (logxys.length > 100)) {
      // si jamais tu repasses a la fourmiliere, remets les compteurs a zero
      state = AuxAlentoursDeLaFourmiliere
    }
    flabel = "" + stateCompteur
    if (previousState != state) {
      if (previousState == FourmiStateMachine.surLaTrace) {
        if (state != FourmiStateMachine.detecte) {
          myPrintDln(anode.toString + " " + previousState.toString + "[" + stateCompteur + "] --> " + state)
          flabel = "No good! previousState: " +
            previousState + "[" + stateCompteur + "] " + carre + anode + jnode + " " + anode.dist(jnode)
          myErrPrintDln(flabel)
          myErrPrintIt(listeDesCarresReniflables)
          myErrPrintIt(listeDesCarresPasDejaParcourus)
          myErrPrintIt(lc.length, lc)
          myErrPrintIt(indexlog, logxys.length, logxys)
          ZePanel.za ! "step"
        }
        if (previousState == FourmiStateMachine.retourne) {
          if (state != FourmiStateMachine.auBercail) {
            myPrintDln(anode.toString + " " + previousState.toString + "[" + stateCompteur + "] --> " + state)
            flabel = "No good! previousState: " +
              previousState + "[" + stateCompteur + "] " + carre + anode + jnode + " " + anode.dist(jnode)
            myErrPrintDln(flabel)
            myErrPrintIt(listeDesCarresReniflables)
            myErrPrintIt(listeDesCarresPasDejaParcourus)
            myErrPrintIt(lc.length, lc)
            myErrPrintIt(indexlog, logxys.length, logxys)
            ZePanel.za ! "step"
          }
        }
        /*myAssert3(state, FourmiStateMachine.retourne, "No good! previousState: " +
          previousState + "[" + stateCompteur + "] " + anode + jnode + " " + anode.dist(jnode))*/
      }
    }
  }

  def doZeJobC(lc: List[Carre]): Unit = {
    previousState = state
    state match {
      case FourmiStateMachine.mort =>
      case FourmiStateMachine.cherche => cherche(lc)
      case FourmiStateMachine.auBercail => cherche(lc)
      case FourmiStateMachine.surLaTrace => cherche(lc)
      case FourmiStateMachine.tourneEnRond => cherche(lc)
      case FourmiStateMachine.detecte =>
        cherche(lc)
        if (aDetecteLaNourriture(20)) {
          state = FourmiStateMachine.retourne
          lisseLeRetour()
          cptShortcut = 0
          indexlog = logxys.length - 1
          myPrintDln("aDetecteLaNourriture " + jnode)
        } else {
          redirige(tbx.zp.largeur, tbx.zp.hauteur, 10, tbx.rnd)
        }
      case FourmiStateMachine.retourne =>
        if ((rembobine == 0) || (estALaFourmiliere)) {
          state = AuxAlentoursDeLaFourmiliere
        }
        redirige(tbx.zp.largeur, tbx.zp.hauteur, 10, tbx.rnd)
      case _ => myErrPrintDln(state)
    }
    lcompteurState(state) = lcompteurState.getOrElse(state, 0) + 1
    if (previousState != state) {
      myPrintDln(anode.toString + " " + carre + " " + previousState.toString + "[" + stateCompteur + "] --> " + state)
      /*if (previousState == FourmiStateMachine.detecte) {
        myAssert2(state, FourmiStateMachine.retourne)
      }*/
      stateCompteur = 0
    } else {
      stateCompteur += 1
    }
    if (stateCompteur > plusAssezDEnergie) {
      state = FourmiStateMachine.mort
    }
    //myPrintDln(toString, previousState + " -> " + state, stateCompteur)
  }

  def aDetecteLaNourriture(limitDetection: Double): Boolean = (anode.dist(jnode) < limitDetection)

  def insert[T](list: List[T], i: Int, values: T*): List[T] = {
    val (front, back) = list.splitAt(i)
    front ++ values ++ back
  }

  def paint(g: Graphics2D): Unit = {
    var pheronomeD = 0
    var fourmiL = 0
    var fourmiH = 0
    g.setColor(tribu.c.color)
    logxys.foreach(p => {
      p._2 match {
        case FourmiStateMachine.mort =>
          pheronomeD = 2
          fourmiL = 5
          fourmiH = 8
        case FourmiStateMachine.cherche =>
          pheronomeD = 2
          fourmiL = 7
          fourmiH = 12
        case FourmiStateMachine.surLaTrace =>
          pheronomeD = 2
          fourmiL = 7
          fourmiH = 12
        case FourmiStateMachine.tourneEnRond =>
          pheronomeD = 2
          fourmiL = 7
          fourmiH = 12
        case FourmiStateMachine.detecte =>
          pheronomeD = 4
          fourmiL = 9
          fourmiH = 14
        case FourmiStateMachine.retourne =>
          pheronomeD = 0
          fourmiL = 10
          fourmiH = 16
        case _ =>
      }
      g.fillOval(p._1.fn.x.toInt, p._1.fn.y.toInt, pheronomeD, pheronomeD)
    })

    g.fillOval(anode.x.toInt, anode.y.toInt, fourmiL, fourmiH)
    if (anode.selected) {
      g.setColor(Color.red)
      g.drawOval(anode.x.toInt - 2, anode.y.toInt - 2, fourmiL + 4, fourmiH + 4)
    } else {
      g.setColor(Color.black)
    }
    //g.drawString("" + stateCompteur, anode.x.toInt, anode.y.toInt)
    g.drawString(flabel, anode.x.toInt, anode.y.toInt)
    g.drawOval(anode.x.toInt, anode.y.toInt, fourmiL, fourmiH)
    if (carre != null) {
      if (!carre.egal(lastlogcarre)) {
        logxys = logxys :+ (carre, state)
        lastlogcarre = carre
      }
    } else {
      myErrPrintDln("[%.0f,%.0f](%d)".format(anode.x, anode.y, logxys.length) + tribu)
    }
    /*if (triggerTrace) {
      myPrintDln(toString, state)
    }*/
  }

  def selPrint(a: Any): Unit = {
    if (anode.selected) {
      myPrintD(a.toString + "\n")
    }
  }
}

case class FourmiStateMachine private(state: String) {
  override def toString: String = state
}

object FourmiStateMachine {
  val cherche = FourmiStateMachine("cherche")
  val surLaTrace = FourmiStateMachine("surLaTrace")
  val tourneEnRond = FourmiStateMachine("tourneEnRond")
  val detecte = FourmiStateMachine("detecte")
  val retourne = FourmiStateMachine("retourne")
  val lisse = FourmiStateMachine("lisse")
  val ratioTourneEnRondSurLaTrace = FourmiStateMachine("ratioTourneEnRondSurLaTrace")
  val mort = FourmiStateMachine("mort")
  val auBercail = FourmiStateMachine("auBercail")
  val undefined = FourmiStateMachine("undefined")
}


