package com.animediscs.support;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by fuhaiwei on 16/5/2.
 */
public class JsonAction {
    protected void responseJson(String text) throws IOException {
        ServletActionContext.getResponse().setContentType("text/json;charset=utf-8");
        PrintWriter out = ServletActionContext.getResponse().getWriter();
        out.print(text);
        out.flush();
        out.close();
    }

    protected void responseSuccess() throws IOException {
        responseJson("\"success\"");
    }

    protected void responseError(String error) throws IOException {
        JSONObject object = new JSONObject();
        object.put("error", error);
        responseJson(object.toString());
    }

}
