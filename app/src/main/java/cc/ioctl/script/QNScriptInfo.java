/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package cc.ioctl.script;


import java.util.Arrays;
import java.util.List;

public class QNScriptInfo {

    /**
     * 作者
     */
    public final String author;
    /**
     * 名称
     */
    public final String name;
    /**
     * 标签（唯一标识）
     */
    public final String label;
    /**
     * 版本
     */
    public final String version;
    /**
     * 脚本简介
     */
    public final String decs;

    public QNScriptInfo(String name, String label, String author, String version, String decs) {
        this.name = name;
        this.label = label;
        this.author = author;
        this.version = version;
        this.decs = decs;
    }

    public static QNScriptInfo getInfo(String code) {
        String execute = code.replace(" ", "");
        if (!execute.startsWith("//InfoStart") && !execute.contains("//InfoEnd")) {
            return null;
        }
        String info = execute.substring(0, code.indexOf("//InfoEnd")).replace("//InfoStart", "");
        String[] e_1s = info.split("\n");
        Builder builder = QNScriptInfo.Builder.builder();
        for (String e_1 : e_1s) {
            List<String> e_2 = Arrays.asList(e_1.replace("//@", "").split(":"));
            if (e_2.size() > 1) {
                switch (e_2.get(0)) {
                    case "author":
                        builder.setAuthor(e_2.get(1));
                    case "name":
                        builder.setName(e_2.get(1));
                    case "version":
                        builder.setVersion(e_2.get(1));
                    case "label":
                        builder.setLabel(e_2.get(1));
                    case "decs":
                        builder.setDecs(e_2.get(1));
                    default:
                }
            } else {
                switch (e_2.get(0)) {
                    case "author":
                    case "name":
                    case "version":
                    case "label":
                        return null;
                    case "decs":
                        builder.setDecs("暂无");
                    default:
                }
            }
        }
        return builder.build();
    }

    public static class Builder {

        private String author;
        private String name;
        private String label;
        private String version;
        private String decs;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setAuthor(String value) {
            this.author = value;
            return this;
        }

        public Builder setName(String value) {
            this.name = value;
            return this;
        }

        public Builder setLabel(String value) {
            this.label = value;
            return this;
        }

        public Builder setVersion(String value) {
            this.version = value;
            return this;
        }

        public Builder setDecs(String value) {
            this.decs = value;
            return this;
        }

        public QNScriptInfo build() {
            return new QNScriptInfo(name, label, author, version, decs);
        }

    }
}
