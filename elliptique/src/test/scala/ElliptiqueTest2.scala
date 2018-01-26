import Elliptique._
import org.scalatest._

import scala.math.BigInt
import scala.util.Random


/*
https://www.coindesk.com/math-behind-bitcoin/
https://crypto.stackexchange.com/questions/44304/understanding-elliptic-curve-point-addition-over-a-finite-field
https://fr.wikipedia.org/wiki/Courbe_elliptique
 */

class ElliptiqueTest2 extends FlatSpec with Matchers {
  "Trouve les nombres premiers qui pourraient marcher" should "be OK" in {
    val premiers = EulerPrime.premiers1000
    println("Trouve les nombres premiers qui pourraient marcher")
    println(premiers.take(100).filter(modlo => {
      val e = new Elliptique(modlo,0,7)
      e.curve.size > modlo & e.curve.filter(p => p._1 * p._2 == 0).isEmpty
    }))
  }

  "CheckLaBoucle241" should "be OK" in {
    val modlo = 241
    println("CheckLaBoucle" + modlo + ": ")
    val e = new Elliptique(modlo,0,7)
    val lp = e.curve.sortBy(p => (p._1 * 100) + p._2)
    println(modlo, lp.size, lp)
    lp.filter(p => p._1 * p._2 == 0).isEmpty shouldEqual true
    lp.size should be >= modlo

    println("\n***2*** " + modlo + " ***** 9 groupes pour les puissances de 2")
    var lgroupsmul2 = List[List[(BigInt, BigInt)]]()

    var lpm = lp
    while (!lpm.isEmpty) {
      val lmul = e.loopmul2(lpm.head)
      println("    " + lmul.size, lmul)
      lgroupsmul2 = lgroupsmul2 :+ lmul
      lpm = lpm.filter(p => !lmul.contains(p))
    }
    println(modlo, lgroupsmul2.size, lgroupsmul2.mkString("\n  ", "\n  ", "\n  "))
    e.checkVerbose((BigInt(70), BigInt(233))) shouldEqual true
    e.checkVerbose((BigInt(85), BigInt(233))) shouldEqual true
    e.checkVerbose((BigInt(86), BigInt(233))) shouldEqual true
    e.plus((BigInt(70), BigInt(233)), (BigInt(70), BigInt(233))) shouldEqual(BigInt(85), BigInt(233))
    lgroupsmul2.flatten.size shouldEqual lgroupsmul2.flatten.distinct.size
    lgroupsmul2.flatten.size shouldEqual lp.size
    lgroupsmul2.size shouldEqual 9

    println("\n***3*** " + modlo + " ***** 15 groupes pour les multiplications par 3")
    var lgroupsmul3 = List[List[(BigInt, BigInt)]]()

    lpm = lp
    while (!lpm.isEmpty) {
      val lmul = e.loopmul3(lpm.head)
      println("    " + lmul.size, lmul)
      lgroupsmul3 = lgroupsmul3 :+ lmul
      lpm = lpm.filter(p => !lmul.contains(p))
    }
    println(modlo, lgroupsmul3.size, lgroupsmul3.mkString("\n  ", "\n  ", "\n  "))
    lgroupsmul3.flatten.size shouldEqual lgroupsmul3.flatten.distinct.size
    lgroupsmul3.flatten.size shouldEqual lp.size
    lgroupsmul3.size shouldEqual 15

    println("\n***4*** " + modlo + " ***** 16 groupes pour les multiplications par 4")
    var lgroupsmul4 = List[List[(BigInt, BigInt)]]()

    lpm = lp
    while (!lpm.isEmpty) {
      val lmul = e.loopmul4(lpm.head)
      println("    " + lmul.size, lmul)
      lgroupsmul4 = lgroupsmul4 :+ lmul
      lpm = lpm.filter(p => !lmul.contains(p))
    }
    println(modlo, lgroupsmul4.size, lgroupsmul4.mkString("\n  ", "\n  ", "\n  "))
    lgroupsmul4.flatten.size shouldEqual lgroupsmul4.flatten.distinct.size
    lgroupsmul4.flatten.size shouldEqual lp.size
    lgroupsmul4.size shouldEqual 16
  }

  "Ordre67" should "be OK" in {
    println("Ordre67: ils ont tous le meme ordre!")
    val e = new Elliptique(67,0,7)
    val lp = e.curve.sortBy(p => (p._1 * 100) + p._2)
    println(67, lp.size, lp)

    var ordre = 0
    lp.foreach(p => {
      var somme = e.plus(p, p)
      (1 to 300).toList.find(i => {
        somme = e.plus(somme, p)
        if(somme._1*somme._2==0) {
          println("===",p,i,somme,"===")
          ordre=i+1
        }
        somme._1*somme._2==0
      })
    })
    ordre shouldEqual e.curve.size
  }
}
