package lab7


import lab7.models.Phone
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{ProvenShape, Tag}

class PhonesTable(tag: Tag) extends Table[Phone](tag, "Phones") {
  // This is the primary key column:
  def id: Rep[Int] = column[Int]("SUP_ID", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("SUP_NAME")
  def displaySize: Rep[String] = column[String]("SUP_DISPLAYSIZE")
  def camera: Rep[String] = column[String]("SUP_CAMERA")
  def price: Rep[String] = column[String]("SUP_PRICE")
  def memory: Rep[String] = column[String]("SUP_MEMORY")
  def * : ProvenShape[Phone] = (id.?, name, displaySize, camera,
  price, memory) <> (Phone.tupled, Phone.unapply)
}