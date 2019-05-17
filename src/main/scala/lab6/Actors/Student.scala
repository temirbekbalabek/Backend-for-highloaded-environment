package lab6

import Student.ChangePassword
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object Student{
  case class ChangePassword(id: String, pp: String, np: String)
}
class Student extends Actor with ActorLogging{

  val kbtuComputer = context.actorOf(Props[KBTUComputer], "kcomputer")
  override def receive: Receive = {
    case ChangePassword(id, previousPassword, newPassword)=>
      kbtuComputer ! Student.ChangePassword(id, previousPassword, newPassword)
      context.become(sendingToBoot(sender()))
  }
  def sendingToBoot(senderRef: ActorRef): Receive = {
    case msg: KBTUComputer.Answer =>
      senderRef ! msg
      context.become(receive)

  }
}
