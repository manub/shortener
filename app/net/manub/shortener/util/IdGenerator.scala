package net.manub.shortener.util

import scala.util.Random

object IdGenerator {

  // TODO: scaladoc
  def generateRandomId: String = Random.alphanumeric.take(6).mkString

}
