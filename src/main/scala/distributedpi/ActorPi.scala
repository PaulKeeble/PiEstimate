package distributedpi

import distributedpi.PiMonteCarlo._
import distributedpi.Samplers._
import scala.concurrent.duration._
import scala.collection.mutable
import scala.util.Random
import scala.Range

import akka.actor._
import akka.actor.ActorSelection.toScala
import akka.routing.FromConfig

object ActorPi {

  val system = Samplers.system
  
  val timeOut = 30.seconds
  
  def pi(samples:Int) = {
    val inbox = getEstimate(samples)
    
    val Estimate(pi) =  inbox.receive(timeOut)
    
    pi
  }
     
  def piParrallel(samples1:Int,samples2:Int) = {
     val inbox1 = getEstimate(samples1)
     
     val inbox2 = getEstimate(samples2)
     
     val Estimate(piA) =  inbox1.receive(timeOut)
     val Estimate(piB) =  inbox2.receive(timeOut)
     
     (piA,piB)
   }
   
   private[this] def getEstimate(samples:Int) = {
     val inbox = Inbox.create(system)
     val estimator = system.actorOf(Props[SingleEstimater])
    
     inbox.send(estimator,GetEstimate(samples))
     
     inbox
   }
   
   private[this] def makeMultipleMessages(samples: Int) : Seq[GetEstimate] = {
      val SPLIT_SIZE = 100
      val average = samples/SPLIT_SIZE
      val remainder = samples % SPLIT_SIZE
      
      val v = Seq.fill(100) {
        GetEstimate(average)
      }
      
      v :+ GetEstimate(remainder)
    }
   
   def shutdown {
     system.shutdown()
   }

  /**
   * Created with every request for a Pi Estimate, communicates to the pi actor locally with the sample size request and 
   * starts a local accumulator for the purpose of estimating Pi 
   */
  private class SingleEstimater extends Actor with ActorLogging {
    val r = new Random
    
    var customer : Option[ActorRef] = None
    
    def receive = {
      case msg@GetEstimate(samples) => {
        customer = Some(sender())
        
        val accumulator = context.actorOf(Props(classOf[SampleCollector],samples),"accumulator")
        
        makeMultipleMessages(samples).foreach { m =>
          context.actorSelection("/user/pi/samplers").tell(m,accumulator)
        }
        
      }
      
      case estimate : Estimate => customer.foreach { _ ! estimate }
    }
    
  }
  

  private case class Estimate(pi:Double)
  
  private  class SampleCollector(expectedSamples: Int) extends Actor with ActorLogging {
    var accumulation = initialSample
    
    def in = accumulation.inCount
    
    def samples = accumulation.totalCount
    
    def initialSample = Sample(0,0)
    
    def receive = {
      case Sample(i,s) => {
        accumulation= Sample(in + i,samples + s)
        
        if(samples == expectedSamples) {
          context.parent ! Estimate(estimatePiFromSamples(in,samples))
          accumulation = initialSample
        }
        
      }
    }
  }
}