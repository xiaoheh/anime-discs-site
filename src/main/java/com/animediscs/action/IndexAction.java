package com.animediscs.action;

public class IndexAction {

    private String pass;

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String execute() {
        if ("123456".equals(pass)) {
            return "admin";
        } else {
            return "success";
        }
    }

}
