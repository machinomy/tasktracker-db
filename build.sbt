addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

name := "tasker"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

mainClass := Some("com.machinomy.tasker.Main")

resolvers ++= Seq(
  "Machinomy Release" at "http://artifactory.machinomy.com/artifactory/release",
  "Machinomy Snapshot" at "http://artifactory.machinomy.com/artifactory/snapshot"
)

val sprayV = "1.3.3"

libraryDependencies ++= Seq(
  "com.machinomy" %% "bergae" % "0.0.2-SNAPSHOT",
  "com.github.scopt" %% "scopt" % "3.5.0",
  "com.typesafe" % "config" % "1.3.0",
  "io.spray"            %%  "spray-can"     % sprayV,
  "io.spray"            %%  "spray-routing-shapeless2" % sprayV
)

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.startsWith("META-INF") => MergeStrategy.discard
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList("org", "apache", xs @ _*) => MergeStrategy.first
  case PathList("org", "jboss", xs @ _*) => MergeStrategy.first
  case "about.html"  => MergeStrategy.rename
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}
}

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)
