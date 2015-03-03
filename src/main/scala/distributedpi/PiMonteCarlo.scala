package distributedpi

import java.lang.Math._;
import scala.util.Random;

object PiMonteCarlo {
  def distanceFromCentre(x:Double, y:Double) = sqrt(x*x + y*y)
  
  def isInCircle(distance:Double):Boolean = distance< 1.0d;
  
  def estimatePiFromSamples(in:Int,samples:Int) = (in.toDouble/samples.toDouble) * 4.0d
  
 
  def inEstimate(r:Random,samples:Int) = {
    val count = Range(0,samples).foldLeft(0) { (inCount,_) =>
      val x = r.nextDouble 
      val y = r.nextDouble
      val z = distanceFromCentre(x,y)
      
      if(isInCircle(z)) inCount +1 else inCount
    }
    count
  } 
 
  def pi(samples: Int) = {
    val r = new Random()
    
    val count = inEstimate(r,samples)
    
    estimatePiFromSamples(count,samples)
  }
}