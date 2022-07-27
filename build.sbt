ThisBuild / scalaVersion     := "3.1.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "tapir-schema-issue",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.0",
      "dev.zio" %% "zio-test" % "2.0.0" % Test,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % "1.0.2",
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "1.0.2",
      "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.2.1",
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
