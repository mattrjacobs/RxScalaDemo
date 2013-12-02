package com.mattrjacobs.rxscala.demo

import java.util.concurrent.CountDownLatch
import rx.apache.http._
import rx.lang.scala.Observable

object TwitterRestClient extends TwitterClient {

  val latch = new CountDownLatch(1)

  def main(args: Array[String]): Unit = {
    println("Twitter REST client starting!")

    try {
      val response: Observable[ObservableHttpResponse] =
        //Very poor man's OAuth integration.  I set up a Twitter app and got
        //OAuth header from: https://dev.twitter.com/docs/api/1.1/get/statuses/mentions_timeline.
        //Remember to escape your quotes!
        getResponse(TWITTER_USER_MENTIONS_REST_URI, "fill_this_in_with_oauth_header")

      val mentionObs: Observable[Mention] = for {
        httpResp <- response
        jsonMap <- getJson(httpResp)
      } yield getMention(jsonMap)

      mentionObs.subscribe(
        (m: Mention) => println("onNext : " + m),
        (ex: Throwable) => {
          println("onError : " + ex)
          latch.countDown()
        },
        () => {
          println("onCompleted")
          latch.countDown()
        })
      latch.await()
    } catch {
      case ex: Exception => println("Received exception : " + ex)
    } finally {
      client.close()
    }
  }
}
