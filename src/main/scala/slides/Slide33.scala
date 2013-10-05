package com.mattrjacobs.rxscalademo

trait Slide33 extends App {
  def getData: String

  def doWork = {
    getData match {
      case "foo" => //do something
      case "bar" => //do something else
      case _     => //default
    }
  }
}
