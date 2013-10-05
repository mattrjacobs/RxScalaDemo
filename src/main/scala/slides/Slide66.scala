package com.mattrjacobs.rxscalademo

import java.util.concurrent.Executor
import rx.subscriptions.Subscriptions
import rx.lang.scala.{ Observable, Observer }

trait Slide66 extends App {

  type Video
  val executor: Executor
  val videoIds: Iterable[Long]
  def getVideoFromNetwork(videoId: Long): Video

  def getVideos(): Observable[Video] =
    Observable(observer => {
      executor.execute(new Runnable {
        override def run() {
          try {
            videoIds.foreach(videoId => {
              val v = getVideoFromNetwork(videoId)
              observer.onNext(v)
            })
            observer.onCompleted()
          } catch {
            case ex: Throwable => observer.onError(ex)
          }
        }
      })
      Subscriptions.empty
    })
}

