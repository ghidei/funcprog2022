package funcprog.model

import zio.json.*

import sttp.tapir.Schema

final case class VoteResult(partyName: String, percentage: Int)

object VoteResult {
  implicit val jsonCodec: JsonCodec[VoteResult] = DeriveJsonCodec.gen
  implicit val schema: Schema[VoteResult]       = Schema.derived
}
