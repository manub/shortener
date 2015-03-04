package acceptance

import com.google.common.net.HttpHeaders
import com.mongodb.casbah.Imports._
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

class ShortenUrlSpec
  extends PlaySpec with OneServerPerSuite with FutureAwaits with DefaultAwaitTimeout with BeforeAndAfter {

  lazy val mongoClient = MongoClient()

  before {
    mongoClient("shortener")("shortenedUrls").drop()
  }

  val baseUrl = s"http://localhost:$port/"
  val url = s"${baseUrl}url"

  def requestToShorten(url: String) = Json.obj("url" -> url)

  "shortening an url" should {

    "return a 201 status with a location header and a 6 characters relative url" in {

      val payload = Json.obj("url" -> "http://www.google.com")

      val result = await(WS.url(url).post(payload))

      result.status mustBe 201
      result.header(HttpHeaders.LOCATION) mustBe 'defined

      val location = result.header(HttpHeaders.LOCATION).get

      location must startWith(baseUrl)

      val relativeLocation: String = location diff baseUrl
      relativeLocation.foreach { c => c.isLetterOrDigit mustBe true}
    }

    "return two different shortened urls for two different urls" in {

      val firstResult = await(WS.url(url).post(requestToShorten("http://www.google.com")))
      val secondResult = await(WS.url(url).post(requestToShorten("http://www.bbc.co.uk")))

      val firstShortenedUrl: Option[String] = firstResult.header(HttpHeaders.LOCATION)
      val secondShortenedUrl: Option[String] = secondResult.header(HttpHeaders.LOCATION)

      firstShortenedUrl mustBe 'defined
      secondShortenedUrl mustBe 'defined
      firstShortenedUrl must not be secondShortenedUrl
    }

    "return the same shortened url for requesting to shorten the same url twice" in {

      val shortenedUrl = await(WS.url(url).post(requestToShorten("http://www.google.com")))
      val sameUrlShortenedAgain = await(WS.url(url).post(requestToShorten("http://www.google.com")))

      shortenedUrl.header(HttpHeaders.LOCATION) mustBe sameUrlShortenedAgain.header(HttpHeaders.LOCATION)
    }

  }

}
