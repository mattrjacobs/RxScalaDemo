package com.mattrjacobs.rxscala.demo

import java.util.concurrent.CountDownLatch
import rx.apache.http._
import rx.lang.scala.Observable

object TwitterStreamingClient extends TwitterClient {
  val latch = new CountDownLatch(1)

  def main(args: Array[String]): Unit = {
    println("Twitter Streaming client starting!")

    try {
      val response: Observable[ObservableHttpResponse] =
        //Very poor man's OAuth integration. I set up a Twitter app and got
        //OAuth header from: https://dev.twitter.com/docs/streaming-apis/streams/user.
        //Remember to escape your quotes!
        getResponse(TWITTER_USER_STREAM_URI, "fill_this_in_with_oauth_header")

      val mentionObs: Observable[Mention] = for {
        httpResp <- response
        jsonMap <- getJson(httpResp) if jsonMentionsMe(jsonMap, "mattrjacobs")
      } yield getMention(jsonMap)

      mentionObs.subscribe(
        (m: Mention) => println("onNext : " + m),
        (ex: Throwable) => {
          println("onError : " + ex)
          ex.printStackTrace()
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
