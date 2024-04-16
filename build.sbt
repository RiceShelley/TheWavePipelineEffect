ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.12.18"
ThisBuild / organization := "org.example"

////////////////////////////////////////////////////////////
// Spinal HDL
////////////////////////////////////////////////////////////
val spinalVersion = "1.9.4"
val spinalCore = "com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion
val spinalLib = "com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion
val spinalIdslPlugin = compilerPlugin(
  "com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion
)

////////////////////////////////////////////////////////////
// Helper libraries
////////////////////////////////////////////////////////////
val bitsLib = "com.tomgibara.bits" % "bits" % "2.1.0"
val jSerialComm = "com.fazecast" % "jSerialComm" % "[2.0.0,3.0.0)"
val scalaChart =
  "com.github.wookietreiber" %% "scala-chart" % "latest.integration"
val scalaTest = "org.scalatest" %% "scalatest" % "3.2.14"
val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.1"

// warnings for unused imports
scalacOptions ++= Seq(
  "-Ywarn-unused-import",
  "-Xfatal-warnings",
  "-deprecation"
)

// from https://stackoverflow.com/questions/24396407/how-to-display-entire-stack-trace-for-thrown-exceptions-from-scalacheck-tests
Test / testOptions += Tests.Argument(
  TestFrameworks.ScalaCheck,
  "-verbosity",
  "2"
)

lazy val wavepipeline = (project in file("."))
  .settings(
    Compile / scalaSource := baseDirectory.value / "src",
    libraryDependencies ++= Seq(
      // SpinalHDL
      spinalCore,
      spinalLib,
      spinalIdslPlugin,
      // Helper libraries
      bitsLib,
      jSerialComm,
      scalaChart,
      scalaTest,
      scalaCheck
    )
  )

fork := true
