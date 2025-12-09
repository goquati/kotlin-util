package io.github.goquati.kotlin.util.serialization

import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder


public fun JsonBuilder.serializersModule(block: SerializersModuleBuilder.() -> Unit) {
    serializersModule = SerializersModule(block)
}

public fun <T : Any> SerializersModuleBuilder.add(serializer: QuatiKotlinxStringSerializer<T>) {
    contextual(serializer.clazz, serializer)
}

public fun SerializersModuleBuilder.add(serializers: Iterable<QuatiKotlinxStringSerializer<*>>) {
    serializers.forEach { add(it) }
}
