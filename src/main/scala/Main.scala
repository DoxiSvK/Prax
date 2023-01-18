object main extends App {

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
  case class Pos(y : Int , x : Int)
  case class Props(red:Int, green:Int, blue:Int, trans: Option[Int])
  case class Pixel(pos:Pos,props:Props)
  val pixels: List[Pixel] =

    for (line <- screen.split("\n").toList.map(_.replace(" ",""))) yield {
      line.trim match{
        case s"$id:$red,$green,$blue,$trans" =>{
          Pixel(Pos(id.toInt / dimension , id.toInt % dimension), Props(red.toInt, green.toInt, blue.toInt, trans.toIntOption))}
        case s"$id:$red,$green,$blue" => {
          Pixel(Pos(id.toInt / dimension , id.toInt % dimension), Props(red.toInt, green.toInt, blue.toInt, None))}
      }
    }
  pixels.foreach(println)
  val red : Map[Int , Pixel] =  pixels.groupBy(_.pos.y).map{
    case (riadok,pixel) => (riadok,pixel.maxBy(_.props.red))
  }
  println("max red --> " + red)
  val trans : Map[Int,Int] = pixels.groupBy(_.pos.y).map{
    case (riadok,pixel) => (riadok,(pixel.maxBy(_.props.trans)).pos.x)
  }
  println("max trans --> " + trans)
}
