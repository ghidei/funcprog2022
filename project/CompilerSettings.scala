object CompilerSettings {

  val strict = true

  lazy val compilerSettings =
    if (strict) stdSettings ++ strictSettings
    else stdSettings

  private lazy val strictSettings = Seq(
    "-Xfatal-warnings",
    "-Ywarn-unused",
    "-Wunused:imports",
    "-Wunused:patvars",
    "-Wunused:privates",
    "-Wvalue-discard",
  )

  private lazy val stdSettings = Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:higherKinds",
    "-language:existentials",
    "-unchecked",
    "-Ywarn-macros:after",
    "-Xsource:3",
    "-Ymacro-annotations",
  )

}
