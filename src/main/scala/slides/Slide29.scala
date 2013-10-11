package com.mattrjacobs.rxscala.slides

import rx.lang.scala.Observable

trait Slide29 extends App {
  def getDataFromLocalMemory(): Iterable[String] =
    1.to(75).map(_.toString)

  def getDataFromNetwork(): Observable[String] = {
    val strings = 1.to(75).map(_.toString)
    Observable(strings: _*)
  }

  //Iterable[String] that contains 75 Strings
  def iterableOutput = {
    getDataFromLocalMemory()
      .drop(10)
      .take(5)
      .map(_ + "transformed")
      .foreach(s => println("next => " + s))
  }

  //Observable[String] that emits 75 Strings
  def observableOutput = {
    getDataFromNetwork()
      .drop(10)
      .take(5)
      .map(_ + "transformed")
      .subscribe(s => println("next => " + s))
  }
}
