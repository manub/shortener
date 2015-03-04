package net.manub.shortener.controllers

import com.google.common.net.HttpHeaders
import net.manub.shortener.domain.ShortenedUrl
import play.api.mvc.{Request, Action, Controller, Result}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import play.modules.reactivemongo.json.BSONFormats._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Url extends Controller with MongoController {

  lazy val collection: JSONCollection = db.collection[JSONCollection]("shortenedUrls")

  def shorten = Action.async(parse.json) { implicit request =>

    val url = (request.body \ "url").toString()

    val query = BSONDocument("originalUrl" -> url)

    collection.find(query).one[ShortenedUrl].flatMap {
      case Some(existingShortenedUrl) => resultFor(existingShortenedUrl)
      case None =>
        val shortenedUrl = ShortenedUrl.create(url)
        collection.insert(shortenedUrl).flatMap(_ => resultFor(shortenedUrl))
    }
  }

  private def resultFor(shortenedUrl: ShortenedUrl)(implicit request: Request[AnyRef]): Future[Result] =
    Future.successful(Created.withHeaders(HttpHeaders.LOCATION -> s"http://${request.host}/${shortenedUrl.id}"))

}
