package kebra

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import MyLog._

object LL {
  var l: LL = _

  def getMyLL: LL = l

  def newMyLL(s_title: String, fil: File, errExt: String, traces: Boolean): LL = {
    newMyLog(this.getClass.getName, new File("out\\cowabunga"), "htm")
    l = new LL(traces)
    l
  }
}

class LL(val traces: Boolean) {
  def myPrint(s: String) = {
    if (traces) {
      L.myPrint(s)
    }
  }

  def myPrintln(a: Any) = myPrint(a.toString + "\n")

  def myPrintD(a: Any) = myPrint(tag(3) + " " + a)

  def myPrintDln(a: Any) = myPrintD(a.toString + "\n")

  def myErrPrint(a: Any) = {
    if (traces) {
      L.myErrPrint(a)
    }
  }

  def myErrPrintln(a: Any) = myErrPrint(a.toString + "\n")

  def myErrPrintD(a: Any) = myErrPrint(tag(3) + " " + a)

  def myErrPrintDln(a: Any) = myErrPrintD(a.toString + "\n")

  def tag(i_level: Int): String = {
    val s_time = new SimpleDateFormat("dd_HH:mm_ss,SSS").format(Calendar.getInstance.getTime) + " "
    try {
      throw new Exception()
    } catch {
      case unknown: Throwable => unknown.getStackTrace.toList.apply(i_level).toString match {
        case MatchFileLine(file, line) => file + ":" + line + " " + s_time
      }
    }
  }
}