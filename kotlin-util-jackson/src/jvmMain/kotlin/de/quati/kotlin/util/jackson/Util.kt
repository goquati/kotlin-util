package de.quati.kotlin.util.jackson

import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.json.JsonMapper


public fun JsonMapper.Builder.addSimpleModule(block: SimpleModule.() -> Unit): JsonMapper.Builder =
    addModule(SimpleModule().apply(block))

public fun <T : Any> SimpleModule.add(serializer: QuatiJacksonStringSerializer<T>) {
    addDeserializer(serializer.clazz.java, serializer.jDeserializer())
    addSerializer(serializer.clazz.java, serializer.jSerializer())
}

public fun SimpleModule.add(serializers: Iterable<QuatiJacksonStringSerializer<*>>) {
    serializers.forEach { add(it) }
}
