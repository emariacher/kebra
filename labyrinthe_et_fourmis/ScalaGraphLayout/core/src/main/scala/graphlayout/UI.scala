package graphlayout

import java.io.File

import kebra.LL._
import kebra.MyLog

import scala.swing.event._
import scala.swing.{Label, Slider, _}

class UI(graph: GraphAbstract) extends SimpleSwingApplication {
  val titre = "graphlayout."
  val buttonStepLbl = "step"
  val buttonStopLbl = "stop"
  val labelLbl = "Idle Label"
  newMyLL(this.getClass.getName, new File("out\\cowabunga"), "htm", true)
  graph.resultat = new Resultat

  def top = new MainFrame {
    title = titre
    val sliderpp = new Slider {
      majorTickSpacing = 20
      minorTickSpacing = 5
      paintLabels = true
      paintTicks = true
      value = 5
    }
    val sliderAttraction = new Slider {
      name = "Attraction"
      majorTickSpacing = 20
      minorTickSpacing = 5
      paintLabels = true
      paintTicks = true
      value = 1
    }
    val sliderRepulsion = new Slider {
      name = "Repulsion"
      majorTickSpacing = 20
      minorTickSpacing = 5
      paintLabels = true
      paintTicks = true
      value = 10
    }
    val buttonStep: Button = new Button {
      text = buttonStepLbl
    }
    val buttonStop: Button = new Button {
      text = buttonStopLbl
    }
    val label: Label = new Label {
      text = labelLbl
    }

    ZePanel.newZePanel(label, new RowCol(70, 100), graph)

    contents = new BoxPanel(Orientation.Vertical) {
      contents += label
      contents += new BoxPanel(Orientation.Horizontal) {
        Tribu.tribus.foreach(t => {
          contents += t.label
        })
      }

      contents += ZePanel.zp
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += sliderpp
        contents += buttonStop
        contents += buttonStep
      }
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += new Label("Attraction")
        contents += new Label("                              ")
        contents += new Label("Repulsion")
      }
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += sliderAttraction
        contents += sliderRepulsion
      }
      border = Swing.EmptyBorder(30, 30, 10, 30)
      listenTo(mouse.clicks, mouse.moves)
      reactions += {
        case e: MouseEvent => ZePanel.za ! (e.toString.substring(0, 6), e.point.x, e.point.y)
        case _ => MyLog.myPrintIt("Ici!")
      }
    }
    listenTo(sliderpp, sliderAttraction, sliderRepulsion, buttonStop, buttonStep)
    reactions += {
      case ValueChanged(b) => ZePanel.za ! (b.name, b.asInstanceOf[Slider].value)
      case ButtonClicked(b) => ZePanel.za ! b.text
      case _ =>
    }
  }

}
