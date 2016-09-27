addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

import AssemblyKeys._

assemblySettings

name := "project_name"

version := "0.0.1"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Machinomy Release" at "http://artifactory.machinomy.com/artifactory/release",
  "Machinomy Snapshot" at "http://artifactory.machinomy.com/artifactory/snapshot"
)

libraryDependencies ++= Seq(
  "com.machinomy" %% "bergae" % "0.0.3-SNAPSHOT",
  "com.typesafe" % "config" % "1.3.0"
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
