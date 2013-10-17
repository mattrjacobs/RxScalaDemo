package com.mattrjacobs.rxscala

import java.util.concurrent.CountDownLatch
import rx.lang.scala.{ Observable, Observer, Subscription }

object IterableWrapper {

  def toObservable[T](iterableT: scala.collection.Iterable[T]): Observable[T] =
    Observable((observer: Observer[T]) => {
      try {
        iterableT.foreach(t => observer.onNext(t))
        observer.onCompleted()
      } catch {
        case ex: Throwable => observer.onError(ex)
      }

      //no unsubscribe
      new Subscription {
        override def unsubscribe() = {}
      }
    })

  def main(args: Array[String]) = {
    println("Starting the IterableWrapper")

    val latch = new CountDownLatch(1)

    val l = List(1, 2, 3, 4, 5, 6)
    val o: Observable[Int] = toObservable(l)

    o.subscribe(
      (i: Int) => println("onNext : " + i),
      (ex: Throwable) => {
        println("onError: " + ex)
        latch.countDown()
      },
      () => {
        println("onCompleted")
        latch.countDown()
      })

    latch.await()
  }
}
