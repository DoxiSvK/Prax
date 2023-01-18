object Main {
  def main(args: Array[String]): Unit = {
    case class Color(red: Int, green: Int, blue: Int)
    case class Pos(x: Int, y: Int)

    sealed trait Pixel {
      def pos: Pos
      def color: Color
    }
    case class TransparentPixel(pos: Pos, color: Color, transparency: Int) extends Pixel
    case class RGBPixel(pos: Pos, color: Color) extends Pixel

    val dimension = 3
    val screen =
      """|0:100,125, 56,200
         |1:255,  0,  0
         |2:  0,135,200
         |3:220, 12,  0,100
         |4: 45, 97,  0
         |5:  0,  0,  0
         |6:  0,  0,  0,0
         |7:  0,  0,  0
         |8:255,255,255
         |""".stripMargin

    val pixels = screen.stripMargin.split("\n").zipWithIndex.map {
      case (line, index) =>
        val Array(x, y) = index.toString.split(":")
        val Array(r, g, b, t) = line.split(",").map(_.trim)
        val transparency = t.toInt
        if (transparency > 0) TransparentPixel(Pos(x.toInt, y.toInt), Color(r.toInt, g.toInt, b.toInt), transparency)
        else RGBPixel(Pos(x.toInt, y.toInt), Color(r.toInt, g.toInt, b.toInt))
    }.toList

    val redPixels = pixels.groupBy(_.pos.x).mapValues { row =>
      row.reduce((p1, p2) => if (p1.color.red > p2.color.red) p1 else p2)
    }

    val mostTransparentPixel = pixels.filter(_.isInstanceOf[TransparentPixel]).reduce((p1, p2) => {
      if (p1.asInstanceOf[TransparentPixel].transparency > p2.asInstanceOf[TransparentPixel].transparency) p1 else p2
    }).pos

    println(pixels)
    println(redPixels)
    println(mostTransparentPixel)
  }
}
