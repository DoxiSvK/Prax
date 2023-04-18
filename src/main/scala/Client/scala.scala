import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.ActorMaterializer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.util.{Failure, Success}
import scala.util.Success
import scala.util.Failure



object Client extends App {
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

  responseF.flatMap { response =>
    if (response.status.isSuccess()) {
      response.entity.toStrict(5.seconds).map(_.data.utf8String)
    } else {
      response.discardEntityBytes()
      throw new RuntimeException(s"Request failed with status ${response.status}")
    }
  }.onComplete { result =>
    result match {
      case scala.util.Success(responseBody) =>
        println(s"Response: $responseBody")
      case scala.util.Failure(ex) =>
        println(s"Request failed: ${ex.getMessage}")
    }
    system.terminate()
  }
}
