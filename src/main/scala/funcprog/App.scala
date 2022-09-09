package funcprog

import funcprog.config.AppConfig
import funcprog.routes.ElectionServer

import zio.*

import zhttp.service.Server

object App {

  def program = ZIO.scoped {
    for {
      config  <- ZIO.service[AppConfig]
      httpApp <- ElectionServer.httpApp
      start   <- Server(httpApp).withBinding(config.http.host, config.http.port).make.orDie
      _       <- ZIO.logInfo(s"Server started on port: ${start.port}")
      _       <- ZIO.never
    } yield ()
  }

}
