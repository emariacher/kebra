import java.util.Calendar

import org.scalatest._
import Euler._
import scala.collection.immutable.{ListSet, TreeSet}
import scala.math.BigInt

class EulerMain extends FlatSpec with Matchers {

  "Euler700" should "be OK" in {
    println("Euler700")

    val root = BigInt("1504170715041707")
    val mod = BigInt("4503599627370517")
    ((root * 3) % mod) shouldEqual BigInt("8912517754604")
    ((root * 3) % mod) < root shouldEqual true
    ((root + ((root * 3) % mod)) % mod) shouldEqual BigInt("1513083232796311")

    val biggestPrime = 220000
    val premiers = (new CheckEulerPrime(biggestPrime, 10000)).premiers
    val rootprimes = new EulerDiv2(root, premiers).primes
    val rootdivisors = new EulerDivisors(rootprimes).getFullDivisors
    val nearmod = rootdivisors.map(i => (i, mod / i, mod % i))
    val nearmod2 = nearmod.sortBy(_._3).tail
    println(root, rootprimes, rootdivisors)
    println("mod/root = ", mod.toDouble / root.toDouble)
    println(nearmod)
    println(nearmod2, "\n")
    /*Range(2,3).toList.foreach(bi => {
      println("  ", bi, mod + bi)
      println("     ", bi, mod + bi, new EulerDiv2(mod + bi, premiers,true).primes)
    })*/
    println(mod, mod + 2, new EulerDiv2(mod + 2, premiers).primes, "\n")

    var eulercoinList = List((1, root, root.toString.length))
    var max = 43000000
    var t_la = Calendar.getInstance()
    (1 to max).foreach(n => {
      val bi = ((root * n) % mod)
      if (bi < eulercoinList.head._2) {
        eulercoinList = (eulercoinList :+ (n, bi, bi.toString.length)).sortBy(_._2)
        val somme = eulercoinList.map(_._2).sum % mod
        //println(somme, bi, new EulerDiv2(bi, premiers).primes, eulercoinList)
        //println(somme, bi, eulercoinList.tail.head._2 - bi, n - eulercoinList.tail.head._1, eulercoinList)
      }
    })
    var oldlast = eulercoinList.map(_._2).last
    var vieilleSomme = eulercoinList.map(_._2).sum % mod
    t_la = timeStamp(t_la, "après1 max: " + max)
    var state = 0
    var n = 1
    var prevdiff = 1
    var prevremdiff = 1.0
    var prevdelta: (BigInt, Double, Double) = null
    var prevy = 1.0
    var prevx = 1.0
    eulercoinList = List((1, root, root.toString.length))
    var max1 = 60000000
    var lastEulerCoin = BigInt(0)
    while (n < max1) {
      val bi = ((root * n) % mod)
      lastEulerCoin = eulercoinList.head._2
      if (bi < lastEulerCoin) {
        eulercoinList = (eulercoinList :+ (n, bi, bi.toString.length)).sortBy(_._2)
        val somme = eulercoinList.map(_._2).sum % mod
        val x = somme.toDouble / n.toDouble
        val diff = n - eulercoinList.tail.head._1
        val remdiff = math.abs((diff.toDouble * root.toDouble) / mod.toDouble) % 1.0
        println("\n[" + n, somme, "" + bi + " < " + lastEulerCoin + "] diff:", diff, remdiff + " vs " + prevremdiff,
          (n - eulercoinList.tail.head._1).toDouble / prevdiff.toDouble)
        val y = math.abs((diff.toDouble * root.toDouble) / mod.toDouble)
        val a = (-20 to 6600).map(u => (u, math.abs(((diff.toDouble + u) * root.toDouble) / mod.toDouble) % 1.0)).filter(u => u._2 > 0.999)
        val b = (0 to 200).toList.map(v => (0 to 200).toList.
          map(u => (u + (v * 500), math.abs(((diff.toDouble + u + (v * 500)) * root.toDouble) / mod.toDouble) % 1.0))).flatten.filter(u => u._2 > 0.999)
        println(y, "   ", a)
        println(y, "   ", b)
        val z = nearmod.map(z => (z._1, 0.0, math.abs(((z._2 - bi)).toDouble))).sortBy(_._3)(Ordering.Double.TotalOrdering)
        prevdelta = z.head
        prevdiff = diff
        prevremdiff = remdiff
        prevy = y
        prevx = x
        n += prevdiff
        if ((n > max) & (eulercoinList.map(_._2).last == oldlast)) {
          println("************Checking! @" + max + "**************")
          eulercoinList.map(_._2).sum % mod shouldEqual vieilleSomme
          timeStamp(t_la, "après2 max: " + max)
        }
        state = 1
      } else if (state == 1) {
        state = 2
        n += prevdiff * 2
      } else {
        state = 2
        n += 1
      }
    }
    t_la = timeStamp(t_la, "après2 max1: " + max1)

    n = 1
    prevdiff = 1
    prevdelta = (BigInt(0), 0.0, 0.0)
    eulercoinList = List((1, root, root.toString.length))
    t_la = timeStamp(t_la, "après3 ")


    var result = 0
    println("Euler700[" + result + "]")
    result shouldEqual 0
  }

}