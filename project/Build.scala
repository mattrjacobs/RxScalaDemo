import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform._

object RxScalaDemoBuild extends Build {
  import Resolvers._

  lazy val buildSettings = Seq(
    organization := "com.mattrjacobs",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.10.2"
  )
  
  lazy val demo = Project(
    id = "rxScalaDemo",
    base = file("."),
    settings = defaultSettings)
  
  override lazy val settings = super.settings ++ buildSettings ++ Seq(
    resolvers := Seq(scalaToolsRepo, sonatypeRepo, typesafeRepo),
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  )

  lazy val baseSettings = Defaults.defaultSettings

  lazy val defaultSettings = {
    import Dependency._
    baseSettings ++ formatSettings ++ Defaults.itSettings ++ Seq(
      resolvers := Seq(scalaToolsRepo, sonatypeRepo, typesafeRepo),
      scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
      libraryDependencies ++= Seq(Compile.jodaTime, Compile.jodaConvert, Compile.rxJavaCore, Compile.rxJavaScala, Compile.rxApacheHttp, Compile.hystrix, Test.specs2, Test.mockito),
      ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet
    )
  }

  lazy val formatSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test    := formattingPreferences,
    ScalariformKeys.preferences in IntegrationTest    := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentClassDeclaration, true)
  }
}

object Dependency {
  object V {
    val Hystrix = "1.3.6"
    val JodaConvert = "1.2"
    val JodaTime = "2.1"
    val Logback = "1.0.3"
    val Mockito = "1.9.0"
    val RxJava = "0.14.2"
    val Specs2 = "1.11"
    val Spray = "1.2-M8"
  }

  object Runtime {
    val logback =        "ch.qos.logback"            %  "logback-classic"   % V.Logback       
  }

  object Compile {
    val hystrix =        "com.netflix.hystrix"       %  "hystrix-core"      % V.Hystrix
    val jodaConvert =    "org.joda"                  %  "joda-convert"      % V.JodaConvert
    val jodaTime =       "joda-time"                 %  "joda-time"         % V.JodaTime
    val rxApacheHttp =   "com.netflix.rxjava"        %  "rxjava-apache-http" % V.RxJava
    val rxJavaCore =     "com.netflix.rxjava"        %  "rxjava-core"       % V.RxJava
    val rxJavaScala =    "com.netflix.rxjava"        %  "rxjava-scala"      % V.RxJava
  }

  object Test {
    val logbackTest =    "ch.qos.logback"            %  "logback-classic"   % V.Logback       % "test"
    val mockito =        "org.mockito"               %  "mockito-all"       % V.Mockito       % "test"
    val specs2 =         "org.specs2"                %% "specs2"            % V.Specs2        % "test"
  }
}

object Resolvers {
  val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
  val scalaToolsRepo = "Scala Tools" at "http://scala-tools.org/repo-snapshots/"
  val typesafeRepo = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases"
}
