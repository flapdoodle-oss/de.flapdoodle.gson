package de.flapdoodle.gson;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

class JsonWalkerTest {
    private static final Gson TEST_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    public void walkSampleJson() {
        JsonWalker.walk(json("sample.json"), new TreeListener() {
            @Override
            public JsonVisitResult inspect(final JsonPath path, final ElementType elementType, final JsonElement jsonElement) {
                System.out.println(path);
                return JsonVisitResult.CONTINUE;
            }

            @Override
            public void inspectionDone(final JsonPath path, final ElementType elementType, final JsonElement jsonElement) {

            }
        });
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

    private static JsonElement json(String resourceName) {
        try {
            final String resourceContent = Resources.toString(Resources.getResource(JsonWalkerTest.class, resourceName), StandardCharsets.UTF_8);
            return JsonParser.parseString(resourceContent);
        } catch ( IOException e ) {
            throw new RuntimeException("could not read json: " + resourceName, e);
        }
    }
}
