package com.example.akka.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.example.akka.util.FileReader
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

class BackEnd extends Actor {

  private val logger = LoggerFactory getLogger BackEnd.getClass

  private val quotes = FileReader.readClasspathFile("quotes.txt")

  override def receive: Receive = {
    case Quote(index) => logger info self+": "+quotes(index)
    case _ => logger warn "Received an unsupported message"
  }

}

object BackEnd {
  private val logger = LoggerFactory getLogger BackEnd.getClass

  private val props = Props[BackEnd]

  def create(nameOfActorSystem:String, baseConfiguration: Config, portNumber: Int*): Seq[(ActorRef, ActorSystem)] = {
    def createInstance(portNumber:Int):(ActorRef, ActorSystem) = {
      val configuration = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$portNumber").withFallback(baseConfiguration)
      val actorSystem = ActorSystem(nameOfActorSystem, configuration)
      logger info s"Creating a backend-end child actor for the actor system ${actorSystem.name}"
      (actorSystem.actorOf(props, name = "backend"), actorSystem)
    }
    portNumber.map(createInstance)
  }


}
