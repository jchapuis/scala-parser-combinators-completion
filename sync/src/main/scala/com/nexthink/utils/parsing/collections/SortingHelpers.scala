package com.nexthink.utils.parsing.collections

object SortingHelpers {
  def lazyQuicksort[A](xs: Stream[A])(implicit o: Ordering[A]): Stream[A] = {
    import o._
    if (xs.isEmpty) xs
    else {
      val (smaller, bigger) = xs.tail.partition(_ < xs.head)
      lazyQuicksort(smaller) #::: xs.head #:: lazyQuicksort(bigger)
    }
  }
}
