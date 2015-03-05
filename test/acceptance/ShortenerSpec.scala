package acceptance

import com.google.common.net.HttpHeaders
import com.mongodb.casbah.Imports._
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

class ShortenerSpec
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

      val response = await(WS.url(url).post(payload))

      response.status mustBe 201
      val location = response.header(HttpHeaders.LOCATION).value

      location must startWith(baseUrl)

      (location diff baseUrl).foreach { c => c.isLetterOrDigit mustBe true}
    }

    "return two different shortened urls for two different urls" in {

      val firstResponse = await(WS.url(url).post(requestToShorten("http://www.google.com")))
      val secondResponse = await(WS.url(url).post(requestToShorten("http://www.bbc.co.uk")))

      val firstShortenedUrl: Option[String] = firstResponse.header(HttpHeaders.LOCATION)
      val secondShortenedUrl: Option[String] = secondResponse.header(HttpHeaders.LOCATION)

      firstShortenedUrl mustBe 'defined
      secondShortenedUrl mustBe 'defined
      firstShortenedUrl must not be secondShortenedUrl
    }

    "return the same shortened url for requesting to shorten the same url twice" in {

      val shortenedUrl = await(WS.url(url).post(requestToShorten("http://www.google.com")))
      val sameUrlShortenedAgain = await(WS.url(url).post(requestToShorten("http://www.google.com")))

      shortenedUrl.header(HttpHeaders.LOCATION) mustBe sameUrlShortenedAgain.header(HttpHeaders.LOCATION)
    }

    "return a 400 error with empty body when requesting to shorten a non valid url" in {

      val invalidUrlResponse = await(WS.url(url).post(requestToShorten("not an url")))

      invalidUrlResponse.status mustBe 400
      invalidUrlResponse.body mustBe 'empty
    }

    "return a 400 error with empty body when posting an invalid payload" in {

      val invalidPayloadResponse = await(WS.url(url).post(Json.obj("test" -> false)))

      invalidPayloadResponse.status mustBe 400
      invalidPayloadResponse.body mustBe 'empty
    }

  }

  "performing a GET on a shortened url" should {

    "return a 301 with the correct location header" in {
      
      val urlToShorten = "http://www.google.com"
      val shortenUrlResponse = await(WS.url(url).post(requestToShorten(urlToShorten)))
      val shortenedUrl = shortenUrlResponse.header(HttpHeaders.LOCATION).value

      val response = await(WS.url(shortenedUrl).withFollowRedirects(false).get())

      response.status mustBe 301
      response.header(HttpHeaders.LOCATION).value mustBe urlToShorten
    }

    "return a 404 for a URL that hasn't been found" in {

      val response = await(WS.url(s"${baseUrl}ABC").withFollowRedirects(false).get())

      response.status mustBe 404
    }
  }

}
