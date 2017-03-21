package models;

import java.util.Map;

/**
 * 饼状图模板
 */
public class PieModel {
    /* 标题 */
    private String title;
    /* 子标题 */
    private String subtitle;
    /* 数据列 */
    PieData[] series;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public PieData[] getSeries() {
        return series;
    }

    public void setSeries(PieData[] series) {
        this.series = series;
    }

}
