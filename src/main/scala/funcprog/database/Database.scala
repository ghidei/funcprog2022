package funcprog.database

import java.time.Instant

import org.flywaydb.core.Flyway

import funcprog.config.*
import funcprog.model.*

import zio.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*

import doobie.*
import doobie.hikari.*
import doobie.implicits.*
import doobie.implicits.legacy.instant.*
import doobie.util.ExecutionContexts

trait Database {
  def insertParty(party: Party): ZIO[Any, UniqueConstraintViolation, Party]
  def insertVote(vote: Vote): ZIO[Any, Nothing, Unit]
  def getVote(nationalIdNumber: Nin): ZIO[Any, Nothing, Option[VoteRecord]]

  private[database] def deleteAllRows: ZIO[Any, Nothing, Unit]
}

object Database {

  def insertParty(party: Party): ZIO[Database, UniqueConstraintViolation, Party] =
    ZIO.serviceWithZIO[Database](_.insertParty(party))

  def insertVote(vote: Vote): ZIO[Database, Nothing, Unit] = ZIO.serviceWithZIO[Database](_.insertVote(vote))

  def getVote(nationalIdNumber: Nin): ZIO[Database, Nothing, Option[VoteRecord]] =
    ZIO.serviceWithZIO[Database](_.getVote(nationalIdNumber))

  def deleteAllRows: ZIO[Database, Nothing, Unit] =
    ZIO.serviceWithZIO[Database](_.deleteAllRows)

  lazy val live: ZLayer[DatabaseConfig, Throwable, Database] = ZLayer.scoped {
    for {
      config     <- ZIO.service[DatabaseConfig]
      _          <- loadAndMigrateFlyway(config)
      ec         <- ExecutionContexts.fixedThreadPool[Task](32).toScopedZIO
      transactor <- HikariTransactor
                      .newHikariTransactor[Task](
                        config.driver,
                        config.url,
                        config.user,
                        config.password,
                        ec
                      )
                      .toScopedZIO
      database    = DatabaseLive(transactor)
      _ <- database.deleteAllRows // Delete all rows to get a fresh DB each start
    } yield database

  }

  case class DatabaseLive(transactor: HikariTransactor[Task]) extends Database {

    override def insertParty(party: Party): ZIO[Any, UniqueConstraintViolation, Party] = {
      val transaction = for {
        _     <- SQL.insertParty(party).run
        party <- SQL.getParty(party).unique
      } yield party

      transaction
        .transact(transactor)
        .catchAll {
          case e: org.postgresql.util.PSQLException if e.getMessage.contains("""unique constraint "party_pkey"""") =>
            ZIO.fail(UniqueConstraintViolation(e.getMessage))

          case e =>
            ZIO.die(e)
        }
    }

    override def insertVote(vote: Vote): ZIO[Any, Nothing, Unit] =
      Clock.instant.flatMap { now =>
        val transaction = for {
          _ <- SQL.insertPerson(vote.person).run
          _ <- SQL.insertVote(vote, now).run
        } yield ()

        transaction.transact(transactor).orDie
      }

    override def getVote(nationalIdNumber: Nin): ZIO[Any, Nothing, Option[VoteRecord]] =
      SQL
        .getVote(nationalIdNumber)
        .option
        .transact(transactor)
        .orDie

    override private[database] def deleteAllRows: ZIO[Any, Nothing, Unit] =
      SQL.deleteAllRows.run.transact(transactor).unit.orDie

  }

  object SQL {

    def getParty(party: Party): Query0[Party] =
      sql"""SELECT *
            FROM party
            WHERE party_name = ${PartyName.unwrap(party.partyName)}
        """
        .query[String]
        .map(partyName => Party(PartyName(partyName)))

    def getVote(nationalIdNumber: Nin): Query0[VoteRecord] =
      sql"""SELECT pe.firstname, pe.lastname, vo.national_identification_number, pa.party_name, vo.created_at
            FROM vote vo
            INNER JOIN person pe ON vo.national_identification_number = pe.national_identification_number
            INNER JOIN party pa ON pa.party_name = vo.party_name
            WHERE vo.national_identification_number = ${Nin.unwrap(nationalIdNumber)}"""
        .query[(String, String, String, String, Instant)]
        .map { case (firstname, lastname, nationalIdNumber, partyName, createdAt) =>
          VoteRecord(
            Person(Firstname(firstname), Lastname(lastname), Nin(nationalIdNumber)),
            Party(PartyName(partyName)),
            createdAt
          )
        }

    def insertParty(party: Party): Update0 =
      sql"""INSERT INTO party
              (party_name)
            VALUES (${PartyName.unwrap(party.partyName)})
        """.update

    def insertPerson(person: Person): Update0 =
      sql"""INSERT INTO person
              (
                firstname,
                lastname,
                national_identification_number
              )
            VALUES (
              ${Firstname.unwrap(person.firstname)},
              ${Lastname.unwrap(person.lastname)},
              ${Nin.unwrap(person.nationalIdNumber)}
            )
        """.update

    def insertVote(vote: Vote, now: Instant): Update0 =
      sql"""INSERT INTO vote
              (national_identification_number, party_name, created_at)
            VALUES (${Nin.unwrap(vote.person.nationalIdNumber)}, ${PartyName.unwrap(vote.party.partyName)}, $now)
        """.update

    def deleteAllRows =
      sql"""
        TRUNCATE party, person, vote;
      """.update

  }

  def loadAndMigrateFlyway(config: DatabaseConfig): Task[Unit] =
    for {
      flyway <- ZIO.attempt {
                  Flyway
                    .configure()
                    .dataSource(config.url, config.user, config.password)
                    .load()
                }
      _      <- ZIO.attempt(flyway.migrate())
    } yield ()

}
