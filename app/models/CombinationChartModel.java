package models;

import java.util.Map;

/**
 * 混合型报表模板
 */
public class CombinationChartModel {
    /* 标题 */
    private String title;
    /* 子标题 */
    private String subtitle;
    /* X轴类型 */
    private String[] categories;
    /* Y轴标题 */
    private String yAxis_title;
    /* 数据 */
    private Map<String, CombinationChartData> series;
    /* 数据点提示框数据单位 */
    private String tooltip;

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

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getyAxis_title() {
        return yAxis_title;
    }

    public void setyAxis_title(String yAxis_title) {
        this.yAxis_title = yAxis_title;
    }

    public Map<String, CombinationChartData> getSeries() {
        return series;
    }

    public void setSeries(Map<String, CombinationChartData> series) {
        this.series = series;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

}
