package funcprog.model

import zio.json.*

import sttp.tapir.Schema

final case class Vote(
  person: Person,
  party: Party
)

object Vote {
  implicit val jsonCodec: JsonCodec[Vote] = DeriveJsonCodec.gen
  implicit val schema: Schema[Vote]       = Schema.derived
}
