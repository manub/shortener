package net.manub.shortener.controllers

import com.google.common.net.HttpHeaders
import net.manub.shortener.domain.ShortenedUrl
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

object Url extends Controller with MongoController {

  lazy val collection: JSONCollection = db.collection[JSONCollection]("shortenedUrls")

  def shorten = Action.async(parse.json) { request =>

    val url = (request.body \ "url").toString()
    val shortenedUrl = ShortenedUrl.create(url)

    collection.insert(shortenedUrl).map { ignored =>
      Created.withHeaders(HttpHeaders.LOCATION -> s"http://${request.host}/${shortenedUrl.id}")
    }

  }

}
