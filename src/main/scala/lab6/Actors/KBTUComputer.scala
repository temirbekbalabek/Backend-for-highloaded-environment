package lab6
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import lab6.KBTUComputer.{Answer}

object KBTUComputer{
  case class Answer(msg: String)
}
class KBTUComputer extends Actor with ActorLogging{
  var listOfStudents = Map.empty[String, String]
  listOfStudents += "t_balabek" -> "March2019"
  val password = context.actorOf(Props[Password],"password")
  override def receive(): Receive = {

    case Student.ChangePassword(id, previousPassword, newPassword) =>
      if(listOfStudents.contains(id)) {
        password ! Password.CheckPassword(id, previousPassword, newPassword)
        context.become(sendingToStudent(sender()))
      }
      else{
        sender() ! Answer("Invalid id - {}".format(id))
      }
  }

  def sendingToStudent(senderRef: ActorRef): Receive ={

    case Password.Changed(true, id, pp, np) =>
      senderRef ! Answer("{} - your password has successfully changed".format(id))
    case Password.Changed(false, _, _, _) =>
      senderRef ! Answer("invalid new password:\n* make sure that your " +
        "new password contains at least 8 symbols")
  }
}
