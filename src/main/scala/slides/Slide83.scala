package com.mattrjacobs.rxscala.slides

import rx.lang.scala.Observable

trait Slide83 extends App {
  type SomeData
  val a: Observable[SomeData]
  val b: Observable[SomeData]

  a.zip(b).subscribe(
    pair => println("a: " + pair._1 + " b: " + pair._2))
}

