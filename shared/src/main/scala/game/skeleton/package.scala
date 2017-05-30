package game

import boopickle.DefaultBasic._
import java.nio.ByteBuffer

package object skeleton {
	private[skeleton] def pickle[T: Pickler](value: T): Array[Byte] = {
		val buffer = Pickle.intoBytes(value)
		val array = new Array[Byte](buffer.remaining)
		buffer.get(array)
		array
	}

	private[skeleton] def unpickle[T: Pickler](buffer: Array[Byte]): T = {
		Unpickle[T].fromBytes(ByteBuffer.wrap(buffer))
	}
}
