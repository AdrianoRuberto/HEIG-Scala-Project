package macros

import scala.annotation.StaticAnnotation
import scala.collection.immutable.Seq
import scala.meta._
import scala.meta.contrib._

class pickle extends StaticAnnotation {
	inline def apply(defn: Any): Any = meta {
		/** Composes the pickler's full type argument */
		def fullType(base: Type, params: Seq[Type.Param]): Type = {
			if (params.isEmpty) base
			else {
				val placeholders = params.map { case Type.Param(_, _, _, bounds, _, _) => Type.Placeholder(bounds) }
				Type.Apply(base, placeholders)
			}
		}

		/** Generates pickler code */
		def genPickler(name: String, tpe: Type): Defn.Val = {
			q"""
				implicit val ${Pat.Var.Term(Term.Name(name + "Pickler"))}: _root_.boopickle.Pickler[$tpe] = {
					import _root_.boopickle.DefaultBasic._
					PicklerGenerator.generatePickler[$tpe]
				}
			"""
		}

		/** Augments the existing companion object */
		def augmentCompanion(defn: Defn, name: String, tpe: Type, companion: Defn.Object): Term.Block = {
			val templateStats = companion.templ.stats.getOrElse(Nil) :+ genPickler(name, tpe)
			val newCompanion = companion.copy(templ = companion.templ.copy(stats = Some(templateStats)))
			Term.Block(Seq(defn, newCompanion))
		}

		defn match {
			case cls @ Defn.Class(mods, name, tparams, ctor, _) =>
				if (!cls.hasMod(mod"case")) abort("@pickle-annotated classes must be case classes")
				if (ctor.paramss.length != 1) abort("@pickle-annotated classes must have a single parameters list")

				// Ensure that the generated companion extends a function type
				val base = ctor"(..${ctor.paramss.flatten.map(_.decltpe.get.toType)}) => ${fullType(name, tparams)}"
				val pickler = genPickler(name.value, fullType(name, tparams))
				val companion = q"object ${Term.Name(name.value)} extends $base { $pickler }"

				Term.Block(Seq(cls, companion))

			case Term.Block(Seq(cls @ Defn.Class(_, name, tparams, _, _), companion: Defn.Object)) =>
				augmentCompanion(cls, name.value, fullType(name, tparams), companion)

			case Term.Block(Seq(trt @ Defn.Trait(_, name, tparams, _, _), companion: Defn.Object)) =>
				require(trt.hasMod(mod"sealed"), "@pickle-annotated traits must be sealed")
				augmentCompanion(trt, name.value, fullType(name, tparams), companion)

			case obj: Defn.Object =>
				val pickler = genPickler(obj.name.value, Type.Singleton(obj.name))
				Term.Block(Seq(obj, pickler))

			case _ =>
				println(defn.syntax)
				println(defn.structure)
				abort("@pickleable must annotate a sealed trait with a companion object or a case class.")
		}
	}
}

