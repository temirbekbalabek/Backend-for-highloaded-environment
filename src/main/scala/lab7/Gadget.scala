package lab7

import akka.actor.{Actor, Props}
import lab7.models.{Phone, Phones}
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object Gadget {

  case class CreatePhone(name: String, displaySize: String, camera: String, price: String, memory: String)
  case class DeletePhone(id: Int)
  case object GetPhones

  def props(db: MySQLProfile.backend.Database) = Props(new Gadget(db))
}

class Gadget(db: MySQLProfile.backend.Database) extends Actor {

  import Gadget._
  val phonesTable = TableQuery[PhonesTable]
  val phones: Seq[Phone] = Await.result(db.run(phonesTable.result), 5.seconds)
  override def receive: Receive = {
    case CreatePhone(newName, ds, c, p, m) =>
      sender() ! db.run(
        phonesTable += Phone(name = newName, displaySize = ds, camera = c, price = p, memory = m)
      )
    case DeletePhone(id) =>
      sender() ! db.run(
        phonesTable.filter(_.id === id).delete
        // phonesTable -= Phone(name = newName, displaySize = ds, camera = c, price = p, memory = m)
      )
    case GetPhones =>
      sender() ! Phones(phones)
  }
}