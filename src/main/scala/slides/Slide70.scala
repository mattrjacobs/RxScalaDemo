package com.mattrjacobs.rxscalademo

import rx.subscriptions.Subscriptions
import rx.lang.scala.{ Observable, Observer }

trait Slide70 extends App {
  trait Video {
    val videoId: Long
  }

  def getVideos(): Observable[Video]

  val subscription = getVideos.subscribe(
    (v: Video) => println("Video: " + v.videoId),
    (ex: Throwable) => {
      println("Error")
      ex.printStackTrace
    },
    () => println("Completed"))
}

