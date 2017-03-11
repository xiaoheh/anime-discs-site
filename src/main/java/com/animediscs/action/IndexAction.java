package com.animediscs.action;

import com.animediscs.runner.AutoRunner;
import com.animediscs.spider.AmazonRankSpider;
import com.animediscs.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

public class IndexAction {

    private String method;
    private String pass;

    private AutoRunner autoRunner;
    private AmazonRankSpider amazonRankSpider;

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Autowired
    public void setAutoRunner(AutoRunner autoRunner) {
        this.autoRunner = autoRunner;
    }

    @Autowired
    public void setAmazonRankSpider(AmazonRankSpider amazonRankSpider) {
        this.amazonRankSpider = amazonRankSpider;
    }

    public String execute() {
        Properties properties = Helper.loadProperties("config/setting.properties");
        String password = properties.getProperty("admin.password");
        if ("update".equals(method)) {
            amazonRankSpider.doUpdateAll(autoRunner.getRankerRunner(), 3);
        }
        if (password.equals(pass)) {
            return "admin";
        } else {
            return "success";
        }
    }

}
