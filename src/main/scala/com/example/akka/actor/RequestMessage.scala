package com.example.akka.actor

sealed trait RequestMessage
case class Quote(number:Int) extends RequestMessage