import org.scalatest._

import scala.math.BigInt


/*
https://www.coindesk.com/math-behind-bitcoin/
https://crypto.stackexchange.com/questions/44304/understanding-elliptic-curve-point-addition-over-a-finite-field
https://fr.wikipedia.org/wiki/Courbe_elliptique
 */

class ElliptiqueTest3 extends FlatSpec with Matchers {
  def rangeStream(a: BigInt, b: BigInt): Stream[BigInt] = a #:: rangeStream(b, 1 + b)

  def stream_zero_a_linfini: Stream[BigInt] = rangeStream(0, 1)
  val l1 = stream_zero_a_linfini.take(79).toList
  val li79 = stream_zero_a_linfini.take(79).toList.tail.map(i => {
    (i, l1.filter(u => ((u * i) % 79) == 1).head)
  })
  def inverse79(a:BigInt) = Elliptique.inverse(79,a)

  "Teste la multiplication part I" should "be OK" in {
    println("Teste la multiplication part I")
    val e = new Elliptique(67, 0, 7)
    (0 to 500).foreach(q => {
      e.mul((BigInt(0), BigInt(0)), q)._2 shouldEqual q
    })
  }

  "Teste la multiplication part II" should "be OK" in {
    println("Teste la multiplication part II")
    val a = 0
    val b = 7
    val modlo = 73
    println("y2 = x3 + " + a + "x + " + b + " modulo " + modlo + ": teste la multiplication part II")
    val e = new Elliptique(modlo, a, b)
    val p = (BigInt(6), BigInt(2))
    println(p,
      e.plus(p, p),
      e.plus(p, (BigInt(60), BigInt(0))),
      e.plus(p, (BigInt(6), BigInt(71)))
    )
    e.mul(p, 0)._3 shouldEqual(BigInt(0), BigInt(0))
    e.mul(p, 1)._3 shouldEqual p
    e.mul(p, 2)._3 shouldEqual e.plus(p, p)
    e.mul(p, 3)._3 shouldEqual(BigInt(6), BigInt(71))
    e.mul(p, 4)._3 shouldEqual(BigInt(0), BigInt(0))
    e.mul(p, 5)._3 shouldEqual p
  }

  "Teste la multiplication part III" should "be OK" in {
    println("Teste la multiplication part III")
    val a = 0
    val b = 7
    val modlo = 67
    println("y2 = x3 + " + a + "x + " + b + " modulo " + modlo + ": teste la multiplication part III")
    val e = new Elliptique(modlo, a, b)
    val p = e.curve.head
    e.mul(p, e.curve.size + 2)._3 shouldEqual p
  }

  "Fais de l encryption" should "be OK" in {
    println("Fais de l encryption https://www.coindesk.com/math-behind-bitcoin/")
    val a = 0
    val b = 7
    val modlo = 67
    println("y2 = x3 + " + a + "x + " + b + " modulo " + modlo + ": Fais de l encryption https://www.coindesk.com/math-behind-bitcoin/")
    val e = new Elliptique(modlo, a, b)
    val basepoint = (BigInt(2), BigInt(22))
    e.check(basepoint)
    val privateKey = 2
    val publicKey = e.mul(basepoint, privateKey)._3
    publicKey shouldEqual(BigInt(52), BigInt(7))
    val data = 17
    println("step 0: basepoint ["+basepoint+"], privateKey ["+privateKey+"], data ["+data+"]")
    val randomNumber_k = 3
    println("step 1: pick random number "+randomNumber_k)
    val thePoint = e.mul(basepoint, 3)._3
    println("step 2: compute the Point "+thePoint)
    val r = thePoint._1
    r should not equal BigInt(0)
    println("step 3: find r "+r)
    inverse79(randomNumber_k) shouldEqual 53
    val s = ((data + (r * privateKey)) * inverse79(randomNumber_k)) % 79
    println("step 4: find s "+s)
    val signature = (r, s)
    println("step 5: siganture "+signature)
    signature shouldEqual(62, 47)
  }


}
