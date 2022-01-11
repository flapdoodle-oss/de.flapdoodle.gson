package de.flapdoodle.gson;

import java.util.Objects;
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
        public boolean equals(final Object o) {
            return this == o || o != null && getClass() == o.getClass();
        }

        @Override
        public int hashCode() {
            return 32;
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

        public int index() {
            return index;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            final Index index1 = (Index) o;
            return index == index1.index && parent.equals(index1.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parent, index);
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

        public String name() {
            return key;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            final Key key1 = (Key) o;
            return parent.equals(key1.parent) && key.equals(key1.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parent, key);
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
