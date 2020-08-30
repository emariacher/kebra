import java.util.Calendar

import org.scalatest._
import Euler._
import scala.collection.immutable.{ListSet, TreeSet}
import scala.math.BigInt

class EulerMain extends FlatSpec with Matchers {


  "Euler493" should "be OK" in {
    println("Euler493")

    def dozejob1(s: String, sampleSize: Int, prev: List[(Int, Int)]) = {
      val z = s.toSeq.combinations(sampleSize).toList
      val y = z.map(e => e.toSeq.distinct.size).groupBy(u => u)
      val x = y.toList.map(a => (a._1, a._2.size)).sortBy(_._1)
      val w = x.map(a => a._1 * a._2).sum
      w shouldEqual z.map(e => e.toSeq.distinct.size).sum
      println(sampleSize, w.toDouble / z.size.toDouble, z.size, z.take(5), z.last)
      println("  " + x)
      if (prev.nonEmpty) {
        val v = prev.dropWhile(c => c._1 < x.head._1)
        println("    " + x.zip(v).map(c => (c._1._1, c._1._2 - c._2._2)))
      }
      x.map(_._2).sum shouldEqual z.size
      x
    }

    def builds(couleur: Int, parCouleur: Int): String = {
      (1 to couleur).map(c => {
        (1 to parCouleur).map(u => c.toString).mkString("")
      }).mkString("")
    }

    var s = builds(5, 6)
    println(s)
    (1 to 12).foreach(i => {
      dozejob1(s, i, List[(Int, Int)]())
    })

    s = builds(7, 10)
    println(s)
    var prev = List[(Int, Int)]()
    (1 to 20).foreach(i => {
      prev = dozejob1(s, i, prev)
    })

    val result = 0
    println("Euler493[" + result + "]")
    result shouldEqual 0
  }

}