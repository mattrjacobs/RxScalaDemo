package com.mattrjacobs.rxscala

import com.twitter.util.Future
import java.util.concurrent.CountDownLatch

object TwitterFutureWrapper extends FutureWrapper {

  def main(args: Array[String]) = {
    println("Starting the TwitterFutureWrapper")

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

    subscribeTo(successObservable, failureObservable, latch)
    latch.await()
  }
}
