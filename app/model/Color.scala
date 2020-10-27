package model

import enumeratum._

// https://index.scala-lang.org/lloydmeta/enumeratum/enumeratum-circe/1.6.1?target=_2.13
sealed trait Color extends EnumEntry
object Color extends Enum[Color] {

  case object Red     extends Color
  case object Orange  extends Color
  case object Green   extends Color

  val values = findValues

//  val Red, Orange, Green = Value;

  // Make Enum to string
//  implicit val myEnumMapper = MappedColumnType.base[Color, String](
//    e => e.toString,
//    s => Color.withName(s)
//  )


}
