package graphlayout

import java.awt.{Color, Graphics2D}

class GNode(val lbl: String) {
  var x = .0
  var y = .0

  var dx: Double = .0
  var dy: Double = .0

  var fixed = false

  override def toString: String = "{" + lbl + "}"

  override def hashCode: Int = lbl.hashCode

  override def equals(anyo: Any): Boolean = {
    anyo match {
      case g: GNode => hashCode.equals(g.hashCode)
      case _ => false
    }
  }

  def paint(g: Graphics2D) {
    val w = g.getFontMetrics.stringWidth(lbl) + 10
    val h = g.getFontMetrics.getHeight() + 4
    g.fillRect(x.toInt - w / 2, y.toInt - h / 2, w, h)
    g.setColor(Color.black)
    g.drawRect(x.toInt - w / 2, y.toInt - h / 2, w - 1, h - 1)
    g.drawString(lbl, x.toInt - (w - 10) / 2, (y.toInt - (h - 4) / 2))
  }

}
