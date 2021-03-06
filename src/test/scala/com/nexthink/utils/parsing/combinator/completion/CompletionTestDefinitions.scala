package com.nexthink.utils.parsing.combinator.completion

import org.json4s.JValue
import org.scalatest.Matchers

object CompletionTestDefinitions {
  trait AssertionSet extends Product with Serializable {
    def tag: String
  }
  case class Default(strings: String*) extends AssertionSet {
    def tag: String = ""
  }
  case class Tagged(tag: String, desc: Option[String], score: Int, meta: Option[JValue], entries: Seq[String], entryMetas: Seq[Option[JValue]]) extends AssertionSet
  case object Tagged {
    def apply(name: String, strings: String*): Tagged =
      Tagged(name, None, 0, None, strings, Nil)

    def apply(name: String, desc: Option[String], score: Int, strings: String*): Tagged =
      Tagged(name, desc, score, None, strings, Nil)
  }
}

trait CompletionTestAsserters extends CompletionTypes with Matchers {
  import CompletionTestDefinitions._
  def assertSetEquals(expected: AssertionSet, actual: CompletionSet): Unit =
    expected match {
      case default @ Default(_*) => {
        default.strings.zip(actual.stringEntries).foreach {
          case (e, a) => a shouldBe e
        }
      }
      case named @ Tagged(name, desc, score, meta, _, _) => {
        actual.tag.label shouldBe name
        actual.tag.score shouldBe score
        actual.tag.description shouldBe desc
        actual.tag.meta shouldBe meta
        named.entries.zip(actual.stringEntries).foreach {
          case (e, a) => a shouldBe e
        }
        named.entryMetas.zip(actual.entries.map(_.meta)).foreach{
          case (e, a) => a shouldBe e
        }
      }
    }
  def assertHasCompletions(expected: Set[AssertionSet], actual: Completions): Unit = {
    expected.toList.sortBy(_.tag).zip(actual.allSets.toList.sortBy(_.tag.label)).foreach {
      case (e, a) => assertSetEquals(e, a)
    }
  }
}
