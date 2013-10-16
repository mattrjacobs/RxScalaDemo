package com.mattrjacobs.rxscala.slides

import rx.lang.scala.Observable

trait Slide141 extends App {

  trait VideoService {
    trait Video {
      val id: Long
      def getMetadata(): Observable[Map[String, Object]]
    }

    def getVideos(userId: Long): Observable[Video]
    def getBookmark(video: Video, userId: Long): Observable[Map[String, Object]]
    def getRating(video: Video, userId: Long): Observable[Map[String, Object]]
  }

  val videoService: VideoService

  def getVideos(userId: Long): Observable[Map[String, Any]] =
    videoService.getVideos(userId)
      .take(10)
      .flatMap(video => {
        val metadata = video.getMetadata.map(md =>
          Map("title" -> md.get("title"),
            "length" -> md.get("duration")))
        val bookmark = videoService.getBookmark(video, userId).map(b =>
          Map("position" -> b.get("position")))
        val rating = videoService.getRating(video, userId).map(r =>
          Map("rating" -> r.get("rating")))
        Observable.zip(Observable(List(metadata, bookmark, rating): _*)).map {
          case m :: b :: r :: Nil =>
            Map("id" -> video.id) ++ m ++ b ++ r
        }
      })
}

