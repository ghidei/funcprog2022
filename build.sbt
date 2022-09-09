import CompilerSettings.*
import Dependencies.*

lazy val root = project
  .in(file("."))
  .settings(
    name                 := "zio-tour",
    version              := "0.1.0-SNAPSHOT",
    scalaVersion         := "2.13.8",
    scalacOptions        := compilerSettings,
    Test / fork          := true,
    compile / run / fork := true,
    libraryDependencies ++= dependencies,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
