/**
 * Copyright (C) 2016
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.gson;

import java.util.ArrayDeque;
import java.util.Deque;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
public class JsonCopyListener implements TreeListener {
    private JsonElement copyRoot;

    private Deque<JsonElement> stack = new ArrayDeque<>();

    public JsonElement copy() {
        return copyRoot;
    }

    @Override
    public JsonVisitResult inspect(final JsonPath path, final ElementType elementType, final JsonElement jsonElement) {
        if (path.isRoot()) {
            copyRoot = createContainer(elementType);
            stack.push(copyRoot);
        } else {
            final JsonElement current = stack.element();
            final ElementType stackElementType = ElementType.of(current);

            switch ( elementType ) {
                case OBJECT:
                case ARRAY:
                    final JsonElement container = createContainer(elementType);
                    addElementToContainer(path, current, stackElementType, container);
                    stack.push(container);
                    break;
                case PRIMITIVE:
                case NULL:
                    final JsonElement copy = jsonElement.deepCopy();
                    addElementToContainer(path, current, stackElementType, copy);

            }
        }

        return JsonVisitResult.CONTINUE;
    }

    private void addElementToContainer(final JsonPath path, final JsonElement current, final ElementType stackElementType,
                                       final JsonElement container) {
        switch ( stackElementType ) {
            case OBJECT:
                current
                        .getAsJsonObject()
                        .add(((JsonPath.Key) path).name(), container);
                break;
            case ARRAY:
                current
                        .getAsJsonArray()
                        .add(container);
                break;
            default:
                throw new IllegalArgumentException("can not add to " + stackElementType);
        }
    }

    private static JsonElement createContainer(ElementType elementType) {
        switch ( elementType ) {
            case OBJECT:
                return new JsonObject();
            case ARRAY:
                return new JsonArray();
            default:
                throw new IllegalArgumentException("type not supported: " + elementType);
        }
    }

    @Override
    public void inspectionDone(final JsonPath path, final ElementType elementType, final JsonElement jsonElement) {
        switch ( elementType ) {
            case ARRAY:
            case OBJECT:
                final JsonElement copy = stack.pop();
                onInspectionDone(path, elementType, copy);
                break;
        }

        if (path.isRoot()) {
            if (!stack.isEmpty()) {
                throw new IllegalStateException("stack is not empty: " + stack);
            }
        }
    }

    public void onInspectionDone(final JsonPath path, final ElementType elementType, final JsonElement copy) {

    }
}
