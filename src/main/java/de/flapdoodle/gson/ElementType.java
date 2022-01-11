package de.flapdoodle.gson;

import com.google.gson.JsonElement;
public enum ElementType {
    OBJECT,
    ARRAY,
    PRIMITIVE,
    NULL;

    public static ElementType of(JsonElement element) {
        if (element.isJsonArray()) {
            return ElementType.ARRAY;
        }
        if (element.isJsonPrimitive()) {
            return ElementType.PRIMITIVE;
        }
        if (element.isJsonObject()) {
            return ElementType.OBJECT;
        }
        if (element.isJsonNull()) {
            return ElementType.NULL;
        }
        throw new IllegalArgumentException("unknown type: " + element);
    }
}
