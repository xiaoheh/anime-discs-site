package fands.support;

import org.apache.struts2.ServletActionContext;

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
}
