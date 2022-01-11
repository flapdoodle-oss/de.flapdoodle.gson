package de.flapdoodle.gson;

import java.util.Map.Entry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
public class JsonWalker {
    public static void walk(JsonElement jsonElement, TreeListener treeListener) {
        walk(JsonPath.root(), jsonElement, treeListener);
    }

    private static JsonVisitResult walk(JsonPath path, JsonElement jsonElement, TreeListener treeListener) {
        final ElementType elementType = ElementType.of(jsonElement);
        final JsonVisitResult visitResult = treeListener.inspect(path, elementType, jsonElement);
        switch ( visitResult ) {
            case TERMINATE:
                return visitResult;
            case CONTINUE:
                switch ( elementType ) {
                    case OBJECT:
                        final JsonObject jsonObject = jsonElement.getAsJsonObject();
                        for ( Entry<String, JsonElement> entry : jsonObject.entrySet() ) {
                            walk(path.add(entry.getKey()), entry.getValue(), treeListener);
                        }
                        break;
                    case ARRAY:
                        final JsonArray jsonArray = jsonElement.getAsJsonArray();
                        for (int i=0;i< jsonArray.size();i++) {
                            walk(path.add(i), jsonArray.get(i), treeListener);
                        }
                        break;
                    case PRIMITIVE:

                        break;
                    case NULL:

                        break;
                }
                break;
        }
        treeListener.inspectionDone(path, elementType, jsonElement);

        return JsonVisitResult.CONTINUE;
    }
}
