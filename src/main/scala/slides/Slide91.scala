package com.mattrjacobs.rxscalademo

import rx.lang.scala.Observable

trait Slide91 extends App {
  type SomeData
  val a: Observable[SomeData]
  val b: Observable[String]

  a.zip(b).subscribe(
    pair => println("a: " + pair._1 + " b: " + pair._2),
    ex => println("error occurred: " + ex.getMessage),
    () => println("completed"))
}

