package com.example.akka.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import akka.routing.FromConfig
import com.typesafe.config.Config
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.Random

class FrontEnd extends Actor {

  private val logger = LoggerFactory getLogger FrontEnd.getClass

  val backend = context.actorOf(FromConfig.props(), name = "backendRouter")
  import context.dispatcher
  context.system.scheduler.schedule(1 seconds, 1 seconds, self, createRequestMessage)

  private def createRequestMessage = {
    Quote(Random nextInt 20)
  }

  def receive = {
    case quote:Quote => logger info s"FrontEnd received the message $quote so forwarding it to BackEnd"
      backend forward quote
    case _ => logger warn "Discarding unsupported message"
  }

}



object FrontEnd {

  private val logger = LoggerFactory getLogger FrontEnd.getClass

  private[this] val props = Props[FrontEnd]
  /*
  private[this] var frontEnd: Option[(ActorRef, ActorSystem)] = None
  def instance = frontEnd.getOrElse(throw new UninitializedError)*/

  def create(nameOfActorSystem:String, baseConfiguration: Config):(ActorRef, ActorSystem) = {
    var actorRef:ActorRef = null
    val actorSystem = ActorSystem(nameOfActorSystem, baseConfiguration)
    logger info s"Registering a front-end for cluster of actor system ${actorSystem.name}"
    Cluster(actorSystem) registerOnMemberUp {
      actorRef = actorSystem.actorOf(props, name = "frontend")
    }
    (actorRef, actorSystem)
  }

}
