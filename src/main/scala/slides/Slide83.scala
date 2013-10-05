package com.mattrjacobs.rxscalademo

import rx.lang.scala.Observable

trait Slide83 extends App {
  type SomeData

  def getDataA(): Observable[SomeData]
  def getDataB(): Observable[SomeData]

  val a: Observable[SomeData] = getDataA()
  val b: Observable[SomeData] = getDataB()

  a.zip(b).subscribe(
    pair => println("a: " + pair._1 + " b: " + pair._2))
}

