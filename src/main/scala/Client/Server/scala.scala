package Client.Server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Server {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("server-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContext = system.dispatcher

    var burgers = Map(1 -> "Cheeseburger", 2 -> "Hamburger", 3 -> "Veggie Burger")

    val route =
      path("burgers" / IntNumber) { burgerId =>
        get {
          val responseF: Future[HttpResponse] = Future {
            burgers.get(burgerId) match {
              case Some(burger) =>
                HttpResponse(entity = HttpEntity(burger))
              case None =>
                HttpResponse(status = StatusCodes.NotFound, entity = HttpEntity(s"Burger with id $burgerId not found"))
            }
          }

          onComplete(responseF) {
            case Success(response) =>
              response.entity.toStrict(5.seconds).map(_.data.utf8String).foreach { burgerJson =>
                println(s"Received response: $burgerJson")
              }
              complete(response)
            case Failure(ex) =>
              complete(HttpResponse(status = StatusCodes.InternalServerError, entity = HttpEntity(ex.getMessage)))
          }
        }
      }

    Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/")
  }
}
