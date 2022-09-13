/*
 * Copyright (c) 2015 Miles Sabin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cats.derived

import cats.Eq
import cats.kernel.CommutativeSemigroup
import cats.kernel.laws.discipline.{CommutativeSemigroupTests, SerializableTests}
import org.scalacheck.Arbitrary

import scala.compiletime.*

class CommutativeSemigroupSuite extends KittensSuite:
  import CommutativeSemigroupSuite.*
  import TestDefns.*

  inline def tests[A]: CommutativeSemigroupTests[A] =
    CommutativeSemigroupTests[A](summonInline)

  inline def validate(inline instance: String): Unit =
    checkAll(s"$instance[CommutativeFoo]", tests[CommutativeFoo].commutativeSemigroup)
    checkAll(s"$instance[Recursive]", tests[Recursive].commutativeSemigroup)
    checkAll(s"$instance[BoxMul]", tests[BoxMul].commutativeSemigroup)
    checkAll(
      s"$instance is Serializable",
      SerializableTests.serializable(summonInline[CommutativeSemigroup[CommutativeFoo]])
    )
    test(s"$instance respects existing instances") {
      val box = summonInline[CommutativeSemigroup[BoxMul]]
      assert(box.combine(Box(Mul(5)), Box(Mul(5))).content.value == 25)
    }

  locally {
    import auto.commutativeSemigroup.given
    validate("auto.commutativeSemigroup")
  }

  locally {
    import semiCommutativeSemigroup.given
    validate("semiauto.commutativeSemigroup")
  }

  locally {
    import derivedCommutativeSemigroup.*
    val instance = "derived.commutativeSemigroup"
    // Copy pasted from `validate`
    checkAll(s"$instance[CommutativeFoo]", tests[CommutativeFoo].commutativeSemigroup)
    checkAll(s"$instance[BoxMul]", tests[BoxMul].commutativeSemigroup)
    checkAll(
      s"$instance is Serializable",
      SerializableTests.serializable(summonInline[CommutativeSemigroup[CommutativeFoo]])
    )
    test(s"$instance respects existing instances") {
      val box = summonInline[CommutativeSemigroup[BoxMul]]
      assert(box.combine(BoxMul(Box(Mul(5))), BoxMul(Box(Mul(5)))).x.content.value == 25)
    }
  }

end CommutativeSemigroupSuite

object CommutativeSemigroupSuite:
  import TestDefns.*

  type BoxMul = Box[Mul]

  object semiCommutativeSemigroup:
    given CommutativeSemigroup[CommutativeFoo] = semiauto.commutativeSemigroup
    given CommutativeSemigroup[Recursive] = semiauto.commutativeSemigroup
    given CommutativeSemigroup[BoxMul] = semiauto.commutativeSemigroup

  object derivedCommutativeSemigroup:
    case class CommutativeFoo(x: TestDefns.CommutativeFoo) derives CommutativeSemigroup
    case class BoxMul(x: CommutativeSemigroupSuite.BoxMul) derives CommutativeSemigroup

  final case class Mul(value: Int)
  object Mul:
    given Eq[Mul] = Eq.fromUniversalEquals
    given Arbitrary[Mul] = Arbitrary(Arbitrary.arbitrary[Int].map(apply))
    given CommutativeSemigroup[Mul] = (x, y) => Mul(x.value * y.value)

end CommutativeSemigroupSuite