package com.mattrjacobs.rxscalademo

import rx.lang.scala.Observable

trait Slide71 extends App {
  trait Video {
    val videoId: Long
  }

  def getVideos(): Observable[Video]

  val subscription = getVideos.subscribe(
    (v: Video) => println("Video: " + v.videoId),
    (ex: Throwable) => {
      println("Error")
      ex.printStackTrace
    })
}

