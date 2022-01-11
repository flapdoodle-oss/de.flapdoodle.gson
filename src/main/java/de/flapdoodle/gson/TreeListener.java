package de.flapdoodle.gson;

import com.google.gson.JsonElement;
public interface TreeListener {
    JsonVisitResult inspect(JsonPath path, ElementType elementType, JsonElement jsonElement);
    void inspectionDone(JsonPath path, ElementType elementType, JsonElement jsonElement);
}
