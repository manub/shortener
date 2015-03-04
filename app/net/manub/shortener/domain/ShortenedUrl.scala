package net.manub.shortener.domain

import net.manub.shortener.util.IdGenerator
import play.api.libs.json.Json

case class ShortenedUrl(id: String, originalUrl: String)

object ShortenedUrl {

  implicit val writes = Json.writes[ShortenedUrl]

  def create(url: String): ShortenedUrl = ShortenedUrl(IdGenerator.generateRandomId, url)
}


