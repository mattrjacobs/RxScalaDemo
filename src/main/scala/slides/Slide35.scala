package com.mattrjacobs.rxscala.slides

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Slide35 extends App {
  def getData: Future[String]

  def doWork = {
    getData.map {
      case "foo" => //do something
      case "bar" => //do something else
      case _     => //default
    }
  }
}
