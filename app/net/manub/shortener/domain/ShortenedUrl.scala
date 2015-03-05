package net.manub.shortener.domain

import net.manub.shortener.util.IdGenerator
import play.api.libs.json.Json

case class ShortenedUrl(_id: String, originalUrl: String)

object ShortenedUrl {

  implicit val format = Json.format[ShortenedUrl]

  def create(url: String): ShortenedUrl = ShortenedUrl(IdGenerator.generateRandomId, url)
}


