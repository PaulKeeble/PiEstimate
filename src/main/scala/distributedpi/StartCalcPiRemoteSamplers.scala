package distributedpi

import scala.collection._
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinPool
import akka.actor.ActorRef
import akka.actor.Address
import akka.remote.routing.RemoteRouterConfig
import akka.routing.RoundRobinGroup
import akka.actor.Inbox
import Samplers.GetEstimate
import ActorPi.Estimate
import ActorPi.SingleEstimater
import com.typesafe.config.ConfigFactory

object StartCalcPiRemoteSamplers {
  def main(args: Array[String]) {
    if(args.length < 2) {
      println("Arguments should be [samples] [server1address]+")
    }
    
    val samples = args(0).toInt
    
    val serverAddresses = args.tail.to[immutable.Iterable]

    val system = ActorSystem("PiAccumulator",ConfigFactory.load("client.conf"))

    println("Pi estimate: " + pi(samples,system,serverAddresses))
    
    system.shutdown
    system.awaitTermination
  }
  
  def pi(samples:Int,system:ActorSystem,addresses:immutable.Iterable[String]) = {
    val routerRemote = system.actorOf(RoundRobinGroup(addresses).props(),"piSamplers")

     val inbox = Inbox.create(system)
     val estimator = system.actorOf(Props(classOf[SingleEstimater],"/user/piSamplers"))
    
     inbox.send(estimator,GetEstimate(samples))
     
      val Estimate(pi) =  inbox.receive(30.seconds)
      pi
  }
}