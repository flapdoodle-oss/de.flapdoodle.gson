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

import java.util.regex.Pattern;
public abstract class JsonPathMatcher {

    public final JsonPathMatcher indexBetween(int min, int max) {
        return new JsonPathMatcher.Index(this, min, max);
    }

    public final JsonPathMatcher match(Pattern pattern) {
        return new JsonPathMatcher.Key(this, pattern);
    }

    public final JsonPathMatcher match(String key) {
        return new JsonPathMatcher.Key(this, Pattern.compile(Pattern.quote(key)));
    }

    public abstract boolean matches(JsonPath path);

    public static class Root extends JsonPathMatcher {
        private Root() {
            // singleton
        }

        @Override
        public boolean matches(final JsonPath path) {
            return path.isRoot();
        }

        @Override
        public String toString() {
            return "<root>";
        }
    }

    public static class Index extends JsonPathMatcher {
        private final JsonPathMatcher parent;

        private final int min;

        private final int max;

        public Index(JsonPathMatcher parent, int min, int max) {
            this.parent = parent;
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean matches(final JsonPath path) {
            if (path instanceof JsonPath.Index) {
                final JsonPath.Index indexPath = (JsonPath.Index) path;
                final int index = indexPath.index();
                return parent.matches(indexPath.parent()) && min <= index && index <= max;
            }
            return false;
        }

        @Override
        public String toString() {
            return parent + ".[" + min+"-"+max + "]";
        }
    }

    public static class Key extends JsonPathMatcher {
        private final JsonPathMatcher parent;

        private Pattern pattern;

        public Key(final JsonPathMatcher parent, final Pattern pattern) {
            this.parent = parent;
            this.pattern = pattern;
        }

        @Override
        public boolean matches(final JsonPath path) {
            if (path instanceof JsonPath.Key) {
                final JsonPath.Key keyPath = (JsonPath.Key) path;
                final String key = keyPath.name();
                return parent.matches(keyPath.parent()) && pattern.matcher(key).matches();
            }
            return false;
        }

        @Override
        public String toString() {
            return parent + "." + parent;
        }
    }

    public static JsonPathMatcher root() {
        return new JsonPathMatcher.Root();
    }


}
