package funcprog.validation

import funcprog.database.Database
import funcprog.model.*

import zio.*

trait ValidationService {
  def validateVote(vote: Vote): ZIO[Any, ValidationError, ValidationResponse]
}

object ValidationService {

  lazy val live: ZLayer[Database, Nothing, ValidationService] = ZLayer {
    for {
      database <- ZIO.service[Database]
    } yield ValidationServiceLive(database)
  }

  final case class ValidationServiceLive(database: Database) extends ValidationService {
    override def validateVote(vote: Vote): IO[ValidationError, ValidationResponse] =
      for {
        _ <- ZIO.logInfo(s"Validating vote: $vote...")
      } yield ValidationResponse.Valid
  }
}
