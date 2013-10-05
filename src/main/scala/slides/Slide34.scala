package com.mattrjacobs.rxscalademo

trait Slide34 extends App {
  def getData: Iterable[String]

  def doWork = {
    getData.foreach {
      case "foo" => //do something
      case "bar" => //do something else
      case _     => //default
    }
  }
}
