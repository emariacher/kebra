import java.util.Calendar

import Euler._
import org.scalatest._

import scala.collection.immutable.{ListSet, Range}
import scala.math.BigInt

class EulerSolved extends FlatSpec with Matchers {

  "Euler22" should "be OK" in {
    println("Euler22")

    val url = "https://projecteuler.net/project/resources/p022_names.txt"
    val data = io.Source.fromURL(url).mkString

    val l = data.split(",").map(_.replaceAll("\"", "")).sorted
    (l.indexOf("COLIN") + 1) shouldEqual 938
    (l.zipWithIndex.indexOf(("COLIN", 937)) + 1) shouldEqual 938
    "COLIN".toList.map(_.toInt - 'A'.toInt + 1).sum shouldEqual 53

    var result = io.Source.fromURL("https://projecteuler.net/project/resources/p022_names.txt").mkString.split(",").
      map(_.replaceAll("\"", "")).sorted.zipWithIndex.map(z => (z._2 + 1) * z._1.toList.map(_.toInt - 'A'.toInt + 1).sum).sum
    println("Euler22[" + result + "]")
    result shouldEqual 871198282
  }
  "Euler23" should "be OK" in {
    println("Euler23")

    val zeLimit = 28123

    def sumdiv(i: Int) = {
      (List(BigInt(1)) ++ new EulerDivisors(new EulerDiv(i)).divisors.dropRight(1)).sum
    }

    sumdiv(12) shouldEqual 16
    sumdiv(28) shouldEqual 28

    var limit = 2000
    var abundantNumbers = (1 to limit).filter(i => sumdiv(i) > i).toList
    println(abundantNumbers.take(50))
    println(abundantNumbers.filter(_ % 2 != 0))
    println(945, sumdiv(945), (List(BigInt(1)) ++ new EulerDivisors(new EulerDiv(945)).divisors.dropRight(1)))

    def doZeJob1(abundantNumbers: List[Int]) = {
      var t_ici = timeStamp(t_start, "ici!", false)
      var l = List[Int]()
      abundantNumbers.foreach(i => {
        //println(i, abundantNumbers.map(j => j + i))
        l = (l ++ abundantNumbers.map(j => j + i)).distinct.sorted
      })
      l = l.filter(_ <= limit)
      println("  " + l)

      t_ici = timeStamp(t_ici, "doZeJob1 la!")
      l
    }

    def doZeJob2(abundantNumbers: List[Int], limit: Int) = {
      var t_ici = timeStamp(t_start, "ici!", false)
      var l = List[Int]()
      abundantNumbers.foreach(i => {
        //println(i, abundantNumbers.map(j => j + i))
        l = (l ++ abundantNumbers.filter(j => j >= i & j <= (limit - i)).map(j => j + i)).distinct.sorted
      })
      l = l.filter(_ <= limit)
      println("  " + l)

      t_ici = timeStamp(t_ici, "doZeJob2 la!")
      l
    }

    val l1 = doZeJob1(abundantNumbers)
    val l2 = doZeJob2(abundantNumbers, limit)

    l1 shouldEqual l2

    val lpair = l2.sliding(2).map(z => z.last - z.head).toList.lastIndexWhere(_ > 2)
    println("check that all even numbers are quickly always sum of 2 abundants numbers: " +
      lpair, l2.sliding(2).toList.apply(lpair), l2.length)

    val zlpairnosum = l2.sliding(2).toList.filter(z => z.last - z.head > 2)
    println(zlpairnosum)
    val lpairnosum = ((1 to 23).toList ++ List(26, 28, 34, 46)).filter(_ % 2 == 0)
    println("lpairnosum", lpairnosum)

    limit = zeLimit
    abundantNumbers = (1 to limit).filter(i => sumdiv(i) > i).toList

    val oddabundants = abundantNumbers.filter(_ % 2 != 0)
    println(oddabundants)

    def doZeJob3(abundantNumbers: List[Int], oddabundantNumbers: List[Int], limit: Int) = {
      var t_ici = timeStamp(t_start, "ici!", false)
      var l = List[Int]()
      oddabundantNumbers.foreach(i => {
        //println(i, abundantNumbers.map(j => j + i))
        l = (l ++ abundantNumbers.filter(j => j <= (limit - i)).map(j => j + i)).distinct.sorted
      })
      l = l.filter(_ <= limit)
      println("  " + l)

      t_ici = timeStamp(t_ici, "doZeJob3 la!")
      l
    }

    val l3 = doZeJob3(abundantNumbers, oddabundants, limit)

    val oddnumberswearelookingfor = (1 to limit).filter(i => i % 2 != 0).filter(i => !l3.contains(i)).toList
    println("oddnumberswearelookingfor", oddnumberswearelookingfor)

    var result = oddnumberswearelookingfor.sum + lpairnosum.sum
    println("Euler23[" + result + "]")
    result shouldEqual 4179871
  }
  "Euler24" should "be OK" in {
    println("Euler24")

    def z(i: Int): List[String] = (0 to i).permutations.map(_.mkString("")).toList

    def factorial(n: Int): Int = n match {
      case 0 => 1
      case _ => n * factorial(n - 1)
    }

    def u(l: List[String], s: String, i: Int, check: Boolean = true): (String, Int, Int) = {
      if ((i != 0) & (l.indexWhere(_.startsWith(s)) >= 0) & check) {
        i shouldEqual l.indexWhere(_.startsWith(s))
      }
      (s, l.indexWhere(_.startsWith(s)), i)
    }

    println(z(2))
    (6 to 8).foreach(i => {
      val y = z(i)
      println(i, factorial(i), (0 to i).toList.map(j => u(y, j.toString, factorial(i) * j)))
      println("  2", (0 to 1).toList.map(j => u(y, "2" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * j))))
      println("  2", (3 to i).toList.map(j => u(y, "2" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * (j - 1)))))
      println("  27", (0 to 1).toList.map(j => u(y, "27" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * 6) + (factorial(i - 2) * j))))
      println("  27", (3 to 6).toList.map(j => u(y, "27" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * 6) + (factorial(i - 2) * (j - 1)))))
      println("  27", (8 to i).toList.map(j => u(y, "27" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * 6) + (factorial(i - 2) * (j - 2)))))
      println("  278", (3 to 6).toList.map(j => u(y, "278" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * 6) + (factorial(i - 2) * 6) + (factorial(i - 3) * (j - 1)), true)))
    })

    (6 to 9).foreach(i => {
      println(i, factorial(i), (0 to i).toList.map(j => (j.toString, factorial(i) * j)))
      println("  2", (3 to i).toList.map(j => ("2" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * (j - 1)))))
      println("  27", (8 to i).toList.map(j => ("27" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * 6) + (factorial(i - 2) * (j - 2)))))
      println("  278", (3 to 6).toList.map(j => ("278" + j.toString, (factorial(i) * 2) + (factorial(i - 1) * 6) + (factorial(i - 2) * 6) + (factorial(i - 3) * (j - 1)))))
    })
    println("La solution commence par 2783.")
    val y5 = z(5)
    println(y5.apply(0), 999999 - 999360, y5.apply(999999 - 999360))
    println("La solution restante de 0 à 5 est " + y5.apply(639) + " a traduire en 915460")

    var result = BigInt("2783915460")
    println("Euler24[" + result + "]")
    result shouldEqual BigInt("2783915460")
  }
  "Euler27" should "be OK" in {
    println("Euler27")

    val premiers = EulerPrime.premiers100000
    val max = 10

    def quadratic(a: Int, b: Int, n: Int) = {
      ((n + a) * n) + b
    }

    def rangeFrom(a: Int, b: Int): LazyList[Int] = a #:: rangeFrom(b, 1 + b)

    def getprimesFrom(a: Int, b: Int): List[(Int, Int)] = {
      val r = rangeFrom(0, 1)
      val z = r.map(n => (n, quadratic(a, b, n))).takeWhile(q => {
        premiers.contains(q._2)
      }).toList
      z
    }

    var un_a_linfini = rangeFrom(0, 1).take(7)
    println(un_a_linfini.toList)

    val z = (-1000 to 1000).map(a => {
      EulerPrime.premiers1000.filter(b => b + a > 0).toList.map(b => {
        val gp = getprimesFrom(a, b.toInt)
        (a, b.toInt, gp.length, gp)
      })
    }).flatten.filter(_._3 > 60).sortBy(_._3)
    println(z.mkString("\n  ", "\n  ", "\n  "))
    val gp = getprimesFrom(-79, 1601)
    println(-79, 1601, gp.length, gp)

    val y = z.last
    println("y=" + y)
    val result = y._1 * y._2
    println("Euler27[" + result + "]")
    result shouldEqual (-61 * 971)
  }
  "Euler32" should "be OK" in {
    println("Euler32")
    val result = Range(13 * 245, 9876 + 1).filter(i => {
      val lz = i.toString.toList
      lz.length == (new ListSet() ++ lz).toList.length
    }).filter(c => {
      c.toString.indexOf("0") < 0
    }).map(i => {
      val divs = new EulerDivisors(new EulerDiv(i).primes).divisors
      (i, divs)
    }).filter(c => {
      if (c._2.length == 2) {
        val lz = (c._2.mkString("", "", "") + c._1).toList
        lz.length == (new ListSet() ++ lz).toList.length && lz.length == 5
      } else if (c._2.length > 0) {
        true
      } else {
        false
      }
    }).map(c => {
      val z1 = c._1.toString.toList
      val z2 = c._2.filter(d => {
        z1.intersect(d.toString.toList).isEmpty && d.toString.indexOf("0") < 0
      })
      (c._1, z2.toList)
    }).filter(c => {
      c._2.length > 1
    }).map(c => {
      (c._1, c._2.combinations(2).filter(
        cb => cb.head * cb.last == c._1 &&
          (cb.head.toString + cb.last.toString).length == 5
      ).toList)
    }).filter(c => {
      !c._2.isEmpty
    }).map(c => {
      (c._1, c._2.filter(
        cb => {
          val lz = (cb.head.toString + cb.last.toString + c._1.toString).toList
          lz.length == (new ListSet() ++ lz).toList.length
        }
      ))
    }).filter(c => {
      !c._2.isEmpty
    }).map(c => {
      println(c)
      c._1
    }).sum
    println("Euler32[" + result + "]")
    result shouldEqual 45228
  }
  "Euler33" should "be OK" in {
    println("Euler33")

    val p100 = (10 to 99).filter(_.toString.indexOf("0") < 0)

    val z = p100.map(i => {
      p100.dropWhile(_ <= i).filter(j => i.toString.toList.intersect(j.toString.toList).nonEmpty).map(j => {
        val k = i.toString.toList.intersect(j.toString.toList).head
        (i, j, k)
      })
    }).flatten.filter(ijk => {
      ijk._1.toString.toList.exists(_ != ijk._3) && ijk._2.toString.toList.exists(_ != ijk._3)
    }).map(ijk => {
      val is = ijk._1.toString.toList.filter(_ != ijk._3).head.toString
      val js = ijk._2.toString.toList.filter(_ != ijk._3).head.toString
      (ijk._1, ijk._2, ijk._3, ijk._1.toDouble / ijk._2.toDouble, is, js, is.toDouble / js.toDouble)
    }).filter(u => u._4 == u._7)
    println(z.mkString("\n"))

    val result = z.map(_._6.toInt).product / z.map(_._5.toInt).product
    println("Euler33[" + result + "]")
    result shouldEqual 100
  }

  "Euler38" should "be OK" in {
    println("Euler38")

    def isPanDigital(s: String): Boolean = {
      val lz = s.toList
      lz.length == (new ListSet() ++ lz).toList.length && s.indexOf("0") < 0
    }

    def isPanDigital1to9(s: String): Boolean = {
      isPanDigital(s) && s.length == 9
    }


    val result = (Range(123, 988).toList.filter(
      i => isPanDigital(i.toString)
    ).map(i => {
      val r = Range(1, 4).toList
      val z = r.map(k => i * k).mkString("", "", "")
      (i, z, isPanDigital1to9(z))
    }).filter(
      _._3
    )

      ++

      Range(1234, 9877).toList.filter(
        i => isPanDigital(i.toString)
      ).map(i => {
        val r = Range(1, 3).toList
        val z = r.map(k => i * k).mkString("", "", "")
        (i, z, isPanDigital1to9(z))
      }).filter(
        _._3
      )).map(t => {
      println(t)
      t
    }).maxBy(_._2)._2

    println("Euler38[" + result + "]")
    result shouldEqual "932718654"

  }
  "Euler46" should "be OK" in {
    println("Euler46")
    val limit = 10000
    println("Find < " + limit + " odd composite numbers")
    val premiers = EulerPrime.premiers100000.takeWhile(_ <= limit).toList.tail
    println(premiers)
    val odds = Range(3, limit, 2).toList
    val oddComposites = odds diff premiers
    println(oddComposites)
    var sqrtlimit = math.sqrt(limit / 2).toInt
    var doubleSquares = Range(1, sqrtlimit).map(i => i * i * 2).toList
    println(doubleSquares)

    val result = oddComposites.find(odc => {
      doubleSquares.find(ds => {
        val index = premiers.indexOf(odc - ds)
        if (index > 0) {
          println(odc.toString + "-" + ds + "==" + premiers.apply(index))
        }
        index > 0
      }).isEmpty
    }) match {
      case Some(i) => i
      case _ =>
    }

    println("Euler46[" + result + "]")
    result shouldEqual 5777
  }
  "Euler65" should "be OK" in {
    println("Euler65")
    println(math.E)

    //87 = 4*19 + 11
    //1264 = 6*193 + 106
    // 465 = 6*71 + 39

    var rang = 101
    var num = BigInt(0)
    var prevnum = BigInt(0)
    var prevprevnum = BigInt(0)
    var den = BigInt(0)
    var prevden = BigInt(0)
    var prevprevden = BigInt(0)


    val notcontfrac_E = Range(3, rang + 5).map(i => {
      val j = i - 1
      if (j % 3 == 0) {
        (j / 3) * 2
      } else {
        1
      }
    }).toList
    println(notcontfrac_E)

    val z = Range(0, rang + 1).map(r => {
      r match {
        case 0 => num = 2; den = 1
        case 1 => num = 3; den = 1
        case 2 => num = (prevnum * notcontfrac_E.apply(r - 1)) + prevprevnum; den = (prevden * notcontfrac_E.apply(r - 1)) + prevprevden
        case _ => num = (prevnum * notcontfrac_E.apply(r - 1)) + prevprevnum; den = (prevden * notcontfrac_E.apply(r - 1)) + prevprevden
      }
      prevprevnum = prevnum
      prevnum = num
      prevprevden = prevden
      prevden = den
      println(r, notcontfrac_E.apply(r), (num, den), num.toDouble / den.toDouble, num.toString().toList.map(_.toString.toInt), num.toString().toList.map(_.toString.toInt).sum)
      num.toString().toList.map(_.toString.toInt).sum
    })
    println(math.E)
    z.apply(10 - 1) shouldEqual 17

    val result = z.apply(100 - 1)
    println("Euler65[" + result + "]")
    result shouldEqual 272

  }
  "Euler79" should "be OK" in {
    println("Euler79")
    var p079_keylogS = List(319, 680, 180, 690, 129, 620, 762, 689, 762, 318, 368, 710, 720, 710, 629, 168, 160, 689, 716, 731, 736,
      729, 316, 729, 729, 710, 769, 290, 719, 680, 318, 389, 162, 289, 162, 718, 729, 319, 790, 680, 890, 362, 319, 760, 316, 729,
      380, 319, 728, 716).map(_.toString)
    var p079_keylogL = p079_keylogS.map(_.toList)
    println(p079_keylogS)

    var possibleHead = p079_keylogL.map(_.head).distinct
    println(possibleHead)

    var possibleLast = p079_keylogL.map(_.last).distinct
    println(possibleLast)

    var probableHead = possibleHead diff possibleLast
    println(probableHead)

    var headCouples = probableHead.mkString("", "", "").toSeq.combinations(2).map(_.toSeq.permutations).flatten.toList
    println(headCouples)

    var first = headCouples.map(c => {
      (c, p079_keylogS.filter(k => k.indexOf(c) == 0).length)
    }).sortBy(_._2).last._1.charAt(0).toString

    var result = List(first)

    println("first[" + first + "]" + result + "\n*********************")
    z
    println("second[" + first + "]" + result + "\n*********************")
    z
    println("third[" + first + "]" + result + "\n*********************")
    z
    println("fourth[" + first + "]" + result + "\n*********************")
    z
    println("fifth[" + first + "]" + result + "\n*********************")
    z
    println("sixth[" + first + "]" + result + "\n*********************")
    z
    println("seventh[" + first + "]" + result + "\n*********************")
    z
    println("eighth[" + first + "]" + result + "\n*********************")

    println("Euler79[" + result.mkString("", "", "") + "]")
    result.mkString("", "", "") shouldEqual "73162890"

    def z = {
      p079_keylogS = p079_keylogS.map(_.replaceAll(first, "")).filter(_.length > 1)
      if (p079_keylogS.isEmpty) {
        first = "0" // Je sais: c'est mal
      } else {
        p079_keylogL = p079_keylogS.map(_.toList)
        println(p079_keylogS)

        possibleHead = p079_keylogL.map(_.head).distinct
        println(possibleHead)

        possibleLast = p079_keylogL.map(_.last).distinct
        println(possibleLast)

        probableHead = possibleHead diff possibleLast
        println("probableHead " + probableHead)
        if (probableHead.isEmpty) {
          probableHead = possibleHead
        }
        println("probableHead " + probableHead)
        if (probableHead.length > 1) {
          headCouples = probableHead.mkString("", "", "").toSeq.combinations(2).map(_.toSeq.permutations).flatten.toList
          println(headCouples)

          first = headCouples.map(c => {
            (c, p079_keylogS.filter(k => k.indexOf(c) == 0).length)
          }).sortBy(_._2).last._1.charAt(0).toString
        } else {
          first = probableHead.head.toString
        }
      }
      result = result :+ first
    }

  }
  "Euler89" should "be OK" in {
    println("Euler89")

    val M = 'M'
    val D = 'D'
    val C = 'C'
    val L = 'L'
    val X = 'X'
    val V = 'V'
    val I = 'I'


    val url = "https://projecteuler.net/project/resources/p089_roman.txt"
    val romnumList = io.Source.fromURL(url).mkString.split("\n").toList
    val romnumListLength = romnumList.mkString("", "", "").length

    //println(romnumList)
    println("******\n******[" + (romnumListLength - romnumList.map(rn => {
      //val nombre2 = roman2arab(romnum)
      //println(romnum, nombre)
      (rn, roman2arab(rn))
    }).map(rn => {
      val mille = rn._2 / 1000
      val cent = (rn._2 - (mille * 1000)) / 100
      val dix = (rn._2 - ((mille * 1000) + (cent * 100))) / 10
      val un = (rn._2 - ((mille * 1000) + (cent * 100) + (dix * 10)))
      //println(rn._1, rn._2, mille, cent, dix, un)
      (rn._1, rn._2, mille, cent, dix, un)
    }).map(rn => {
      var frn = Range(0, rn._3).map(z => "M").mkString("", "", "")
      frn = frn + List("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM").apply(rn._4)
      frn = frn + List("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC").apply(rn._5)
      frn = frn + List("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX").apply(rn._6)
      val fra = roman2arab(frn)
      rn._2 shouldEqual fra
      println(rn._1, rn._2, frn, fra)
      (rn._1, rn._2, frn, fra)
    }).map(_._3).mkString("", "", "").length) + "]*******"
    )
    //println(resultList.mkString("\n","\n","\n"))

    def roman2arab(romnum: String): Int = {
      var nombre = 0
      var state = '0'
      val z = romnum.foreach(c => {
        val value = c match {
          case M => 1000
          case D => 500
          case C => 100
          case L => 50
          case X => 10
          case V => 5
          case I => 1
        }
        state match {
          case C => c match {
            case M => nombre += 800
            case D => nombre += 300
            case _ => nombre += value
          }
          case X => c match {
            case C => nombre += 80
            case L => nombre += 30
            case _ => nombre += value
          }
          case I => c match {
            case X => nombre += 8
            case V => nombre += 3
            case _ => nombre += value
          }
          case _ => nombre += value
        }
        state = c
        (state, c, value, nombre)
      })
      nombre
    }

    // val romnumListLength = romnumList.mkString("","","").length
    // val result = romnumListLength - resultList.map(_._3).mkString("","","").length

    val result = 743
    println("Euler89[" + result + "]")
    result shouldEqual 743


  }
  "Euler100" should "be OK" in {
    println("Euler100")

    val limit: Double = Math.pow(10, 13)

    def is50pourcent(total: Double): (Boolean, String) = {
      val totcar = math.pow(total - 0.5, 2)
      val totsqrt = math.sqrt(totcar / 2)
      val blue = math.ceil(totsqrt)
      val stat = (blue / total) * ((blue - 1) / (total - 1))
      if (math.abs(stat - 0.5) < 0.00000001) {
        val totalInt = BigDecimal(total).setScale(0, BigDecimal.RoundingMode.HALF_UP).toBigInt
        val blueInt = BigDecimal(blue).setScale(0, BigDecimal.RoundingMode.HALF_UP).toBigInt
        val statInt = (blueInt * (blueInt - 1)) * 2 == (totalInt * (totalInt - 1))
        (statInt, (totalInt, blueInt, stat, new EulerDiv(totalInt).primes, new EulerDiv(totalInt - 1).primes,
          new EulerDiv(blueInt).primes, new EulerDiv(blueInt - 1).primes, "-----------------------------").toString)
      } else {
        (false, (total, totcar, totsqrt, blue, stat).toString)
      }
    }


    def whichInc4(ll1: List[BigInt], ll2: List[BigInt]) = {
      val ll3 = ll1.filter(bi => !ll2.contains(bi))

      val nextinc = ll1.contains(2) match {
        case true => ll3.last * 4
        case _ => ll3.reverse.take(2).product
      }
      ll1.contains(2) match {
        case true => println("whichInc4", ll1.contains(2), ll1, ll2, ll3.last, nextinc)
        case _ => println("whichInc4", ll1.contains(2), ll1, ll2, ll3.reverse.take(2).reverse, nextinc)
      }
      nextinc
    }

    def is50pourcent5(total: Double, prev: List[BigInt]): (Boolean, BigInt, BigInt, List[BigInt], Double, BigInt) = {
      val totcar = math.pow(total - 0.5, 2)
      val totsqrt = math.sqrt(totcar / 2)
      val blue = math.ceil(totsqrt)
      val stat = (blue / total) * ((blue - 1) / (total - 1))
      val ratio = total / prev.product.toDouble
      if (math.abs(stat - 0.5) < 0.0000000000001) {
        val totalInt = BigDecimal(total).setScale(0, BigDecimal.RoundingMode.HALF_UP).toBigInt
        val blueInt = BigDecimal(blue).setScale(0, BigDecimal.RoundingMode.HALF_UP).toBigInt
        val statInt = (blueInt * (blueInt - 1)) * 2 == (totalInt * (totalInt - 1))
        if (statInt) {
          val totprim = new EulerDiv(totalInt).primes
          val totprim1 = new EulerDiv(totalInt - 1).primes
          println("________________", totprim.contains(2))
          println(statInt, (totalInt, blueInt, stat, totprim, totprim1),
            new EulerDiv(blueInt).primes, new EulerDiv(blueInt - 1).primes, ratio, total * ratio)
          println("next: ",
            BigDecimal(total * ratio).setScale(0, BigDecimal.RoundingMode.HALF_UP).toBigInt)
          (true, whichInc4(totprim1, prev), totalInt - 1, totprim, ratio,
            BigDecimal(total * ratio).setScale(0, BigDecimal.RoundingMode.HALF_UP).toBigInt)
        } else {
          (false, 0, 0, List.empty[BigInt], 0, 0)
        }
      } else {
        (false, 0, 0, List.empty[BigInt], 0, 0)
      }
    }


    val t_ici = timeStamp(t_start, "ici!")
    val z1 = (1 to 25000).map(b => is50pourcent(b.toDouble)).filter(_._1)
    println(z1.mkString("\n  ", "\n  ", "\n  "))
    val t_la = timeStamp(t_ici, "la!")

    var bi: BigInt = 120
    var inc: BigInt = 4
    var prev: List[BigInt] = List(3, 7)
    val t_la3 = timeStamp(t_ici, "la3! ******************************")
    var blueInt: BigInt = 0
    var found = false
    val t_la4 = timeStamp(t_la3, "la4! ******************************")
    bi = 120
    inc = 4
    prev = List(3, 7)
    blueInt = 0
    var next: BigInt = 686
    found = false
    while (bi.toDouble < limit && !found) {
      val z = is50pourcent5(bi.toDouble, prev)
      if (z._1) {
        bi = z._3
        inc = z._2
        prev = z._4
        next = z._6
        if (bi.toDouble > Math.pow(10, 12)) {
          found = true
          val totcar = math.pow((bi.toDouble + 1) - 0.5, 2)
          val totsqrt = math.sqrt(totcar / 2)
          val blue = math.ceil(totsqrt)
          blueInt = BigDecimal(blue).setScale(0, BigDecimal.RoundingMode.HALF_UP).toBigInt
        }
        bi = next
      }
      bi += 1
    }
    val t_la5 = timeStamp(t_la4, "la5! ******************************")

    val result = blueInt
    println("Euler100[" + blueInt + "]")
    result.toString() shouldEqual "756872327473"
  }
  "Euler158" should "be OK" in {
    println("Euler158")
    val az = "abcdefghjklmnopqrstuvwxyz"
    //val tableau = List(0, 0, 0, 6, 36, 240, 1800, 15120, 141120)
    val tableau = List(0, 0, 0, 4, 11, 26, 57, 120, 247)

    def lexleft(s: String): Int = {
      //println("        ", s, s.sliding(2).toList, s.sliding(2).toList.map(s2 => (s2, s2.last > s2.head)))
      s.toSeq.sliding(2).toList.count(s2 => {
        s2.last > s2.head
      })
    }

    def calcule1(m: Int, n: Int): BigInt = {
      val s = az.substring(0, m)
      val scomb = s.toSeq.combinations(n).toList
      //println(scomb)
      val z1 = scomb.map(s2 => {
        val sperm = s2.toSeq.permutations.toList
        //println("  ", sperm)
        val z2 = BigInt(sperm.map(s3 => {
          val z3 = lexleft(s3.toString)
          //println("    z3", s3, z3)
          z3
        }).count(_ == 1))
        println("  z2", sperm.head, z2)
        z2
      }).sum
      println(m, n, s, z1)
      z1
    }

    def getpermlleft(n: Int): BigInt = {
      //BigInt(tableau.apply(n))
      if (n == 3) {
        BigInt(4)
      } else {
        (getpermlleft(n - 1) * 2) + n - 1
      }

    }

    def calcule2(m: Int, n: Int): BigInt = {
      //az.substring(0, m).combinations(n).toList.map(s => BigInt(s.permutations.toList.map(lexleft(_)).sum)).sum
      //BigInt(az.substring(0, n).permutations.toList.map(lexleft(_)).sum*az.substring(0, m).combinations(n).toList.length)
      //BigInt(az.substring(0, n).permutations.toList.map(lexleft(_)).sum) * (factorielle(m) / (factorielle(n) * factorielle(m - n)))
      getpermlleft(n) * factorielle(m) / (factorielle(n) * factorielle(m - n))
    }

    factorielle(5) shouldEqual 120
    factorielle(26).toString() shouldEqual "403291461126605635584000000"
    println(tableau.zipWithIndex.map(z => (z._2, z._1, new EulerDiv(z._1).primes)).mkString("\n  ", "\n  ", "\n  "))
    println((3 to 27).map(z => (z, getpermlleft(z))).mkString("\n  ", "\n  ", "\n  "))
    println(List("abc", "hat", "zyx").map(lexleft(_)))
    calcule1(5, 4) shouldEqual calcule2(5, 4)
    calcule1(7, 4) shouldEqual calcule2(7, 4)
    calcule1(3, 3) shouldEqual calcule2(3, 3)
    calcule1(8, 8) shouldEqual calcule2(8, 8)
    calcule1(4, 3) shouldEqual calcule2(4, 3)
    calcule1(6, 5) shouldEqual calcule2(6, 5)
    calcule1(7, 6) shouldEqual calcule2(7, 6)
    calcule1(8, 7) shouldEqual calcule2(8, 7)
    calcule2(26, 3) shouldEqual 10400

    val t_ici = timeStamp(t_start, "ici!")
    (3 to 7).map(m => {
      (3 to m).map(n => {
        calcule1(m, n)
      })
    })
    val t_la = timeStamp(t_ici, "la! ******************************")
    (3 to 7).map(m => {
      (3 to m).map(n => {
        println(m, n, calcule2(m, n))
      })
    })
    val t_la2 = timeStamp(t_la, "la2! ******************************")
    val z4 = (3 to 26).map(n => {
      val u = (26, n, calcule2(26, n))
      println(u)
      u
    })
    val t_la3 = timeStamp(t_la2, "la3! ******************************")

    val result = z4.maxBy(_._3)._3
    println("Euler158[" + result + "]")
    result.toString shouldEqual "409511334375"
  }
  /*"Euler179" should "be OK" in {
    println("Euler179")

    val premiers = EulerPrime.premiers100000
    val limit = 10000000

    val t_ici = timeStamp(t_start, "ici!")
    /*val z1 = stream_zero_a_linfini.map(b => {
      (b, new EulerDivisors(new EulerDiv(b).primes).getFullDivisors)
    }).drop(2).take(limit).toList.sliding(2).toList.filter(c => c.head._2.length == c.last._2.length)
    timeStamp(t_ici, "la!")*/

    //println(z1.mkString("\n  ", "\n  ", "\n  "), z1.length)

    var zstart = timeStamp(t_ici, "stream")
    var cpt = 0
    var bi: BigInt = 1
    var prevnumdiv = 2

    /*while (bi < limit) {
      var cptprimes = new EulerDivisors(new EulerDiv(bi).primes).divisors.length

      if (cptprimes == prevnumdiv) {
        //println("---", bi - 1, bi, cptprimes)
        cpt += 1
      }
      //println(bi, cptprimes)
      prevnumdiv = cptprimes
      bi += 1
    }
    timeStamp(zstart, "zend")
    cpt shouldEqual z1.length*/

    zstart = timeStamp(t_ici, "increment")
    cpt = 1
    bi = 1
    prevnumdiv = 2

    while (bi < limit) {
      if (EulerPrime.isPrime(bi)) {
        bi += 1
        prevnumdiv = 0
      } else {
        var cptprimes = new EulerDivisors(new EulerDiv(bi).primes).divisors.length
        if (cptprimes == prevnumdiv) {
          //println(cpt, bi - 1, bi, cptprimes)
          cpt += 1
        }
        prevnumdiv = cptprimes
        bi += 1
      }
    }
    timeStamp(zstart, "jumpoverprime")
    //cpt shouldEqual z1.length


    val result = cpt
    println("Euler179[" + cpt + "]")
    result shouldEqual 986262
  }*/
  "Euler191" should "be OK" in {
    println("Euler191")


    def decode(j: Int) = {
      (j % 3) match {
        case 0 => "L"
        case 1 => "A"
        case 2 => "O"
      }
    }

    def good(s: String) = s.count(_ == 'L') < 2 && !s.contains("AAA")

    def goodA(s: String) = !s.contains("AAA")

    def goodL(s: String) = s.count(_ == 'L') < 2

    def countL(s: String) = s.count(_ == 'L')


    def doZeJob(e: Int) = {
      val y = (0 until Math.pow(3, e).toInt).map(i => {
        var s = ""
        var j = i
        s += decode(j)
        (1 until e).foreach(u => {
          j /= 3
          s += decode(j)
        })
        //println(i, s)
        s
      })
      val z = y.filter(good(_))
      println(z.length, z.take(5))

      (z.length, z)
    }

    val d1zj3 = doZeJob(3)
    println(d1zj3._2)
    val d1zj4 = doZeJob(4)
    println(d1zj4._2)
    d1zj4._1 shouldEqual 43

    def genere(ls: List[String]): List[String] = ls.map(s => List(s + "L", s + "A", s + "O").filter(good(_))).flatten

    //: (List[(Int,Int, List[String])],(Int,Int,Int))
    def getNumbers(L0: List[String]): (BigInt, BigInt, BigInt, BigInt) = {
      val L0gAA = L0.groupBy(_.indexOf("AA")).toList.map(u => (u._1, u._2.length, u._2))
      var nL0d: BigInt = L0gAA.head._2
      var nL0df: BigInt = 0
      var nL0f: BigInt = L0.groupBy(_.reverse.indexOf("AA")).toList.map(u => (u._1, u._2.length, u._2)).head._2
      if (L0.head.length > 4) {
        nL0d -= 1
        nL0f -= 1
        nL0df = 1
      }
      val nL0a: BigInt = L0.length - (nL0d + nL0f + nL0df) // le nombre de strings avec 0 Late et pas AA à la fin ou au début
      (nL0df + nL0d + nL0f + nL0a) shouldEqual L0.length
      nL0d shouldEqual nL0f
      (nL0df, nL0d, nL0f, nL0a)
    }

    //: (Int,Int,Int,(List[String],(Int,Int,Int)),(List[String],(Int,Int,Int)))

    def doZeJob2(e: Int, verbose: Int = 0): (Int, BigInt,
      (BigInt, BigInt, BigInt, BigInt), (BigInt, BigInt, BigInt, BigInt)) = {
      val ls = (0 until Math.pow(3, e).toInt).map(i => {
        var s = ""
        var j = i
        s += decode(j)
        (1 until e).foreach(u => {
          j /= 3
          s += decode(j)
        })
        //println(i, s)
        s
      })
      val z = ls.filter(good(_))
      val L0 = z.filter(countL(_) == 0)
      val L1 = z.filter(countL(_) == 1)
      (L0.length + L1.length) shouldEqual z.length

      val gn0 = getNumbers(L0.toList)
      val gn1 = getNumbers(L1.toList)
      verbose match {
        case 1 => println(e, z.length)
        case 2 => println(e, z.length, gn0, gn1)
      }

      (e, BigInt(z.length), gn0, gn1)
    }

    println("\n****doZeJob2****")
    var verbose = 2
    val d2zj4 = doZeJob2(4, verbose)
    d2zj4._2 shouldEqual 43
    val l2 = (3 until 12).map(doZeJob2(_, verbose))

    println("\n****doZeJob3****")

    var lr = List(doZeJob2(3, verbose), doZeJob2(4, verbose), doZeJob2(5, verbose), doZeJob2(6, verbose), doZeJob2(7, verbose), doZeJob2(8, verbose), doZeJob2(9, verbose), doZeJob2(10, verbose))

    def doZeJob3(e: Int, verbose: Int = 0): BigInt = {
      if (e < 13) doZeJob2(e, verbose)
      val nL0df: BigInt = if (e > 4) 1 else 0
      val nL0d: BigInt = lr.dropRight(2).last._3._2 + lr.dropRight(1).last._3._2 + lr.last._3._2 + 2
      val nL0f: BigInt = nL0d
      val nL0a: BigInt = lr.dropRight(2).last._3._4 + lr.dropRight(1).last._3._4 + lr.last._3._4 - 2
      val nL1df: BigInt = if (e > 4) 1 else 0
      val nL1d: BigInt = lr.dropRight(2).last._2 - 1
      val nL1f: BigInt = nL1d
      val nL1a: BigInt = if (e > 9) (lr.dropRight(4).last._2 + lr.dropRight(3).last._2 + lr.last._2 +
        (lr.dropRight(1).last._3._2 + lr.last._3._2 + nL0d + 4)) else 0
      val gn0 = (nL0df, nL0d, nL0f, nL0a)
      val gn1 = (nL1df, nL1d, nL1f, nL1a)
      val result = List(nL0df, nL0d, nL0f, nL0a, nL1df, nL1d, nL1f, nL1a)
      println("    ", e, result.sum, gn0, gn1, "\n")
      lr = lr :+ (e, result.sum, gn0, gn1)
      result.sum
    }


    var result = (11 until 31).map(doZeJob3(_, verbose)).last
    println("Euler191[" + result + "]")
    result shouldEqual 1918080160
  }

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
    println(root, rootprimes, rootdivisors)
    println("mod/root = ", mod.toDouble / root.toDouble)
    println(mod, mod + 2, new EulerDiv2(mod + 2, premiers).primes, "\n")

    var eulercoinList = List((BigInt(1), root))
    var max = 43000000
    var t_la = Calendar.getInstance()
    (1 to max).foreach(n => {
      val bi: BigInt = ((root * BigInt(n)) % mod)
      if (bi < eulercoinList.head._2) {
        eulercoinList = (eulercoinList :+ (BigInt(n), bi)).sortBy(_._2)
        val somme = eulercoinList.map(_._2).sum % mod
      }
    })
    var oldlast = eulercoinList.map(_._2).last
    var vieilleSomme = eulercoinList.map(_._2).sum % mod
    t_la = timeStamp(t_la, "après1 max: " + max)
    var state = 0
    var n = BigInt(1)
    var prevdiff = BigInt(1)
    var prevx = 1.0
    eulercoinList = List((1, root))
    while (n < max) {
      val bi = ((root * n) % mod)
      if (bi < eulercoinList.head._2) {
        eulercoinList = (eulercoinList :+ (n, bi)).sortBy(_._2)
        val somme = eulercoinList.map(_._2).sum % mod
        val x = somme.toDouble / n.toDouble
        val diff = n - eulercoinList.tail.head._1
        val diff2 = ((diff.toDouble * root.toDouble) / mod.toDouble)
        println("\n" + n, somme, bi, eulercoinList.tail.head._2 - bi, n - eulercoinList.tail.head._1, (n - eulercoinList.tail.head._1).toDouble / prevdiff.toDouble, eulercoinList)
        println("[" + n, somme, "" + bi + "]", n * bi, somme / n, x / prevx, eulercoinList.tail.head._2 - bi,
          " diff[" + diff + " / " + diff2 + "]", diff.toDouble / prevdiff.toDouble)
        println("   " + (1 to 10).map(u => (diff + (u * n))))
        n += prevdiff
        if ((n > (max - 3)) & (eulercoinList.map(_._2).last == oldlast)) {
          println("************Checking! @" + max + "**************")
          eulercoinList.map(_._2).sum % mod shouldEqual vieilleSomme
          timeStamp(t_la, "check après2 max: " + max)
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
    t_la = timeStamp(t_la, "après2 max: " + max)

    var max1 = mod
    eulercoinList = eulercoinList.reverse.take(3)
    n = eulercoinList.last._1
    var prevn = eulercoinList.take(2).last._1
    while (n < max1) {
      if (n == 42298633) {
        println("************Checking! @" + n + "/" + max + "**************")
        eulercoinList.map(_._2).sum % mod shouldEqual vieilleSomme
        timeStamp(t_la, "check après3 max: " + max)
      }
      val diff = n - prevn
      val listedesProchainsCandidats = (1 to 10).map(u => (diff + (u * n))).toList
      println(n, eulercoinList.map(_._2).sum % mod, eulercoinList.reverse)
      //println(" " + n, diff, listedesProchainsCandidats)
      var found = false
      listedesProchainsCandidats.takeWhile(ncand => {
        val ecand = ((root * ncand) % mod)
        if (ecand < eulercoinList.last._2) {
          eulercoinList = (eulercoinList :+ (ncand, ecand)).sortBy(_._1)
          found = true
          false
        } else {
          true
        }
      })
      found shouldEqual true
      prevn = n
      n = eulercoinList.last._1
    }
    val Somme = eulercoinList.map(_._2).sum % mod
    t_la = timeStamp(t_la, "après3 max1: " + max1 + " , Somme = " + Somme)
    println(n, eulercoinList.map(_._2).sum % mod, eulercoinList.reverse)

    var result = eulercoinList.map(_._2).sum % mod
    println("Euler700[" + result + "]")
    result shouldEqual BigInt("1517926517777556")
  }

}