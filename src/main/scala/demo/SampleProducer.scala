package com.mattrjacobs.rxscala

import rx.lang.scala.Observable

object Producer {
  def stream(): Observable[String] = {
    val stringList = (1 to 100).map(x => "a")
    Observable(stringList: _*)
  }
}
