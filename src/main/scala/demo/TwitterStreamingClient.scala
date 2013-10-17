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
        getResponse(TWITTER_USER_STREAM_URI, "OAuth oauth_consumer_key=\"ix3M2MWX1ircNSudKLpVcw\", oauth_nonce=\"9875fd624a61f431c5a756f8d7fd1c71\", oauth_signature=\"GjkjuV8ycqKu4WqpB%2FmbrZTp1zc%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1381979943\", oauth_token=\"6463862-NLQ9vwfqZBjX5hyVzDwzktYLeICfdknLIr1yutjbUg\", oauth_version=\"1.0\"")

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
