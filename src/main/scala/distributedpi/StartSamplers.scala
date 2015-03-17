package distributedpi

object StartSamplers {
  def main(args: Array[String]) = {
    import Samplers._
    val actorSystem = ConstructActorSystem.system
  }
}