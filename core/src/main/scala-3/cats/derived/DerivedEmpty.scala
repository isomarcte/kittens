package cats.derived

import alleycats.Empty
import shapeless3.deriving.K0

import scala.annotation.implicitNotFound
import scala.compiletime.*

@implicitNotFound("""Could not derive an instance of Empty[A] where A = ${A}.
Make sure that A satisfies one of the following conditions:
  * it is a case class where all fields have an Empty instance
  * it is a sealed trait where exactly one subclass has an Empty instance""")
type DerivedEmpty[A] = Derived[Empty[A]]
object DerivedEmpty:
  type Or[A] = Derived.Or[Empty[A]]
  inline def apply[A]: Empty[A] =
    import DerivedEmpty.given
    summonInline[DerivedEmpty[A]].instance

  given product[A](using inst: K0.ProductInstances[Or, A]): DerivedEmpty[A] =
    Empty(inst.unify.construct([a] => (_: Empty[a]).empty))

  inline given coproduct[A](using gen: K0.CoproductGeneric[A]): DerivedEmpty[A] =
    Empty(gen.withOnly[Or, A]([a <: A] => (_: Or[a]).unify.empty))
