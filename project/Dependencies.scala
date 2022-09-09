import sbt.*

object Dependencies {

  val DoobieVersion = "1.0.0-RC2"

  val LogbackVersion        = "1.2.11"
  val LogbackEncoderVersion = "4.11"

  val tapirVersion = "1.0.2"

  val ZIOVersion               = "2.0.1"
  val ZIOConfigVersion         = "3.0.1"
  val ZIOLoggingVersion        = "2.0.1"
  val ZIOTestContainersVersion = "0.8.0"

  lazy val database = Seq(
    "org.tpolecat" %% "doobie-core"           % DoobieVersion,
    "org.tpolecat" %% "doobie-hikari"         % DoobieVersion,
    "org.tpolecat" %% "doobie-postgres"       % DoobieVersion,
    "org.flywaydb"  % "flyway-core"           % "6.1.0"
  )

  lazy val logging = Seq(
    "ch.qos.logback" % "logback-core"    % LogbackVersion,
    "ch.qos.logback" % "logback-classic" % LogbackVersion,
    "org.slf4j"      % "slf4j-api"       % "1.7.36"
  )

  lazy val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"              % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"   % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion
  )

  lazy val zio = Seq(
    "io.d11"                %% "zhttp"                             % "2.0.0-RC10",
    "dev.zio"               %% "zio"                               % ZIOVersion,
    "io.github.scottweaver" %% "zio-2-0-db-migration-aspect"       % ZIOTestContainersVersion,
    "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % ZIOTestContainersVersion,
    "dev.zio"               %% "zio-config"                        % ZIOConfigVersion,
    "dev.zio"               %% "zio-config-typesafe"               % ZIOConfigVersion,
    "dev.zio"               %% "zio-config-magnolia"               % ZIOConfigVersion,
    "dev.zio"               %% "zio-interop-cats"                  % "3.3.0",
    "dev.zio"               %% "zio-json"                          % "0.3.0-RC10",
    "dev.zio"               %% "zio-mock"                          % "1.0.0-RC8",
    "dev.zio"               %% "zio-prelude"                       % "1.0.0-RC15",
    "dev.zio"               %% "zio-streams"                       % ZIOVersion,
    "dev.zio"               %% "zio-test"                          % ZIOVersion % Test,
    "dev.zio"               %% "zio-test-magnolia"                 % ZIOVersion % Test,
    "dev.zio"               %% "zio-test-sbt"                      % ZIOVersion % Test
  )

  lazy val dependencies = database ++ logging ++ tapir ++ zio

}
