package com.machinomy.tasker

import java.net.URI
import java.util.UUID

import akka.actor._
import com.machinomy.bergae.Node
import com.machinomy.bergae.storage.Storage
import com.machinomy.bergae.storage.Storage.{Operation, SimpleMessage}
import spray.http.MediaTypes._
import spray.http.{StatusCode, StatusCodes, Uri}
import spray.routing.HttpService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class TaskerServiceActor(storage: Storage, node: ActorRef) extends Actor with HttpService {
  implicit val system = context.system
  implicit val executionContext: ExecutionContext = system.dispatcher
  override def receive: Receive = runRoute(routes)
  override implicit def actorRefFactory: ActorRefFactory = context

  def all(): Future[String] = storage.all().map { uuids => views.html.index(uuids).toString }

  val routes = pathPrefix("") {
    pathEndOrSingleSlash {
      get {
        respondWithMediaType(`text/html`) {
          onComplete(all()) {
            case Success(string) =>
              complete(string)
            case Failure(err) =>
              complete(err)
          }
        }
      }
    }
  } ~
  pathPrefix("tasks" / JavaUUID) { uuid =>
    get {
      respondWithMediaType(`text/html`) {
        val response = storage.get(uuid).map { operations => views.html.show(operations).toString }
        complete(response)
      }
    }
  } ~
  pathPrefix("tasks" / "add") {
    get {
      respondWithMediaType(`text/html`) {
        val response = views.html.add().toString
        complete(response)
      }
    }
  } ~
  pathPrefix("tasks") {
    pathEndOrSingleSlash {
      post {
        formFields('text.as[String]) { text =>
          respondWithMediaType(`text/plain`) {
            val uuid = UUID.randomUUID()
            val simpleMessage = SimpleMessage(text)
            node ! Node.Update(uuid, simpleMessage)
            redirect(Uri(s"/tasks/$uuid"), StatusCodes.Found)
          }
        }
      }
    }
  }
}

object TaskerServiceActor {
  def props(storage: Storage, node: ActorRef) = Props(classOf[TaskerServiceActor], storage, node)
}
