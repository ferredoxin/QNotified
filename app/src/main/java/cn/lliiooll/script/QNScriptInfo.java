package cn.lliiooll.script;

import cn.lliiooll.utils.QNScriptUtils;

import java.util.List;

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
            // System.out.println(infoLabel.toString());
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
        this.decs = QNScriptUtils.isNullOrEmpty(value) ? "none" : QNScriptUtils.replaceSpace(value);
    }

    private void setLabel(String value) {
        this.label = QNScriptUtils.isNullOrEmpty(value) ? null : QNScriptUtils.replaceSpace(value);
    }

    private void setVersion(String value) {
        this.version = QNScriptUtils.isNullOrEmpty(value) ? "none" : QNScriptUtils.replaceSpace(value);
    }

    private void setName(String value) {
        this.name = QNScriptUtils.isNullOrEmpty(value) ? "none" : QNScriptUtils.replaceSpace(value);
    }

    private void setAuthor(String value) {
        this.author = QNScriptUtils.isNullOrEmpty(value) ? "none" : QNScriptUtils.replaceSpace(value);
    }

    public String getDecs() {
        return this.decs;
    }

    public String getLabel() {
        return this.label;
    }

    public String getVersion() {
        return this.version;
    }

    public String getName() {
        return this.name;
    }

    public String getAuthor() {
        return this.author;
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
