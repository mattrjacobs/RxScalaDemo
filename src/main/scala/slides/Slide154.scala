package com.mattrjacobs.rxscala.slides

import com.netflix.hystrix.{ HystrixCommand, HystrixCommandGroupKey }
import rx.lang.scala.Observable

trait Slide154 extends App {
  trait User {
    val username: String
  }

  trait Geo {
    val country: String
  }

  trait Request

  val userId = 12345L
  val request: Request

  val groupKey: HystrixCommandGroupKey = null

  class GetUserCommand(userId: Long) extends HystrixCommand[User](groupKey) {
    override def run(): User = null
  }

  class GetGeoCommand(request: Request) extends HystrixCommand[Geo](groupKey) {
    override def run(): Geo = null
  }

  val user: Observable[User] =
    new Observable(new GetUserCommand(userId).observe())
  val geo: Observable[Geo] =
    new Observable(new GetGeoCommand(request).observe())

  user.zip(geo).map {
    case (u, g) => Map("username" -> u.username,
      "currentLocation" -> g.country)
  }
}

