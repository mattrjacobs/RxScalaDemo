package com.mattrjacobs.rxscala.demo

import java.util.concurrent.CountDownLatch
import org.apache.http.client.methods.HttpGet
import org.apache.http.nio.client.HttpAsyncClient
import org.apache.http.impl.nio.client.{ CloseableHttpAsyncClient, HttpAsyncClients }
import org.apache.http.nio.client.methods.HttpAsyncMethods
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import rx.apache.http.{ ObservableHttp, ObservableHttpResponse }
import rx.lang.scala.{ Observable, Observer, Subscription }
import scala.collection.JavaConverters._

/*
 * Example curl
 * curl --get 'https://api.twitter.com/1.1/statuses/mentions_timeline.json' --data 'count=2&since_id=14927799' --header 'Authorization: OAuth oauth_consumer_key="ix3M2MWX1ircNSudKLpVcw", oauth_nonce="57e9bbd91259f7e7513a08dbe3177446", oauth_signature="sh3958IndFJrtGmd7c9kiDlS%2Fy0%3D", oauth_signature_method="HMAC-SHA1", oauth_timestamp="1381525038", oauth_token="6463862-NLQ9vwfqZBjX5hyVzDwzktYLeICfdknLIr1yutjbUg", oauth_version="1.0"' --verbose
*/

case class Mention(time: DateTime, name: String, screenName: String, picture: String)

object TwitterRestClient {
  val TWITTER_API_HOST = "api.twitter.com"
  val MENTIONS_PATH = "/1.1/statuses/mentions_timeline.json"

  val client: CloseableHttpAsyncClient = {
    val underlying = HttpAsyncClients.createDefault
    println("About to start the client")
    underlying.start()
    println("Client is starting!")
    underlying
  }

  val latch = new CountDownLatch(1)
  val timeParser = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy")

  val numMentions = 10

  val TWITTER_API_URI = "https://api.twitter.com/1.1/statuses/mentions_timeline.json?count=2&since_id=14927799"

  def main(args: Array[String]): Unit = {
    println("Twitter REST client starting!")

    try {
      val apacheReq: HttpGet = {
        val underlying = new HttpGet(TWITTER_API_URI)
        underlying.setHeader("Authorization", "OAuth oauth_consumer_key=\"ix3M2MWX1ircNSudKLpVcw\", oauth_nonce=\"42a5cfe121bdd9a02164de11356cd01f\", oauth_signature=\"QOMjyqvFlvHPAYHe8LroBpysmfg%3D\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1381865861\", oauth_token=\"6463862-NLQ9vwfqZBjX5hyVzDwzktYLeICfdknLIr1yutjbUg\", oauth_version=\"1.0\"")
        underlying
      }
      val apacheAsyncReq = HttpAsyncMethods.create(apacheReq)
      val request: ObservableHttp[ObservableHttpResponse] =
        ObservableHttp.createRequest(apacheAsyncReq, client)
      val obs: rx.Observable[ObservableHttpResponse] = request.toObservable
      val scalaObs: Observable[ObservableHttpResponse] = Observable(obs)

      val byteArrayObs: Observable[Array[Byte]] =
        scalaObs.flatMap(resp => Observable(resp.getContent))
      val stringObs: Observable[String] = byteArrayObs.map(new String(_))
      val jsonObs: Observable[List[Any]] = stringObs.map(parseIntoJson)
      val mentionObs: Observable[Mention] = jsonObs.flatMap(getMentions)
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
      println("About to close the client")
      client.close()
      println("Client is closed")
    }
  }

  private def parseIntoJson(s: String) = scala.util.parsing.json.JSON.parseFull(s) match {
    case Some(l: List[Any]) => l
    case _                  => Nil
  }

  private def getMentions(l: List[Any]): Observable[Mention] = Observable(
    (observer: Observer[Mention]) => {
      try {
        l.foreach {
          case m: Map[_, _] => {
            val typedMap: Map[String, Any] = m.asInstanceOf[Map[String, Any]]
            val userMap: Map[String, Any] = typedMap("user").asInstanceOf[Map[String, Any]]
            val name = userMap("name").asInstanceOf[String]
            val screenName = userMap("screen_name").asInstanceOf[String]
            val imageUrl = userMap("profile_image_url").asInstanceOf[String]
            val time = parseDate(typedMap("created_at").asInstanceOf[String])
            val mention = Mention(time, name, screenName, imageUrl)
            observer.onNext(mention)
          }

        }
        observer.onCompleted()
      } catch {
        case ex: Throwable => observer.onError(ex)
      }

      new Subscription {
        override def unsubscribe() = {}
      }
    })

  private def parseDate(s: String): DateTime = timeParser.parseDateTime(s)
}

/*
 * TODO
 *
 * 1) Do I need an implicit to get conversion from rx.Observable -> rx.lang.scala.Observable to happen for free?
 * This happens when creating scalaObs and byteArrayObs

*/
