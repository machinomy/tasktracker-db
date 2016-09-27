package com.machinomy.tasker

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout

import scala.concurrent.duration._
import com.machinomy.bergae.Bergae
import com.machinomy.bergae.storage.{RedisStorage, Storage}
import spray.can.Http
import akka.pattern.ask

object Main extends App {
  case class Arguments(config: File = new File("./application.json"))

  implicit val system = ActorSystem("tasker")

  val configuration: Configuration = parse(args) match {
    case Some(arguments) =>
      Configuration.load(arguments.config)
    case None =>
      Configuration.load()
  }

  val storage = new RedisStorage(configuration.redis)
  val node = Bergae.node(configuration.node, storage, system)

  configuration.httpOpt.foreach { httpConfiguration =>
    val service = system.actorOf(TaskerServiceActor.props(storage, node), "tasker-service")
    implicit val timeout = Timeout(5.seconds)
    IO(Http) ? Http.Bind(service, interface = httpConfiguration.host, port = httpConfiguration.port)
  }


  def parse(args: Array[String]): Option[Arguments] = {
    val parser = new scopt.OptionParser[Arguments]("bergae") {
      head("tasker")

      opt[File]('c', "config")
        .required()
        .valueName("<file>")
        .action((f, c) => c.copy(config = f))
        .text("config is a required property")
    }
    parser.parse(args, Arguments())
  }
}
