package com.example

import scalaprops._
import scalaz._
import scalaz.std.anyVal._
import scalaz.std.tuple._

object Test1 extends Scalaprops {

  final case class Point[A](x: A, y: A)

  object Point {

    implicit def equal[A: Equal]: Equal[Point[A]] =
      Equal.equalBy(p => (p.x, p.y))

    implicit def gen[A: Gen]: Gen[Point[A]] =
      Gen[(A, A)].map{
        case (x, y) => Point(x, y)
      }

    implicit val functor: Functor[Point] =
      new Functor[Point] {
        def map[A, B](fa: Point[A])(f: A => B) =
          Point(f(fa.x), f(fa.y))
      }

  }

  val pointLaws = Properties.list(
    scalazlaws.equal.all[Point[Int]],
    scalazlaws.functor.all[Point]
  )

}
