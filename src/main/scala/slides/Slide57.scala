package com.mattrjacobs.rxscalademo

import rx.subscriptions.Subscriptions
import rx.lang.scala.{ Observable, Observer }

/**
 * Where does id come from?
 * Original example doesn't return a Subscription
 */
trait Slide57 extends App {

  case class Video(id: Long)

  val id = 12345L

  val o: Observable[Video] =
    Observable((observer: Observer[Video]) => {
      try {
        observer.onNext(Video(id))
        observer.onCompleted()
      } catch {
        case ex: Throwable => observer.onError(ex)
      }
      Subscriptions.empty
    })
}

