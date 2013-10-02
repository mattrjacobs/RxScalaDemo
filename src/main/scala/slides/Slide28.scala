import scala.concurrent.Future

trait Slide28 extends App {
  def getData1[T](): T

  def getData2[T](callback: T => Unit): Unit

  def getData3[T](): Future[T]

  def getData4[T](): Future[List[Future[T]]]
}
