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
                    switch ( stackElementType ) {
                        case OBJECT:
                            current
                                    .getAsJsonObject()
                                    .add(((JsonPath.Key) path).name(), container);
                            stack.push(container);
                            break;
                        case ARRAY:
                            current
                                    .getAsJsonArray()
                                    .add(container);
                            stack.push(container);
                            break;
                        default:
                            throw new IllegalArgumentException("not supported: " + stackElementType);
                    }
                    break;
                case PRIMITIVE:
                case NULL:
                    final JsonElement copy = jsonElement.deepCopy();
                    switch ( stackElementType ) {
                        case OBJECT:
                            current
                                    .getAsJsonObject()
                                    .add(((JsonPath.Key) path).name(), copy);
                            break;
                        case ARRAY:
                            current
                                    .getAsJsonArray()
                                    .add(copy);
                            break;
                        default:
                            throw new IllegalArgumentException("not supported: " + stackElementType);
                    }

            }
        }

        return JsonVisitResult.CONTINUE;
    }

    private static boolean isValue(final ElementType elementType) {
        return elementType == ElementType.NULL || elementType == ElementType.PRIMITIVE;
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
                stack.pop();
                break;
        }

        if (path.isRoot()) {
            if (!stack.isEmpty()) {
                throw new IllegalStateException("stack is not empty: " + stack);
            }
        }
    }
}
