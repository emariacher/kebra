
/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * @author  John Miller
 * @version 1.0
 * @date    Wed Aug 26 18:41:26 EDT 2009
 * @see     LICENSE (MIT style license file).
 */

package scalation.advmath

import scala.math.abs
import scalation.util.Error
import scala.reflect.ClassTag

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * Convenience definitions for commonly used types of matrices.
 */
object Matrices
{
    type MatrixI = MatrixN [Int]
    type MatrixL = MatrixN [Long]
    type MatrixF = MatrixN [Float]
    type MatrixD = MatrixN [Double]
    type MatrixC = MatrixN [Complex]
    type ArrayII = Array [Array [Int]]
    type ArrayLL = Array [Array [Long]]
    type ArrayFF = Array [Array [Float]]
    type ArrayDD = Array [Array [Double]]
    type ArrayCC = Array [Array [Complex]]

} // Matrices object

/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * The MatrixN class stores and operates on Numeric Matrices of various sizes
 * and types.  The element type may be any subtype of Numeric.
 * @param dim1  the first/row dimension
 * @param dim2  the second/column dimension
 * @param v     the 2D array used to store matrix elements
 */
case class MatrixN [T <% Ordered [T]: Numeric: ClassTag] (d1: Int,
                                                      d2: Int,
                                           private var v: Array [Array [T]] = null)
     extends Matrix [T] (d1, d2) with Error
{
    {
        if (v == null) {
            v = Array.ofDim [T] (dim1, dim2)
        } else if (dim1 != v.length || dim2 != v(0).length) {
            flaw ("constructor", "dimensions are wrong")
        } // if
    } // primary constructor

    private val nu = implicitly[Numeric[T]]
    import nu._
    
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Construct a dim1 by dim1 square matrix.
     * @param dim1  the row and column dimension
     */
    def this (dim1: Int)
    {
        this (dim1, dim1)
    } // constructor

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Construct a dim1 by dim2 matrix and assign each element the value x.
     * @param dim1  the row dimension
     * @param dim2  the column dimesion
     * @param x     the scalar value to assign
     */
    def this (dim1: Int, dim2: Int, x: T)
    {
        this (dim1, dim2)                          // invoke primary constructor
        for (i <- range1; j <- range2) v(i)(j) = x
    } // constructor

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Construct a dim1 by dim1 square matrix with x assigned on the diagonal
     * and y assigned off the diagonal.  To obtain an identity matrix, let x = 1
     * and y = 0.
     * @param dim1  the row and column dimension
     * @param x     the scalar value to assign on the diagonal
     * @param y     the scalar value to assign off the diagonal
     */
    def this (dim1: Int, x: T, y: T)
    {
        this (dim1, dim1)                          // invoke primary constructor
        for (i <- range1; j <- range1) v(i)(j) = if (i == j) x else y
    } // constructor

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Construct a matrix and assign values from array of arrays u.
     * @param u  the 2D array of values to assign
     */
    def this (u: Array [Array [T]])
    {
        this (u.length, u(0).length, u)            // invoke primary constructor
    } // constructor

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Construct a matrix from repeated values.
     * @param dim1  the row dimension
     * @param u     the repeated values
     */
    def this (dim1: Int, u: T*)
    {
        this (dim1, u.length / dim1)               // invoke primary constructor
       for (i <- range1; j <- range2) v(i)(j) = u(i * dim2 + j)
    } // constructor

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Construct a matrix and assign values from array of vectors u.
     * @param u  the 2D array of values to assign
     */
    def this (u: Array [Vec[T]])
    {
        this (u.length, u(0).length)                  // invoke primary constructor
        for (i <- range1; j <- range2) v(i)(j) = u(i)(j)
    } // constructor

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Construct a matrix and assign values from matrix u.
     * @param u  the matrix of values to assign
     */
    def this (u: MatrixN [T])
    {
        this (u.dim1, u.dim2)                      // invoke primary constructor
        for (i <- range1; j <- range2) v(i)(j) = u.v(i)(j)
    } // constructor

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Get this matrix's element at the i,j-th index position. 
     * @param i  the row index
     * @param j  the column index
     */
    def apply (i: Int, j: Int): T = 
    {
        v(i)(j)
    } // apply

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Get this matrix's vector at the i-th index position (i-th row).
     * @param i  the row index
     */
    def apply (i: Int): Vec[T] =
    {
        Vec.fromSeq(v(i).toSeq)
    } // apply

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Set this matrix's element at the i,j-th index position to the scalar x.
     * @param i  the row index
     * @param j  the column index
     * @param x  the scalar value to assign
     */
    def update (i: Int, j: Int, x: T)
    {
        v(i)(j) = x
    } // update

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Set this matrix's row at the i-th index position to the vector u.
     * @param i  the row index
     * @param u  the vector value to assign
     */
    def update (i: Int, u: Vec [T])
    {
        v(i) = u.toArray
    } // update

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Slice this matrix row-wise from to end.
     * @param from  the start row of the slice (inclusive)
     * @param end   the end row of the slice (exclusive)
     */
    def slice (from: Int, end: Int): MatrixN [T] =
    {
        MatrixN [T] (end - from, dim2, v.slice (from, end))
    } // slice

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Slice this matrix row-wise r_from to r_end and column-wise c_from to c_end.
     * @param r_from  the start of the row slice
     * @param r_end   the end of the row slice
     * @param c_from  the start of the column slice
     * @param c_end   the end of the column slice
     */
    def slice (r_from: Int, r_end: Int, c_from: Int, c_end: Int): MatrixN [T] = 
    {
        val c = MatrixN [T] (r_end - r_from, c_end - c_from)
        for (i <- 0 until c.dim1; j <- 0 until c.dim2) c.v(i)(j) = v(i + r_from)(j + c_from)
        c
    } // slice

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Slice this matrix excluding the given row and column.
     * @param row  the row to exclude
     * @param col  the column to exclude
     */
    def sliceExclude (row: Int, col: Int): MatrixN [T] =
    {
        val c = MatrixN [T] (dim1 - 1, dim2 - 1)
        for (i <- range1 if i != row) for (j <- range2 if j != col) {
            c.v(i - oneIf (i > row))(j - oneIf (j > col)) = v(i)(j)
        } // for
        c
    } // sliceExclude

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Transpose this matrix (rows => columns).
     */
    def t: MatrixN [T] =
    {
        val b = MatrixN [T] (dim2, dim1)
        for (i <- b.range1; j <- b.range2) b.v(i)(j) = v(j)(i)
        b
    } // t

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Add this matrix and matrix b.
     * @param b  the matrix to add (requires sameCrossDimensions)
     */
    def + (b: Matrix [T]): MatrixN [T] =
    {
        val c = MatrixN [T] (dim1, dim2)
        for (i <- c.range1; j <- c.range2) c.v(i)(j) = nu.plus (v(i)(j), b(i, j))
        c
    } // +

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Add inplace this matrix and matrix b.
     * @param b  the matrix to add (requires sameCrossDimensions)
     */
    def += (b: Matrix [T])
    {
        for (i <- range1; j <- range2) v(i)(j) = nu.plus (v(i)(j), b(i, j))
    } // +=

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Add this matrix and scalar s.
     * @param s  the scalar to add
     */
    def + (s: T): MatrixN [T] =
    {
        val c = MatrixN [T] (dim1, dim2)
        for (i <- c.range1; j <- c.range2) c.v(i)(j) = nu.plus (v(i)(j), s)
        c
    } // +
 
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Add inplace this matrix and scalar s.
     * @param s  the scalar to add
     */
    def += (s: T) 
    {
        for (i <- range1; j <- range2) v(i)(j) = nu.plus (v(i)(j), s)
    } // +=
 
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * From this matrix substract matrix b.
     * @param b  the matrix to subtract (requires sameCrossDimensions)
     */
    def - (b: Matrix [T]): MatrixN [T] =
    {
        val c = MatrixN [T] (dim1, dim2)
        for (i <- c.range1; j <- c.range2) c.v(i)(j) = nu.minus (v(i)(j), b(i, j))
        c
    } // -
 
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * From this matrix substract inplace matrix b.
     * @param b  the matrix to subtract (requires sameCrossDimensions)
     */
    def -= (b: Matrix [T]) 
    {
        for (i <- range1; j <- range2) v(i)(j) = nu.minus (v(i)(j), b(i, j))
    } // -=
 
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * From this matrix subtract scalar s.
     * @param s  the scalar to subtract
     */
    def - (s: T): MatrixN [T] =
    {
        val c = MatrixN [T] (dim1, dim2)
        for (i <- c.range1; j <- c.range2) c.v(i)(j) = nu.minus (v(i)(j), s)
        c
    } // -
 
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * From this matrix subtract inplace scalar s.
     * @param s  the scalar to subtract
     */
    def -= (s: T) 
    {
        for (i <- range1; j <- range2) v(i)(j) = nu.minus (v(i)(j), s)
    } // -=
 
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Multiply this matrix by matrix b.
     * @param b  the matrix to multiply by (requires sameCrossDimensions)
     */
    def * (b: Matrix [T]) : MatrixN [T] =
    {
        val c = MatrixN [T] (dim1, b.dim2)
        for (i <- c.range1; j <- c.range2) c.v(i)(j) = row(i) dot b.col(j)
        c
    } // *

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Multiply inplace this matrix by matrix b.
     * @param b  the matrix to multiply by (requires sameCrossDimensions)
     */
    def *= (b: Matrix [T]) 
    {
        for (i <- range1; j <- range2) v(i)(j) = row(i) dot b.col(j)
    } // *=

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Multiply this matrix by vector b.
     * @param b  the vector to multiply by
     */
    def * (b: Vec[T]): Vec[T] =
    {
        val c = Vec.ofLength[T](dim1)
        for (i <- range1) c(i) = row(i) dot b
        c
    } // *

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Multiply this matrix by scalar s.
     * @param s  the scalar to multiply by
     */
    def * (s: T): MatrixN [T] =
    {
        val c = MatrixN [T] (dim1, dim2)
        for (i <- c.range1; j <- c.range2) c.v(i)(j) = nu.times (v(i)(j), s)
        c
    } // *

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Multiply inplace this matrix by scalar s.
     * @param s  the scalar to multiply by
     */
    def *= (s: T) 
    {
        for (i <- range1; j <- range2) v(i)(j) = nu.times (v(i)(j), s)
    } // *=

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Multiply this matrix by vector b to produce another matrix (a_ij * b_j)
     * @param b  the vector to multiply by
     */
    def ** (b: Vec[T]) : MatrixN [T] =
    {
        val c = MatrixN [T] (dim1, dim2)
        for (i <- c.range1; j <- c.range2) c.v(i)(j) = nu.times (v(i)(j), b(j))
        c
    } // **

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Multiply inplace this matrix by vector b to produce another matrix (a_ij * b_j)
     * @param b  the vector to multiply by
     */
    def **= (b: Vec[T]) 
    {
        for (i <- range1; j <- range2) v(i)(j) = nu.times (v(i)(j), b(j))
    } // **=

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Decompose this matrix into the product of upper and lower triangular
     * matrices (l, u) using the LU Decomposition algorithm.  This version uses
     * no partial pivoting.
     */
    def lud_npp (implicit nu: Fractional [T]): Tuple2 [MatrixN [T], MatrixN [T]] =
    {
        val _0 = nu.zero; val _1 = nu.one
        val l  = MatrixN [T] (dim1, dim2)   // lower triangular matrix
        val u  = new MatrixN [T] (this)     // upper triangular matrix (a copy of this)

        for (i <- u.range1) {
            val pivot = u(i, i)
            if (pivot == 0) flaw ("lud_npp", "use lud since you have a zero pivot")
            l(i, i) = _1
            for (j <- i + 1 until u.dim2) l(i, j) = _0
            for (k <- i + 1 until u.dim1) {
                val mul = nu.div (u(k, i), pivot)
                l(k, i) = mul
                for (j <- u.range2) u(k, j) = nu.minus (u(k, j), nu.times (mul, u(i, j)))
            } // for
        } // for
        Tuple2 (l, u)
    } // lud_npp

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Decompose this matrix into the product of lower and upper triangular
     * matrices (l, u) using the LU Decomposition algorithm.  This version uses
     * partial pivoting.
     */
    def lud (implicit nu: Fractional [T]): Tuple2 [MatrixN [T], MatrixN [T]] =
    {
        val _0 = nu.zero; val _1 = nu.one
        val l  = MatrixN [T] (dim1, dim2)  // lower triangular matrix
        val u  = new MatrixN [T] (this)    // upper triangular matrix (a copy of this)

        for (i <- u.range1) {
            var pivot = u(i, i)
            if (pivot == 0) {
                val k = partialPivoting (u, i)   // find the maxiumum element below pivot
                swap (u, i, k, i)                // swap rows i and k from column k
                pivot = u(i, i)                  // reset the pivot
            } // if
            l(i, i) = _1
            for (j <- i + 1 until u.dim2) l(i, j) = _0
            for (k <- i + 1 until u.dim1) {
                val mul = nu.div (u(k, i), pivot)
                l(k, i) = mul
                for (j <- u.range2) u(k, j) = nu.minus (u(k, j), nu.times (mul, u(i, j)))
            } // for
        } // for
        Tuple2 (l, u)
    } // lud

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Decompose inplace this matrix into the product of lower and upper triangular
     * matrices (l, u) using the LU Decomposition algorithm.  This version uses
     * partial pivoting.
     */
    def lud_ip (implicit nu: Fractional [T]): Tuple2 [MatrixN [T], MatrixN [T]] =
    {
        val _0 = nu.zero; val _1 = nu.one
        val l  = MatrixN [T] (dim1, dim2)  // lower triangular matrix
        val u  = this                      // upper triangular matrix (this)

        for (i <- u.range1) {
            var pivot = u(i, i)
            if (pivot == 0) {
                val k = partialPivoting (u, i)   // find the maxiumum element below pivot
                swap (u, i, k, i)                // swap rows i and k from column k
                pivot = u(i, i)                  // reset the pivot
            } // if
            l(i, i) = _1
            for (j <- i + 1 until u.dim2) l(i, j) = _0
            for (k <- i + 1 until u.dim1) {
                val mul = nu.div (u(k, i), pivot)
                l(k, i) = mul
                for (j <- u.range2) u(k, j) = nu.minus (u(k, j), nu.times (mul, u(i, j)))
            } // for
        } // for
        Tuple2 (l, u)
    } // lud_ip

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Use partial pivoting to find a maximal non-zero pivot and return its row
     * index, i.e, find the maximum element (k, i) below the pivot (i, i).
     * @param a  the matrix to perform partial pivoting on
     * @param i  the row and column index for the current pivot
     */
    private def partialPivoting (a: MatrixN [T], i: Int) (implicit nu: Fractional [T]): Int =
    {
        var max  = a(i, i)   // initially set to the pivot
        var kMax = i         // initially the pivot row

        for (k <- i + 1 until a.dim1 if nu.gt (nu.abs (a(k, i)), max)) {
            max  = nu.abs (a(k, i))
            kMax = k
        } // for
        if (kMax == i) flaw ("partialPivoting", "unable to find a non-zero pivot")
        kMax
    } // partialPivoting

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Swap the elements in rows i and k starting from column col.
     * @param a    the matrix containing the rows to swap
     * @param i    the higher row  (e.g, contains a zero pivot)
     * @param k    the lower row (e.g, contains max element below pivot)
     * @param col  the starting column for the swap
     */
    private def swap (a: MatrixN [T], i: Int, k: Int, col: Int)
    {
        for (j <- col until a.dim2) {
            val tmp = a(k, j); a(k, j) = a(i, j); a(i, j) = tmp
        } // for
    } // swap

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Solve for x in the equation l*u*x = b (see lud above).
     * @param l  the lower triangular matrix
     * @param u  the upper triangular matrix
     * @param b  the constant vector
     */
    override def solve (l: Matrix [T], u: Matrix [T], b: Vec [T])
        (implicit nu: Fractional [T]): Vec [T] =
    {
        val _0 = nu.zero
        val y  = Vec.ofLength[T] (l.dim2)
        for (k <- 0 until y.length) {                   // solve for y in l*y = b
            var sum = _0
            for (j <- 0 until k) sum = nu.plus (sum, nu.times (l(k, j), y(j)))
            y(k) = nu.minus (b(k), sum)
        } // for

        val x = Vec.ofLength[T] (u.dim2)
        for (k <- x.length - 1 to 0 by -1) {            // solve for x in u*x = y
            var sum = _0
            for (j <- k + 1 until u.dim2) sum = nu.plus (sum, nu.times (u(k, j), x(j)))
            x(k) = nu.div (nu.minus (y(k), sum), u(k, k))
        } // for
        x
    } // solve

   /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
   /**
     * Solve for x in the equation l*u*x = b (see lud above).
     * @param lu  the lower and upper triangular matrices
     * @param b   the constant vector
     */
    override def solve (lu: Tuple2 [Matrix [T], Matrix [T]], b: Vec [T])
        (implicit nu: Fractional [T]): Vec [T] =
    {
       solve (lu._1, lu._2, b)
    } // solve

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Solve for x in the equation a*x = b where a is this matrix (see lud above).
     * @param b  the constant vector.
     */
    override def solve (b: Vec [T])
        (implicit nu: Fractional [T]): Vec [T] =
    {
        solve (lud, b)
    } // solve

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Combine this matrix with matrix b, placing them along the diagonal and
     * filling in the bottom left and top right regions with zeroes; [this, b].
     * @param b  the matrix to combine with this matrix
     */
    def diag (b: Matrix [T]): MatrixN [T] =
    {
        val _0 = nu.zero
        val m  = dim1 + b.dim1
        val n  = dim2 + b.dim2
        val c  = MatrixN [T] (m, n)

        for (i <- 0 until m; j <- 0 until n) {
            c.v(i)(j) = if (i <  dim1 && j <  dim2) v(i)(j)
                   else if (i >= dim1 && j >= dim2) b(i - dim1, j - dim2)
                      else                          _0
        } // for
        c
    } // diag

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Form a matrix [Ip, this, Iq] where Ir is a r by r identity matrix, by
     * positioning the three matrices Ip, this and Iq along the diagonal.
     * @param p  the size of identity matrix Ip
     * @param q  the size of identity matrix Iq
     */
    def diag (p: Int, q: Int): MatrixN [T] =
    {
        if (! isSymmetric) flaw ("diag", "this matrix must be symmetric")
        val _0 = nu.zero; val _1 = nu.one
        val n  = dim1 + p + q 
        val c  = MatrixN [T] (n, n)

        for (i <- 0 until n; j <- 0 until n) {
            c.v(i)(j) = if (i < p || i > p + dim1) if (i == j) _1 else _0
                        else                       v(i - p)(j - p)
        } // for
        c
    } // diag

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Invert this matrix (requires a squareMatrix).  This version does not use
     * partial pivoting.
     */
    def inverse_npp (implicit nu: Fractional [T]): MatrixN [T] =
    {
        val _0 = nu.zero; val _1 = nu.one
        val b  = new MatrixN [T] (this)           // copy this matrix into b
        val c  = new MatrixN [T] (dim1, _1, _0)   // let c represent the augmentation

        for (i <- b.range1) {
            val pivot = b(i, i)
            if (pivot == 0) flaw ("inverse_npp", "use inverse since you have a zero pivot")
            for (j <- b.range2) {
                b(i, j) = nu.div (b(i, j), pivot)
                c(i, j) = nu.div (c(i, j), pivot)
            } // for
            for (k <- 0 until b.dim1 if k != i) {
                val mul = b(k, i)
                for (j <- b.range2) {
                     b(k, j) = nu.minus (b(k, j), nu.times (mul, b(i, j)))
                     c(k, j) = nu.minus (c(k, j), nu.times (mul, c(i, j)))
                } // for
            } // for
        } // for
        c
    } // inverse_npp

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Invert this matrix (requires a squareMatrix).  This version uses partial
     * pivoting.
     */
    def inverse (implicit nu: Fractional [T]): MatrixN [T] =
    {
        val _0 = nu.zero; val _1 = nu.one
        val b  = new MatrixN [T] (this)           // copy this matrix into b
        val c  = new MatrixN [T] (dim1, _1, _0)   // let c represent the augmentation

        for (i <- b.range1) {
            var pivot = b(i, i)
            if (pivot == 0) {
                val k = partialPivoting (b, i)  // find the maxiumum element below pivot
                swap (b, i, k, i)               // in b, swap rows i and k from column i
                swap (c, i, k, 0)               // in c, swap rows i and k from column 0
                pivot = b(i, i)                 // reset the pivot
            } // if
            for (j <- b.range2) {
                b(i, j) = nu.div (b(i, j), pivot)
                c(i, j) = nu.div (c(i, j), pivot)
            } // for
            for (k <- 0 until dim1 if k != i) {
                val mul = b(k, i)
                for (j <- b.range2) {
                     b(k, j) = nu.minus (b(k, j), nu.times (mul, b(i, j)))
                     c(k, j) = nu.minus (c(k, j), nu.times (mul, c(i, j)))
                } // for
            } // for
        } // for
        c
    } // inverse

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Invert inplace this matrix (requires a squareMatrix).  This version uses
     * partial pivoting.
     */
    def inverse_ip (implicit nu: Fractional [T]): MatrixN [T] =
    {
        val _0 = nu.zero; val _1 = nu.one
        val b  = this                             // use this matrix for b
        val c  = new MatrixN [T] (dim1, _1, _0)   // let c represent the augmentation

        for (i <- b.range1) {
            var pivot = b(i, i)
            if (pivot == 0) {
                val k = partialPivoting (b, i)  // find the maxiumum element below pivot
                swap (b, i, k, i)               // in b, swap rows i and k from column i
                swap (c, i, k, 0)               // in c, swap rows i and k from column 0
                pivot = b(i, i)                 // reset the pivot
            } // if
            for (j <- b.range2) {
                b(i, j) = nu.div (b(i, j), pivot)
                c(i, j) = nu.div (c(i, j), pivot)
            } // for
            for (k <- 0 until dim1 if k != i) {
                val mul = b(k, i)
                for (j <- b.range2) {
                     b(k, j) = nu.minus (b(k, j), nu.times (mul, b(i, j)))
                     c(k, j) = nu.minus (c(k, j), nu.times (mul, c(i, j)))
                } // for
            } // for
        } // for
        c
    } // inverse_ip

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Use Guass-Jordan reduction on this matrix to make the left part embed an
     * identity matrix.  A constraint on this m by n matrix is that n >= m.
     */
    def reduce (implicit nu: Fractional [T]): MatrixN [T] =
    {
        if (dim2 < dim1) flaw ("reduce", "requires n (columns) >= m (rows)")
        val b = new MatrixN [T] (this)    // copy this matrix into b

        for (i <- b.range1) {
            var pivot = b(i, i)
            if (pivot == 0) {
                val k = partialPivoting (b, i)  // find the maxiumum element below pivot
                swap (b, i, k, i)               // in b, swap rows i and k from column i
                pivot = b(i, i)                 // reset the pivot
            } // if
            for (j <- b.range2) {
                b(i, j) = nu.div (b(i, j), pivot)
            } // for
            for (k <- 0 until dim1 if k != i) {
                val mul = b(k, i)
                for (j <- b.range2) {
                     b(k, j) = nu.minus (b(k, j), nu.times (mul, b(i, j)))
                } // for
            } // for
        } // for
        b
    } // reduce

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Use Guass-Jordan reduction inplace on this matrix to make the left part
     * embed an identity matrix.  A constraint on this m by n matrix is that n >= m.
     */
    def reduce_ip (implicit nu: Fractional [T])
    {
        if (dim2 < dim1) flaw ("reduce", "requires n (columns) >= m (rows)")
        val b = this         // use this matrix for b

        for (i <- b.range1) {
            var pivot = b(i, i)
            if (pivot == 0) {
                val k = partialPivoting (b, i)  // find the maxiumum element below pivot
                swap (b, i, k, i)               // in b, swap rows i and k from column i
                pivot = b(i, i)                 // reset the pivot
            } // if
            for (j <- b.range2) {
                b(i, j) = nu.div (b(i, j), pivot)
            } // for
            for (k <- 0 until dim1 if k != i) {
                val mul = b(k, i)
                for (j <- b.range2) {
                     b(k, j) = nu.minus (b(k, j), nu.times (mul, b(i, j)))
                } // for
            } // for
        } // for
    } // reduce_ip

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Compute the determinant of this matrix.
     */
    def det(): T =
    {
        val _0 = nu.zero
        if ( ! isSquare) flaw ("det", "determinant only works on square matrices")
        var sum = _0
        var b: MatrixN [T] = null
        for (j <- range2) {
            b = sliceExclude (0, j)   // the submatrix that excludes row 0 and column j
            if (j % 2 == 0) {
                sum = nu.plus (sum, nu.times (v(0)(j), (if (b.dim1 == 1) b(0, 0) else b.det)))
            } else {
                sum = nu.minus (sum, nu.times (v(0)(j), (if (b.dim1 == 1) b(0, 0) else b.det)))
            } // if
        } // for 
        sum
    } // det

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Check whether this matrix is rectangular (all rows have the same number
     * of columns).
     */
    def isRectangular: Boolean =
    {
        for (i <- range1 if v(i).length != dim2) return false
        true
    } // isRectangular

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /**
     * Convert this matrix to a string.
     */
    override def toString: String = 
    {
        var s = new StringBuilder ("\nMatrixN(")
        for (i <- range1) {
            s ++= this(i).toString
            s ++= (if (i < dim1 - 1) ",\n\t" else ")")
        } // for
        s.toString
    } // toString
  
} // MatrixN class

/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/**
 * The MatrixN object tests the operations provided by MatrixN.
 */
object MatrixNTest extends App
{
    for (l <- 1 to 4) {
        println ("\n\tTest MatrixN on integer matrices of dim " + l)
        val a = MatrixN [Int] (l, l)
        val b = MatrixN [Int] (l, l)
        a.set (2)
        b.set (3)
        println ("a + b = " + (a + b))
        println ("a - b = " + (a - b))
        println ("a * b = " + (a * b))
        println ("a * 4 = " + (a * 4))

        println ("\n\tTest MatrixN on real matrices of dim " + l)
        val x = MatrixN [Double] (l, l)
        val y = MatrixN [Double] (l, l)
        x.set (2)
        y.set (3)
        println ("x + y  = " + (x + y))
        println ("x - y  = " + (x - y))
        println ("x * y  = " + (x * y))
        println ("x * 4. = " + (x * 4))
    } // for

    println ("\n\tTest MatrixN on additional operations")

    val z = MatrixN [Double] (2, 2)
    z.set (Array (Array (1.toDouble, 2.toDouble), Array (3.toDouble, 2.toDouble)))
    val b = Vec(8.toDouble, 7.toDouble)
    val lu  = z.lud
    val lu2 = z.lud_npp

    println ("z         = " + z)
    println ("z.t       = " + z.t)
    println ("z.lud     = " + lu)
    println ("z.lud_npp = " + lu2)
    println ("z.solve   = " + z.solve (lu._1, lu._2, b))
    println ("z.inverse = " + z.inverse)
    println ("z.det     = " + z.det)

    val w = MatrixN [Double] (2, 3)
    w.set (Array (Array[Double] (2, 3, 5), Array[Double] (-4, 2, 3)))
    val v = MatrixN [Double] (3, 2)
    v.set (Array (Array[Double] (2, -4), Array[Double] (3, 2), Array[Double] (5, 3)))

    println ("w         = " + w)
    println ("v         = " + v)
    println ("w.reduce  = " + w.reduce)

    println ("right:    w.nullspace = " + w.nullspace)
    println ("check right nullspace = " + w * w.nullspace)

    println ("left:   v.t.nullspace = " + v.t.nullspace)
    println ("check left  nullspace = " + v.t.nullspace * v)

    for (row <- z) println ("row = " + row.deep)

} // MatrixNTest object

