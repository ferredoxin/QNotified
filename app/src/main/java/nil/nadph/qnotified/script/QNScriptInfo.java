package nil.nadph.qnotified.script;


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
