package funcprog

import zio.*

import io.netty.channel.{ChannelFactory, ServerChannel}
import zhttp.service.EventLoopGroup
import zhttp.service.server.ServerChannelFactory

object HttpServerSettings {
  type HttpServerSettings = ChannelFactory[ServerChannel] & EventLoopGroup
  lazy val default: ZLayer[Any, Nothing, HttpServerSettings] = EventLoopGroup.auto(0) ++ ServerChannelFactory.auto
}
