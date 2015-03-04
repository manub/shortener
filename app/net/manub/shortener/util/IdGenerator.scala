package net.manub.shortener.util

import scala.util.Random

object IdGenerator {

  /**
   * Generates a random id composed of 6 random alphanumeric characters.
   *
   * @return a random id composed of 6 random alphanumeric characters.
   */
  def generateRandomId: String = Random.alphanumeric.take(6).mkString

}
