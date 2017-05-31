package game.skeleton

import boopickle.DefaultBasic._
import java.nio.ByteBuffer

package object node {
	/** Pickle a value of type T into an array of bytes. */
	private[node] def pickle[T: Pickler](value: T): Array[Byte] = {
		val buffer = Pickle.intoBytes(value)
		val array = new Array[Byte](buffer.remaining)
		buffer.get(array)
		array
	}

	/** Unpickle a value of type T from an array of bytes. */
	private[node] def unpickle[T: Pickler](buffer: Array[Byte]): T = {
		Unpickle[T].fromBytes(ByteBuffer.wrap(buffer))
	}
}
