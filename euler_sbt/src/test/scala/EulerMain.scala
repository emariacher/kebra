import java.util.Calendar

import org.scalatest._
import Euler._
import scala.collection.immutable.{ListSet, TreeSet}
import scala.math.BigInt

class EulerMain extends FlatSpec with Matchers {

  "Euler706" should "be OK" in {
    println("Euler706")

    def f(n: BigInt, verbose: Boolean = false): BigInt = {
      val s = n.toString
      val l = s.length
      val z = (1 to l).map(i => s.toSeq.sliding(i)).toList.flatten
      if (verbose) {
        println(n, z.filter(u => (u.sum % 3 == 0)).length, z)
      }
      z.filter(u => (u.sum % 3 == 0)).length
    }

    def F(d: Int): BigInt = {
      val start = BigInt("1" + (2 to d).map(z => "0").mkString)
      val end = start * 10
      val z = (start until end).toList.filter(bi => f(bi) % 3 == 0).length
      println(d, (start until end), z)
      z
    }

    f(2573, true) shouldEqual 3
    f(100, true) shouldEqual 3
    f(150, true) shouldEqual 3
    f(1000, true) shouldEqual 6
    /*val z = (2 to 6).map(i => (i, F(i)))
    z.apply(0) shouldEqual(2, 30)
    z.apply(4) shouldEqual(6, 290898)*/

    /*def f2(n: BigInt): List[BigInt] = {
      val s = n.toString
      val l = s.length
      val z = (1 to l).map(i => s.toSeq.sliding(i)).toList.flatten
      //println(n, z.filter(u => (u.sum % 3 == 0)).length, z)
      z.filter(u => (u.sum % 3 == 0)).map(u => BigInt(u.toString))
    }

    def F2(d: Int): BigInt = {
      val start = BigInt("1" + (2 to d).map(z => "0").mkString)
      val end = start * 10
      val y = (start until end).toList.map(bi => (bi, f2(bi))).filter(_._2.length % 3 == 0)
      val z = y.length
      println(d, (start until end), z, y.groupBy(_._2.length).mkString("\n  ", "\n  ", "\n"))
      z
    }

    F2(2) shouldEqual 30
    F2(3) shouldEqual F(3)*/

    def F3(start: BigInt, end: BigInt): BigInt = {
      val z = (start until end).toList.filter(bi => f(bi) % 3 == 0).length
      print(" [" + start + "-" + end + "]: " + z + ",")
      z
    }

    var t_la = Calendar.getInstance()
    /*F3(10, 100) shouldEqual 30
    F3(10, 100) shouldEqual ((F3(30, 40) * 3) + (F3(10, 20) * 6))
    F3(100, 1000) shouldEqual ((F3(300, 400) * 3) + (F3(100, 200) * 6))
    F3(200010010, 200010020) shouldEqual F3(200010070, 200010080)
    //F3(100, 200) shouldEqual (0 until 10).map(i => F3(100 + (i * 10), 100 + ((i + 1) * 10))).toList.sum
    F3(100, 200) shouldEqual ((F3(100, 110) * 7) + (F3(110, 120) * 3))
    F3(1000, 2000) shouldEqual ((F3(1000, 1100) * 7) + (F3(1100, 1200) * 3))
    F3(300, 400) shouldEqual ((F3(300, 310) * 4) + (F3(310, 320) * 6))
    F3(3000, 4000) shouldEqual ((F3(3000, 3100) * 4) + (F3(3100, 3200) * 6))
    F3(100, 1000) shouldEqual ((((F3(300, 310) * 4) + (F3(310, 320) * 6)) * 3) + (((F3(100, 110) * 7) + (F3(110, 120) * 3)) * 6))
    F3(1000, 10000) shouldEqual ((((F3(3000, 3100) * 4) + (F3(3100, 3200) * 6)) * 3) + (((F3(1000, 1100) * 7) + (F3(1100, 1200) * 3)) * 6))
    F3(3000, 3100) shouldEqual (0 until 10).map(i => F3(3000 + (i * 10), 3000 + ((i + 1) * 10))).toList.sum
    F3(3100, 3200) shouldEqual (0 until 10).map(i => F3(3100 + (i * 10), 3100 + ((i + 1) * 10))).toList.sum
    F3(1000, 1100) shouldEqual (0 until 10).map(i => F3(1000 + (i * 10), 1000 + ((i + 1) * 10))).toList.sum
    F3(1100, 1200) shouldEqual (0 until 10).map(i => F3(1100 + (i * 10), 1100 + ((i + 1) * 10))).toList.sum*/

    def dozemap(start: BigInt, inc: BigInt): BigInt = {
      print("   dzm:")
      val troisPremiersIcrements = (0 until 3).map(i => F3(start + (i * inc), start + ((i + 1) * inc))).toList
      println("")
      (troisPremiersIcrements.apply(0) * 4) + (troisPremiersIcrements.apply(1) * 3) + (troisPremiersIcrements.apply(2) * 3)
    }

    def F4(d: Int, check: Boolean = true): BigInt = {
      val start = BigInt("1" + (2 to d).map(z => "0").mkString)
      val end = start * 10
      val delta = start / 10
      val inc = delta / 10
      val result = ((((F3(start * 3, (start * 3) + inc) * 40) + (F3((start * 3) + delta, (start * 3) + (delta * 2)) * 6)) * 3) +
        (((F3(start, start + delta) * 7) + (F3(start + delta, start + delta + inc) * 30)) * 6)
        )
      if (check) {
        F3(start, end) shouldEqual result
      }
      println("")
      F3(start * 3, (start * 3) + inc) shouldEqual dozemap(start * 3, inc / 10)
      F3((start * 3) + delta, (start * 3) + (delta * 2)) shouldEqual dozemap((start * 3) + delta, inc)
      F3(start, start + delta) shouldEqual dozemap(start, inc)
      F3(start + delta, start + delta + inc) shouldEqual dozemap(start + delta, inc / 10)
      println("**[" + start + "-" + end + "]: " + result + "               delta: " + delta + ", inc: " + inc)
      result
    }

    t_la = timeStamp(t_la, "F3")
    //F4(3) shouldEqual 342
    t_la = timeStamp(t_la, "F4 " + 3)
    F4(4)
    t_la = timeStamp(t_la, "F4" + 4)
    F4(5)
    t_la = timeStamp(t_la, "F4" + 5)
    F4(6, false) shouldEqual 290898
    t_la = timeStamp(t_la, "F4" + 6)

    var result = 0
    println("Euler706[" + result + "]")
    result shouldEqual 0
  }

}