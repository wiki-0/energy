package models;

import java.util.Map;

/**
 * 双Y轴报表数据模型
 */
public class ChartModelDoubleYAxis {
    private String type;
    private ChartData[] arrays1;
    private ChartData[] arrays2;
    
    private String[] categories;
    private Double[] electricityConsumption;
    private Double[] waterConsumption;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public ChartData[] getArrays1() {
        return arrays1;
    }
    public void setArrays1(ChartData[] arrays1) {
        this.arrays1 = arrays1;
    }
    public ChartData[] getArrays2() {
        return arrays2;
    }
    public void setArrays2(ChartData[] arrays2) {
        this.arrays2 = arrays2;
    }
    public String[] getCategories() {
        return categories;
    }
    public void setCategories(String[] categories) {
        this.categories = categories;
    }
    public Double[] getElectricityConsumption() {
        return electricityConsumption;
    }
    public void setElectricityConsumption(Double[] electricityConsumption) {
        this.electricityConsumption = electricityConsumption;
    }
    public Double[] getWaterConsumption() {
        return waterConsumption;
    }
    public void setWaterConsumption(Double[] waterConsumption) {
        this.waterConsumption = waterConsumption;
    }

}
