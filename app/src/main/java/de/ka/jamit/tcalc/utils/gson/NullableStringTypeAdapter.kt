package de.ka.jamit.tcalc.utils.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class NullableStringTypeAdapter<T>(
        private val serializer: (value: T) -> String,
        private val deserializer: (serialized: String) -> T
) : TypeAdapter<T?>() {

    override fun write(writer: JsonWriter, value: T?) {
        if (value == null) {
            writer.nullValue()
        } else {
            val serialized: String = serializer(value)
            writer.value(serialized)
        }
    }

    override fun read(reader: JsonReader): T? {
        return if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            null
        } else {
            val serialized: String = reader.nextString()
            deserializer(serialized)
        }
    }

}
