package funcprog

import com.typesafe.config.ConfigFactory

import zio.*
import zio.config.*
import zio.config.magnolia.*
import zio.config.syntax.*
import zio.config.typesafe.TypesafeConfigSource.fromTypesafeConfig
import zio.prelude.*

package object config {

  type ConfigEnv = AppConfig & HttpConfig & DatabaseConfig

  final case class AppConfig(
    database: DatabaseConfig,
    http: HttpConfig
  )

  object AppConfig {
    private lazy val appConfigLayer: ZLayer[Any, Nothing, AppConfig] = ZLayer {
      val getTypesafeConfig = ZIO.attempt(ConfigFactory.load.resolve)
      val getConfig         = read(descriptor[AppConfig].from(fromTypesafeConfig(getTypesafeConfig)))
      getConfig.orDie
    }

    val live: ZLayer[Any, Nothing, ConfigEnv] = ZLayer.make[ConfigEnv](
      appConfigLayer,
      appConfigLayer.narrow(_.database),
      appConfigLayer.narrow(_.http)
    )
  }

  // Http config

  case class HttpConfig(
    host: Host,
    port: Port
  )

  type Host = Host.Type
  object Host extends Subtype[String] {
    implicit lazy val d: Descriptor[Host] = derive(implicitly[Descriptor[String]])
  }

  type Port = Port.Type
  object Port extends Subtype[Int] {
    implicit lazy val d: Descriptor[Port] = derive(implicitly[Descriptor[Int]])
  }

  // Database config

  case class DatabaseConfig(
    driver: DbDriver,
    password: DbPassword,
    url: DbUrl,
    user: DbUser
  )

  type DbDriver = DbDriver.Type
  object DbDriver extends Subtype[String] {
    implicit lazy val d: Descriptor[DbDriver] = derive(implicitly[Descriptor[String]])
  }

  type DbPassword = DbPassword.Type
  object DbPassword extends Subtype[String] {
    implicit lazy val d: Descriptor[DbPassword] = derive(implicitly[Descriptor[String]])
  }

  type DbUrl = DbUrl.Type
  object DbUrl extends Subtype[String] {
    implicit lazy val d: Descriptor[DbUrl] = derive(implicitly[Descriptor[String]])
  }

  type DbUser = DbUser.Type
  object DbUser extends Subtype[String] {
    implicit lazy val d: Descriptor[DbUser] = derive(implicitly[Descriptor[String]])
  }

}
