package funcprog

import funcprog.HttpServerSettings.*
import funcprog.config.AppConfig
import funcprog.database.Database
import funcprog.routes.ElectionServer
import funcprog.validation.ValidationService

import zio.*

object Main extends ZIOAppDefault {

  lazy val liveLayer: ZLayer[Any, Nothing, AppConfig & ElectionServer & HttpServerSettings] = ???

  override val run =
    App.program.provide(
      liveLayer
    )

}
