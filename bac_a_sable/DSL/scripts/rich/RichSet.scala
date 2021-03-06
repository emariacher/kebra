/* $Id: RichSet.scala 66 2011-02-25 17:08:30Z mepcotterell@gmail.com $ */

package rich

import scalation.ScalaTion

/**
 * Example of how to take advantage of RichSet
 * @author Michael Cotterell
 */
object RichSet extends ScalaTion
{
	private def printSection(title: String)
	{
		println
		for (i <- 1 to 80) print(":"); println
		println(title.toUpperCase)
		for (i <- 1 to 80) print(":"); println
		println
	}
	
	def main(args : Array[String]) 
	{
		
		println("Example of how to take advantage of RichSet.")
		
		printSection("Define sets")
		
		val ab = Set("a", "b")
		val ac = Set("a", "c")
		val bc = Set("b", "c")
		val x  = Set(1, 2, 3, 4)
		
		println("∅ = " + ∅)
		println("ab = " + ab)
		println("ac = " + ac)
		println("bc = " + bc)
		
		printSection("define vectors")
		
		val v = VectorN(1, 2, 3, 4)
		
		println("v = " + v)
		
		printSection("Contains")
		
		println("ab ∋ a = " + ab ∋ "a")
		println("ab ∋ b = " + ab ∋ "b")
		println("ab ∋ c = " + ab ∋ "c")
		
		println("ab ∌ a = " + ab ∌ "a")
		println("ab ∌ b = " + ab ∌ "b")
		println("ab ∌ c = " + ab ∌ "c")
		
		printSection("union")
		
		println("∅ ∪ ab = " + ∅ ∪ ab)
		println("ab ∪ bc = " + ab ∪ bc)
		println("ab ∪ ac = " + ab ∪ ac)
		println("ac ∪ bc = " + ac ∪ bc)
		
		printSection("intersection")
		
		println("∅ ∩ ab = " + ∅ ∩ ab)
		println("ab ∩ ab = " + ab ∩ ab)
		println("ab ∩ bc = " + ab ∩ bc)
		println("ab ∩ ac = " + ab ∩ ac)
		
		printSection("quantifiers")
		
		println("x ∀ (_ < 10) = " + x ∀ (_ < 10))
		println("x ∀ (_ > 10) = " + x ∀ (_ > 10))
		println("ab ∀ (_ == \"a\") == " + ab ∀ (_ == "a"))
		println("ab ∀ (_.isInstanceOf[String]) == " + ab ∀ (_.isInstanceOf[String]))
		
		// more examples to come
		
	}
}
