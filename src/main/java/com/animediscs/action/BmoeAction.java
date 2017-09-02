package com.animediscs.action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class BmoeAction {

    private String data = "本日暂无数据";

    public String list() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = formatter.format(LocalDateTime.now());
        File path = new File("/home/ubuntu/bmoe-2017/output/" + date);
        if (path.exists() && path.isDirectory()) {
            File[] files = path.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".txt"));
            Arrays.sort(files, (o1, o2) -> o2.getName().compareTo(o1.getName()));
            StringBuilder builder = new StringBuilder();

            appendStart(builder, "output", "连记票统计-姓名顺序排序");
            try {
                Files.readAllLines(new File(path.getParent(), "output.txt").toPath()).forEach(line -> {
                    appendLine(builder, line);
                });
            } catch (IOException e) {
                appendLine(builder, e.getMessage());
            }
            appendEnd(builder);

            appendStart(builder, "output-sort", "连记票统计-从多到少排序");
            try {
                Files.readAllLines(new File(path.getParent(), "output-sort.txt").toPath()).forEach(line -> {
                    appendLine(builder, line);
                });
            } catch (IOException e) {
                appendLine(builder, e.getMessage());
            }
            appendEnd(builder);

            builder.append("<hr>");

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                appendStart(builder, "data" + i, file.getName());
                try {
                    Files.readAllLines(file.toPath()).forEach(line -> {
                        appendLine(builder, line);
                    });
                } catch (IOException e) {
                    appendLine(builder, e.getMessage());
                }
                appendEnd(builder);
            }

            data = builder.toString();
        }
        return "success";
    }

    private void appendStart(StringBuilder builder, String id, String text) {
        builder.append("<button type='button' class='btn btn-primary' data-toggle='collapse' ");
        builder.append("data-target='#").append(id).append("'>").append(text).append("</button><br/>");
        builder.append("<div id='").append(id).append("' class='collapse'>");
    }

    private void appendLine(StringBuilder builder, String line) {
        builder.append(line).append("<br>");
    }

    private void appendEnd(StringBuilder builder) {
        builder.append("</div>");
    }

    public String getData() {
        return data;
    }
}
