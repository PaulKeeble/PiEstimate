
import java.lang.Integer.MAX_VALUE
import distributedpi._

import distributedpi.Samplers._

object WS {
  PiMonteCarlo.pi(1000)                           //> res0: Double = 3.076

  ActorPi.pi(100000000)                           //> res1: Double = 3.14196696

  ActorPi.shutdown
}