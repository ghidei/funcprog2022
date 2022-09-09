package funcprog

import funcprog.ElectionService.*
import funcprog.database.*
import funcprog.model.*
import funcprog.validation.*

import zio.*
import zio.json.*

import sttp.tapir.Schema

trait ElectionService {
  def createParty(party: Party): ZIO[Any, Nothing, Party]
  def registerVote(vote: Vote): ZIO[Any, Error.InvalidInput, Unit]
  def getVote(nationalIdNumber: Nin): ZIO[Any, Error.NotFound, Vote]
}

object ElectionService {

  lazy val live: ZLayer[Database & ValidationService, Nothing, ElectionService] = ???

  final case class ElectionServiceLive(
    database: Database,
    validationService: ValidationService
  ) extends ElectionService {

    // TODO
    override def createParty(party: Party): ZIO[Any, Nothing, Party] =
      ???

    // TODO
    // 1. Log entry
    // 2. Validate the vote using the external vote validation service
    // 3. Handle the response from the validation service
    override def registerVote(vote: Vote): ZIO[Any, Error.InvalidInput, Unit] = {

      def validateVote(vote: Vote): ZIO[Any, Nothing, ValidationResponse] = ???

      def handleResponse(response: ValidationResponse): ZIO[Any, Error.InvalidInput, Unit] = ???

      for {
        _        <- ZIO.logInfo(s"Registering vote: $vote")
        response <- validateVote(vote)
        _        <- handleResponse(response)
      } yield ()

    }

    // TODO
    override def getVote(nationalIdNumber: Nin): ZIO[Any, Error.NotFound, Vote] =
      ???
  }

  sealed trait Error
  object Error {
    implicit lazy val codec: JsonCodec[Error] = DeriveJsonCodec.gen

    case class InvalidInput(error: String) extends Error
    object InvalidInput {
      implicit lazy val codec: JsonCodec[InvalidInput] = DeriveJsonCodec.gen
      implicit lazy val schema: Schema[InvalidInput]   = Schema.derived
    }

    case class NotFound(message: String) extends Error
    object NotFound {
      implicit lazy val codec: JsonCodec[NotFound] = DeriveJsonCodec.gen
      implicit lazy val schema: Schema[NotFound]   = Schema.derived
    }
  }

}
