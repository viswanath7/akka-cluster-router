package com.example.akka.application

import akka.actor.{ActorRef, ActorSystem}
import com.example.akka.actor.{BackEnd, FrontEnd}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.collection.immutable


object ClusterLoadBalancerApplication extends App {

  private val logger = LoggerFactory getLogger ClusterLoadBalancerApplication.getClass

  private[this] val actorSystemName = "ClusterSystem"

  def getBaseConfiguration(roleName:String): Config =
    ConfigFactory.parseString(s"akka.cluster.roles = [$roleName]")
    .withFallback(ConfigFactory.load().getConfig("LoadBalancer"))

  val frontEnd = FrontEnd create (actorSystemName, getBaseConfiguration("frontend"))
  val backEnds = BackEnd create (actorSystemName, getBaseConfiguration("backend"),2551, 2552, 2553)


  private val tuples: immutable.Seq[(ActorRef, ActorSystem)] = frontEnd :: backEnds.toList
  terminate(tuples:_*)

  def terminate(input:(ActorRef, ActorSystem)*): Unit = {
    Thread.sleep(15000)
    //input.map(_._2).foreach(system=>Await.result(system.whenTerminated, 30 seconds))
    input.map(_._2).foreach(system=>system.terminate())
  }



}
