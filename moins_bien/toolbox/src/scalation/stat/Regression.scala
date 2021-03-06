
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * @author  John Miller
 * @version 1.0
 * @date    Wed Aug 26 18:41:26 EDT 2009
 * @see     LICENSE (MIT style license file).
 */

package scalation
package stat

import scala.math._
import advmath._
import advmath.Matrices._
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * The Regression object supports multiple linear regression.
 */
object Regression
{
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Evaluate the formula y = x * b.
     * @param x the design matrix augmented with a first column of ones.
     * @param b the parameter vector
     */
    def eval (x: MatrixD, b: Vec[Double]): Vec[Double] =
    {
        x * b
    } // eval

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Fit the parameter vector (b-vector) in the regression equation y = x * b + e
     * using the least squares method.
     * @param x the design matrix augmented with a first column of ones.
     * @param y the response vector
     */
    def fit (x: MatrixD, y: Vec[Double]): Tuple2 [Vec[Double], Double] =
    {
        val b    = (x.t * x).inverse * x.t * y   // parameter vector
        val e    = y - x * b                     // error vector
        val sse  = e.norm2                       // sum of squared errors
        val ssto = y.norm2 - (pow (y.sum, 2)) / y.length
        val r2   = (ssto - sse) / ssto           // coefficient of determination
        Tuple2 (b, r2)
    } // fit

} // Regression object


/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * Object to test Regression object (y = x * b = b0 + b1*x1 + b2*x2).
 */
object RegressionTest extends App
{
     // five data points: constant term, x1 coordinate, x2 coordinate
     val x = new MatrixD (5, 1, 36,  66,                    // 5-by-3 matrix
                             1, 37,  68,
                             1, 47,  64,
                             1, 32,  53,
                             1,  1, 101)
     // five data points: y coordinate
     val y = Vec (745.toDouble, 895.toDouble, 442.toDouble, 440.toDouble, 1598.toDouble)

     val tp = Regression.fit (x, y)
     val yp = Regression.eval (x, tp._1)
     println ("b  = " + tp._1)
     println ("r2 = " + tp._2)
     println ("y  = " + y)
     println ("yp = " + yp)

} // RegressionTest object

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * Object to test Regression object (y = x * b = b0 + b1*x1 + b2*x2).
 */
object RegressionTest2 extends App
{
     // four data points: constant term, x1 coordinate, x2 coordinate
     val x = new MatrixD (4, 1, 1, 1,                       // 4-by-3 matrix
                             1, 1, 2,
                             1, 2, 1,
                             1, 2, 2)
     // four data points: y coordinate
     val y = Vec(6.toDouble, 8.toDouble, 7.toDouble, 9.toDouble)

     val tp = Regression.fit (x, y)
     val yp = Regression.eval (x, tp._1)
     println ("b  = " + tp._1)
     println ("r2 = " + tp._2)
     println ("y  = " + y)
     println ("yp = " + yp)

} // RegressionTest2 object

