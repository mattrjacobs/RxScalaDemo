package com.mattrjacobs.rxscala

import java.util.concurrent.CountDownLatch
import rx.lang.scala.{ Observable, Observer, Subscription }
import scala.util.{ Failure, Success }

trait FutureWrapper {

  def toObservable[T](futureT: scala.concurrent.Future[T]): Observable[T] =
    Observable((observer: Observer[T]) => {
      import scala.concurrent.ExecutionContext.Implicits.global

      futureT.onComplete {
        case Success(t) => {
          observer.onNext(t)
          observer.onCompleted()
        }
        case Failure(ex) => observer.onError(ex)
      }

      //no present way to cancel a Scala future
      //http://stackoverflow.com/questions/16009837/how-to-cancel-future-in-scala
      new Subscription {
        override def unsubscribe() = {}
      }
    })

  def toObservable[T](futureT: com.twitter.util.Future[T]): Observable[T] =
    Observable((observer: Observer[T]) => {
      futureT.onSuccess {
        t =>
          {
            observer.onNext(t)
            observer.onCompleted()
          }
      }.onFailure {
        ex => observer.onError(ex)
      }

      new Subscription {
        override def unsubscribe() =
          futureT.raise(new com.twitter.util.FutureCancelledException)
      }
    })

  protected def subscribeTo(a: Observable[Int], b: Observable[Int], latch: CountDownLatch) = {
    a.subscribe(
      (i: Int) => println("A onNext : " + i),
      (ex: Throwable) => {
        println("A onError : " + ex)
        latch.countDown()
      },
      () => {
        println("A onCompleted")
        latch.countDown()
      })

    b.subscribe(
      (i: Int) => println("B onNext : " + i),
      (ex: Throwable) => {
        println("B onError : " + ex)
        latch.countDown()
      },
      () => {
        println("B onCompleted")
        latch.countDown()
      })
  }
}

