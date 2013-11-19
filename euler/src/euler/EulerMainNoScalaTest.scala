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

object EulerMainNoScalaTest extends App {
    //MyLog.newMyLog("EulerMainNoScalaTest", new File("log"), "log")
    try {
        var t_start = Calendar.getInstance
        myPrintDln("Hello World!")

        new Euler346
        //new Euler133

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

class Euler346 {

    myAssert2(doZejob(1000), 15864)
    myAssert2(doZejob2(1000), 15864)
    myAssert2(doZejob3(1000), 15864)
    myAssert2(doZejob(100000), 12755696)
    myAssert2(doZejob2(100000), 12755696)
    myAssert2(doZejob3(100000), 12755696)

    myPrintIt(getRepUnits(1020, 2))
    myPrintIt(getRepUnits(1020, 3))
    myPrintIt(getRepUnits(1020, 4))
    myPrintIt(getRepUnits(1020, 5))

    def doZejob3(limit: Int) = {
        val z = getRepUnits(limit)
        val y = (ListSet.empty[Int] ++ z.map(_._2).flatten).toList.sorted
        y.sum + 1
    }

    def getRepUnits(limit: Double): List[(BigInt, List[Int], Int)] = {
        (2 until Math.sqrt(limit).toInt + 1).toList.map(n => getRepUnits(limit, n))
    }

    def getRepUnits(limit: Double, n: BigInt): (BigInt, List[Int], Int) = {
        var powers = List.empty[Int]
        var p = 1
        var r = 1.0
        while (r < limit) {
            powers = powers :+ r.toInt
            r = Math.pow(n.toDouble, p)
            p += 1
        }
        powers.sorted

        var repUnits = List.empty[Int]
        var lrepu = powers.sorted.reverse
        while (lrepu.length > 1) {
            repUnits = repUnits :+ lrepu.sum
            //myPrintIt(lrepu, repUnits)
            lrepu = lrepu.tail
        }
        repUnits = repUnits.sorted.reverse
        if (repUnits.head > limit) {
            repUnits = repUnits.tail
        }
        repUnits = repUnits.reverse.tail
        //myPrintln(n, repUnits.sorted, repUnits.sum)
        (n, repUnits.sorted, repUnits.sum)
    }

    def doZejob2(limit: Int) = {
        val result = (6 until limit).map(n => (n, isRepUnit2(n))).filter(_._2).map(_._1)
        myPrintDln("\n" + result.sum + " " + result.toList)
        result.sum + 1
    }
    def isRepUnit2(n: Int) = {
        getRacines(n).exists(b => shiftrec(n, b)._1)
    }

    def doZejob(limit: Int) = {
        val result = (1 until limit).map(n => (n, isRepUnit(n))).filter(_._2.length > 0)
        myPrintDln("\n" + result.map(_._1).sum + " " + result.map(_._1).toList)
        result.map(_._1).sum + 1
    }
    def isRepUnit(n: Int) = {
        //myPrint(".")
        val z = (2 until (Math.sqrt(n) + 1).toInt).filter(b => shiftrec(n, b)._1).toList
        if (z.length == 1) {
            myPrintln("  " + n + " " + z + " " + getRacines(n))
        }
        z
    }
    def shiftrec(n: Int, b: Int): (Boolean, Int) = {
        var shifted = shift(n, b)
        shifted match {
            case 0 => (false, 0)
            case 1 => (true, 1)
            case _ => if (shifted >= b) {
                shiftrec(shifted, b)
            } else {
                (false, shifted)
            }
        }
    }
    def shift(n: Int, b: Int) = {
        if ((n - 1) % b == 0) {
            (n - 1) / b
        } else {
            0
        }
    }

    def getRacines(n: Int) = {
        var racines = ListSet.empty[Int]
        var p = 2
        var r = 0
        do {
            r = Math.pow(n, 1.0 / p).toInt
            racines = racines + r
            p += 1
        } while (r > 2)
        racines.toList.sorted.reverse
    }
}

class Euler133 {
    // 615963 10Millions :(
    // 453688666 10Millions mais correct
    val premiers = EulerPrime.premiers100000

    val list2test = List[Int](Math.pow(10, 4).toInt, Math.pow(10, 5).toInt, Math.pow(10, 6).toInt, Math.pow(2, 23).toInt, Math.pow(5, 10).toInt, Math.pow(10, 7).toInt)

    doZeJob

    def doZeJob {
        val primes2 = doZeJob6

        val result = premiers.filter(!primes2.contains(_)).sum

        myPrintIt(result)
        myPrintIt(primes2)
        myPrintIt(premiers.filter(!primes2.contains(_)).take(40))
        myPrintDln("Job done!")
    }

    def doZeJob4(n: Int, primesI: List[BigInt]): List[BigInt] = {
        MyLog.waiting(1 second)
        myErrPrintln("doZeJob4 *********** " + n)
        MyLog.waiting(1 second)
        val bi = BigInt((1 until n + 1).map(z => "1").mkString)
        val bi2 = bi / primesI.product
        //val start = primesI.last
        val start = 1
        //myPrintIt("\n", n, start, bi2)
        val primes = new EulerDiv132(bi, premiers, start, 40).primes
        val justChecking = primes.product
        myPrintln("\n", n, primes.length, primes.sum, justChecking.toString.toList.length, "\n")
        val zprimes = primes.take(40)
        myPrintln("\n  zprimes: ", zprimes, zprimes.length)
        MyLog.waiting(1 second)
        myErrPrintln(n, primes.take(40).sum)
        MyLog.waiting(1 second)
        primes.sorted
    }
    def doZeJob6 = {
        var primes2 = ListSet.empty[BigInt]
        var primes = List[BigInt](1)
        list2test.map(i => {
            primes = doZeJob4(i, primes)
            primes2 = primes2 ++ primes
            val zprimes2 = primes2.toList.sorted.take(40)
            MyLog.waiting(1 second)
            myErrPrintln("\n  zprimes2: ", zprimes2, zprimes2.length)
            MyLog.waiting(1 second)
        })
        primes2
    }

}
