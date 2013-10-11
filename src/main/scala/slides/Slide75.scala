package com.mattrjacobs.rxscala.slides

import rx.lang.scala.Observable

trait Slide75 extends App {
  type SomeData
  val a: Observable[SomeData]
  val b: Observable[SomeData]

  a.merge(b).subscribe(
    (element: SomeData) => println("data: " + element))
}

