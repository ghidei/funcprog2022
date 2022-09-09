package funcprog.database

import com.dimafeng.testcontainers.PostgreSQLContainer

import funcprog.Generators
import funcprog.config.*

import zio.*
import zio.test.*
import zio.test.TestAspect.*

import io.github.scottweaver.zio.testcontainers.postgres.*

object DatabaseSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment, Throwable] =
    databaseSuite
      .provideSome(
        Database.live,
        databaseLayer,
        ZPostgreSQLContainer.live,
        ZPostgreSQLContainer.Settings.default
      )

  lazy val databaseLayer: ZLayer[PostgreSQLContainer, Nothing, DatabaseConfig] = {
    val cfg = ZIO.serviceWith[PostgreSQLContainer] { container =>
      DatabaseConfig(
        DbDriver(container.container.getDriverClassName()),
        DbPassword(container.container.getPassword()),
        DbUrl(container.container.getJdbcUrl()),
        DbUser(container.container.getUsername())
      )
    }
    ZLayer(cfg)
  }

  lazy val databaseSuite = suite("DatabaseSpec")(
    test("should be able to insert party") {
      check(Generators.genParty) { party =>
        for {
          _ <- Database.insertParty(party)
          _ <- Database.deleteAllRows
        } yield assertCompletes
      }
    },
    test("should fail with UniqueConstraintViolation if inserting twice") {
      check(Generators.genParty) { party =>
        for {
          _   <- Database.insertParty(party)
          res <- Database.insertParty(party).either
          _   <- Database.deleteAllRows
        } yield assertTrue(res.isLeft)
      }
    },
    test("should be able to insert vote") {
      check(Generators.genVote) { vote =>
        for {
          _ <- Database.insertParty(vote.party)
          _ <- Database.insertVote(vote)
          _ <- Database.deleteAllRows
        } yield assertCompletes
      }
    },
    test("should be able to insert and get vote") {
      check(Generators.genVote) { vote =>
        for {
          _      <- Database.insertParty(vote.party)
          _      <- Database.insertVote(vote)
          actual <- Database.getVote(vote.person.nationalIdNumber)
          _      <- Database.deleteAllRows
        } yield assertTrue(vote == actual.get.toVote)
      }
    }
  ) @@ timed

}
