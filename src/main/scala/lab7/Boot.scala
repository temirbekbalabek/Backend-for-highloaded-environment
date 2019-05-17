package lab7

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{concat, pathPrefix}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import lab7.models.{Phone, Phones, Response}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object Boot extends App with JsonSupport {
  val log = LoggerFactory.getLogger("Boot")

  // needed to run the route
  implicit val system = ActorSystem()

  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.dispatcher

  implicit val timeout = Timeout(30.seconds)

  val db: MySQLProfile.backend.Database = Database.forConfig("mysql")

  val phones = TableQuery[PhonesTable]

  val gadget = system.actorOf(Gadget.props(db), "gadget")

  val route =
    pathPrefix("gadget") {
      path("phones") {
        get {
          complete {
            (gadget ? Gadget.GetPhones).mapTo[Phones]
          }
        }
      } ~
        path("phones") {
          post {
            entity(as[Phone]) { phone =>
              complete {
                (gadget ? Gadget.CreatePhone(phone.name, phone.displaySize, phone.camera,
                  phone.price, phone.memory)).mapTo[Future[Int]].flatten.map ( x => Response(x))
              }
            }
          }
        } ~
        (path("phone") & delete){
          entity(as[Phone]){ phone =>
            complete {
              (gadget ? Gadget.DeletePhone(phone.id)).mapTo[Future[Int]].flatten.map(x=>Response(x))
            }
          }
      }


    }

  val config = ConfigFactory.load()

  val shouldCreate = config.getBoolean("create-schema")

  if (shouldCreate) {
    try {
      Await.result(db.run(DBIO.seq(
        phones.schema.create,

        phones.result.map(println),
      )), Duration.Inf)
    }
  }


  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  log.info("Listening on port 8080...")
}
