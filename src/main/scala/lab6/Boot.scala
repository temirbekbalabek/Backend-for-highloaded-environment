package lab6

import java.util

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import lab6.Actors.JsonSupport
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
object Boot extends App with JsonSupport {

  val log = LoggerFactory.getLogger("Boot")
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(30.seconds)

  val student = system.actorOf(Props[Student], "student")
  val route = {
    pathPrefix("student") {
      (path("change_password") & post) {
        entity(as[Student.ChangePassword]) { s: Student.ChangePassword =>
          complete {
            (student ? Student.ChangePassword(s.id, s.pp, s.np)).mapTo[KBTUComputer.Answer]
          }
        }
      }
    }
  }


  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  log.info("Listening on port 8080...")
}
