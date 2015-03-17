package distributedpi

import akka.actor.ActorSystem
import akka.actor.Props

import distributedpi.Samplers.SamplerSupervisor

object ConstructActorSystem {
  
   val system = ActorSystem("DistributedPi")
   
   val samplerSupervisor = system.actorOf(Props[SamplerSupervisor], "pi")
   
    def shutdown {
     system.shutdown()
   }
}