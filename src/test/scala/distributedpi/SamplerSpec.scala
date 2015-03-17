package distributedpi

import scala.concurrent.duration._
import akka.testkit.TestActorRef
import akka.actor.Inbox
import org.scalatest.FunSpec
import distributedpi.ActorPi._
import distributedpi.Samplers._
import akka.actor.Props


class SamplerSpec extends FunSpec {
  implicit val system = ConstructActorSystem.system
	
  val randomSeed = 0L
  val testId = 1
	val requestedSamples = 100
  
  val inbox = Inbox.create(system)
  val actor = TestActorRef(Props(classOf[Sampler],randomSeed))
  
  describe("A Sampler") {
    it("should reply with samples") {
      inbox.send(actor,GetEstimate(requestedSamples))
      
      val Sample(inCount,samples) =  inbox.receive(10.seconds)
      
      assert(inCount !== 0)
      assert(inCount !== requestedSamples)
      assert(requestedSamples === samples)
    }
  }
}