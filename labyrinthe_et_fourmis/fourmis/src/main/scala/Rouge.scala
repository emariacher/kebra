package labyrinthe

import labyrinthe.FrontiereV._
import kebra._
import labyrinthe.LL._

class Rouge(couleur: String, rayon: Int, fourmiliere: Fourmiliere) extends Jeton(couleur, rayon, fourmiliere) {
  val ordreChoix = new Circular(List(nord, ouest, sud, est), nextf, prevf)
  l.myPrintln(MyLog.func(1) + couleur + " " + lastDirection)

  // n'essaye pas de continuer dans la meme direction que la derniere fois mais continue a parcourir les priorites
  def firstStep: RowCol = new RowCol(888, 888)

  def nextf(c: Circular): Frontiere = {
    if (c.pos < 0) c.pos = c.elements.length - 1
    if (c.pos == c.elements.length) c.pos = 0
    val value = c.elements(c.pos)
    c.pos += 1
    value
  }

  def prevf(c: Circular): Frontiere = {
    if (c.pos < 0) c.pos = c.elements.length - 1
    if (c.pos == c.elements.length) c.pos = 0
    val value = c.elements(c.pos)
    c.pos -= 1
    value
  }
}

class Orange(couleur: String, rayon: Int, fourmiliere: Fourmiliere) extends Rouge(couleur, rayon, fourmiliere) {
  // essaye de continuer dans la meme direction que la derniere fois
  override def firstStep: RowCol = getNext(lastDirection, true, true)

  override def retourne() = raccourci()
}

class VertFonce(couleur: String, rayon: Int, fourmiliere: Fourmiliere) extends Rouge(couleur, rayon, fourmiliere) {
  override val ordreChoix = new Circular(List(nord, est, sud, ouest), nextf, prevf)
  // n'essaye pas de continuer dans la meme direction que la derniere fois mais continue a parcourir les priorites
}

class VertClair(couleur: String, rayon: Int, fourmiliere: Fourmiliere) extends VertFonce(couleur, rayon, fourmiliere) {
  // essaye de continuer dans la meme direction que la derniere fois
  override def firstStep: RowCol = getNext(lastDirection, true, true)

  override def retourne() = raccourci()
}

