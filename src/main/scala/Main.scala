object Main {
  def main(args: Array[String]): Unit = {
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

    case class Color(r: Int, g: Int, b: Int)
    case class Pos(x: Int, y: Int)
    object Pos {
      def apply(index: Int): Pos = Pos(index % dimension, index / dimension)
    }
    trait Pixel {
      val pos: Pos
      val color: Color
    }
    case class RGBPixel(pos: Pos, color: Color) extends Pixel
    case class TransparentPixel(pos: Pos, color: Color, transparency: Int) extends Pixel

    val pixels = screen.split("\n").toList.map { line =>
      val Array(pix, color, transparency) = line.split(":")
      val Array(r, g, b) = color.split(",")
      if (transparency.nonEmpty) {
        TransparentPixel(Pos(pix.toInt), Color(r.toInt, g.toInt, b.toInt), transparency.toInt)
      } else {
        RGBPixel(Pos(pix.toInt), Color(r.toInt, g.toInt, b.toInt))
      }
    }

    val maxReds = pixels.groupBy(_.pos.y).mapValues(_.maxBy(_.color.r))

    val maxTransparentPos = pixels.collect { case p: TransparentPixel => p }.maxBy(_.transparency).pos

    pixels.foreach { p =>
      print(s"|$p")
      if (p.pos.x > 1) println()
    }

    println(maxReds)
    println(maxTransparentPos)
  }
}
