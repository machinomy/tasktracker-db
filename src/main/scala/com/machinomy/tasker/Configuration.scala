package com.machinomy.tasker

import java.io.File

import com.machinomy.bergae.configuration.{NodeConfiguration, RedisStorageConfiguration, XicityNodeConfiguration}
import com.machinomy.tasker.Configuration.HttpConfiguration
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

class Configuration(val redis: RedisStorageConfiguration, val node: NodeConfiguration, val httpOpt: Option[HttpConfiguration])

object Configuration {
  class HttpConfiguration(val host: String, val port: Int)

  def load(file: File): Configuration = load(ConfigFactory.parseFile(file))

  def load(): Configuration = load(ConfigFactory.load())

  def load(config: Config): Configuration = {
    val nodeConfiguration = XicityNodeConfiguration.apply(config)
    val redisStorageConfiguration = RedisStorageConfiguration.apply(config.getConfig("redis"))
    val httpConfiguration = Try(config.getConfig("http")).toOption.map { httpConfiguration =>
      val httpName = httpConfiguration.getString("name")
      val httpPort = httpConfiguration.getInt("port")
      new HttpConfiguration(httpName, httpPort)
    }
    new Configuration(redisStorageConfiguration, nodeConfiguration, httpConfiguration)
  }
}
