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
      ???
    } @@ ignore,
    test("should fail with UniqueConstraintViolation if inserting twice") {
      ???
    } @@ ignore,
    test("should be able to insert vote") {
      ???
    } @@ ignore,
    test("should be able to insert and get vote") {
      ???
    } @@ ignore
  ) @@ timed

}
