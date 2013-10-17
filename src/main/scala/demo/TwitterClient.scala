package com.mattrjacobs.rxscala.demo

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.nio.client.{ CloseableHttpAsyncClient, HttpAsyncClients }
import org.apache.http.nio.client.methods.HttpAsyncMethods
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import rx.apache.http._
import rx.lang.scala.{ Observable, Observer, Subscription }
import scala.util.parsing.json.JSON

case class Mention(time: DateTime, name: String, screenName: String, picture: String)

trait TwitterClient extends OAuthSupport {
  val TWITTER_REST_API_HOST = "https://api.twitter.com"
  val TWITTER_STREAMING_API_HOST = "https://userstream.twitter.com"
  val MENTIONS_REST_PATH = "/1.1/statuses/mentions_timeline.json"
  val USER_STREAMING_PATH = "/1.1/user.json"

  val TWITTER_USER_MENTIONS_REST_URI =
    TWITTER_REST_API_HOST + MENTIONS_REST_PATH + "?count=2&since_id=14927799"
  val TWITTER_USER_STREAM_URI = TWITTER_STREAMING_API_HOST + USER_STREAMING_PATH

  val client: CloseableHttpAsyncClient = {
    val underlying = HttpAsyncClients.createDefault
    underlying.start()
    underlying
  }

  val timeParser = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy")

  def getResponse(uri: String, oauthHeader: String): Observable[ObservableHttpResponse] = {
    val apacheReq = new HttpGet(uri)
    apacheReq.setHeader("Authorization", oauthHeader)
    apacheReq.setHeader("Accept", "*/*")
    val apacheAsyncReq = HttpAsyncMethods.create(apacheReq)
    val observableHttp = ObservableHttp.createRequest(apacheAsyncReq, client)
    Observable(observableHttp.toObservable)
  }

  protected def getJson(httpResp: ObservableHttpResponse): Observable[Map[String, Any]] = {
    val bytesObs = Observable(httpResp.getContent)
    val stringObs = bytesObs.map(new String(_))
    stringObs.flatMap(s => JSON.parseFull(s) match {
      case Some(l: List[Any]) => {
        val tweets = l.asInstanceOf[List[Map[String, Any]]]
        Observable(tweets: _*)
      }
      case Some(m: Map[_, _]) =>
        Observable(m.asInstanceOf[Map[String, Any]])
      case _ => Observable.never
    })
  }

  protected def getMentions(l: List[Any]): Observable[Mention] = Observable(
    (observer: Observer[Mention]) => {
      try {
        l.foreach {
          case m: Map[_, _] => {
            val mention = getMention(m.asInstanceOf[Map[String, Any]])
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

  protected def getMention(m: Map[String, Any]): Mention = {
    val userMap: Map[String, Any] = m("user").asInstanceOf[Map[String, Any]]
    val name = userMap("name").asInstanceOf[String]
    val screenName = userMap("screen_name").asInstanceOf[String]
    val imageUrl = userMap("profile_image_url").asInstanceOf[String]
    val time = parseDate(m("created_at").asInstanceOf[String])
    Mention(time, name, screenName, imageUrl)
  }

  protected def jsonMentionsMe(m: Map[String, Any], myScreenName: String) = {
    val userMentionsOption = for {
      entities <- m.get("entities")
      userMentions <- entities.asInstanceOf[Map[String, Any]].get("user_mentions")
    } yield userMentions

    userMentionsOption match {
      case Some(mentions) => mentions.asInstanceOf[List[Map[String, Any]]] exists {
        case m: Map[_, _] => {
          val typedMap = m.asInstanceOf[Map[String, Any]]
          typedMap("screen_name").equals(myScreenName)
        }
      }
      case None => false
    }
  }

  protected def parseDate(s: String): DateTime = timeParser.parseDateTime(s)
}
