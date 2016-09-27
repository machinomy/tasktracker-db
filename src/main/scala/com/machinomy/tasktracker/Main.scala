package com.machinomy.tasktracker

import com.typesafe.config.ConfigFactory

import akka.actor.{ActorRef, ActorSystem}

import com.machinomy.bergae.Bergae
import com.machinomy.bergae.configuration.{XicityNodeConfiguration, RedisStorageConfiguration}
import com.machinomy.bergae.storage.{RedisStorage, Storage}
import io.circe._
import io.circe.generic.JsonCodec
import io.circe.generic.auto._
import io.circe.parser
import io.circe.syntax._

object Domain {

  @JsonCodec
  sealed trait Action extends Storage.Operation

  case class AddSomething(smth: String) extends Action

  object Action
}

class JsonSerializer extends Storage.Serializable[Domain.Action] {
  def serialize(operation: Domain.Action): String = {
    operation.asJson.noSpaces
  }

  def deserialize(str: String): Domain.Action = {
    parser.decode[Domain.Action](str).toOption.get
  }
}

object Main extends App {

  val config = ConfigFactory.load()

  implicit val system = ActorSystem("bergae")
  implicit val serializer = new JsonSerializer()

  val nodeConfig = XicityNodeConfiguration(config.getConfig("node"))
  val redisConfig = RedisStorageConfiguration(config.getConfig("redis"))
  val redisStorage = new RedisStorage[Domain.Action](redisConfig)

  val node = Bergae.node(nodeConfig, redisStorage)

}
