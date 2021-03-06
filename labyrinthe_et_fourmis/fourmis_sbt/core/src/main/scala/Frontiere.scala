package labyrinthe

import java.awt.{Color, Graphics2D}

class Frontiere(val f: FrontiereV.Value) {
  def paint(g: Graphics2D, horiz: Int, vert: Int, x: Int, y: Int): Unit = {
    f match {
      case FrontiereV.NORD => g.drawLine(x, y, x, y - vert)
      case FrontiereV.EST => g.drawLine(x, y, x + horiz, y)
      case FrontiereV.SUD => g.drawLine(x, y, x, y + vert)
      case FrontiereV.OUEST => g.drawLine(x, y, x - horiz, y)
    }
  }

  def paint(g: Graphics2D, horiz: Int, vert: Int, x: Int, y: Int, color: Color): Unit = {
    g.setColor(color)
    f match {
      case FrontiereV.NORD => g.drawLine(x, y, x, y - vert)
      case FrontiereV.EST => g.drawLine(x, y, x + horiz, y)
      case FrontiereV.SUD => g.drawLine(x, y, x, y + vert)
      case FrontiereV.OUEST => g.drawLine(x, y, x - horiz, y)
    }
  }

  override def toString: String = {
    f match {
      case FrontiereV.NORD => f.toString + "_A"
      case FrontiereV.EST => f.toString + "_>"
      case FrontiereV.SUD => f.toString + "_V"
      case FrontiereV.OUEST => "<_" + f.toString
    }

  }
}

object FrontiereV extends Enumeration {
  type FrontiereV = Value
  val NORD, EST, SUD, OUEST = Value
  val nord = new Frontiere(FrontiereV.NORD)
  val est = new Frontiere(FrontiereV.EST)
  val sud = new Frontiere(FrontiereV.SUD)
  val ouest = new Frontiere(FrontiereV.OUEST)
  FrontiereV.values foreach println
}

