package model

object Color extends Enumeration {
  type Color = Value

  val Red, Orange, Green = Value;
}
//  val Red, Orange, Green = Value;

  // Make Enum to string
//  implicit val myEnumMapper = MappedColumnType.base[Color, String](
//    e => e.toString,
//    s => Color.withName(s)
//  )



