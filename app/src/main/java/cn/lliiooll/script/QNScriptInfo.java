
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
package cn.lliiooll.script;

import java.util.*;

public class QNScriptInfo {
    
    private String decs;// 简介
    private String label;// 标签
    private String version;// 版本
    private String name;// 名称
    private String author;// 作者
    
    /**
     * 举例:
     * //@QNScriptInfoStart
     * //@author:          lliiooll
     * //@name:            示例脚本
     * //@version:         0.0.1
     * //@label:           demo
     * //@decs:            用于QN脚本开发的入门示例
     * //@QNScriptInfoEnd
     *
     * @param lines 处理的脚本
     * @return QNScriptInfo实例
     */
    public static QNScriptInfo parse(List<String> lines) {
        QNScriptInfo info = new QNScriptInfo();
        String line = lines.get(0);
        if (line.startsWith("//@")) {// 是标识
            QNScriptInfoLabel infoLabel = QNScriptInfoLabel.parse(line);
            if (infoLabel.getKey() != null && infoLabel.getKey().equalsIgnoreCase("QNScriptInfoStart")) {
                for (int i = 1; i < lines.size(); i++) {// 确保从第二行读取
                    line = lines.get(i);
                    if (line.startsWith("//@")) {
                        infoLabel = QNScriptInfoLabel.parse(line);
                        if (infoLabel.getKey() != null) {
                            if (infoLabel.getKey().equalsIgnoreCase("QNScriptInfoEnd")) {
                                break;
                            }
                            switch (infoLabel.getKey()) {
                                case "author":
                                    info.setAuthor(infoLabel.getValue());
                                case "name":
                                    info.setName(infoLabel.getValue());
                                case "version":
                                    info.setVersion(infoLabel.getValue());
                                case "label":
                                    info.setLabel(infoLabel.getValue());
                                case "decs":
                                    info.setDecs(infoLabel.getValue());
                                default:
                            }
                        }
                    }
                }
            }
        }
        return info;
    }
    
    private void setDecs(String value) {
        decs = QNScriptUtils.isNullOrEmpty(value) ? "none" : QNScriptUtils.replaceSpace(value);
    }
    
    private void setLabel(String value) {
        label = QNScriptUtils.isNullOrEmpty(value) ? null : QNScriptUtils.replaceSpace(value);
    }
    
    private void setVersion(String value) {
        version = QNScriptUtils.isNullOrEmpty(value) ? "none" : QNScriptUtils.replaceSpace(value);
    }
    
    private void setName(String value) {
        name = QNScriptUtils.isNullOrEmpty(value) ? "none" : QNScriptUtils.replaceSpace(value);
    }
    
    private void setAuthor(String value) {
        author = QNScriptUtils.isNullOrEmpty(value) ? "none" : QNScriptUtils.replaceSpace(value);
    }
    
    public String getDecs() {
        return decs;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAuthor() {
        return author;
    }
    
    
    public static class QNScriptInfoLabel {
        
        private String key;
        private String value;
        
        public static QNScriptInfoLabel parse(String line) {
            QNScriptInfoLabel label = new QNScriptInfoLabel();
            String[] parse = line.split(":");
            if (parse.length > 0) {
                label.key = parse[0].replace("//@", "");
            }
            if (parse.length > 1) {
                label.value = parse[1];
            }
            return label;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return "key:" + getKey() + " value:" + getValue();
        }
    }
}
