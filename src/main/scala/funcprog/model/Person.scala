package funcprog.model

import zio.json.*

import sttp.tapir.Schema

final case class Person(
  firstname: Firstname,
  lastname: Lastname,
  nationalIdNumber: Nin
)

object Person {
  implicit val jsonCodec: JsonCodec[Person] = DeriveJsonCodec.gen
  implicit val schema: Schema[Person]       = Schema.derived
}
