package com.mattrjacobs.rxscala.slides

import rx.lang.scala.Observable

trait Slide45 extends App {
  def getData: Observable[String]

  def doWork = {
    getData.map {
      case "foo" => //do something
      case "bar" => //do something else
      case _     => //default
    }
  }
}
