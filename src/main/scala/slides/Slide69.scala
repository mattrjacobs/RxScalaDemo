package com.mattrjacobs.rxscala.slides

import rx.subscriptions.Subscriptions
import rx.lang.scala.{ Observable, Observer }

trait Slide69 extends App {
  trait Video {
    val videoId: Long
  }

  def getVideos(): Observable[Video]

  val subscription = getVideos.subscribe(new rx.Observer[Video] {
    def onNext(v: Video) = println("Video: " + v.videoId)
    def onError(ex: Throwable) {
      println("Error")
      ex.printStackTrace
    }
    def onCompleted() {
      println("Completed")
    }
  })
}

