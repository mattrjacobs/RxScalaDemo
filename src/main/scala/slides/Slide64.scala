package com.mattrjacobs.rxscala.slides

import rx.subscriptions.Subscriptions
import rx.lang.scala.{ Observable, Observer }

trait Slide64 extends App {

  type Video
  val videos: List[Video]

  def getVideos(): Observable[Video] = Observable(observer => {
    try {
      videos.foreach(observer.onNext)
      observer.onCompleted()
    } catch {
      case ex: Throwable => observer.onError(ex)
    }
    Subscriptions.empty
  })
}

