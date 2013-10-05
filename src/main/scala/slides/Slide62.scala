package com.mattrjacobs.rxscalademo

import java.util.concurrent.Executor
import rx.subscriptions.Subscriptions
import rx.lang.scala.{ Observable, Observer }

trait Slide62 extends App {

  type VideoRating
  val executor: Executor
  def getRatingFromNetwork(userId: Long, videoId: Long): VideoRating

  def getRating(userId: Long, videoId: Long): Observable[VideoRating] =
    Observable(observer => {
      executor.execute(new Runnable {
        override def run() {
          try {
            val rating = getRatingFromNetwork(userId, videoId)
            observer.onNext(rating)
            observer.onCompleted()
          } catch {
            case ex: Throwable => observer.onError(ex)
          }
        }
      })
      Subscriptions.empty
    })
}

