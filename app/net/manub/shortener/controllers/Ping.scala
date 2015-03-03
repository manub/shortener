package net.manub.shortener.controllers

import play.api.mvc.{Action, Controller}

object Ping extends Controller {

  def ping = Action {
    Ok("pong")
  }

}
