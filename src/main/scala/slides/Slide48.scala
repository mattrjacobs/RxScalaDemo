package com.mattrjacobs.rxscala.slides

import rx.Observable

trait Slide48 extends App {

  case class VideoList(vs: List[VideoMetadata])
  case class VideoMetadata(id: Long, name: String)
  case class VideoRating(id: Long, rating: Double)
  case class VideoBookmark(id: Long, bookmark: Double)

  object Blocking {
    trait VideoService {
      def getPersonalizedListOfMovies(userId: Long): VideoList
      def getBookmark(userId: Long, videoId: Long): VideoBookmark
      def getRating(userId: Long, videoId: Long): VideoRating
      def getMetadata(videoId: Long): VideoMetadata
    }
  }

  object Observable {
    trait VideoService {
      def getPersonalizedListOfMovies(userId: Long): Observable[VideoList]
      def getBookmark(userId: Long, videoId: Long): Observable[VideoBookmark]
      def getRating(userId: Long, videoId: Long): Observable[VideoRating]
      def getMetadata(videoId: Long): Observable[VideoMetadata]
    }
  }

}
