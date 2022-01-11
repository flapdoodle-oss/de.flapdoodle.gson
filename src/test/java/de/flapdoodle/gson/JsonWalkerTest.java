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

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
class JsonWalkerTest {
    private static final Gson TEST_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void walkSampleJson() {
        final JsonPathMatcher stuffNameMatcher = JsonPathMatcher
                .root()
                .match("stuff")
                .match("name");

        AtomicReference<String> name=new AtomicReference<>();

        JsonWalker.walk(json("sample.json"), new TreeListener() {
            @Override
            public JsonVisitResult inspect(final JsonPath path, final ElementType elementType, final JsonElement jsonElement) {
                if (stuffNameMatcher.matches(path)) {
                    name.set(jsonElement.getAsJsonPrimitive().getAsString());
                }
                return JsonVisitResult.CONTINUE;
            }

            @Override
            public void inspectionDone(final JsonPath path, final ElementType elementType, final JsonElement jsonElement) {

            }
        });

        assertThat(name.get()).isEqualTo("Michael");
    }

    @Test
    public void copySampleJson() {
        final JsonElement source = json("sample.json");

        final JsonCopyListener copyListener = new JsonCopyListener();
        JsonWalker.walk(source, copyListener);
        final JsonElement copy = copyListener.copy();


        assertThat(TEST_GSON.toJson(copy))
                .isEqualTo(TEST_GSON.toJson(source));
    }

    @Test
    public void rewriteSampleJson() {
        final JsonElement source = json("sample.json");
        final JsonElement expected = json("sample-after-rewrite.json");

        final JsonPathMatcher stuffMatcher = JsonPathMatcher
                .root()
                .match("stuff");

        final JsonPathMatcher matchAlsoArraySomewhere = JsonPathMatcher
                .root()
                .match("array")
                .indexBetween(0, 100)
                .match("also array");

        final JsonCopyListener copyListener = new JsonCopyListener() {
            @Override
            public void onInspectionDone(final JsonPath path, final ElementType elementType, final JsonElement copy) {
                if (stuffMatcher.matches(path)) {
                    final JsonObject object = copy.getAsJsonObject();
                    object.addProperty("name","Peter");
                    object.addProperty("age", object.getAsJsonPrimitive("age").getAsInt()+1);
                    object.addProperty("flag", true);
                }

                if (matchAlsoArraySomewhere.matches(path)) {
                    final JsonArray array = copy.getAsJsonArray();
                    array.set(2, new JsonPrimitive(true));
                }
//                if (path.equals(JsonPath.root().add("array").add(2).add("also array"))) {
//                    final JsonArray array = copy.getAsJsonArray();
//                    array.set(2, new JsonPrimitive(true));
//                }
            }
        };

        JsonWalker.walk(source, copyListener);
        final JsonElement copy = copyListener.copy();


        assertThat(TEST_GSON.toJson(copy))
                .isEqualTo(TEST_GSON.toJson(expected));
    }

    private static JsonElement json(String resourceName) {
        try {
            final String resourceContent = Resources.toString(Resources.getResource(JsonWalkerTest.class, resourceName), StandardCharsets.UTF_8);
            return JsonParser.parseString(resourceContent);
        } catch ( IOException e ) {
            throw new RuntimeException("could not read json: " + resourceName, e);
        }
    }
}
