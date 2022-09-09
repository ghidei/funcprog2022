package funcprog

import funcprog.config.AppConfig
import funcprog.database.Database
import funcprog.routes.ElectionServer
import funcprog.validation.ValidationService

import zio.*

object Main extends ZIOAppDefault {

  override val run =
    App.program.provide(
      HttpServerSettings.default,
      AppConfig.live,
      ElectionServer.live,
      ElectionService.live,
      Database.live,
      ValidationService.live
    )

}
