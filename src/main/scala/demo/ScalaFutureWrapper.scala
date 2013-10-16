package com.mattrjacobs.rxscala

import java.util.concurrent.CountDownLatch
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ScalaFutureWrapper extends FutureWrapper {

  def main(args: Array[String]) = {
    println("Starting the ScalaFutureWrapper")

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
