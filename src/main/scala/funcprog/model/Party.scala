package funcprog.model

import zio.json.*

import sttp.tapir.Schema

final case class Party(partyName: PartyName)

object Party {
  implicit val jsonCodec: JsonCodec[Party] = DeriveJsonCodec.gen
  implicit val schema: Schema[Party]       = Schema.derived
}
