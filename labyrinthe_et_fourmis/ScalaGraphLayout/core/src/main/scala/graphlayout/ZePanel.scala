package graphlayout

import java.awt.{Color, Dimension, Graphics2D}

import akka.actor._
import graphlayout.Tableaux._

import scala.math._
import scala.swing.{Label, Panel}
//import kebra.LL._
import kebra._

import scala.concurrent.duration._
import scala.language.postfixOps

class ZeActor extends Actor {
  context.setReceiveTimeout(10 milliseconds)

  def receive: PartialFunction[Any, Unit] = {
    case ReceiveTimeout =>
      if ((!ZePanel.zp.pause) && (!ZePanel.zp.step)) ZePanel.zp.repaint(); tbx.doZeJob("timeout", true)
    case slider: (String, Int)@unchecked =>
      tbx.graph.doZeSliderJob(slider)
      context.setReceiveTimeout(tbx.graph.slider_timeout millisecond)
    case "stop" =>
      MyLog.myErrPrintln("stop")
      tbx.graph.resultat.printFinalLog1
      MyLog.myAssert2(true, false)
    case "step" =>
      LL.l.myErrPrintDln("step")
      ZePanel.zp.repaint()
      context.setReceiveTimeout(10 seconds)
      tbx.doZeJob("step", true)
      ZePanel.zp.step = true
    case mouse: (String, Int, Int)@unchecked =>
      tbx.graph.doZeMouseJob(mouse)
  }
}

object ZePanel {
  var zp: ZePanel = _
  var za: ActorRef = _
  implicit val system: ActorSystem = MyLog.system

  def newZePanel(lbl: Label, maxRC: RowCol, graph: GraphAbstract): Unit = {
    zp = new ZePanel(lbl, maxRC, graph)
    za = system.actorOf(Props[ZeActor](), "zePanelActor")
  }
}

class ZePanel(val lbl: Label, val maxRC: RowCol, val graph: GraphAbstract) extends Panel {
  var pause = false
  var step = false
  var run = false
  var largeur = 1000
  val hauteur = 700
  var limit: Int = _
  preferredSize = new Dimension(largeur, hauteur)
  val origin = new Dimension(0, 0)
  newTbx(this, maxRC, preferredSize, origin, graph)

  override def paint(g: Graphics2D): Unit = {
    g.setColor(Color.white)
    g.fillRect(0, 0, largeur, hauteur)

    g.setColor(Color.black)
    tbx.lc.foreach(_.paint(g))
    graph.paint(g)
  }
}
