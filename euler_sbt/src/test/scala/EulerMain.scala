import java.util.Calendar

import org.scalatest._
import Euler._
import scala.collection.immutable.{ListSet, TreeSet}
import scala.math.BigInt

class EulerMain extends FlatSpec with Matchers {

  "Euler457" should "be OK" in {
    println("Euler457")

    def f(n: BigInt): BigInt = (n * n) - (3 * n) - 1

    def OK(p: BigInt, n: BigInt): Boolean = f(n) % (p * p) == 0

    var t_la = Calendar.getInstance()
    val premiers1000 = EulerPrime.premiers1000
    println(premiers1000.toList.map(p => {
      (p, p * p, (0 to 1000).map(n => (n, f(n), OK(p, n))).filter(_._3))
    }).filter(!_._3.isEmpty).mkString("\n"))
    t_la = timeStamp(t_la, "ici")
    println((0 to 1000).map(n => (n, f(n))).map(nn => {
      (nn._1, nn._2, premiers1000.filter(p => nn._2 % (p * p) == 0).mkString("-"))
    }).filter(!_._3.isEmpty).mkString("\n"))
    t_la = timeStamp(t_la, "là")

    var result = 0
    println("Euler457[" + result + "]")
    result shouldEqual 0
  }

}