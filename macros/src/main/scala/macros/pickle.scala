package macros

import scala.annotation.StaticAnnotation
import scala.collection.immutable.Seq
import scala.meta._

class pickle extends StaticAnnotation {
	inline def apply(defn: Any): Any = meta {
		defn match {
			case trt @ Defn.Trait(_, name, tparam, _, _) =>
				pickle.genCompanion(trt, name, tparam)

			case cls @ Defn.Class(_, name, tparam, _, _) =>
				pickle.genCompanion(cls, name, tparam)

			case Term.Block(Seq(cls @ Defn.Class(_, name, tparam, _, _), companion: Defn.Object)) =>
				pickle.augmentCompanion(cls, name, tparam, companion)

			case Term.Block(Seq(trt @ Defn.Trait(_, name, tparam, _, _), companion: Defn.Object)) =>
				pickle.augmentCompanion(trt, name, tparam, companion)

			case obj: Defn.Object =>
				pickle.augmentCaseObject(obj)

			case _ =>
				println(defn.syntax)
				println(defn.structure)
				abort("@pickleable must annotate a sealed trait or a case class.")
		}
	}
}

object pickle {
	def genCompanion(defn: Defn, tpe: Type.Name, tparam: Seq[Type.Param]): Term.Block = {
		val pickler = genPickler(tpe.value, tpe, tparam)
		val companion = q"object ${Term.Name(tpe.value)} { $pickler }"
		Term.Block(Seq(defn, companion))
	}

	def augmentCompanion(defn: Defn, tpe: Type.Name, tparam: Seq[Type.Param], companion: Defn.Object): Term.Block = {
		val pickler = genPickler(tpe.value, tpe, tparam)
		val templateStats = companion.templ.stats.getOrElse(Nil) :+ pickler
		val newCompanion = companion.copy(templ = companion.templ.copy(stats = Some(templateStats)))
		Term.Block(Seq(defn, newCompanion))
	}

	def augmentCaseObject(obj: Defn.Object): Term.Block = {
		Term.Block(Seq(obj, genPickler(obj.name.value, Type.Singleton(obj.name), Nil)))
	}

	def genPickler(name: String, tpe: Type, tparam: Seq[Type.Param]): Defn.Val = {
		val fullType = tparam match {
			case Nil => tpe
			case list =>
				val placeholders = list.map { case Type.Param(_, _, _, bounds, _, _) => Type.Placeholder(bounds) }
				Type.Apply(tpe, placeholders)
		}
		q"""
			implicit val ${Pat.Var.Term(Term.Name(name + "Pickler"))}: _root_.boopickle.Pickler[$fullType] = {
				import _root_.boopickle.DefaultBasic._
				PicklerGenerator.generatePickler[$fullType]
			}
		"""
	}
}
