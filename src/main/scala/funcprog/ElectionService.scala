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

  lazy val live: ZLayer[Database & ValidationService, Nothing, ElectionService] = ZLayer {
    for {
      database          <- ZIO.service[Database]
      validationService <- ZIO.service[ValidationService]
    } yield ElectionServiceLive(database, validationService)
  }

  final case class ElectionServiceLive(
    database: Database,
    validationService: ValidationService
  ) extends ElectionService {

    override def createParty(party: Party): ZIO[Any, Nothing, Party] =
      database
        .insertParty(party)
        .tap(inserted => ZIO.logInfo(s"Created party: $inserted"))
        .catchAll { e =>
          ZIO.logInfo(s"Got unique constraint violation error: $e. Ignoring...").as(party)
        }

    override def registerVote(vote: Vote): ZIO[Any, Error.InvalidInput, Unit] = {

      def validateVote(vote: Vote) =
        validationService
          .validateVote(vote)
          .retry(
            Schedule.recurs(5) && Schedule.spaced(10.millis) && Schedule.recurWhile(_ == ValidationError.Temporary)
          )
          .tapError(e => ZIO.logError(s"Failed to validate vote: $vote with error: $e"))
          .orDie

      def handleResponse(response: ValidationResponse) = response match {
        case ValidationResponse.Valid   =>
          ZIO.logInfo(s"Vote: $vote is valid. Inserting.") *> database.insertVote(vote)
        case ValidationResponse.Invalid =>
          ZIO.fail(Error.InvalidInput(s"Invalid vote: $vote"))
      }

      for {
        _        <- ZIO.logInfo(s"Registering vote: $vote")
        response <- validateVote(vote)
        _        <- handleResponse(response)
      } yield ()

    }

    override def getVote(nationalIdNumber: Nin): ZIO[Any, Error.NotFound, Vote] =
      database
        .getVote(nationalIdNumber)
        .someOrFail(Error.NotFound(s"Vote for $nationalIdNumber not found."))
        .map(_.toVote)
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
