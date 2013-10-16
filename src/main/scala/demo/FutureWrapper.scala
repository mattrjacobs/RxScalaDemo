package com.mattrjacobs.rxscala

import java.util.concurrent.CountDownLatch
import rx.lang.scala.{ Observable, Observer, Subscription }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

object FutureWrapper {

  def toObservable[T](futureT: Future[T]): Observable[T] =
    Observable((observer: Observer[T]) => {
      futureT.onComplete {
        case Success(t) => {
          observer.onNext(t)
          observer.onCompleted()
        }
        case Failure(ex: Throwable) => observer.onError(ex)
      }

      //no present way to cancel a Scala future
      //http://stackoverflow.com/questions/16009837/how-to-cancel-future-in-scala
      NoSubscription()
    })

  def main(args: Array[String]) = {
    println("Starting the FutureWrapper")

    val latch = new CountDownLatch(2)

    val successFuture: Future[Int] = Future {
      Thread.sleep(1000)
      3
    }

    val failureFuture: Future[Int] = Future {
      Thread.sleep(1000)
      throw new RuntimeException("future failed!")
      3
    }

    val successObservable = toObservable(successFuture)
    val failureObservable = toObservable(failureFuture)

    successObservable.subscribe(
      (i: Int) => println("A onNext : " + i),
      (ex: Throwable) => {
        println("A onError : " + ex)
        latch.countDown()
      },
      () => {
        println("A onCompleted")
        latch.countDown()
      })

    failureObservable.subscribe(
      (i: Int) => println("B onNext : " + i),
      (ex: Throwable) => {
        println("B onError : " + ex)
        latch.countDown()
      },
      () => {
        println("B onCompleted")
        latch.countDown()
      })

    latch.await()
  }

  object NoSubscription {
    def apply() = new Subscription {
      override def unsubscribe() = {}
    }
  }
}
