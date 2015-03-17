package distributedpi

import scala.concurrent.duration._

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSpecLike

import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import akka.testkit.TestKit

import ActorPi._
import Samplers._

class FunctionalSpec(_system: ActorSystem) extends TestKit(_system) with FunSpecLike with ImplicitSender with BeforeAndAfterAll {
  def this() = this(ConstructActorSystem.system)
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  
  def isRoughlyPi(v:Double) {
     assert(v > 3.0d)
     assert(v < 3.3d)
  } 
  
  describe("A configured cluster of Samplers") {
    
    it("should respond to requests for IdentifiedSamples") {
      val samplers = system.actorSelection("/user/pi/samplers")
      
      samplers ! GetEstimate(100)
      
      val Sample(inCount,samples)= receiveOne(5.seconds)
      
      assert(inCount !== 0)
      assert(inCount !== 100)
      assert(samples === 100)
      
    }
  }
  
  describe("The pi function") {
    it("should produce a single pi") {
      val pi = ActorPi.pi(10000)
      isRoughlyPi(pi)
    }
    
    it("should produce multiple parallel pi") {
      val (a,b) = ActorPi.piParrallel(10000,10000)
      isRoughlyPi(a)
      isRoughlyPi(b)
    }
  }
}