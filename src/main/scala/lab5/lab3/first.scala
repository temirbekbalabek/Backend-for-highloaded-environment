//package lab5
//
//import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
//import lab5.DeviceManager.RequestTrackDevice
//
//object Device {
//  def props(groupId: String, deviceId: String): Props = Props(new Device(groupId, deviceId))
//
//  final case class RecordTemperature(requestId: Long, value: Double)
//  final case class TemperatureRecorded(requestId: Long)
//
//  final case class ReadTemperature(requestId: Long)
//  final case class RespondTemperature(requestId: Long, value: Option[Double])
//}
//
//class Device(groupId: String, deviceId: String) extends Actor with ActorLogging {
//  import Device._
//
//  var lastTemperatureReading: Option[Double] = None
//
//  override def preStart(): Unit = log.info("Device actor {}-{} started", groupId, deviceId)
//

//  override def postStop(): Unit = log.info("Device actor {}-{} stopped", groupId, deviceId)
//
//  override def receive: Receive = {
//    case DeviceManager.RequestTrackDevice(`groupId`, `deviceId`) ⇒
//      sender() ! DeviceManager.DeviceRegistered
//
//    case DeviceManager.RequestTrackDevice(groupId, deviceId) ⇒
//      log.warning(
//        "Ignoring TrackDevice request for {}-{}.This actor is responsible for {}-{}.",
//        groupId, deviceId, this.groupId, this.deviceId
//      )
//
//    case RecordTemperature(id, value) ⇒
//      log.info("Recorded temperature reading {} with {}", value, id)
//      lastTemperatureReading = Some(value)
//      sender() ! TemperatureRecorded(id)
//
//    case ReadTemperature(id) ⇒
//      sender() ! RespondTemperature(id, lastTemperatureReading)
//  }
//}
//
//
//
//object DeviceGroup {
//  def props(groupId: String): Props = Props(new DeviceGroup(groupId))
//
//  final case class RequestDeviceList(requestId: Long)
//  final case class ReplyDeviceList(requestId: Long, ids: Set[String])
//}
//
//class DeviceGroup(groupId: String) extends Actor with ActorLogging {
//  var deviceIdToActor = Map.empty[String, ActorRef]
//  var actorToDeviceId = Map.empty[ActorRef, String]
//
//  override def preStart(): Unit = log.info("DeviceGroup {} started", groupId)
//
//  override def postStop(): Unit = log.info("DeviceGroup {} stopped", groupId)
//
//  override def receive: Receive = {
//    case trackMsg @ RequestTrackDevice(`groupId`, _) ⇒
//      deviceIdToActor.get(trackMsg.deviceId) match {
//        case Some(deviceActor) ⇒
//          deviceActor forward trackMsg
//        case None ⇒
//          log.info("Creating device actor for {}", trackMsg.deviceId)
//          val deviceActor = context.actorOf(Device.props(groupId, trackMsg.deviceId), s"device-${trackMsg.deviceId}")
//          context.watch(deviceActor)
//          actorToDeviceId += deviceActor -> trackMsg.deviceId
//          deviceIdToActor += trackMsg.deviceId -> deviceActor
//          deviceActor forward trackMsg
//      }
//
//    case RequestTrackDevice(groupId, deviceId) ⇒
//      log.warning(
//        "Ignoring TrackDevice request for {}. This actor is responsible for {}.",
//        groupId, this.groupId
//      )
//
//    case RequestDeviceList(requestId) ⇒
//      sender() ! ReplyDeviceList(requestId, deviceIdToActor.keySet)
//
//    case Terminated(deviceActor) ⇒
//      val deviceId = actorToDeviceId(deviceActor)
//      log.info("Device actor for {} has been terminated", deviceId)
//      actorToDeviceId -= deviceActor
//      deviceIdToActor -= deviceId
//
//  }
//}
//
//
//
//object DeviceManager {
//  def props(): Props = Props(new DeviceManager)
//
//  final case class RequestTrackDevice(groupId: String, deviceId: String)
//  case object DeviceRegistered
//}
//
//class DeviceManager extends Actor with ActorLogging {
//  var groupIdToActor = Map.empty[String, ActorRef]
//  var actorToGroupId = Map.empty[ActorRef, String]
//
//  override def preStart(): Unit = log.info("DeviceManager started")
//
//  override def postStop(): Unit = log.info("DeviceManager stopped")
//
//  override def receive = {
//    case trackMsg @ RequestTrackDevice(groupId, _) ⇒
//      groupIdToActor.get(groupId) match {
//        case Some(ref) ⇒
//          ref forward trackMsg
//        case None ⇒
//          log.info("Creating device group actor for {}", groupId)
//          val groupActor = context.actorOf(DeviceGroup.props(groupId), "group-" + groupId)
//          context.watch(groupActor)
//          groupActor forward trackMsg
//          groupIdToActor += groupId -> groupActor
//          actorToGroupId += groupActor -> groupId
//      }
//
//    case Terminated(groupActor) ⇒
//      val groupId = actorToGroupId(groupActor)
//      log.info("Device group actor for {} has been terminated", groupId)
//      actorToGroupId -= groupActor
//      groupIdToActor -= groupId
//
//  }
//
//}
//
//
//
//
//final case class RequestAllTemperatures(requestId: Long)
//final case class RespondAllTemperatures(requestId: Long, temperatures: Map[String, TemperatureReading])
//
//sealed trait TemperatureReading
//final case class Temperature(value: Double) extends TemperatureReading
//case object TemperatureNotAvailable extends TemperatureReading
//case object DeviceNotAvailable extends TemperatureReading
//case object DeviceTimedOut extends TemperatureReading
//
