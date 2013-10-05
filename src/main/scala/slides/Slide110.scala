package com.mattrjacobs.rxscalademo

import org.apache.http.HttpResponse
import org.apache.http.nio.client.HttpAsyncClient
import org.apache.http.nio.client.methods.HttpAsyncMethods
import rx.apache.http.{ ObservableHttp, ObservableHttpResponse }
import rx.lang.scala.Observable
import rx.lang.scala.ImplicitFunctionConversions._

/*
 * Here's an example where Scala adaptor hasn't caught up to
 * Java version - we need to import implicits to convert HTTP
 * resp to Scala version to get the map function
 */
trait Slide110 extends App {
  val client: HttpAsyncClient

  ObservableHttp.createRequest(
    HttpAsyncMethods.createGet("http://www.wikipedia.com"), client)
    .toObservable()
    .flatMap((resp: ObservableHttpResponse) => {
      val s: rx.Observable[String] =
        resp.getContent.map((bb: Array[Byte]) => new String(bb))
      s
    }).subscribe(
      (s: String) => System.out.println(s))
}

