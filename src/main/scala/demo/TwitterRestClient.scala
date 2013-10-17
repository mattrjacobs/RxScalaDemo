package com.mattrjacobs.rxscala.demo

import java.util.concurrent.CountDownLatch
import rx.apache.http._
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
      val response: Observable[ObservableHttpResponse] =
        getResponse(TWITTER_USER_MENTIONS_REST_URI, "OAuth oauth_consumer_key=\"ix3M2MWX1ircNSudKLpVcw\", oauth_nonce=\"e8965178c1e927af4380fab3bc227a96\", oauth_signature=\"nuh7sKJ66yumgLZHy45ulqc0Zac%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1381928205\", oauth_token=\"6463862-NLQ9vwfqZBjX5hyVzDwzktYLeICfdknLIr1yutjbUg\", oauth_version=\"1.0\"")

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
