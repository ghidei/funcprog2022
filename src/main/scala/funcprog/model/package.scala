package funcprog

import java.net.{URLDecoder, URLEncoder}

import zio.json.*
import zio.prelude.*

import sttp.tapir.*
import sttp.tapir.CodecFormat.TextPlain

package object model {

  type Firstname = Firstname.Type
  object Firstname extends Subtype[String] {
    implicit lazy val jsonCodec: JsonCodec[Firstname] = derive[JsonCodec]
    implicit lazy val schema: Schema[Firstname]       = derive[Schema]
  }

  type Lastname = Lastname.Type
  object Lastname extends Subtype[String] {
    implicit lazy val jsonCodec: JsonCodec[Lastname] = derive[JsonCodec]
    implicit lazy val schema: Schema[Lastname]       = derive[Schema]
  }

  type Nin = Nin.Type
  object Nin extends Subtype[String] {
    implicit lazy val jsonCodec: JsonCodec[Nin]            = derive[JsonCodec]
    implicit lazy val schema: Schema[Nin]                  = derive[Schema]
    implicit lazy val codec: Codec[String, Nin, TextPlain] =
      derive[Codec[String, *, TextPlain]](
        Codec.string.map(URLDecoder.decode(_, "UTF-8"))(URLEncoder.encode(_, "UTF-8"))
      )
  }

  type PartyName = PartyName.Type

  object PartyName extends Subtype[String] {
    implicit lazy val jsonCodec: JsonCodec[PartyName] = derive[JsonCodec]
    implicit lazy val schema: Schema[PartyName]       = derive[Schema]
  }

}
