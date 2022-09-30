// package cats.derived // compiles when this is uncommented

import cats._
import cats.derived._

final case class Cat(value: String) extends AnyVal

final class CatSuite extends KittensSuite {
  test("semiauto.hash[Cat] should compile"){
    val hash: Hash[Cat] = semiauto.hash

    assertEquals(hash.hash(Cat("")), hash.hash(Cat("")))
  }
}
