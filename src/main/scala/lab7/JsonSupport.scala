package lab7

import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
import lab7.models.{Phone, Phones, Response}

trait JsonSupport {
  implicit val phoneFormat: RootJsonFormat[Phone] = jsonFormat6(Phone)
  implicit val phonesFormat: RootJsonFormat[Phones] = jsonFormat1(Phones)
  implicit val responses: RootJsonFormat[Response] = jsonFormat1(Response)
  implicit val a: RootJsonFormat[Gadget.CreatePhone] = jsonFormat5(Gadget.CreatePhone)
  implicit val b: RootJsonFormat[Gadget.DeletePhone] = jsonFormat1(Gadget.DeletePhone)

}