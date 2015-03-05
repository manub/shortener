package net.manub.shortener.controllers

import java.net.URL

import com.google.common.net.HttpHeaders
import net.manub.shortener.domain.ShortenedUrl
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller, Request, Result}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object Url extends Controller with MongoController {

  lazy val collection: JSONCollection = db.collection[JSONCollection]("shortenedUrls")

  def shorten = Action.async(parse.json) { implicit request =>

    UrlToShorten.fromRequest.map { urlToShorten =>
      val url = urlToShorten.url
      val query = BSONDocument("originalUrl" -> url)

      collection.find(query).one[ShortenedUrl].flatMap {
        case Some(existingShortenedUrl) => created(existingShortenedUrl)
        case None =>
          val shortenedUrl = ShortenedUrl.create(url)
          collection.insert(shortenedUrl).flatMap(_ => created(shortenedUrl))
      }
    }.getOrElse(Future.successful(BadRequest))
  }

  private def created(shortenedUrl: ShortenedUrl)(implicit request: Request[AnyRef]): Future[Result] =
    Future.successful(Created.withHeaders(HttpHeaders.LOCATION -> s"http://${request.host}/${shortenedUrl.id}"))

  def go(id: String) = Action.async { implicit request =>

    val query = BSONDocument("id" -> id)

    collection.find(query).one[ShortenedUrl].flatMap {
      case Some(shortenedUrl) => Future.successful(MovedPermanently(shortenedUrl.originalUrl))
      case None => Future.successful(NotFound)
    }

  }

}

case class UrlToShorten(url: String)

object UrlToShorten {

  def fromRequest(implicit request: Request[JsValue]): Try[UrlToShorten] = {
    Try {
      val urlFromRequest = (request.body \ "url").as[String]
      new URL(urlFromRequest)
      UrlToShorten(urlFromRequest)
    }
  }
}
