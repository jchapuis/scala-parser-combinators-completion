/*                                                       *\
**  scala-parser-combinators-completion                  **
**  Copyright (c) by Nexthink S.A.                       **
**  Lausanne, Switzerland (http://www.nexthink.com)      **
**  Author: jonas.chapuis@nexthink.com                   **
\*                                                       */

package com.nexthink.util.parsing.distance

import java.util

import scala.collection.JavaConverters._

object DiceSorensenDistance {
  def diceSorensenSimilarity(a: String, b: String): Double = {
    val aWords            = tokenizeWords(a.toLowerCase)
    val bWords            = tokenizeWords(b.toLowerCase)
    val aBigrams          = aWords.flatMap(bigramsWithAffixing).toList
    val bBigrams          = bWords.flatMap(bigramsWithAffixing).toList
    val matchesSearchList = new util.LinkedList[String](bBigrams.asJava)

    def hasMatchingBigramInB(bigram: String) = {
      def findAndRemoveBigramIter(bigram: String, iterator: util.Iterator[String]): Boolean = {
        if (!iterator.hasNext) {
          false
        } else if (iterator.next() == bigram) {
          iterator.remove()
          true
        } else {
          findAndRemoveBigramIter(bigram, iterator)
        }
      }
      findAndRemoveBigramIter(bigram, matchesSearchList.iterator())
    }

    val intersection = aBigrams.count(hasMatchingBigramInB)
    2.0 * intersection / (aBigrams.size + bBigrams.size)
  }

}
