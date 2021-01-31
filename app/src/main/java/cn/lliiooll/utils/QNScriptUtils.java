/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
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
}
