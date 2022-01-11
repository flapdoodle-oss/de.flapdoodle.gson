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

import java.util.Objects;
import java.util.Optional;
public abstract class JsonPath {
    public final JsonPath add(int index) {
        return new Index(this, index);
    }

    public final JsonPath add(String key) {
        return new Key(this, key);
    }

    public final boolean isRoot() {
        return this instanceof Root;
    }

    public static class Root extends JsonPath {
        private Root() {
            // singleton
        }

        @Override
        public String toString() {
            return "<root>";
        }
    }

    public static class Index extends JsonPath {
        private final JsonPath parent;

        private final int index;

        public Index(JsonPath parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        public JsonPath parent() {
            return parent;
        }

        public int index() {
            return index;
        }

        @Override
        public String toString() {
            return parent + ".[" + index + "]";
        }
    }
    public static class Key extends JsonPath {
        private final JsonPath parent;

        private final String key;

        public Key(final JsonPath parent, final String key) {
            this.parent = parent;
            this.key = key;
        }

        public JsonPath parent() {
            return parent;
        }

        public String name() {
            return key;
        }

        @Override
        public String toString() {
            return parent + "." + key;
        }
    }

    public static JsonPath root() {
        return new Root();
    }
}
