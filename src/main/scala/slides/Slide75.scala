package com.mattrjacobs.rxscalademo

import rx.lang.scala.Observable

trait Slide75 extends App {
  type SomeData

  def getDataA(): Observable[SomeData]
  def getDataB(): Observable[SomeData]

  val a: Observable[SomeData] = getDataA()
  val b: Observable[SomeData] = getDataB()

  val merged = a.merge(b).subscribe(
    (element: SomeData) => println("data: " + element))
}

