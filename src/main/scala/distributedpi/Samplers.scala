package distributedpi

import scala.concurrent.duration._
import scala.collection.mutable
import scala.util.Random
import scala.Range

import akka.actor._
import akka.routing.FromConfig
import akka.actor.ActorSelection.toScala

import distributedpi.PiMonteCarlo._

object Samplers {
   val system = ActorSystem("DistributedPi")
   val piEstimator = system.actorOf(Props[SamplerSupervisor], "pi")
   
  case class GetEstimate(samples:Int)
  case class Sample(inCount:Int,totalCount:Int)

  class SamplerSupervisor extends Actor with ActorLogging {
    val r = new Random
    
    val samplers = context.actorOf(FromConfig.props(Props(classOf[Sampler],r.nextLong)),"samplers")

    def receive = { case _ =>
    }
  }

  class Sampler(val seed:Long) extends Actor with ActorLogging {
    val r = new Random(seed)

    def receive = {
      case GetEstimate(samples) => {
        val inCount = inEstimate(r,samples)
        
        sender ! Sample(inCount,samples)
      }
    }
  }
}