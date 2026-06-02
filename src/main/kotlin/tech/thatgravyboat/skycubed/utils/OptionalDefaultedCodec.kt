package tech.thatgravyboat.skycubed.utils

import com.mojang.serialization.*
import java.util.*
import java.util.stream.Stream

data class OptionalDefaultedCodec<Type : Any>(val field: String,  val codec: Codec<Type>, val isLenient: Boolean = false, val constructor: () -> Type) : MapCodec<Optional<Type>>() {
    constructor(field: String,  codec: Codec<Type>, constructor: () -> Type) : this(field, codec, false, constructor)

    override fun <T> keys(ops: DynamicOps<T>): Stream<T> {
        return Stream.of<T>(ops.createString(field))
    }

    override fun <T> decode(
        ops: DynamicOps<T>,
        input: MapLike<T>,
    ): DataResult<Optional<Type>> {
        val key = ops.createString(field)
        if (input.entries().anyMatch { pair -> pair.first == key }) {
            if (input.get(key) == null) return DataResult.success(Optional.empty())

            val result = codec.parse(ops, input.get(key)).map { Optional.ofNullable(it) }

            if (isLenient && result.isError) {
                return DataResult.success(Optional.empty())
            }

            return result
        }

        return DataResult.success(Optional.ofNullable(constructor()))
    }

    override fun <T> encode(
        input: Optional<Type>,
        ops: DynamicOps<T>,
        prefix: RecordBuilder<T>,
    ): RecordBuilder<T> {
        if (input.isPresent) {
            return prefix.add(field, codec.encodeStart<T>(ops, input.get()))
        }
        return prefix
    }

    override fun hashCode(): Int = Objects.hash(field, codec, constructor, isLenient)

    override fun toString(): String = "OptionalDefaultedCodec[$field:$codec]"
}
