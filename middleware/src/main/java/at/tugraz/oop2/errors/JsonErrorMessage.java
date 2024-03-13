package at.tugraz.oop2.errors;

import org.json.simple.JSONObject;

public class JsonErrorMessage extends JSONObject {

    public JsonErrorMessage(String message) {
        super();
        super.put("message", message);
    }
}
