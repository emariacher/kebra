package bugstats

object DefectState extends Enumeration {
	type DefectState = Value
			val OPENED, INQA, CLOSED, MODIFYING, STABILIZING, STABLE, DONTCARE = Value
}