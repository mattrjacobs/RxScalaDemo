package com.mattrjacobs.rxscala.demo

import com.mattrjacobs.rxscala.http._
import java.util.concurrent.CountDownLatch
import rx.lang.scala.Observable

/*
 * Example curl
 * curl --get 'https://api.twitter.com/1.1/statuses/mentions_timeline.json' --data 'count=2&since_id=14927799' --header 'Authorization: OAuth oauth_consumer_key="ix3M2MWX1ircNSudKLpVcw", oauth_nonce="57e9bbd91259f7e7513a08dbe3177446", oauth_signature="sh3958IndFJrtGmd7c9kiDlS%2Fy0%3D", oauth_signature_method="HMAC-SHA1", oauth_timestamp="1381525038", oauth_token="6463862-NLQ9vwfqZBjX5hyVzDwzktYLeICfdknLIr1yutjbUg", oauth_version="1.0"' --verbose
*/

object TwitterRestClient extends TwitterClient {

  val latch = new CountDownLatch(1)

  def main(args: Array[String]): Unit = {
    println("Twitter REST client starting!")

    try {
      val request: ObservableHttp[ObservableHttpResponse] =
        getRequest(TWITTER_USER_MENTIONS_REST_URI, "OAuth oauth_consumer_key=\"ix3M2MWX1ircNSudKLpVcw\", oauth_nonce=\"b6e690e58674d88b01448fdb4d8f3888\", oauth_signature=\"mH14usZ8J7JgpaSsa6tH7ZKoCHs%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1381891550\", oauth_token=\"6463862-NLQ9vwfqZBjX5hyVzDwzktYLeICfdknLIr1yutjbUg\", oauth_version=\"1.0\"")

      val obs: rx.Observable[ObservableHttpResponse] = request.toObservable
      val scalaObs: Observable[ObservableHttpResponse] = Observable(obs)

      val mentionObs: Observable[Mention] = for {
        httpResp <- scalaObs
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
