package com.mattrjacobs.rxscala.demo

import java.util.concurrent.CountDownLatch
import rx.apache.http._
import rx.lang.scala.Observable

object TwitterStreamingClient extends TwitterClient {
  val latch = new CountDownLatch(1)

  def main(args: Array[String]): Unit = {
    println("Twitter Streaming client starting!")

    try {
      val request: ObservableHttp[ObservableHttpResponse] =
        getRequest(TWITTER_USER_STREAM_URI, "OAuth oauth_consumer_key=\"ix3M2MWX1ircNSudKLpVcw\", oauth_nonce=\"9a62da240e9a8a7e3c0f5606623c9bf3\", oauth_signature=\"sqk4zE%2BeHSqRqqWzbV%2F73hT%2FwYE%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1381928265\", oauth_token=\"6463862-NLQ9vwfqZBjX5hyVzDwzktYLeICfdknLIr1yutjbUg\", oauth_version=\"1.0\"")

      val obs: rx.Observable[ObservableHttpResponse] = request.toObservable
      val scalaObs: Observable[ObservableHttpResponse] = Observable(obs)

      val mentionObs: Observable[Mention] = for {
        httpResp <- scalaObs
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
