
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * @author  John Miller
 * @version 1.0
 * @date    Wed Sep 30 18:41:26 EDT 2009
 * @see     LICENSE (MIT style license file).
 *
 * Many of the algorithms used are from:
 *   Averill M. Law and W. David Kelton
 *   Simulation Modeling and Analysis, 2nd Edition
 *   McGraw-Hill, Inc, NY, 1991.
 */

package scalation.stat

import scala.math._
import scalation.advmath._
import scalation.util.Error

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This abstract class serves as a base class for all the random variate (RV)
 * generators. They use one of the Random Number Generators (RNG's) from
 * Random.scala to generate numbers following their particular distribution.
 */
abstract class Variate (stream: Int = 0) extends Error with Combinatorics
{
    /** Random number stream selected by the stream number
     */
    protected val r = RNG.rand (stream)

    /** Indicates whether the distribution is discrete or continuous (default)
     */
    protected var _discrete = false

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Determine whether the distribution is discrete or continuous.
     */
    def discrete: Boolean = _discrete

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Compute the mean for the particular distribution.
     */
    def mean: Double

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Compute the probability function (pf):
     * The probability density function (pdf) for continuous RV's or
     * the probability mass function (pmf) for discrete RV's.
     */
    def pf (z: Double): Double

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Determine the next random number for the particular distribution.
     */
    def gen: Double

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Determine the next random integer for the particular distribution.
     * It is only valid for discrete random variates.
     */
    def igen: Int =
    {
        if (_discrete) {
           round (gen).asInstanceOf [Int]
        } else {
           flaw ("igen", "should not be invoked on continuous RV's"); 0
        } // if
    } // igen

} // Variate class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Bernoulli random variates.
 * This discrete RV models the one trial (success is 1, failure is 0).
 * @param p       the probability of success
 * @param stream  the random number stream
 */
case class Bernoulli (p: Double = 0.5, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (p < 0 || p > 1) flaw ("constructor", "parameter p must be in [0, 1]")
        _discrete = true
    } // primary constructor

    def mean: Double = p

    def pf (z: Double): Double = 
    {
        if (approx (z, 0)) 1 - p else if (approx (z, 1)) p
        else               0
    } // pf

    def gen: Double = 
    {
        if (r.gen < p) 1
        else           0
    } // gen

} // Bernoulli class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Beta random variates.
 * This continuous RV models random proportions.
 * Beta =  Gamma1 / (Gamma1 + Gamma2).
 * @param alpha   the shape parameter for Gamma1
 * @param beta    the shape parameter for Gamma2
 * @param stream  the random number stream
 */
case class Beta (alpha: Double = 2, beta: Double = 3, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (alpha <= 0 || beta <= 0) flaw ("constructor", "parameters alpha and beta must be positive")
    } // primary constructor

    private val gamma1 = Gamma (alpha, 1.0, stream)
    private val gamma2 = Gamma (beta, 1.0, stream)

    def mean: Double = alpha / (alpha + beta)

    def pf (z: Double): Double =
    {
        if (0 < z && z < 1) pow (z, alpha - 1) * pow (1 - z, beta - 1) / betaF (alpha, beta)
        else                0
    } // pf

    def gen: Double = 
    {
        var g1 = gamma1.gen
        var g2 = gamma2.gen
        g1 / (g1 + g2)
    } //gen

} // Beta class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Binomial random variates.
 * This discrete RV models the number of successes in n trials.
 * @param p       the probability of success
 * @param n       the number of independent trials
 * @param stream  the random number stream
 */
case class Binomial (p: Double = 0.5, n: Int = 5, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (p < 0 || p > 1) flaw ("constructor", "parameter p must be in [0, 1]")
        if (n <= 0)         flaw ("constructor", "parameter n must be positive")
        _discrete = true
    } // primary constructor

    private val q    = 1 - p
    private val coin = Bernoulli (p, stream)

    def mean: Double = p * n

    def pf (z: Double): Double =
    {
        val k: Int = (floor (z)).asInstanceOf [Int]
        if (z == k && 0 <= k && k <= n) choose (n, k) * pow (p, k) * pow (q, n-k)
        else                            0
    } // pf

    def gen: Double =
    {
        var sum = 0
        for (i <- 0 until n) sum += (coin.gen).asInstanceOf [Int]
        sum
    } // gen

} // Binomial class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Cauchy (or Lorentz) random variates.
 * This continuous RV models data with heavier tails than normally distributed.
 * @param alpha   the location parameter (median)
 * @param beta    the scale parameter 
 * @param stream  the random number stream
 */
case class Cauchy (alpha: Double = 2.5, beta: Double = 1, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (beta <= 0) flaw ("constructor", "parameter beta must be positive")
    } // primary constructor

    def mean: Double = alpha    // but, technically does not exist

    def pf (z: Double): Double = 
    {
        beta / ((pow (z - alpha, 2) + pow (beta, 2)) * Pi)
    } // pf

    def gen: Double = beta * tan (Pi * (r.gen - 0.5)) + alpha

} // Cauchy class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates ChiSquare random variates.
 * This continuous RV models the variance of a distribution.
 * @param df      the degrees of freedom 
 * @param stream  the random number stream
 */
case class ChiSquare (df: Int = 2, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (df <= 0) flaw ("constructor", "parameter df must be positive")
    } // primary constructor

    private val gamma  = Gamma (df / 2, 2.0, stream)
    private val normal = Normal (0.0, 1.0, stream)
    private val k      = df / 2.0

    def mean: Double = df

    def pf (z: Double): Double =
    {
        pow (.5, k) * pow (z, k - 1) * exp (-z / 2.0) / gammaF (k)
    } // pf

    def gen: Double =
    {
        gamma.gen + (if (df % 2 == 0) 0.0 else pow (normal.gen, 2))
    } // gen

} // ChiSquare class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Deterministic random variates.
 * This discrete RV models the case when the variance is 0.
 * @param x       the value for this constant distribution
 * @param stream  the random number stream
 */
case class Deterministic (x: Double = 1, stream: Int = 0)
     extends Variate (stream)
{
    {
        _discrete = true
    } // primary constructor

    def mean: Double = x

    def pf (z: Double): Double =
    {
        if (approx (z, x)) 1
        else               0 
    } // pf

    def gen: Double = x

} // Deterministic class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates generalized Discrete random variates for a given
 * distribution specified using either a probability mass function (pmf)
 * or a cummulative distribution function (cdf).
 * This discrete RV models arbitrary experiments with discrete outcomes.
 * @param dist         the distribution function (pdf or cdf)
 * @param x            the x-coordinate values (mass points)
 * @param cummulative  whether dist is cummulative (cdf) or not (pmf)
 * @param stream       the random number stream
 */
case class Discrete (dist: Vec[Double] = Vec (.2, .2, .2, .2, .2), x: Vec[Double] = null,
                     cummulative: Boolean = false, stream: Int = 0)
     extends Variate (stream)
{
    private val cdf = if (cummulative) dist else dist.cummulate
    private val n   = dist.length
    private val xx  = if (x == null || x.length == 0) dist.increasing() else x

    {
        if (xx.length != dist.length) flaw ("Discrete", "dist and xx must have the same length")
        _discrete = true
    } // primary constructor

    def mean: Double =
    {
       var sum = xx(0) * cdf(0)
       for (i <- 1 until n) sum += xx(i) * (cdf(i) - cdf(i-1))
       sum
    } // mean

    def pf (z: Double): Double =
    {
        var j = -1
        for (i <- 0 until n if xx(i) == z) j = i
        if (j >= 0) if (j == 0) cdf(0) else cdf(j) - cdf(j-1) else 0 
    } // pf

    def gen: Double =
    {
        val ran = r.gen
        for (i <- 0 until n if ran <= cdf(i)) return xx(i)
        xx(cdf.length - 1)
    } // gen

} // Discrete class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Erlang random variates.
 * This continuous RV models the time until k stages complete.
 * @param mu      the mean of exponential samples (Erlang mean = mu * k)
 * @param k       the number of stages (or Exponential samples)
 * @param stream  the random number stream
 */
case class Erlang (mu: Double = 1, k: Int = 2, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (mu <= 0 || k <= 0) flaw ("constructor", "parameters mu and k must be positive")
    } // primary constructor

    private val l = 1 / mu   // lambda

    def mean: Double = mu * k

    def pf (z: Double): Double =
    {
        pow (l, k) * pow (z, k - 1) * exp (-l * z) / fac (k - 1)
    } // pf

    def gen: Double =
    {
        var prod = 1.0
        for (i <- 0 until k) prod *= r.gen
        -mu * log (prod)
    } // gen

} // Erlang class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Exponential random variates.
 * This continuous RV models the time until an event occurs.
 * @param mu      the mean
 * @param stream  the random number stream
 */
case class Exponential (mu: Double = 1, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (mu <= 0) flaw ("constructor", "parameter mu must be positive")
    } // primary constructor

    private val l = 1 / mu   // lambda

    def mean: Double = mu

    def pf (z: Double): Double = 
    {
        if (z >= 0) l * exp (-l * z)
        else        0
    } // pf

    def gen: Double = -mu * log (r.gen)

} // Exponential class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Fisher (F-Distribution) random variates.
 * This continuous RV models the ratio of variances.
 * @param df1     the degrees of freedom for numerator Chi-Square
 * @param df2     the degrees of freedom for denominator Chi-Square
 * @param stream  the random number stream
 */
case class Fisher (df1: Int = 6, df2: Int = 4, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (df1 <= 0 || df2 <= 2)
            flaw ("constructor", "parameters df1 and df2 must be at least 1 and 3, respectively")
    } // primary constructor

    private val chi1 = ChiSquare (df1, stream)
    private val chi2 = ChiSquare (df2, stream)

    def mean: Double = df2 / (df2 - 2)

    def pf (z: Double): Double =
    {
        if (z >= 0) sqrt (pow (df1 * z, df1) * pow (df2, df2) / pow (df1 * z + df2, df1 + df2)) /
                         (z * betaF (df1 / 2.0, df2 / 2.0))
        else        0
    } // pf

    def gen: Double =
    {
        val c1 = chi1.gen
        val c2 = chi2.gen
        (df2 * c1) / (df1 * c2)
    } // gen

} // Fisher class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Gamma random variates.
 * This continuous RV models the time until an event occurs.
 * Note: variance = alpha * beta ^ 2.
 * @param alpha   the shape parameter
 * @param beta    the scale parameter
 * @param stream  the random number stream
 */
case class Gamma (alpha: Double = 1, beta: Double = 1, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (alpha <= 0 || beta <= 0) flaw ("constructor", "parameters alpha and beta must be positive")
    } // primary constructor

    private val a    = (floor (alpha)).asInstanceOf [Int]  // integral part
    private val b    = alpha - a                           // fractional part  
    private val erl1 = Erlang (beta, a, stream)
    private val erl2 = Erlang (beta, a + 1, stream)

    def mean: Double = alpha * beta
    
    def pf (z: Double): Double =
    {
        if (z > 0) pow (beta, -alpha) * pow (z,  alpha - 1) * exp (-z / beta) / gammaF (alpha)
        else       0
    } // pf

    def gen: Double =
    {
        var x = 0.0
        var y = 0.0
        if (alpha < 1.0) {                                // 0 < alpha < 1
            do {
                x = pow (r.gen, 1.0 / alpha)
                y = pow (r.gen, 1.0 / (1.0 - alpha))
            } while (x + y > 1)
            return (x / (x + y)) * (-log (r.gen)) * beta
        } else if (alpha < 5.0) {                         // 1 <= alpha < 5
            do {
                x = alpha / a;
                var prod = 1.0
                for (i <- 0 until a)  prod *= r.gen
                x *= -log (prod)
            } while (r.gen > pow (x / alpha, b) * exp (-b * x / (alpha - 1.0)))
            return x * beta
        } else {                                          // alpha >= 5
            if (r.gen >= b) erl1.gen else erl2.gen
        } // if
    } // gen

} // Gamma class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Geometric random variates.
 * This discrete RV models the number of failures before the first success.
 * @param p       the probability of success
 * @param stream  the random number stream
 */
case class Geometric (p: Double = 0.5, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (p < 0 || p > 1) flaw ("constructor", "parameter p must be in [0, 1]")
        _discrete = true
    } // primary constructor

    private val q      = 1.0 - p
    private val log_q  = log (q)
    private val q_by_p = q / p

    def mean: Double = q_by_p

    def pf (z: Double): Double = 
    {
        val k = floor (z).asInstanceOf [Int]
        if (z == k && k >= 0) p * pow (q, k) 
        else                  0
    } // pf

    def gen: Double = (floor (log (r.gen) / log_q)).asInstanceOf [Int]

} // Geometric class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates HyperExponential random variates (two rates).
 * This continuous RV models the time until an event occurs (higher coefficient
 * of variation than exponetial distribution).  
 * @param p       the probability of first vs. second rates
 * @param mu1     the first mean (1 / lambda1)
 * @param mu2     the second mean (1 / lambda2)
 * @param stream  the random number stream
 */
case class HyperExponential (p: Double = 0.5, mu1: Double = 1, mu2: Double = 2, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (p < 0 || p > 1)       flaw ("constructor", "parameter p must be in [0, 1]")
        if (mu1 <= 0 || mu2 <= 0) flaw ("constructor", "parameters mu1 and mu2 must be positive")
    } // primary constructor

    private val q  = 1 - p
    private val l1 = 1 / mu1   // lambda 1
    private val l2 = 1 / mu2   // lambda 2

    def mean: Double = p * mu1 + q * mu2

    def pf (z: Double): Double =
    {
        if (z >= 0) p * l1 * exp (-l1 * z) + q * l2 * exp (-l2 * z)
        else        0
    } // pf

    def gen: Double =
    {
        log (r.gen) * (if (r.gen < p) -mu1 else -mu2)
    } // gen

} // HyperExponential class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates HyperExponential random variates.
 * This continuous RV models the time until an event occurs (higher coefficient
 * of variation than exponetial distribution). FIX
 * @param mu      the mean
 * @param sigma   the standard deviation
 * @param stream  the random number stream
 */
case class _HyperExponential (mu: Double = 1, sigma: Double = 2, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (mu <= 0 || sigma <= 0) flaw ("constructor", "parameters mu and sigma must be positive")
    } // primary constructor

    private val cv2 = pow (sigma / mu, 2)
    private val p   = 0.5 * (1.0 - sqrt ((cv2 - 1.0) / (cv2 + 1.0)))

    private val l = 2 * p / mu   // adjusted lambda

    def mean: Double = mu

    def pf (z: Double): Double =
    {
        if (z >= 0) l * exp (-l * z)
        else        0
    } // pf

    def gen: Double =
    {
        val z = if (r.gen > p) mu / (1 - p) else mu / p
        -0.5 * z * log (r.gen)
    } // gen

} // _HyperExponential class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates HyperGeometric random variates.
 * This discrete RV models the number of successes in n draws from a finite population.
 * @param p       the probability of success (red balls)
 * @param n       the number of draws (balls drawn)
 * @param pop     the size of the finite population (total number of balls)
 * @param stream  the random number stream
 */
case class HyperGeometric (p: Double = 0.5, n: Int = 5, pop: Int = 10, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (p < 0 || p > 1)     flaw ("constructor", "parameter p must be in [0, 1]")
        if (n <= 0 || pop <= 0) flaw ("constructor", "parameters n and pop must be positive")
        _discrete = true
    } // primary constructor

    private val reds: Int = (floor (p * pop)).asInstanceOf [Int]

    def mean: Double = n * reds / pop

    def pf (z: Double): Double =
    {
        val k: Int = (floor (z)).asInstanceOf [Int]
        if (k == z && 0 <= k && k <= reds && k <= n) {
            choose (reds, k) * choose (pop - reds, n - k) / choose (pop, n).asInstanceOf [Double]
        } else {
            0
        } // if
    } // pf

    def gen: Double =
    {
        var b: Double = pop    // population of number of balls
        var rd = reds           // number of red/success balls in population
        var s = 0              // count number of successes
        for (i <- 0 until n) {
            if (r.gen <= rd / b) { s += 1; rd -=1 }
            b -= 1
        } // for
        s
    } // gen

} // HyperGeometric class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates LogNormal random variates.
 * This continuous RV models data that is normally distributed after a log transformation.
 * @param mu      the mean for underlying Normal
 * @param sigma2  the variance (sigma squared) for underlying Normal
 * @param stream  the random number stream
 */
case class LogNormal (mu: Double = 0, sigma2: Double = 1, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (sigma2 <= 0) flaw ("constructor", "parameter sigma2 must be positive")
    } // primary constructor

    private val _2sigma2 = 2 * sigma2
    private val normal   = Normal (mu, sigma2, stream)   // associated Normal distribution

    def mean: Double = exp (mu + sigma2 / 2.0)

    def pf (z: Double): Double =
    {
        val denom = z * sqrt (Pi * _2sigma2)
        if (z > 0) exp (-pow (log (z) - mu, 2) / _2sigma2) / denom
        else       0
    } // pf

    def gen: Double = exp (normal.gen)

} // LogNormal class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Multinomial random variates.
 * This discrete RV models the ...?
 * @param p       array of probabilities
 * @param n       the number of independent trials
 * @param stream  the random number stream
 */
case class Multinomial (p: Array[Double] = Array (.4, .3, .3), n: Int = 5, stream: Int = 0)
     extends Variate (stream)
{
    {
        for (pi <- p if pi < 0 || pi > 1) flaw ("constructor", "parameter pi must be in [0, 1]*")
        if (n <= 0) flaw ("constructor", "parameter n must be positive")
        _discrete = true
    } // primary constructor

    def mean: Double = 0   // FIX

    def pf (z: Double): Double =
    {
        0   // FIX
    } // pf

    def gen: Double =
    {
        0   // FIX
    } // gen

} // Multinomial

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates NegativeBinomial random variates.
 * This discrete RV models the number of failures before s-th success.
 * @param p       the probability of success
 * @param s       the number of successes
 * @param stream  the random number stream
 */
case class NegativeBinomial (p: Double = 0.5, s: Int = 2, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (p < 0 || p > 1) flaw ("constructor", "parameter p must be in [0, 1]")
        if (s <= 0)         flaw ("constructor", "parameter s must be positive")
        _discrete = true
    } // primary constructor

    private val q    = 1 - p
    private val geom = Geometric (p, stream)

    def mean: Double = s * q / p

    def pf (z: Double): Double =
    {
        val k: Int = (floor (z)).asInstanceOf [Int]
        if (k == z && k >= 0) choose (s + k - 1, k) * pow (p, s) * pow (q, k)
        else                  0
    } // pf

    def gen: Double = 
    {
        var sum = 0
        for (i <- 0 until s) sum += (geom.gen).asInstanceOf [Int]
        sum
    } // gen

} // NegativeBinomial class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Normal (Gaussian) random variates.
 * This continuous RV models normally distributed data.
 * @param mu      the mean
 * @param sigma2  the variance (sigma squared)
 * @param stream  the random number stream
 */
case class Normal (mu: Double = 0, sigma2: Double = 1, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (sigma2 < 0) flaw ("constructor", "parameter sigma2 must be nonnegative")
    } // primary constructor

    private val sigma    = sqrt (sigma2)
    private val _2sigma2: Double = 2 * sigma2
    private var computed = true       // toggle, since values gen in pairs
    private var save: Double     = 0         // save second in pair

    def mean: Double = mu

    def pf (z: Double): Double =
    {
        (1 / sqrt (Pi * _2sigma2)) * exp (-pow (z - mu, 2) / _2sigma2)
    } // pf

    def gen: Double =             // use acceptance-rejection method
    {
        var a: Double = 0; var b: Double = 0; var w: Double = 0
        var temp: Double = 0

        computed = ! computed;
        if (computed) return save * sigma2 + mu
        do {
            a = 2 * r.gen - 1
            b = 2 * r.gen - 1
            w = a * a + b * b
        } while (w > 1)
        temp = sqrt (-2 * log (w) / w)
        save = b * temp
        (a * temp) * sigma + mu
    } // gen

    def gen2: Double =            // use inverse transform method
    {
        Quantile.normalInv (r.gen)
    } // gen2
    
    override def toString: String = this.getClass.getName+"(%1.3f,%1.3f)".format(mu,sigma2)

} // Normal class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Poisson random variates (discrete).
 * This discrete RV models the number of events in a time interval of unit length.
 * @param mu      the mean
 * @param stream  the random number stream
 */
case class Poisson (mu: Double = 2, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (mu <= 0) flaw ("constructor", "parameter mu must be positive")
        _discrete = true
    } // primary constructor

    private val cutoff = exp (-mu)

    def mean: Double = mu

    def pf (z: Double): Double =
    {
        val k = (floor (z)).asInstanceOf [Int]
        if (k == z && k >= 0) exp (-mu) * pow (mu, k) / fac (k)
        else                  0
    } // pf

    def gen: Double = 
    {
        var n = -1
        var prod = 1.0
        do { prod *= r.gen; n += 1 } while (prod >= cutoff)
        n
    } // gen
    
    override def toString: String = this.getClass.getName+"(%1.3f)".format(mu)

} // Poisson class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Randi random variates (random integers).
 * This discrete RV models equiprobable integral outcomes.
 * @param a       the lower bound
 * @param b       the upper bound
 * @param stream  the random number stream
 */
case class Randi (a: Int = 0, b: Int = 5, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (a > b) flaw ("constructor", "parameter a must not be greater than b")
        _discrete = true
    } // primary constructor

    private val width = b + 1 - a

    def mean: Double = (a + b) / 2.0

    def pf (z: Double): Double =
    {
        val k = (floor (z)).asInstanceOf [Int]
        if (k == z && a <= k && k <= b) 1 / width.asInstanceOf [Double]
        else                            0
    } // pf

    def gen: Double = (floor (a + width * r.gen)).asInstanceOf [Int]

} // Randi class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates StudentT random variates.
 * This continuous RV models cases where data are normally distributed but
 * variability increases since the variance is unknown.
 * @param df      the degrees of freedom
 * @param stream  the random number stream
 */
case class StudentT (df: Int = 4, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (df <= 0) flaw ("constructor", "parameter df must be positive")
    } // primary constructor

    private val _df    = (df + 1) / 2.0
    private val normal = Normal (0.0, 1.0, stream)
    private val chi    = ChiSquare (df, stream)

    def mean: Double = 0.0

    def pf (z: Double): Double =
    {
        gammaF (_df) * pow (1 + (z * z) / df, -_df) / (sqrt (df * Pi) * gammaF (df / 2.0))
    } // pf

    def gen: Double = normal.gen / sqrt (chi.gen / df) 

} // StudentT case

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates simple Triangular random variates with the mode in the middle.
 * This continuous RV models cases where outcomes cluster around the mode.
 * @param a       the lower bound
 * @param b       the upper bound
 * @param c       the mode
 * @param stream  the random number stream
 */
case class Triangular (a: Double = 0, b: Double = 5, c: Double = Double.MaxValue, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (a > b) flaw ("constructor", "parameter a must not be greater than b")
    } // primary constructor

    private val width = b - a
    private val mode  = if (c < Double.MaxValue) c else (a + b) / 2.0
    private val left  = mode - a
    private val right = b - mode

    def mean: Double = (a + mode + b) / 3.0

    def pf (z: Double): Double =
    {
        if (a <= z && z <= mode) 2 * (z - a) / (width * left)
        else if (z <= b)         2 * (b - z) / (width * right)
        else                     0
    } // pf

    def gen: Double = 
    {
        val ran = r.gen    
        if (ran <= left / width) a + sqrt (left * width * ran)
        else b - sqrt (right * width * (1 - ran))
    } // gen
    
    override def toString: String = if (c < Double.MaxValue) this.getClass.getName+"(%1.3f,%1.3f,%1.3f)".format(a,b,c) else this.getClass.getName+"(%1.3f,%1.3f)".format(a,b)

} // Triangular class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Uniform random variates in the range (a, b).
 * This continuous RV models equiprobable outcomes.
 * @param a       the lower bound
 * @param b       the upper bound
 * @param stream  the random number stream
 */
case class Uniform (a: Double = 0, b: Double = 5, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (a > b) flaw ("constructor", "parameter a must not be greater than b")
    } // primary constructor

    private val width = b - a

    def mean: Double = (a + b) / 2.0

    def pf (z: Double): Double =
    {
        if (a <= z && z <= b) 1 / width
        else                  0
    } // pf

    def gen: Double = a + width * r.gen

} // Uniform class

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * This class generates Weibull random variates.
 * This continuous RV models the time for an event to occur.
 * @param alpha   the shape parameter
 * @param beta    the scale parameter
 * @param stream  the random number stream
 */
case class Weibull (alpha: Double = 2, beta: Double = 2, stream: Int = 0)
     extends Variate (stream)
{
    {
        if (alpha <= 0 || beta <= 0) flaw ("constructor", "parameters alpha and beta must be positive")
    } // primary constructor

    private val shape_recip = 1 / alpha

    def mean: Double = beta * shape_recip * gammaF (shape_recip)

    def pf (z: Double): Double =
    {
        if (z > 0) alpha * pow (beta, -alpha) * pow (z, alpha - 1) * exp (- pow (z / beta, alpha))
        else       0
    } // pf

    def gen: Double = 
    {
        beta * pow (-log (r.gen), shape_recip)
    } // gen
    
    override def toString: String = this.getClass.getName+"(%1.3f,%1.3f)".format(alpha,beta)

} // Weibull

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * Conduct two simple tests of the Random Variate Generators: (1) Means Test
 * (2) Goodness of Fit Test.
 */
object VariateTest extends App
{
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Perform a means test (average of generated rv's close to mean for distribution).
     * @param rv  the random variate to test
     */
    def meansTest (rv: Variate)
    {
        println ("\nTest the " + rv.getClass.getSimpleName () + " random variate generator")

        var ran: Double = 0
        var sum: Double = 0
        val rep: Int    = 100000
        print ("rv.gen  = {")
        for (i <- 1 to rep) {
            ran = rv.gen
            if (i <= 10) print (" " + ran)
            sum += ran
        } // for
        println (" ... }")
        println ("rv.mean = " + rv.mean + " estimate = " + sum / rep.asInstanceOf [Double])
    } // meansTest

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Perform a goodness of fit test using histogram of generated rv's matches pf
     * (either pmf or pdf).
     * @param rv  the random variate to test
     */
    def distrTest (rv: Variate)
    {
        println ("\nTest the " + rv.getClass.getSimpleName () + " random variate generator")

        val rep    = 50000          // replications
        var j      = 0              // interval number
        var x      = 0.0            // x coordinate
        var o      = 0.0            // observed value: height of histogram
        var e      = 0.0            // expected value: pf (x)
        var chi2   = 0.0            // ChiSquare statistic
        var n      = 0              // Number of nonzero intervals
        val offset = if (rv.isInstanceOf [StudentT] || rv.isInstanceOf [Normal]) 25 else 0
        val sum    = new Array [Int] (51)

        for (i <- 0 until sum.length) sum (i) = 0

        for (i <- 1 to rep) {
            j = (floor (rv.gen * 10.0)).asInstanceOf [Int] + offset
            if (0 <= j && j <= 50) sum (j) += 1
        } // for

        for (i <- 0 until sum.length) {
            x = (i - offset) / 10.0
            o = sum (i)
            e = round (if (rv.discrete) 50000 * rv.pf (x) else 5000 * rv.pf (x + .05) )
            if (e >= 5) {
                chi2 += pow (o - e, 2) / e
                n += 1
            } // if
            print ("\tsum (" + x + ") = " + o + " : " + e + " ")
            if (i % 5 == 4) println ()
        } // for
        n -= 1
        if (n < 2)  n = 2
        if (n > 49) n = 49 
        println ("\nchi2 = " + chi2 + " : chi2(0.95, " + n + ") = " + Quantile.chiSquareInv (0.95, n))
    } // distrTest

    meansTest (Bernoulli ())
    meansTest (Beta ())
    meansTest (Binomial ())
    meansTest (Cauchy ())
    meansTest (ChiSquare ())
    meansTest (Deterministic ())
    meansTest (Discrete ())
    meansTest (Erlang ())
    meansTest (Exponential ())
    meansTest (Fisher ())
    meansTest (Gamma ())
    meansTest (Geometric ())
    meansTest (HyperExponential ())
    meansTest (HyperGeometric ())
    meansTest (LogNormal ())
    meansTest (NegativeBinomial ())
    meansTest (Normal ())
    meansTest (Poisson ())
    meansTest (Randi ())
    meansTest (StudentT ())
    meansTest (Triangular ())
    meansTest (Uniform ())
    meansTest (Weibull ())

    distrTest (Bernoulli ())
    distrTest (Beta ())
    distrTest (Binomial ())
    distrTest (Cauchy ())
    distrTest (ChiSquare ())
    distrTest (Deterministic ())
    distrTest (Discrete ())
    distrTest (Erlang ())
    distrTest (Exponential ())
    distrTest (Fisher ())
    distrTest (Gamma ())
    distrTest (Geometric ())
    distrTest (HyperExponential ())
    distrTest (HyperGeometric ())
    distrTest (LogNormal ())
    distrTest (NegativeBinomial ())
    distrTest (Normal ())
    distrTest (Poisson ())
    distrTest (Randi ())
    distrTest (StudentT ())
    distrTest (Triangular ())
    distrTest (Uniform ())
    distrTest (Weibull ())

} // VariateTest object

