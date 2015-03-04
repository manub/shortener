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

  "shortening an url" should {

    "return a 201 status with a location header and a 6 characters relative url" in {

      val baseUrl = s"http://localhost:$port/"
      val url = s"${baseUrl}url"
      val payload = Json.obj("url" -> "http://www.google.com")

      val result = await(WS.url(url).post(payload))

      result.status mustBe 201
      result.header(HttpHeaders.LOCATION) mustBe 'defined

      val location = result.header(HttpHeaders.LOCATION).get

      location must startWith(baseUrl)

      val relativeLocation: String = location diff baseUrl
      relativeLocation.foreach { c => c.isLetterOrDigit mustBe true}
    }

  }

}
