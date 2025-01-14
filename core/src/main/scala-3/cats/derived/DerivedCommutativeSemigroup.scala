package cats.derived

import cats.kernel.CommutativeSemigroup
import shapeless3.deriving.K0

import scala.annotation.implicitNotFound
import scala.compiletime.*

@implicitNotFound("""Could not derive an instance of CommutativeSemigroup[A] where A = ${A}.
Make sure that A is a case class where all fields have a CommutativeSemigroup instance.""")
type DerivedCommutativeSemigroup[A] = Derived[CommutativeSemigroup[A]]
object DerivedCommutativeSemigroup:
  type Or[A] = Derived.Or[CommutativeSemigroup[A]]
  inline def apply[A]: CommutativeSemigroup[A] =
    import DerivedCommutativeSemigroup.given
    summonInline[DerivedCommutativeSemigroup[A]].instance

  given [A](using inst: => K0.ProductInstances[Or, A]): DerivedCommutativeSemigroup[A] =
    given K0.ProductInstances[CommutativeSemigroup, A] = inst.unify
    new Product[CommutativeSemigroup, A] {}

  trait Product[F[x] <: CommutativeSemigroup[x], A](using inst: K0.ProductInstances[F, A])
      extends DerivedSemigroup.Product[F, A],
        CommutativeSemigroup[A]
