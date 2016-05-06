package fands.action;

import fands.support.JsonAction;
import org.json.JSONObject;

import java.io.IOException;

public class ErrorAction extends JsonAction {

    private Exception exception;
    private String exceptionStack;

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setExceptionStack(String exceptionStack) {
        this.exceptionStack = exceptionStack;
    }

    public void error() {
        JSONObject object = new JSONObject();
        object.put("error", exception.getMessage());
        object.put("class", exception.getClass());
        object.put("stack", exceptionStack);
        try {
            responseJson(object.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
