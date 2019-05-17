package lab6
import akka.actor.{Actor, ActorLogging}

object Password{
  case class CheckPassword(id: String, pp: String, np: String)
  case class Changed(ans: Boolean, id: String, pp: String, np: String )
}
class Password extends Actor with ActorLogging{

  override def receive: Receive = {
    case Password.CheckPassword(id, pp, np) =>
//    if(listOfStudents.contains(id)){
//      listOfStudents -= id
//      listOfStudents += id -> np
//      println(listOfStudents)
      if(np.matches("[a-zA-Z0-9]{8,}")){

        sender() ! Password.Changed(true, id, pp, np)
      }
      else{
        sender() ! Password.Changed(false, id, pp, np)
      }
  }
}
