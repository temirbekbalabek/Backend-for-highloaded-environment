package lab6.Actors

import lab6.{KBTUComputer, Student, Password}
import spray.json.DefaultJsonProtocol._

import spray.json.RootJsonFormat

trait JsonSupport {
  implicit val f1: RootJsonFormat[KBTUComputer.Answer] = jsonFormat1(KBTUComputer.Answer)
  implicit val f3: RootJsonFormat[Student.ChangePassword] = jsonFormat3(Student.ChangePassword)
  implicit val f4: RootJsonFormat[Password.Changed] = jsonFormat4(Password.Changed)
  implicit val f5: RootJsonFormat[Password.CheckPassword] = jsonFormat3(Password.CheckPassword)

}
