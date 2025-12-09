package io.github.goquati.kotlin.util.jackson

import tools.jackson.core.Version
import tools.jackson.core.json.PackageVersion
import tools.jackson.databind.module.SimpleModule

public class QuatiOptionModule() : SimpleModule() {
    private companion object {
        private const val NAME: String = "QuatiOptionModule"
    }

    override fun version(): Version? = PackageVersion.VERSION
    override fun getModuleName(): String = NAME
    override fun setupModule(context: SetupContext) {
        context.addSerializers(OptionSerializers)
        context.addDeserializers(OptionDeserializers)
        context.addTypeModifier(OptionTypeModifier)
        context.addSerializerModifier(OptionValueSerializerModifier())
    }
}