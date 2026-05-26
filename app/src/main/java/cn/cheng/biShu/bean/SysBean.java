package cn.cheng.biShu.bean;

public class SysBean {

    // 是否开启动图过滤
    private boolean flagGif;

    // 是否开启打印网页源码
    private boolean flagHtml;

    // 是否开启打印网页日志
    private boolean flagLog;

    // 是否开启爬虫
    private boolean flagSpider;

    public boolean isFlagGif() {
        return flagGif;
    }

    public void setFlagGif(boolean flagGif) {
        this.flagGif = flagGif;
    }

    public boolean isFlagHtml() {
        return flagHtml;
    }

    public void setFlagHtml(boolean flagHtml) {
        this.flagHtml = flagHtml;
    }

    public boolean isFlagLog() {
        return flagLog;
    }

    public void setFlagLog(boolean flagLog) {
        this.flagLog = flagLog;
    }

    public boolean isFlagSpider() {
        return flagSpider;
    }

    public void setFlagSpider(boolean flagSpider) {
        this.flagSpider = flagSpider;
    }
}
