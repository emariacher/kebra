package euler

import kebra.MyLog._
import kebra.MyLog
import kebra.MyFileChooser
import scala.collection.immutable.TreeSet
import java.util.Calendar
import scala.language.postfixOps
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import scala.io.Source
import java.io.BufferedInputStream
import scala.collection.JavaConversions._
import scala.collection.immutable.ListSet
import akka.actor._
import akka.routing.RoundRobinRouter
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import euler.EulerFactorielle
//import Factorielles._

object EulerMainNoScalaTest extends App {
    //MyLog.newMyLog("EulerMainNoScalaTest", new File("log"), "log")
    try {
        var t_start = Calendar.getInstance
        myPrintDln("Hello World!")

        new Euler429

        timeStamp(t_start, "Au revoir Monde!")
    } catch {
        case ex: Exception => {
            println("\n" + ex)
            println("\n" + ex.getStackTrace.mkString("\n  ", "\n  ", "\n  ") +
                "\n")
        }
    } finally {
        println("\nHere!")
        MyLog.closeFiles
        println("\nThere!")
    }
}

class Euler429 {
    val modulo = BigInt(1000000009)

    myPrintIt(modulo, new EulerDiv(modulo).primes)
    (2 until 100).map(doZeJob1(_))

    def doZeJob1(i: Int) = {
        val fbi = EulerFactorielle.fact2(i)
        val primes = new EulerDiv(fbi).primes
        val byPrime = primes.groupBy(_ + 0).map(_._2.product).toList.sorted
        val prod = products(byPrime)
        val res = result(i, prod)
        //myPrintln(i, res)
        res
    }

    def products(byPrime: List[BigInt]) = {
        val len = byPrime.length - 1
        ((1 to len).map(byPrime.combinations(_).toList.map(_.product)).flatten
            ++ List[BigInt](1, byPrime.product)).sorted.toList
    }
    def result(i: Int, products: List[BigInt]) = {
        val sumod = products.map(bi => bi * bi).sum % modulo
        myPrintln("  " + i + " " + sumod + " " +
            new EulerDiv(sumod).primes.groupBy(_ + 0).map(c => (c._1, c._2.length)).toList.sortBy(_._1))
        sumod
    }
}
