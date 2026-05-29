package cn.cheng.biShu.bean;

import androidx.annotation.Nullable;

import cn.cheng.biShu.util.CommonUtils;

// 爬虫设置数据实体
public class SpiderBean {

    public SpiderBean() {}

    public SpiderBean(String url) {
        setUrl(url);
    }

    private String url;

    private String title;

    private String chapter;

    private String txtContent;

    private String filter;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getTxtContent() {
        return txtContent;
    }

    public void setTxtContent(String txtContent) {
        this.txtContent = txtContent;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isNotEmpty() {
        return url != null && title != null && chapter != null && txtContent != null;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (url == null || obj == null) return false;
        if (obj instanceof String) {
            return CommonUtils.getUrlDomain(url).equals(CommonUtils.getUrlDomain((String) obj));
        } else if (obj instanceof SpiderBean) {
            return CommonUtils.getUrlDomain(url).equals(CommonUtils.getUrlDomain(((SpiderBean) obj).getUrl()));
        }
        return false;
    }
}
