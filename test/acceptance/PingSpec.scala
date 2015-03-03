package acceptance

import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.ws.WS
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

class PingSpec extends PlaySpec with OneServerPerSuite with FutureAwaits with DefaultAwaitTimeout {

  "/ping" should {

    """return a 200 with a "pong" body""" in {

      val pingUrl = s"http://localhost:$port/ping"

      val result = await(WS.url(pingUrl).get())

      result.status mustBe 200
      result.body mustBe "pong"
    }

  }

}
