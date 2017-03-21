package models;

import java.util.Map;

/**
 * 报表模板
 */
public class ChartModel {
    /* 标题 */
    private String title;
    /* 子标题 */
    private String subtitle;
    /* 类型 */
    private String type = "spline";
    /* Y轴标题 */
    private String yAxis_title;
    /* Y 数据列 */
    private Map<Object, ChartData[]> series;
    /* 数据点提示框数据单位 */
    private String tooltip;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getyAxis_title() {
        return yAxis_title;
    }

    public void setyAxis_title(String yAxis_title) {
        this.yAxis_title = yAxis_title;
    }

    public Map<Object, ChartData[]> getSeries() {
        return series;
    }

    public void setSeries(Map<Object, ChartData[]> series) {
        this.series = series;
    }
}
