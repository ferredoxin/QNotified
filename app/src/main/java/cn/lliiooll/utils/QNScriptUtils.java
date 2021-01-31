package cn.lliiooll.utils;

import nil.nadph.qnotified.util.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QNScriptUtils {
    public static List<String> readLines(InputStream is) {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            //TODO: 上报错误
        }
        return lines;
    }

    public static List<String> readLines(File file) {
        try {
            return readLines(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Utils.log(e);
        }
        return new ArrayList<>();
    }

    public static boolean isNullOrEmpty(String value) {
        return value != null && value.replace(" ", "").length() < 1;
    }

    public static String replaceSpace(String value) {
        String result = value;
        while (result.startsWith(" ")) {
            result = result.replaceFirst(" ", "");
        }
        return result;
    }

    public static String parseListToString(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static boolean isArkApp(String msg) {
        return msg.startsWith("{") || msg.startsWith("<?xml");
    }

    public static InputStream getInputStream(String s) {
        return QNScriptUtils.class.getClassLoader().getResourceAsStream("assets/test.java");
    }
}
