package com.mattrjacobs.rxscalademo

import rx.lang.scala.Observable

trait Slide95 extends App {
  type SomeData
  def getDataB: Observable[String]
  def getFallbackForB: Observable[String]

  val a: Observable[SomeData]
  val b: Observable[String] = getDataB.onErrorResumeNext(getFallbackForB)

  a.zip(b).subscribe(
    pair => println("a: " + pair._1 + " b: " + pair._2),
    ex => println("error occurred: " + ex.getMessage),
    () => println("completed"))
}

