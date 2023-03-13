import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.ActorMaterializer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object BurgersClient extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val apiKeyHeader = RawHeader("X-Rapidapi-Key", "88f69f9591msh50a3c173bb8fc51p114cbdjsndedf25fdc2a2")
  val apiHostHeader = RawHeader("X-Rapidapi-Host", "burgers-hub.p.rapidapi.com")
  val headers = List(apiKeyHeader, apiHostHeader)

  print("Enter a burger ID: ")
  val burgerId = StdIn.readInt()

  val request = HttpRequest(
    HttpMethods.GET,
    uri = s"https://burgers-hub.p.rapidapi.com/burgers/$burgerId",
    headers = headers
  )

  val responseF = Http().singleRequest(request)

  responseF.map { response =>
    if (response.status.isSuccess()) {
      response.entity.toStrict(5.seconds).map(_.data.utf8String).foreach(println)
    } else {
      println(s"Request failed with status ${response.status}")
      response.discardEntityBytes()
    }
  }.onComplete(_ => system.terminate())
}
