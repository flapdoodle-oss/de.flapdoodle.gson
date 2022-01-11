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
