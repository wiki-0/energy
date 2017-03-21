package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.ChartData;
import models.ChartModel;
import models.ChartModelDoubleYAxis;
import models.CombinationChartData;
import models.CombinationChartModel;
import models.EnergyMonitHailar;
import models.EnergyMonitQuanzhou;
import models.OneDayData;
import models.PieData;
import models.PieModel;
import net.sf.ezmorph.primitive.CharMorpher;
import play.db.jpa.JPA;
import play.mvc.Controller;
import utils.ExportUtils;

public class DataAnalysis extends Controller {

    public static void dataSingle() {
        render("dataAnalysis/data_singleFactor.html");
    }

    public static void single_hailaer() {
        render();
    }
    
    public static void single_quanzhou() {
        render();
    }

    public static void dataMulti() {
        render("dataAnalysis/data_multiFactor.html");
    }
    
    public static void multi_hailaer() {
        render("dataAnalysis/multi_hailaer.html");
    }
    
    public static void multi_quanzhou() {
        render("dataAnalysis/multi_quanzhou.html");
    }

    public static void data_history_hailaer() {
        render("dataAnalysis/data_history_hailaer.html");
    }
    
    public static void data_history_quanzhou(){
         render("dataAnalysis/data_history_quanzhou.html");
    }

  

    /**
     * 海拉尔室内外空气温度对比
     */
    public static void hailaerTemperatureCompare() {
        List<EnergyMonitHailar> list = getHailaerModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] parlourTempArrays = new ChartData[size];
        ChartData[] hallTempArrays = new ChartData[size];
        ChartData[] outdoorTempArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitHailar temp : list) {
            ChartData hallTempData = new ChartData();
            ChartData outdoorTempData = new ChartData();
            ChartData parlourTempData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            hallTempData.setTime(time);
            outdoorTempData.setTime(time);
            parlourTempData.setTime(time);
            hallTempData.setValue(temp.hall_temperature);
            outdoorTempData.setValue(temp.outdoor_temperature);
            parlourTempData.setValue(temp.parlour_temperature);
            hallTempArrays[i] = hallTempData;
            outdoorTempArrays[i] = outdoorTempData;
            parlourTempArrays[i] = parlourTempData;
            i++;
        }
        series.put("客厅温度", parlourTempArrays);
        series.put("门斗温度", hallTempArrays);
        series.put("室外温度", outdoorTempArrays);
        chartModel.setTitle("室内外空气温度逐时对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气温度 (°C)");
        chartModel.setTooltip("°C");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }

    public static void hailaerHumidityCompare() {
        List<EnergyMonitHailar> list = getHailaerModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] parlourHumidityArrays = new ChartData[size];
        ChartData[] hallHumidityArrays = new ChartData[size];
        ChartData[] outdoorHumidityArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitHailar temp : list) {
            ChartData parlourHumidityData = new ChartData();
            ChartData hallHumidityData = new ChartData();
            ChartData outdoorHumidityData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            hallHumidityData.setTime(time);
            parlourHumidityData.setTime(time);
            outdoorHumidityData.setTime(time);
            hallHumidityData.setValue(temp.hall_humidity);
            parlourHumidityData.setValue(temp.parlour_humidity);
            outdoorHumidityData.setValue(temp.outdoor_humidity);
            hallHumidityArrays[i] = hallHumidityData;
            parlourHumidityArrays[i] = parlourHumidityData;
            outdoorHumidityArrays[i] = outdoorHumidityData;
            i++;
        }
        series.put("门斗湿度", hallHumidityArrays);
        series.put("室外湿度", outdoorHumidityArrays);
        series.put("客厅湿度", parlourHumidityArrays);
        chartModel.setTitle("室内空气相对湿度逐时对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气相对湿度(%)");
        chartModel.setTooltip("%");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }

    public static void hailaerTemperatureMultiCompare() {
        String type = params.get("type");
        List<Object[]> list = new ArrayList<Object[]>();
        list.add(getHailaerObjectArray("parlour_temperature",type));
        list.add(getHailaerObjectArray("hall_temperature",type));
        list.add(getHailaerObjectArray("outdoor_temperature",type));
        CombinationChartModel combinationChartModel = new CombinationChartModel();
        Map<String, CombinationChartData> series = new HashMap<>();
        String[] maxArrays = new String[3];
        String[] minArrays = new String[3];
        String[] averageArrays = new String[3];
        String[] categories = new String[3];
        categories[0] = "客厅";
        categories[1] = "门斗";
        categories[2] = "室外";
        for (int i = 0; i < 3; i++) {
            maxArrays[i] = list.get(i)[0].toString();
            minArrays[i] = list.get(i)[1].toString();
            averageArrays[i] = list.get(i)[2].toString();
        }
        if (type != null && type.equals("month")) {
            series.put("月平均值", new CombinationChartData("column", averageArrays));
            series.put("月最大值", new CombinationChartData("scatter", maxArrays));
            series.put("月最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("月变化");
        } else {
            series.put("日平均值", new CombinationChartData("column", averageArrays));
            series.put("日最大值", new CombinationChartData("scatter", maxArrays));
            series.put("日最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("日变化");
        }
        combinationChartModel.setTitle("海拉尔室内外空气温度均峰值对比");
        combinationChartModel.setyAxis_title("空气温度 (°C)");
        combinationChartModel.setCategories(categories);
        combinationChartModel.setTooltip("°C");
        combinationChartModel.setSeries(series);
        renderJSON(combinationChartModel);
    }

    public static void hailaerHumidityMultiCompare() {
        String type = params.get("type");
        List<Object[]> list = new ArrayList<Object[]>();
        list.add(getHailaerObjectArray("parlour_humidity", type));
        list.add(getHailaerObjectArray("hall_humidity", type));
        list.add(getHailaerObjectArray("outdoor_humidity", type));
        CombinationChartModel combinationChartModel = new CombinationChartModel();
        Map<String, CombinationChartData> series = new HashMap<>();
        String[] maxArrays = new String[3];
        String[] minArrays = new String[3];
        String[] averageArrays = new String[3];
        String[] categories = new String[3];
        categories[0] = "客厅";
        categories[1] = "门斗";
        categories[2] = "室外";
        for (int i = 0; i < 3; i++) {
            maxArrays[i] = list.get(i)[0].toString();
            minArrays[i] = list.get(i)[1].toString();
            averageArrays[i] = list.get(i)[2].toString();
        }
        if (type != null && type.equals("month")) {
            series.put("月平均值", new CombinationChartData("column", averageArrays));
            series.put("月最大值", new CombinationChartData("scatter", maxArrays));
            series.put("月最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("月变化");
        } else {
            series.put("日平均值", new CombinationChartData("column", averageArrays));
            series.put("日最大值", new CombinationChartData("scatter", maxArrays));
            series.put("日最小值", new CombinationChartData("scatter", minArrays));
        }
        combinationChartModel.setSubtitle("日变化");
        combinationChartModel.setTitle("海拉尔室内外空气相对湿度均峰值对比");
        combinationChartModel.setyAxis_title("空气相对湿度 (%)");
        combinationChartModel.setCategories(categories);
        combinationChartModel.setTooltip("%");
        combinationChartModel.setSeries(series);
        renderJSON(combinationChartModel);
    }

    /**
     * 海拉尔室内空气温度对比
     */
    public static void hailaerIndoorTemperatureCompare() {
        List<EnergyMonitHailar> list = getHailaerModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] diningTempArrays = new ChartData[size];
        ChartData[] parlourTempArrays = new ChartData[size];
        ChartData[] bedroomTempArrays = new ChartData[size];
        ChartData[] hallTempArrays = new ChartData[size];
        ChartData[] washroomTempArrays = new ChartData[size];
        ChartData[] kitchenTempArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitHailar temp : list) {
            ChartData diningTempData = new ChartData();
            ChartData parlourTempData = new ChartData();
            ChartData bedroomTempData = new ChartData();
            ChartData hallTempData = new ChartData();
            ChartData washroomTempData = new ChartData();
            ChartData kitchenTempData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            diningTempData.setTime(time);
            parlourTempData.setTime(time);
            bedroomTempData.setTime(time);
            hallTempData.setTime(time);
            washroomTempData.setTime(time);
            kitchenTempData.setTime(time);
            diningTempData.setValue(temp.dining_temperature);
            parlourTempData.setValue(temp.parlour_temperature);
            bedroomTempData.setValue(temp.bedroom_temperature);
            hallTempData.setValue(temp.hall_temperature);
            washroomTempData.setValue(temp.washroom_temperature);
            kitchenTempData.setValue(temp.kitchen_temperature);
            diningTempArrays[i] = diningTempData;
            parlourTempArrays[i] = parlourTempData;
            bedroomTempArrays[i] = bedroomTempData;
            hallTempArrays[i] = hallTempData;
            washroomTempArrays[i] = washroomTempData;
            kitchenTempArrays[i] = kitchenTempData;
            i++;
        }
        series.put("饭厅温度", diningTempArrays);
        series.put("客厅温度", parlourTempArrays);
        series.put("卧室温度", bedroomTempArrays);
        series.put("门厅温度", hallTempArrays);
        series.put("卫生间温度", washroomTempArrays);
        series.put("厨房温度", kitchenTempArrays);
        chartModel.setTitle("室内空气温度逐时对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气温度 (°C)");
        chartModel.setTooltip("°C");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }

    public static void hailaerIndoorHumidityCompare() {
        List<EnergyMonitHailar> list = getHailaerModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] diningHumidityArrays = new ChartData[size];
        ChartData[] parlourHumidityArrays = new ChartData[size];
        ChartData[] bedroomHumidityArrays = new ChartData[size];
        ChartData[] hallHumidityArrays = new ChartData[size];
        ChartData[] washroomHumidityArrays = new ChartData[size];
        ChartData[] kitchenHumidityArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitHailar temp : list) {
            ChartData diningHumidityData = new ChartData();
            ChartData parlourHumidityData = new ChartData();
            ChartData bedroomHumidityData = new ChartData();
            ChartData hallHumidityData = new ChartData();
            ChartData washroomHumidityData = new ChartData();
            ChartData kitchenHumidityData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            diningHumidityData.setTime(time);
            parlourHumidityData.setTime(time);
            bedroomHumidityData.setTime(time);
            hallHumidityData.setTime(time);
            washroomHumidityData.setTime(time);
            kitchenHumidityData.setTime(time);
            diningHumidityData.setValue(temp.dining_humidity);
            parlourHumidityData.setValue(temp.parlour_humidity);
            bedroomHumidityData.setValue(temp.bedroom_humidity);
            hallHumidityData.setValue(temp.hall_humidity);
            washroomHumidityData.setValue(temp.washroom_humidity);
            kitchenHumidityData.setValue(temp.kitchen_humidity);
            diningHumidityArrays[i] = diningHumidityData;
            parlourHumidityArrays[i] = parlourHumidityData;
            bedroomHumidityArrays[i] = bedroomHumidityData;
            hallHumidityArrays[i] = hallHumidityData;
            washroomHumidityArrays[i] = washroomHumidityData;
            kitchenHumidityArrays[i] = kitchenHumidityData;
            i++;
        }
        series.put("饭厅湿度", diningHumidityArrays);
        series.put("客厅湿度", parlourHumidityArrays);
        series.put("卧室湿度", bedroomHumidityArrays);
        series.put("门厅湿度", hallHumidityArrays);
        series.put("卫生间湿度", washroomHumidityArrays);
        series.put("厨房湿度", kitchenHumidityArrays);
        chartModel.setTitle("室内空气相对湿度逐时对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气相对湿度(%)");
        chartModel.setTooltip("%");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }

    public static void hailaerIndoorTemperatureMultiCompare() {
        String type = params.get("type");
        List<Object[]> list = new ArrayList<Object[]>();
        list.add(getHailaerObjectArray("dining_temperature", type));
        list.add(getHailaerObjectArray("parlour_temperature", type));
        list.add(getHailaerObjectArray("bedroom_temperature", type));
        list.add(getHailaerObjectArray("hall_temperature", type));
        list.add(getHailaerObjectArray("washroom_temperature", type));
        list.add(getHailaerObjectArray("kitchen_temperature", type));
        CombinationChartModel combinationChartModel = new CombinationChartModel();
        Map<String, CombinationChartData> series = new HashMap<>();
        String[] maxArrays = new String[6];
        String[] minArrays = new String[6];
        String[] averageArrays = new String[6];
        String[] categories = new String[6];
        categories[0] = "饭厅";
        categories[1] = "客厅";
        categories[2] = "卧室";
        categories[3] = "门斗";
        categories[4] = "卫生间";
        categories[5] = "厨房";
        for (int i = 0; i < 6; i++) {
            maxArrays[i] = list.get(i)[0].toString();
            minArrays[i] = list.get(i)[1].toString();
            averageArrays[i] = list.get(i)[2].toString();
        }
        if (type != null && type.equals("month")) {
            series.put("月平均值", new CombinationChartData("column", averageArrays));
            series.put("月最大值", new CombinationChartData("scatter", maxArrays));
            series.put("月最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("月变化");
        } else {
            series.put("日平均值", new CombinationChartData("column", averageArrays));
            series.put("日最大值", new CombinationChartData("scatter", maxArrays));
            series.put("日最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("日变化");
        }
        combinationChartModel.setTitle("海拉尔室内不同房间空气温度均峰值对比");
        combinationChartModel.setyAxis_title("空气温度 (°C)");
        combinationChartModel.setCategories(categories);
        combinationChartModel.setTooltip("°C");
        combinationChartModel.setSeries(series);
        renderJSON(combinationChartModel);
    }

    public static void hailaerIndoorHumidityMultiCompare() {
        String type = params.get("type");
        List<Object[]> list = new ArrayList<Object[]>();
        list.add(getHailaerObjectArray("dining_humidity", type));
        list.add(getHailaerObjectArray("parlour_humidity", type));
        list.add(getHailaerObjectArray("bedroom_humidity", type));
        list.add(getHailaerObjectArray("hall_humidity", type));
        list.add(getHailaerObjectArray("washroom_humidity", type));
        list.add(getHailaerObjectArray("kitchen_humidity", type));
        CombinationChartModel combinationChartModel = new CombinationChartModel();
        Map<String, CombinationChartData> series = new HashMap<>();
        String[] maxArrays = new String[6];
        String[] minArrays = new String[6];
        String[] averageArrays = new String[6];
        String[] categories = new String[6];
        categories[0] = "饭厅";
        categories[1] = "客厅";
        categories[2] = "卧室";
        categories[3] = "门斗";
        categories[4] = "卫生间";
        categories[5] = "厨房";
        for (int i = 0; i < 6; i++) {
            maxArrays[i] = list.get(i)[0].toString();
            minArrays[i] = list.get(i)[1].toString();
            averageArrays[i] = list.get(i)[2].toString();
        }
        if (type != null && type.equals("month")) {
            series.put("月平均值", new CombinationChartData("column", averageArrays));
            series.put("月最大值", new CombinationChartData("scatter", maxArrays));
            series.put("月最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("月变化");
        } else if (type != null && type.equals("week")) {
            series.put("周平均值", new CombinationChartData("column", averageArrays));
            series.put("周最大值", new CombinationChartData("scatter", maxArrays));
            series.put("周最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("周变化");
        } else {
            series.put("日平均值", new CombinationChartData("column", averageArrays));
            series.put("日最大值", new CombinationChartData("scatter", maxArrays));
            series.put("日最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("日变化");
        }
        combinationChartModel.setTitle("海拉尔室内不同房间空气相对湿度均峰值对比");
        combinationChartModel.setyAxis_title("空气相对湿度(%)");
        combinationChartModel.setCategories(categories);
        combinationChartModel.setTooltip("%");
        combinationChartModel.setSeries(series);
        renderJSON(combinationChartModel);
    }

    /**
     * 海拉尔客厅内壁面温度对比
     */
    public static void hailaerIndoorWallTemperatureCompare() {
        List<EnergyMonitHailar> list = getHailaerModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] groundTempArrays = new ChartData[size];
        ChartData[] eastWallTempArrays = new ChartData[size];
        ChartData[] westWallTempArrays = new ChartData[size];
        ChartData[] southWallTempArrays = new ChartData[size];
        ChartData[] northWallTempArrays = new ChartData[size];
        ChartData[] roofTempArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitHailar temp : list) {
            ChartData groundTempData = new ChartData();
            ChartData eastWallTempData = new ChartData();
            ChartData westWallTempData = new ChartData();
            ChartData southWallTempData = new ChartData();
            ChartData northWallTempData = new ChartData();
            ChartData roofTempData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            groundTempData.setTime(time);
            eastWallTempData.setTime(time);
            westWallTempData.setTime(time);
            southWallTempData.setTime(time);
            northWallTempData.setTime(time);
            roofTempData.setTime(time);
            groundTempData.setValue(temp.ground_temperature);
            eastWallTempData.setValue(temp.east_wall_temperature);
            westWallTempData.setValue(temp.west_wall_temperature);
            southWallTempData.setValue(temp.south_wall_temperature);
            northWallTempData.setValue(temp.north_wall_temperature);
            roofTempData.setValue(temp.roof_temperature);
            groundTempArrays[i] = groundTempData;
            eastWallTempArrays[i] = eastWallTempData;
            westWallTempArrays[i] = westWallTempData;
            southWallTempArrays[i] = southWallTempData;
            northWallTempArrays[i] = northWallTempData;
            roofTempArrays[i] = roofTempData;
            i++;
        }
        series.put("地面温度", groundTempArrays);
        series.put("东墙温度", eastWallTempArrays);
        series.put("西墙温度", westWallTempArrays);
        series.put("南墙温度", southWallTempArrays);
        series.put("北墙温度", northWallTempArrays);
        series.put("屋顶温度", roofTempArrays);
        chartModel.setTitle("客厅内壁面温度日变化");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("温度 (°C)");
        chartModel.setTooltip("°C");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }

    /**
     * 海拉尔用电量动态变化
     */
    public static void hailaerElectricityConsumption() {
        String type = params.get("type");
        List<EnergyMonitHailar> list = getHailaerModelList(type);
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] electricityConsumptionArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitHailar temp : list) {
            ChartData electricityConsumptionData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            electricityConsumptionData.setTime(time);
            electricityConsumptionData.setValue(temp.electric_quantity);
            electricityConsumptionArrays[i] = electricityConsumptionData;
            i++;
        }
        if (type != null && type.equals("month")) {
            chartModel.setSubtitle("月变化");
        } else {
            chartModel.setSubtitle("日变化");
        }
        series.put("总用电量", electricityConsumptionArrays);
        chartModel.setTitle("用电量变化");
        chartModel.setyAxis_title("总用电量(kWh)");
        chartModel.setTooltip("kWh");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }

    public static void quanzhouTemperatureCompare() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] secondFloorEastTempArrays = new ChartData[size];
        ChartData[] secondFloorBalconyTempArrays= new ChartData[size];
        ChartData[] outdoorTempArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData secondFloorEastTempData = new ChartData();
            ChartData secondFloorBalconyTempData = new ChartData();
            ChartData outdoorTempData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            secondFloorEastTempData.setTime(time);
            secondFloorBalconyTempData.setTime(time);
            outdoorTempData.setTime(time);
            secondFloorEastTempData.setValue(temp.twoeastwall_temperature);
            secondFloorBalconyTempData.setValue(temp.twobalcony_temperature);
            outdoorTempData.setValue(temp.outdoor_temperature);
            secondFloorEastTempArrays[i] = secondFloorEastTempData;
            secondFloorBalconyTempArrays[i] = secondFloorBalconyTempData;
            outdoorTempArrays[i] = outdoorTempData;
            i++;
        }
        series.put("二楼东卧温度", secondFloorEastTempArrays);
        series.put("二楼阳台温度", secondFloorBalconyTempArrays);
        series.put("室外温度", outdoorTempArrays);
        chartModel.setTitle("室内外空气温度逐时对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气温度 (°C)");
        chartModel.setTooltip("°C");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }

    public static void quanzhouHumidityCompare() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] secondFloorEastHumidityArrays = new ChartData[size];
        ChartData[] secondFloorBalconyHumidityArrays = new ChartData[size];
        ChartData[] outdoorHumidityArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData secondFloorEastHumidityData = new ChartData();
            ChartData secongFloorBalconyHumidityData = new ChartData();
            ChartData outdoorHumidityData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            secongFloorBalconyHumidityData.setTime(time);
            secondFloorEastHumidityData.setTime(time);
            outdoorHumidityData.setTime(time);
            secondFloorEastHumidityData.setValue(temp.twolyingeast_humidity);
            secongFloorBalconyHumidityData.setValue(temp.twobalcony_humidity);
            outdoorHumidityData.setValue(temp.outdoor_humidity);
            secondFloorEastHumidityArrays[i] = secondFloorEastHumidityData;
            secondFloorBalconyHumidityArrays[i] = secongFloorBalconyHumidityData;
            outdoorHumidityArrays[i] = outdoorHumidityData;
            i++;
        }
        series.put("二楼东卧湿度", secondFloorEastHumidityArrays);
        series.put("二楼阳台湿度", secondFloorBalconyHumidityArrays);
        series.put("室外湿度", outdoorHumidityArrays);
        chartModel.setTitle("室内空气相对湿度逐时对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气相对湿度(%)");
        chartModel.setTooltip("%");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }
    
    public static void quanzhouTemperatureMultiCompare() {
        String type = params.get("type");
        List<Object[]> list = new ArrayList<Object[]>();
        list.add(getQuanzhouObjectArray("twolyingeast_temperature", type));
        list.add(getQuanzhouObjectArray("twobalcony_temperature", type));
        list.add(getQuanzhouObjectArray("outdoor_temperature", type));
        CombinationChartModel combinationChartModel = new CombinationChartModel();
        Map<String, CombinationChartData> series = new HashMap<>();
        String[] maxArrays = new String[3];
        String[] minArrays = new String[3];
        String[] averageArrays = new String[3];
        String[] categories = new String[3];
        categories[0] = "二楼东卧";
        categories[1] = "二楼阳台";
        categories[2] = "室外";
        for (int i = 0; i < 3; i++) {
            maxArrays[i] = list.get(i)[0].toString();
            minArrays[i] = list.get(i)[1].toString();
            averageArrays[i] = list.get(i)[2].toString();
        }
        if (type != null && type.equals("month")) {
            series.put("月平均值", new CombinationChartData("column", averageArrays));
            series.put("月最大值", new CombinationChartData("scatter", maxArrays));
            series.put("月最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("月变化");
        } else {
            series.put("日平均值", new CombinationChartData("column", averageArrays));
            series.put("日最大值", new CombinationChartData("scatter", maxArrays));
            series.put("日最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("日变化");
        }
        combinationChartModel.setTitle("席里村室内外空气温度均峰值对比");
        combinationChartModel.setyAxis_title("空气温度 (°C)");
        combinationChartModel.setCategories(categories);
        combinationChartModel.setTooltip("°C");
        combinationChartModel.setSeries(series);
        renderJSON(combinationChartModel);
    }

    public static void quanzhouHumidityMultiCompare() {
        String type = params.get("type");
        List<Object[]> list = new ArrayList<Object[]>();
        list.add(getQuanzhouObjectArray("twolyingeast_humidity", type));
        list.add(getQuanzhouObjectArray("twobalcony_humidity", type));
        list.add(getQuanzhouObjectArray("outdoor_humidity", type));
        CombinationChartModel combinationChartModel = new CombinationChartModel();
        Map<String, CombinationChartData> series = new HashMap<>();
        String[] maxArrays = new String[3];
        String[] minArrays = new String[3];
        String[] averageArrays = new String[3];
        String[] categories = new String[3];
        categories[0] = "二楼东卧";
        categories[1] = "二楼阳台";
        categories[2] = "室外";
        for (int i = 0; i < 3; i++) {
            maxArrays[i] = list.get(i)[0].toString();
            minArrays[i] = list.get(i)[1].toString();
            averageArrays[i] = list.get(i)[2].toString();
        }
        if (type != null && type.equals("month")) {
            series.put("月平均值", new CombinationChartData("column", averageArrays));
            series.put("月最大值", new CombinationChartData("scatter", maxArrays));
            series.put("月最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("月变化");
        } else if (type != null && type.equals("week")) {
            series.put("周平均值", new CombinationChartData("column", averageArrays));
            series.put("周最大值", new CombinationChartData("scatter", maxArrays));
            series.put("周最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("周变化");
        } else {
            series.put("日平均值", new CombinationChartData("column", averageArrays));
            series.put("日最大值", new CombinationChartData("scatter", maxArrays));
            series.put("日最小值", new CombinationChartData("scatter", minArrays));
            combinationChartModel.setSubtitle("日变化");
        }
        combinationChartModel.setTitle("席里村室内外空气相对湿度均峰值对比");
        combinationChartModel.setyAxis_title("空气相对湿度 (%)");
        combinationChartModel.setCategories(categories);
        combinationChartModel.setTooltip("%");
        combinationChartModel.setSeries(series);
        renderJSON(combinationChartModel);
    }
    
    public static void quanzhouStructureTemperatureEffect() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] firstFloorEastTempArrays = new ChartData[size];
        ChartData[] secondFloorEastTempArrays= new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData firstFloorEastTempData = new ChartData();
            ChartData secondFloorEastTempData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            firstFloorEastTempData.setTime(time);
            secondFloorEastTempData.setTime(time);
            firstFloorEastTempData.setValue(temp.onelyingeast_temperature);
            secondFloorEastTempData.setValue(temp.twolyingeast_temperature);
            firstFloorEastTempArrays[i] = firstFloorEastTempData;
            secondFloorEastTempArrays[i] = secondFloorEastTempData;
            i++;
        }
        series.put("一楼东卧——石材围护结构", firstFloorEastTempArrays);
        series.put("二楼东卧——砖材围护结构", secondFloorEastTempArrays);
        chartModel.setTitle("围护结构构造对室内空气温度的影响");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("室内空气温度 (°C)");
        chartModel.setTooltip("°C");
        chartModel.setSeries(series);
        chartModel.setType("area");
        renderJSON(chartModel);
    }
    
    public static void quanzhouStructureHumidityEffect() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] firstFloorEastHumidityArrays = new ChartData[size];
        ChartData[] secondFloorEastHumidityArrays= new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData firstFloorEastHumidityData = new ChartData();
            ChartData secondFloorEastHumidityData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            firstFloorEastHumidityData.setTime(time);
            secondFloorEastHumidityData.setTime(time);
            firstFloorEastHumidityData.setValue(temp.onelyingeast_humidity);
            secondFloorEastHumidityData.setValue(temp.twolyingeast_humidity);
            firstFloorEastHumidityArrays[i] = firstFloorEastHumidityData;
            secondFloorEastHumidityArrays[i] = secondFloorEastHumidityData;
            i++;
        }
        series.put("一楼东卧——石材围护结构", firstFloorEastHumidityArrays);
        series.put("二楼东卧——砖材围护结构", secondFloorEastHumidityArrays);
        chartModel.setTitle("围护结构构造对室内空气相对湿度的影响");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("室内空气相对湿度 (%)");
        chartModel.setTooltip("%");
        chartModel.setSeries(series);
        chartModel.setType("area");
        renderJSON(chartModel);
    }
    
    public static void quanzhouFirstFloorTemperatureCompare() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] eastTempArrays = new ChartData[size];
        ChartData[] kitchenTempArrays= new ChartData[size];
        ChartData[] aisleTempArrays = new ChartData[size];
        ChartData[] washroomTempArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData eastTempData = new ChartData();
            ChartData kitchenTempData = new ChartData();
            ChartData aisleTempData = new ChartData();
            ChartData washroomTempData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            eastTempData.setTime(time);
            kitchenTempData.setTime(time);
            aisleTempData.setTime(time);
            washroomTempData.setTime(time);
            eastTempData.setValue(temp.onelyingeast_temperature);
            kitchenTempData.setValue(temp.onekitchen_temperature);
            aisleTempData.setValue(temp.oneaisle_temperature);
            washroomTempData.setValue(temp.onewashroom_temperature);
            eastTempArrays[i] = eastTempData;
            kitchenTempArrays[i] = kitchenTempData;
            aisleTempArrays[i] = aisleTempData;
            washroomTempArrays[i] = washroomTempData;
            i++;
        }
        series.put("东卧温度", eastTempArrays);
        series.put("厨房温度", kitchenTempArrays);
        series.put("过道温度", aisleTempArrays);
        series.put("卫生间温度", washroomTempArrays);
        chartModel.setTitle("一层室内空气温度对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气温度 (°C)");
        chartModel.setTooltip("°C");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }
    
    public static void quanzhouFirstFloorHumidityCompare() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] eastHumidityArrays = new ChartData[size];
        ChartData[] kitchenHumidityArrays= new ChartData[size];
        ChartData[] aisleHumidityArrays = new ChartData[size];
        ChartData[] washroomHumidityArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData eastHumidityData = new ChartData();
            ChartData kitchenHumidityData = new ChartData();
            ChartData aisleHumidityData = new ChartData();
            ChartData washroomHumidityData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            eastHumidityData.setTime(time);
            kitchenHumidityData.setTime(time);
            aisleHumidityData.setTime(time);
            washroomHumidityData.setTime(time);
            eastHumidityData.setValue(temp.onelyingeast_humidity);
            kitchenHumidityData.setValue(temp.onekitchen_humidity);
            aisleHumidityData.setValue(temp.oneaisle_humidity);
            washroomHumidityData.setValue(temp.onewashroom_humidity);
            eastHumidityArrays[i] = eastHumidityData;
            kitchenHumidityArrays[i] = kitchenHumidityData;
            aisleHumidityArrays[i] = aisleHumidityData;
            washroomHumidityArrays[i] = washroomHumidityData;
            i++;
        }
        series.put("东卧湿度", eastHumidityArrays);
        series.put("厨房湿度", kitchenHumidityArrays);
        series.put("过道湿度", aisleHumidityArrays);
        series.put("卫生间湿度", washroomHumidityArrays);
        chartModel.setTitle("一层室内空气温度对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气湿度 (%)");
        chartModel.setTooltip("%");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }
    
    public static void quanzhouSecondFloorTemperatureCompare() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] eastTempArrays = new ChartData[size];
        ChartData[] balconyTempArrays = new ChartData[size];
        ChartData[] parlourTempArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData eastTempData = new ChartData();
            ChartData aisleTempData = new ChartData();
            ChartData parlourTempData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            eastTempData.setTime(time);
            aisleTempData.setTime(time);
            parlourTempData.setTime(time);
            eastTempData.setValue(temp.twolyingeast_temperature);
            aisleTempData.setValue(temp.twobalcony_temperature);
            parlourTempData.setValue(temp.twoparlour_temperature);
            eastTempArrays[i] = eastTempData;
            balconyTempArrays[i] = aisleTempData;
            parlourTempArrays[i] = parlourTempData;
            i++;
        }
        series.put("东卧温度", eastTempArrays);
        series.put("阳台温度", balconyTempArrays);
        series.put("客厅温度", parlourTempArrays);
        chartModel.setTitle("二层室内空气温度对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气温度 (°C)");
        chartModel.setTooltip("°C");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }
    
    public static void quanzhouSecondFloorHumidityCompare() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] eastHumidityArrays = new ChartData[size];
        ChartData[] balconyHumidityArrays = new ChartData[size];
        ChartData[] parlourHumidityArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData eastHumidityData = new ChartData();
            ChartData aisleHumidityData = new ChartData();
            ChartData parlourHumidityData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            eastHumidityData.setTime(time);
            aisleHumidityData.setTime(time);
            parlourHumidityData.setTime(time);
            eastHumidityData.setValue(temp.twolyingeast_humidity);
            aisleHumidityData.setValue(temp.twobalcony_humidity);
            parlourHumidityData.setValue(temp.twoparlour_humidity);
            eastHumidityArrays[i] = eastHumidityData;
            balconyHumidityArrays[i] = aisleHumidityData;
            parlourHumidityArrays[i] = parlourHumidityData;
            i++;
        }
        series.put("东卧湿度", eastHumidityArrays);
        series.put("阳台湿度", balconyHumidityArrays);
        series.put("客厅湿度", parlourHumidityArrays);
        chartModel.setTitle("二层室内空气湿度对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气湿度 (%)");
        chartModel.setTooltip("%");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }
    
    public static void quanzhouWallTemperatureCompare() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] eastWallTempArrays = new ChartData[size];
        ChartData[] westWallTempArrays = new ChartData[size];
        ChartData[] southWallTempArrays = new ChartData[size];
        ChartData[] northWallTempArrays = new ChartData[size];
        ChartData[] floorTempArrays = new ChartData[size];
        ChartData[] roofTempArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData eastWallTempData = new ChartData();
            ChartData westWallTempData = new ChartData();
            ChartData southWallTempData = new ChartData();
            ChartData northWallTempData = new ChartData();
            ChartData floorTempData = new ChartData();
            ChartData roofTempData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            eastWallTempData.setTime(time);
            westWallTempData.setTime(time);
            southWallTempData.setTime(time);
            northWallTempData.setTime(time);
            floorTempData.setTime(time);
            roofTempData.setTime(time);
            eastWallTempData.setValue(temp.twoeastwall_temperature);
            westWallTempData.setValue(temp.twowesternwall_temperature);
            southWallTempData.setValue(temp.twosouthwall_temperature);
            northWallTempData.setValue(temp.twonorthwall_temperature);
            floorTempData.setValue(temp.twofloor_temperature);
            roofTempData.setValue(temp.twosouthwailimian_temperature);
            eastWallTempArrays[i] = eastWallTempData;
            westWallTempArrays[i] = westWallTempData;
            southWallTempArrays[i] = southWallTempData;
            northWallTempArrays[i] = northWallTempData;
            floorTempArrays[i] = floorTempData;
            roofTempArrays[i] = roofTempData;
            i++;
        }
        series.put("东墙温度", eastWallTempArrays);
        series.put("西墙温度", westWallTempArrays);
        series.put("南墙温度", southWallTempArrays);
        series.put("北墙温度", northWallTempArrays);
        series.put("地面温度", floorTempArrays);
        series.put("屋顶温度", roofTempArrays);
        chartModel.setTitle("内壁面温度日动态变化及对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("空气温度 (°C)");
        chartModel.setTooltip("°C");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }
    
    public static void quanzhouWindSpeedCompare() {
        List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
        ChartModel chartModel = new ChartModel();
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        int size = list.size();
        ChartData[] firstFloorIndoorWindSpeedArrays = new ChartData[size];
        ChartData[] firstFloorAisleWindSpeedArrays = new ChartData[size];
        ChartData[] secondFloorAisleWindSpeedArrays = new ChartData[size];
        ChartData[] outdoorWindSpeedTempArrays = new ChartData[size];
        int i = 0;
        for (EnergyMonitQuanzhou temp : list) {
            ChartData firstFloorIndoorWindSpeedData = new ChartData();
            ChartData firstFloorAisleWindSpeedData = new ChartData();
            ChartData secondFloorAisleWindSpeedData = new ChartData();
            ChartData outdoorAisleWindSpeedData = new ChartData();
            String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
            firstFloorIndoorWindSpeedData.setTime(time);
            firstFloorAisleWindSpeedData.setTime(time);
            secondFloorAisleWindSpeedData.setTime(time);
            outdoorAisleWindSpeedData.setTime(time);
            firstFloorIndoorWindSpeedData.setValue(temp.onelyingeast_windspeed);
            firstFloorAisleWindSpeedData.setValue(temp.oneaisle_windspeed);
            secondFloorAisleWindSpeedData.setValue(temp.twoaisle_windspeed);
            outdoorAisleWindSpeedData.setValue(temp.wind_speed);
            firstFloorIndoorWindSpeedArrays[i] = firstFloorIndoorWindSpeedData;
            firstFloorAisleWindSpeedArrays[i] = firstFloorAisleWindSpeedData;
            secondFloorAisleWindSpeedArrays[i] = secondFloorAisleWindSpeedData;
            outdoorWindSpeedTempArrays[i] = outdoorAisleWindSpeedData;
            i++;
        }
        series.put("一楼室内风速", firstFloorIndoorWindSpeedArrays);
        series.put("一楼过道风速", firstFloorAisleWindSpeedArrays);
        series.put("二楼过道风速", secondFloorAisleWindSpeedArrays);
        series.put("室外风速", outdoorWindSpeedTempArrays);
        chartModel.setTitle("风速日动态变化及对比");
        chartModel.setSubtitle("");
        chartModel.setyAxis_title("风速(m/s)");
        chartModel.setTooltip("m/s");
        chartModel.setSeries(series);
        renderJSON(chartModel);
    }
    
    public static void quanzhouEnergyConsumptionCompare() {
        String type = params.get("type");
        ChartModelDoubleYAxis chartModelDoubleYAxis = new ChartModelDoubleYAxis();
        if (type != null && type.equals("month")) {
            List list = JPA.em()
                    .createQuery(
                            "SELECT MONTH(t_date) as month,MAX(water_index)-MIN(water_index),MAX(oneelectric_quantity+twoelectric_quantity)-MIN(oneelectric_quantity+twoelectric_quantity) as value from EnergyMonitQuanzhou WHERE YEAR(t_date) = YEAR(NOW()) GROUP BY MONTH(t_date)")
                    .getResultList();
            String[] categories = new String[list.size()];
            Double[] waterConsumption = new Double[list.size()];
            Double[] electricityConsumption = new Double[list.size()];
            for(int i = 0;i<list.size();i++){
                Object[] array = (Object[]) list.get(i);
                categories[i] = array[0].toString() + "月";
                waterConsumption[i] = (double) array[1];
                electricityConsumption[i] = (double) array[2];
            }
            chartModelDoubleYAxis.setType("month");
            chartModelDoubleYAxis.setCategories(categories);
            chartModelDoubleYAxis.setWaterConsumption(waterConsumption);
            chartModelDoubleYAxis.setElectricityConsumption(electricityConsumption);
        } else {
            List<EnergyMonitQuanzhou> list = getQuanzhouModelList();
            int size = list.size();
            ChartData[] electricityConsumptionArrays = new ChartData[size];
            ChartData[] waterConsumptionArrays = new ChartData[size];
            int i = 0;
            for (EnergyMonitQuanzhou temp : list) {
                ChartData electricityConsumptionData = new ChartData();
                ChartData waterConsumptionSpeedData = new ChartData();
                String time = toNextMinute(temp.t_date.trim() + " " + temp.t_time.trim());
                electricityConsumptionData.setTime(time);
                waterConsumptionSpeedData.setTime(time);
                electricityConsumptionData.setValue(temp.oneelectric_quantity + temp.twoelectric_quantity);
                waterConsumptionSpeedData.setValue(temp.water_index);
                electricityConsumptionArrays[i] = electricityConsumptionData;
                waterConsumptionArrays[i] = waterConsumptionSpeedData;
                i++;
            }
            chartModelDoubleYAxis.setType("day");
            chartModelDoubleYAxis.setArrays1(waterConsumptionArrays);
            chartModelDoubleYAxis.setArrays2(electricityConsumptionArrays);
        }
        renderJSON(chartModelDoubleYAxis);
    }
    
    public static void quanzhouElectricityConsumptionDiffCompare() {
        EnergyMonitQuanzhou energyMonitQuanzhou = EnergyMonitQuanzhou
                .find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t) ORDER BY TIME(t_time) DESC")
                .first();
        PieModel pieModel = new PieModel();
        PieData pieData1 = new PieData();
        PieData pieData2 = new PieData();
        pieData1.setName("一层用电量");
        pieData2.setName("二层用电量");
        pieData1.setValue(energyMonitQuanzhou.oneelectric_quantity);
        pieData2.setValue(energyMonitQuanzhou.twoelectric_quantity);
        PieData[] series = new PieData[2];
        series[0] = pieData1;
        series[1] = pieData2;
        pieModel.setTitle("不同楼层用电量所占比例对比");
        pieModel.setSeries(series);
        renderJSON(pieModel);
    }
    
    /**
     * @param inputStr
     * 获取离当前时间最近的下一个整分钟
     * @return
     */
    public static String toNextMinute(String inputStr) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(inputStr);
            cal.setTime(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MINUTE, 1);
        return sdf.format(cal.getTime());
    }
    
    public static List<EnergyMonitHailar> getHailaerModelList() {
        List<EnergyMonitHailar> list = EnergyMonitHailar
                .find("from EnergyMonitHailar t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitHailar t)")
                .fetch();
        return list;
    }
    
    public static List<EnergyMonitHailar> getHailaerModelList(String type) {
        List<EnergyMonitHailar> list = null;
        if (type != null && type.equals("week")) {
            list = EnergyMonitQuanzhou
                    .find("from EnergyMonitHailar t where YEAR(t_date) = YEAR(NOW()) AND WEEK(t_date) = (SELECT MAX(WEEK(t_date)) from EnergyMonitHailar t where YEAR(t_date) = YEAR(NOW()))")
                    .fetch();
        } else if (type != null && type.equals("month")) {
            list = EnergyMonitQuanzhou
                    .find("from EnergyMonitHailar t where YEAR(t_date) = YEAR(NOW()) AND MONTH(t_date) = (SELECT MAX(MONTH(t_date)) from EnergyMonitHailar t where YEAR(t_date) = YEAR(NOW()))")
                    .fetch();
        } else {
            list = getHailaerModelList();
        }
        return list;
    }

    public static Object[] getHailaerObjectArray(String property) {
        Object[] array = (Object[]) JPA.em()
                .createQuery("select max(" + property + ") as max,min(" + property + ") as min,avg("
                        + property
                        + ") as average from EnergyMonitHailar t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitHailar t)")
                .getSingleResult();
        return array;
    }
    
    public static Object[] getHailaerObjectArray(String property, String type) {
        Object[] array = null;
        if (type != null && type.equals("week")) {
            array = (Object[]) JPA.em()
                    .createQuery("select max(" + property + ") as max,min(" + property
                            + ") as min,avg(" + property
                            + ") as average from EnergyMonitHailar t where YEAR(t_date) = YEAR(NOW()) AND WEEK(t_date) = (select MAX(WEEK(t_date)) from EnergyMonitHailar t where YEAR(t_date) = YEAR(NOW()))")
                    .getSingleResult();
        } else if (type != null && type.equals("month")) {
            array = (Object[]) JPA.em()
                    .createQuery("select max(" + property + ") as max,min(" + property
                            + ") as min,avg(" + property
                            + ") as average from EnergyMonitHailar t where YEAR(t_date) = YEAR(NOW()) AND MONTH(t_date) = (select MAX(MONTH(t_date)) from EnergyMonitHailar t where YEAR(t_date) = YEAR(NOW()))")
                    .getSingleResult();
        } else {
            array = getHailaerObjectArray(property);
        }
        return array;
    }
    
    public static List<EnergyMonitQuanzhou> getQuanzhouModelList() {
        List<EnergyMonitQuanzhou> list = EnergyMonitQuanzhou
                .find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)")
                .fetch();
        return list;
    }

    public static List<EnergyMonitQuanzhou> getQuanzhouModelList(String type) {
        List<EnergyMonitQuanzhou> list = null;
        if (type != null && type.equals("week")) {
            list = EnergyMonitQuanzhou
                    .find("from EnergyMonitQuanzhou t where YEAR(t_date) = YEAR(NOW()) AND WEEK(t_date) = (SELECT MAX(WEEK(t_date)) from EnergyMonitQuanzhou t where YEAR(t_date) = YEAR(NOW()))")
                    .fetch();
        } else if (type != null && type.equals("month")) {
            list = EnergyMonitQuanzhou
                    .find("from EnergyMonitQuanzhou t where YEAR(t_date) = YEAR(NOW()) AND MONTH(t_date) = (SELECT MAX(MONTH(t_date)) from EnergyMonitQuanzhou t where YEAR(t_date) = YEAR(NOW()))")
                    .fetch();
        } else {
            list = getQuanzhouModelList();
        }
        return list;
    }
    
    public static Object[] getQuanzhouObjectArray(String property) {
        Object[] array = (Object[]) JPA.em()
                .createQuery("select max(" + property + ") as max,min(" + property + ") as min,avg("
                        + property
                        + ") as average from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)")
                .getSingleResult();
        return array;
    }
    
    public static Object[] getQuanzhouObjectArray(String property, String type) {
        Object[] array = null;
        if (type != null && type.equals("week")) {
            array = (Object[]) JPA.em()
                    .createQuery("select max(" + property + ") as max,min(" + property
                            + ") as min,avg(" + property
                            + ") as average from EnergyMonitQuanzhou t where YEAR(t_date) = YEAR(NOW()) AND WEEK(t_date) = (select MAX(WEEK(t_date)) from EnergyMonitQuanzhou t where YEAR(t_date) = YEAR(NOW()))")
                    .getSingleResult();
        } else if (type != null && type.equals("month")) {
            array = (Object[]) JPA.em()
                    .createQuery("select max(" + property + ") as max,min(" + property
                            + ") as min,avg(" + property
                            + ") as average from EnergyMonitQuanzhou t where YEAR(t_date) = YEAR(NOW()) AND MONTH(t_date) = (select MAX(MONTH(t_date)) from EnergyMonitQuanzhou t where YEAR(t_date) = YEAR(NOW()))")
                    .getSingleResult();
        } else {
            array = getQuanzhouObjectArray(property);
        }
        return array;
    }
    //海拉尔--逐时分析
    public static void hailaerHoursData() {
        String type = params.get("type");//卧室温度、餐厅温度等
        //找出数据库中最后一天的数据
        String jpql = "select concat(t_date,' ',t_time),"+type+" from EnergyMonitHailar t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitHailar t)";
        List list = JPA.em().createQuery(jpql).getResultList();
        int size = list.size();
//        System.out.println(jpql + " " + size);
        
        String[] tmp = new String[size];
        tmp = hailaerSetHighChartsyTitle(type);
        String yTitle = tmp[0],toolTip = tmp[1];
        
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        ChartData[] chartDatas = new ChartData[size];
        ChartData chartData;
        if(list!=null){
        	Iterator iterator = list.iterator();
        	int i = 0;
        	while(iterator.hasNext()){
        		Object[] row = ( Object[])iterator.next();
        		chartData = new ChartData();
        		chartData.setTime(row[0].toString());
        		chartData.setValue(Double.valueOf(row[1].toString()));
        		chartDatas[i] = chartData;
        		i++;
        	}
        }
        series.put(yTitle, chartDatas);

        ChartModel chartModel = new ChartModel();
        chartModel.setTitle("");
        chartModel.setSubtitle("");
        chartModel.setSeries(series);
        chartModel.setyAxis_title(yTitle);
        chartModel.setTooltip(toolTip);
       
        renderJSON(chartModel);
    }
    
    //海拉尔--返回highcharts表单的y轴和tooltip
    private static String[] hailaerSetHighChartsyTitle(String type){
    	String[] res = new String[2];
     	String yTitle=null,tooltip=null;
    	if(type.endsWith("temperature")){
    		tooltip = "°C";
    	}else if(type.endsWith("humidity")){
    		tooltip = "%";
    	}
    	
    	if (type.equals("bedroom_temperature")) {
    		yTitle = "卧室温度(°C)";
    	}else if (type.equals("dining_temperature")) {
    		yTitle = "餐厅温度(°C)";
    	}else if (type.equals("parlour_temperature")) {
    		yTitle = "客厅温度(°C)";
    	}else if (type.equals("hall_temperature")) {
    		yTitle = "门厅温度(°C)";
    	}else if (type.equals("washroom_temperature")) {
    		yTitle = "浴室温度(°C)";
    	}else if (type.equals("kitchen_temperature")) {
    		yTitle = "厨房温度(°C)";
    	}//湿度
    	else if (type.equals("bedroom_humidity")) {
    		yTitle = "卧室湿度(%)";
    	}else if (type.equals("dining_humidity")) {
    		yTitle = "餐厅湿度(%)";
    	}else if (type.equals("parlour_humidity")) {
    		yTitle = "客厅湿度(%)";
    	}else if (type.equals("hall_humidity")) {
    		yTitle = "门厅湿度(%)";
    	}else if (type.equals("washroom_humidity")) {
    		yTitle = "浴室湿度(%)";
    	}else if (type.equals("kitchen_humidity")) {
    		yTitle = "厨房湿度(%)";
    	}//壁面温度
    	else if (type.equals("roof_temperature")) {
    		yTitle = "屋顶温度(°C)";
    	}else if (type.equals("ground_temperature")) {
    		yTitle = "地面温度(°C)";
    	}else if (type.equals("east_wall_temperature")) {
    		yTitle = "东墙温度(°C)";
    	}else if (type.equals("west_wall_temperature")) {
    		yTitle = "西墙温度(°C)";
    	}else if (type.equals("south_wall_temperature")) {
    		yTitle = "南墙温度(°C)";
    	}else if (type.equals("north_wall_temperature")) {
    		yTitle = "北墙温度(°C)";
    	}else if (type.equals("outdoor_temperature")) {
    		yTitle = "室外温度(°C)";
    	}else if (type.equals("outdoor_humidity")){
    		yTitle = "室外湿度(%)";
    	}//电能耗
    	else if(type.equals("power")){
    		yTitle ="功耗(W)";
    		tooltip = "W";
    	}else if(type.equals("electric_quantity")){
    		yTitle ="电量(W)";
    	}else if(type.equals("electric_tension")){
    		yTitle ="电压(V)";
    		tooltip = "V";
    	}else if(type.equals("electric_current")){
    		yTitle ="电流(A)";
    		tooltip = "A";
    	}//室外参数
    	else if(type.equals("barometric")){
    		yTitle = "大气压力(Pa)";
    		tooltip = "Pa";
    	}else if(type.equals("total_radiation")){
    		yTitle = "总辐射(Ci)";
    		tooltip = "Ci";
    	}else if(type.equals("direct_radiation")){
    		yTitle = "直接辐射(Ci)";
    		tooltip = "Ci";
    	}else if(type.equals("wind_speed")){
    		yTitle = "风速(m/s)";
    		tooltip = "m/s";
    	}
    	res[0] = yTitle;res[1] = tooltip;
    	return res;
    }
    
    /**
     *  单因素--海拉尔--逐日分析--获取过去一周的数据
     */
    public static void hailaerPast7DaysData() {
        String type = params.get("type");//测点参数：卧室温度、厨房温度等
        
        DateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String nowDate = sdf.format(date);
        calendar.add(Calendar.DATE, -7);
        String pastDate = sdf.format(calendar.getTime());
        //选取过去七天的数据
        String jpql = "select t_date,max(" + type + ")" + " as max,min(" + type + ")" + " as min,avg("
                + type + ")" + " as average from EnergyMonitHailar t where date(t_date) between '"+pastDate+"' and '"+nowDate+"' GROUP BY t_date ORDER BY date(t_date) ASC";
//        System.out.println(jpql+"\n"+nowDate);

        List list = JPA.em().createQuery(jpql).getResultList();
        int size = list.size();
        
        String[] tmp = new String[size];
        tmp = hailaerSetHighChartsyTitle(type);
        String yTitle = tmp[0],toolTip = tmp[1];
        
        String[] time1 = new String[size];
        String[] max1 = new String[size];
        String[] min1 = new String[size];
        String[] avg1 = new String[size];
        
        if(list!=null){
        	Iterator iterator = list.iterator();
        	int i = 0;
        	while(iterator.hasNext()){
        		Object[] row = ( Object[])iterator.next();
        		time1[i] = row[0].toString();
        		max1[i] = row[1].toString();
        		min1[i] = row[2].toString();
        		avg1[i] = row[3].toString();
        		i++;
        	}
        }
        Map<String, CombinationChartData> series = new HashMap<>();
        series.put("平均值", new CombinationChartData("column", avg1));
        series.put("最大值", new CombinationChartData("scatter", max1));
        series.put("最小值", new CombinationChartData("scatter", min1));
        if(type.equals("bedroom_temperature")){
        }

        CombinationChartModel combinationChartModel = new CombinationChartModel();
        combinationChartModel.setTitle("");
        combinationChartModel.setSubtitle("");
        combinationChartModel.setCategories(time1);
        combinationChartModel.setSeries(series);
    	combinationChartModel.setyAxis_title(yTitle);
    	combinationChartModel.setTooltip(toolTip);

        renderJSON(combinationChartModel);
    }
    
    public static void hailaerMonthData() {
        String type = params.get("type");//测点参数：卧室温度、厨房温度等
        if (type.endsWith("temp"))
            type += "erature";
        
        //选取每个月的最大最小平均值
        String jpql = "select SUBSTRING(t_date,1,6) as date,max(" + type + ")" + " as max,min(" + type + ")" + " as min,avg("
                + type + ")" + " as average from EnergyMonitHailar t GROUP BY SUBSTRING(t_date,1,6)";
//        System.out.println(jpql);

        List list = JPA.em().createQuery(jpql).getResultList();
        int size = list.size();
        
        String[] tmp = new String[size];
        tmp = hailaerSetHighChartsyTitle(type);
        String yTitle = tmp[0],toolTip = tmp[1];
        
        String[] time1 = new String[size];
        String[] max1 = new String[size];
        String[] min1 = new String[size];
        String[] avg1 = new String[size];
        
        if(list!=null){
        	Iterator iterator = list.iterator();
        	int i = 0;
        	while(iterator.hasNext()){
        		Object[] row = ( Object[])iterator.next();
        		time1[i] = row[0].toString();
        		max1[i] = row[1].toString();
        		min1[i] = row[2].toString();
        		avg1[i] = row[3].toString();
        		i++;
        	}
        }
        Map<String, CombinationChartData> series = new HashMap<>();
        series.put("平均值", new CombinationChartData("column", avg1));
        series.put("最大值", new CombinationChartData("scatter", max1));
        series.put("最小值", new CombinationChartData("scatter", min1));

        CombinationChartModel combinationChartModel = new CombinationChartModel();
        combinationChartModel.setTitle("");
        combinationChartModel.setSubtitle("");
        combinationChartModel.setCategories(time1);
        combinationChartModel.setSeries(series);
    	combinationChartModel.setyAxis_title(yTitle);
    	combinationChartModel.setTooltip(toolTip);
       

        renderJSON(combinationChartModel);
    }
    //海拉尔历史数据查询
    public static void hailaerFindHistData() {
        String position = params.get("position");
        String parameter = params.get("param");
        String startTime = params.get("start");
        String endTime = params.get("end");
        String type = position + "_" + parameter;
       
        //处理传过来的url参数
        if(type.startsWith("spec") || type.equals("electric_power")){//1： 室外参数，2 ：电能耗--功耗
        	type = parameter;
        }
        
        String[] tmp = new String[2];// 0 : type, 1 : y坐标   2： 提示框
    	tmp = hailaerSetHighChartsyTitle(type);
    	
    	String yTitle = tmp[0];
    	String toolTip = tmp[1];
    	
    	 String hql = "select CONCAT(t_date,' ',t_time)," +type+ " from EnergyMonitHailar t where date(t_date) between '" + startTime
                 + "'" + " and '" + endTime + "'";
    	 List list = JPA.em().createQuery(hql).getResultList();
         int size = list.size();
//         System.out.println(hql+ " "+ size);
         
         Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
         ChartData[] array = new ChartData[size];
         ChartData chartData;
         if(list!=null){
         	Iterator iterator = list.iterator();
         	int i=0;
         	while(iterator.hasNext()){
         		chartData = new ChartData();
         		Object[] row = ( Object[])iterator.next();
         		chartData.setTime(row[0].toString());
         		chartData.setValue(Double.valueOf( row[1].toString()));
         		array[i]=chartData;
         		i++;
         	}
         }
         series.put(yTitle, array);
         
         ChartModel chartModel = new ChartModel();
         chartModel.setTitle("");
         chartModel.setSubtitle("");
         chartModel.setSeries(series);
         chartModel.setyAxis_title(yTitle);
         chartModel.setTooltip(toolTip);
         
        renderJSON(chartModel);
    }
    
    //泉州历史数据查询
    public static void quanzhouFindHistData(){
    	String position = params.get("position");
        String parameter = params.get("param");
        String startTime = params.get("start");
        String endTime = params.get("end");
        String type = position + "_" + parameter;
        //处理传过来的url参数
        if(type.startsWith("wall")&&type.endsWith("temperature")){//壁面温度
        	type = position;
        }else if(type.equals("oneelectric_power")){//电能耗--功耗
        	type = "onepower";
        }else if(type.equals("twoelectric_power")){
        	type = "twopower";
        }else if(type.endsWith("water_index")){
        	type = position;
        }
        
        String[] tmp = new String[3];// 0 : type, 1 : y坐标   2： 提示框
    	tmp = setHighChartsyTitle(type);
    	
    	type = tmp[0];
    	String yTitle = tmp[1];
    	String toolTip = tmp[2];
    	
        String hql = "select CONCAT(t_date,' ',t_time)," +type+ " from EnergyMonitQuanzhou t where date(t_date) between '" + startTime
                + "'" + " and '" + endTime + "'";
//        System.out.println(hql + " " + type);

        List list = JPA.em().createQuery(hql).getResultList();
        int size = list.size();
        
        Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
        ChartData[] array = new ChartData[size];
        ChartData chartData;
        if(list!=null){
        	Iterator iterator = list.iterator();
        	int i=0;
        	while(iterator.hasNext()){
        		chartData = new ChartData();
        		Object[] row = ( Object[])iterator.next();
        		chartData.setTime(row[0].toString());
        		chartData.setValue(Double.valueOf( row[1].toString()));
        		array[i]=chartData;
        		i++;
        	}
        }
        series.put(yTitle, array);
       
        ChartModel chartModel = new ChartModel();
        chartModel.setTitle("");
        chartModel.setSubtitle("");
        chartModel.setSeries(series);
        chartModel.setyAxis_title(yTitle);
        chartModel.setTooltip(toolTip);
        
        renderJSON(chartModel);
    }
    
    
    public static void quanzhouDemo(){
    	String type = params.get("type");
    	//一楼室内温湿度
    	if(type.startsWith("one")&&(type.endsWith("temperature") || type.endsWith("humidity"))){
    		quanzhouOneTH(type);
    	}
    	//二楼室内温湿度
    	if(type.startsWith("two")&&(type.endsWith("temperature") || type.endsWith("humidity"))){
    		quanzhouTwoTH(type);
    	}
    	//二楼壁面温度
    	if(type.startsWith("wall")){
    		quanzhouTwoWallT(type);
    	}
    	//室内风速
    	if((type.startsWith("one")|| type.startsWith("two"))&&type.endsWith("speed")){
    		quanzhouOutSpeed(type);
    	}
    	//电能耗情况
    	if(type.contains("electric") || type.contains("power")){
    		quanzhouElectric(type);
    	}
    	
    	//室外参数 ,未定
    	if(type.startsWith("spec")){
    		quanzhouOutdoor(type);
    	}
    	//用水量
    	if(type.startsWith("water_index")){
    		quanzhouWater(type);
    	}
    }
    
    //一楼室内温湿度
    private static void quanzhouOneTH(String type){
    	List<EnergyMonitQuanzhou> list = EnergyMonitQuanzhou.find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)").fetch();
    	int size = list.size();
    	
    	String legendType = null;//highchart图表的legend
    	Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
    	ChartData[] chartDatas = new ChartData[size];
    	if(list!=null&&size>0){
    		for(int i=0;i<size;i++){
    			ChartData chartData = new ChartData();
    			EnergyMonitQuanzhou e= list.get(i);
    			String time = toNextMinute(e.t_date + " " + e.t_time);
    			//一楼东卧温度
    			if(type.equals("onelyingeast_temperature")){
    				chartData.setTime(time);
    				chartData.setValue(e.onelyingeast_temperature);
    				legendType = "东卧温度";
    			}
    			//一楼厨房温度
    			if(type.equals("onekitchen_temperature")){
    				chartData.setTime(time);
    				chartData.setValue(e.onekitchen_temperature);
    				legendType = "厨房温度";
    			}
    			//一楼过道温度
    			if(type.equals("oneaisle_temperature")){
    				chartData.setTime(time);
    				chartData.setValue(e.oneaisle_temperature);
    				legendType = "过道温度";
    			}
    			//一楼卫生间温度
    			if(type.equals("onewashroom_temperature")){
    				chartData.setTime(time);
    				chartData.setValue(e.onewashroom_temperature);
    				legendType = "卫生间温度";
    			}
    			/*******************************************/
    			//一楼东卧湿度
    			if(type.equals("onelyingeast_humidity")){
    				chartData.setTime(time);
    				chartData.setValue(e.onelyingeast_humidity);
    				legendType = "东卧湿度";
    			}
    			//一楼厨房湿度
    			if(type.equals("onekitchen_humidity")){
    				chartData.setTime(time);
    				chartData.setValue(e.onekitchen_humidity);
    				legendType = "厨房湿度";
    			}
    			//一楼过道湿度
    			if(type.equals("oneaisle_humidity")){
    				chartData.setTime(time);
    				chartData.setValue(e.oneaisle_humidity);
    				legendType = "过道湿度";
    			}
    			//一楼卫生间湿度
    			if(type.equals("onewashroom_humidity")){
    				chartData.setTime(time);
    				chartData.setValue(e.onewashroom_humidity);
    				legendType = "卫生间湿度";
    			}
    			
    			chartDatas[i] = chartData;
    		}
    		series.put(legendType, chartDatas);

            ChartModel chartModel = new ChartModel();
            chartModel.setTitle("");
            chartModel.setSubtitle("");
            chartModel.setSeries(series);
            if (type.endsWith("temperature")) {
                chartModel.setyAxis_title(legendType+"(°C)");
                chartModel.setTooltip("°C");
            } else if (type.endsWith("humidity")) {
                chartModel.setyAxis_title(legendType+"(°%)");
                chartModel.setTooltip("%");
            }
            renderJSON(chartModel);
    	}
    }
    //二楼室内温湿度
    private static void quanzhouTwoTH(String type){
    	List<EnergyMonitQuanzhou> list = EnergyMonitQuanzhou.find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)").fetch();
    	int size = list.size();
    	
    	String legendType = null;//highchart图表的legend
    	Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
    	ChartData[] chartDatas = new ChartData[size];
    	if(list!=null&&size>0){
    		for(int i=0;i<size;i++){
    			ChartData chartData = new ChartData();
    			EnergyMonitQuanzhou e= list.get(i);
    			String time = toNextMinute(e.t_date + " " + e.t_time);
    			//二楼东卧温度
    			if(type.equals("twolyingeast_temperature")){
    				chartData.setTime(time);
    				chartData.setValue(e.onelyingeast_temperature);
    				legendType = "东卧温度";
    			}
    			//二楼客厅温度
    			if(type.equals("twoparlour_temperature")){
    				chartData.setTime(time);
    				chartData.setValue(e.twoparlour_temperature);
    				legendType = "客厅温度";
    			}
    			//二楼阳台温度
    			if(type.equals("twobalcony_temperature")){
    				chartData.setTime(time);
    				chartData.setValue(e.twobalcony_temperature);
    				legendType = "阳台温度";
    			}
    			/*******************************************/
    			//二楼东卧湿度
    			if(type.equals("twolyingeast_humidity")){
    				chartData.setTime(time);
    				chartData.setValue(e.twolyingeast_humidity);
    				legendType = "东卧湿度";
    			}
    			//二楼客厅湿度
    			if(type.equals("twoparlour_humidity")){
    				chartData.setTime(time);
    				chartData.setValue(e.twoparlour_humidity);
    				legendType = "客厅湿度";
    			}
    			//二楼阳台湿度
    			if(type.equals("twobalcony_humidity")){
    				chartData.setTime(time);
    				chartData.setValue(e.twobalcony_humidity);
    				legendType = "阳台湿度";
    			}
    			
    			chartDatas[i] = chartData;
    		}
    		series.put(legendType, chartDatas);

            ChartModel chartModel = new ChartModel();
            chartModel.setTitle("");
            chartModel.setSubtitle("");
            chartModel.setSeries(series);
            if (type.endsWith("temperature")) {
                chartModel.setyAxis_title(legendType+"(°C)");
                chartModel.setTooltip("°C");
            } else if (type.endsWith("humidity")) {
                chartModel.setyAxis_title(legendType+"(°%)");
                chartModel.setTooltip("%");
            }
            renderJSON(chartModel);
    	}
    }
    //二楼壁面温度
    private static void quanzhouTwoWallT(String type){
    	List<EnergyMonitQuanzhou> list = EnergyMonitQuanzhou.find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)").fetch();
    	int size = list.size();
    	
    	String legendType = null;//highchart图表的legend
    	Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
    	ChartData[] chartDatas = new ChartData[size];
    	if(list!=null&&size>0){
    		for(int i=0;i<size;i++){
    			ChartData chartData = new ChartData();
    			EnergyMonitQuanzhou e= list.get(i);
    			String time = toNextMinute(e.t_date + " " + e.t_time);
    			//东墙温度
    			if(type.equals("wall_east")){
    				chartData.setTime(time);
    				chartData.setValue(e.twoeastwall_temperature);
    				legendType = "东墙温度";
    			}
    			//西墙温度
    			if(type.equals("wall_west")){
    				chartData.setTime(time);
    				chartData.setValue(e.twowesternwall_temperature);
    				legendType = "西墙温度";
    			}
    			//南墙温度
    			if(type.equals("wall_sounth")){
    				chartData.setTime(time);
    				chartData.setValue(e.twosouthwall_temperature);
    				legendType = "南墙温度";
    			}
    			//北墙温度
    			if(type.equals("wall_north")){
    				chartData.setTime(time);
    				chartData.setValue(e.twonorthwall_temperature);
    				legendType = "北墙温度";
    			}
    			//地面温度
    			if(type.equals("wall_ground")){
    				chartData.setTime(time);
    				chartData.setValue(e.twofloor_temperature);
    				legendType = "地面温度";
    			}
    			//屋顶温度
    			if(type.equals("wall_roof")){
    				chartData.setTime(time);
    				chartData.setValue(e.twosouthwailimian_temperature);
    				legendType = "屋顶温度";
    			}
    			
    			chartDatas[i] = chartData;
    		}
    		series.put(legendType, chartDatas);

            ChartModel chartModel = new ChartModel();
            chartModel.setTitle("");
            chartModel.setSubtitle("");
            chartModel.setSeries(series);
            chartModel.setyAxis_title(legendType+"(°C)");
            chartModel.setTooltip("°C");
            renderJSON(chartModel);
    	}
    }
    
    //室内风速
    private static void quanzhouOutSpeed(String type){
    	List<EnergyMonitQuanzhou> list = EnergyMonitQuanzhou.find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)").fetch();
    	int size = list.size();
    	
    	String legendType = null;//highchart图表的legend
    	Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
    	ChartData[] chartDatas = new ChartData[size];
    	if(list!=null&&size>0){
    		for(int i=0;i<size;i++){
    			ChartData chartData = new ChartData();
    			EnergyMonitQuanzhou e= list.get(i);
    			String time = toNextMinute(e.t_date + " " + e.t_time);
    			//一楼东卧
    			if(type.equals("onelyingeast_windspeed")){
    				chartData.setTime(time);
    				chartData.setValue(e.onelyingeast_windspeed);
    				legendType = "一楼东卧风速";
    			}
    			//一楼过道
    			if(type.equals("oneaisle_windspeed")){
    				chartData.setTime(time);
    				chartData.setValue(e.oneaisle_windspeed);
    				legendType = "一楼过道风速";
    			}
    			//二楼东卧
    			if(type.equals("twolyingeast_windspeed")){
    				chartData.setTime(time);
    				chartData.setValue(e.twolyingeast_windspeed);
    				legendType = "二楼东卧风速";
    			}
    			//二楼过道
    			if(type.equals("twoaisle_windspeed")){
    				chartData.setTime(time);
    				chartData.setValue(e.twoaisle_windspeed);
    				legendType = "二楼过道风速";
    			}
    			
    			chartDatas[i] = chartData;
    		}
    		series.put(legendType, chartDatas);

            ChartModel chartModel = new ChartModel();
            chartModel.setTitle("");
            chartModel.setSubtitle("");
            chartModel.setSeries(series);
          
            chartModel.setyAxis_title(legendType+"(m/s)");
            chartModel.setTooltip("m/s");
       
            renderJSON(chartModel);
    	}
    }
    //电能耗情况
    private static void quanzhouElectric(String type){
    	List<EnergyMonitQuanzhou> list = EnergyMonitQuanzhou.find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)").fetch();
    	int size = list.size();
    	
    	String legendType = null;//highchart图表的legend
    	Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
    	ChartData[] chartDatas = new ChartData[size];
    	if(list!=null&&size>0){
    		for(int i=0;i<size;i++){
    			ChartData chartData = new ChartData();
    			EnergyMonitQuanzhou e= list.get(i);
    			String time = toNextMinute(e.t_date + " " + e.t_time);
    			//一楼电量
    			if(type.equals("oneelectric_quantity")){
    				chartData.setTime(time);
    				chartData.setValue(e.oneelectric_quantity);
    				legendType = "一楼电量";
    			}
    			//一楼电压
    			if(type.equals("oneelectric_tension")){
    				chartData.setTime(time);
    				chartData.setValue(e.oneelectric_tension);
    				legendType = "一楼电压";
    			}
    			//一楼电流
    			if(type.equals("oneelectric_current")){
    				chartData.setTime(time);
    				chartData.setValue(e.oneelectric_current);
    				legendType = "一楼电流";
    			}
    			//一楼功耗
    			if(type.equals("onepower")){
    				chartData.setTime(time);
    				chartData.setValue(e.power);
    				legendType = "一楼功耗";
    			}
    			/************************************************/
    			//二楼电量
    			if(type.equals("twoelectric_quantity")){
    				chartData.setTime(time);
    				chartData.setValue(e.onelyingeast_windspeed);
    				legendType = "二楼电量";
    			}
    			//二楼电压
    			if(type.equals("twoelectric_tension")){
    				chartData.setTime(time);
    				chartData.setValue(e.oneaisle_windspeed);
    				legendType = "二楼电压";
    			}
    			//二楼电流
    			if(type.equals("twoelectric_current")){
    				chartData.setTime(time);
    				chartData.setValue(e.twolyingeast_windspeed);
    				legendType = "二楼电流";
    			}
    			//二楼功耗
    			if(type.equals("twopower")){
    				chartData.setTime(time);
    				chartData.setValue(e.twopower);
    				legendType = "二楼功耗";
    			}
    			
    			chartDatas[i] = chartData;
    		}
    		series.put(legendType, chartDatas);

            ChartModel chartModel = new ChartModel();
            chartModel.setTitle("");
            chartModel.setSubtitle("");
            chartModel.setSeries(series);
            if(type.endsWith("tension")){
            	chartModel.setyAxis_title(legendType+"(V)");
                chartModel.setTooltip("V");
            }else if(type.endsWith("current")){
            	chartModel.setyAxis_title(legendType+"(mA)");
                chartModel.setTooltip("mA");
            }
            else if(type.endsWith("power")){
            	chartModel.setyAxis_title(legendType+"(W)");
                chartModel.setTooltip("W");
            }
            else if(type.endsWith("quantity")){
            	chartModel.setyAxis_title(legendType+"(C)");
                chartModel.setTooltip("C");
            }
            renderJSON(chartModel);
    	}
    }
    //室外参数
    private static void quanzhouOutdoor(String type){
    	List<EnergyMonitQuanzhou> list = EnergyMonitQuanzhou.find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)").fetch();
    	int size = list.size();
    	
    	String legendType = null;//highchart图表的legend
    	Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
    	ChartData[] chartDatas = new ChartData[size];
    	if(list!=null&&size>0){
    		for(int i=0;i<size;i++){
    			ChartData chartData = new ChartData();
    			EnergyMonitQuanzhou e= list.get(i);
    			String time = toNextMinute(e.t_date + " " + e.t_time);
    			//气压
    			if(type.equals("spec_barometric")){
    				chartData.setTime(time);
    				chartData.setValue(e.barometric);
    				legendType = "气压";
    			}
    			//风速
    			if(type.equals("spec_wind_speed")){
    				chartData.setTime(time);
    				chartData.setValue(e.wind_speed);
    				legendType = "风速";
    			}
    			//总辐射
    			if(type.equals("spec_total_radiation")){
    				chartData.setTime(time);
    				chartData.setValue(e.total_radiation);
    				legendType = "总辐射";
    			}
    			//直接辐射
    			if(type.equals("spec_direct_radiation")){
    				chartData.setTime(time);
    				chartData.setValue(e.direct_radiation);
    				legendType = "直接辐射";
    			}
    			/************************************************/
    			//室外温度
    			if(type.equals("sepc_outdoor_temperature")){
    				chartData.setTime(time);
    				chartData.setValue(e.outdoor_temperature);
    				legendType = "室外温度";
    			}
    			//室外湿度
    			if(type.equals("spec_outdoor_humidity")){
    				chartData.setTime(time);
    				chartData.setValue(e.outdoor_humidity);
    				legendType = "室外湿度";
    			}
    			
    			chartDatas[i] = chartData;
    		}
    		series.put(legendType, chartDatas);

            ChartModel chartModel = new ChartModel();
            chartModel.setTitle("");
            chartModel.setSubtitle("");
            chartModel.setSeries(series);
            if(type.endsWith("barometric")){
            	chartModel.setyAxis_title(legendType+"(Pa)");
            	chartModel.setTooltip("Pa");
            }else if(type.endsWith("speed")){
            	chartModel.setyAxis_title(legendType+"(m/s)");
            	chartModel.setTooltip("m/s");
            }else if(type.endsWith("speed")){
            	chartModel.setyAxis_title(legendType+"(m/s)");
            	chartModel.setTooltip("m/s");
            }else if(type.endsWith("radiation")){
            	chartModel.setyAxis_title(legendType+"(Ci)");
            	chartModel.setTooltip("Ci");
            }else if(type.endsWith("temperature")){
            	chartModel.setyAxis_title(legendType+"(°C)");
            	chartModel.setTooltip("°C");
            }else if(type.endsWith("humidity")){
            	chartModel.setyAxis_title(legendType+"(%)");
            	chartModel.setTooltip("%");
            }
            renderJSON(chartModel);
    	}
    }
    //泉州用水量
    private static void quanzhouWater(String type){
    	List<EnergyMonitQuanzhou> list = EnergyMonitQuanzhou.find("from EnergyMonitQuanzhou t where DATEDIFF(NOW(),t_date) = (select MIN(DATEDIFF(now(), t_date)) from EnergyMonitQuanzhou t)").fetch();
    	int size = list.size();
    	
    	String legendType = null;//highchart图表的legend
    	Map<Object, ChartData[]> series = new HashMap<Object, ChartData[]>();
    	ChartData[] chartDatas = new ChartData[size];
    	if(list!=null&&size>0){
    		for(int i=0;i<size;i++){
    			ChartData chartData = new ChartData();
    			EnergyMonitQuanzhou e= list.get(i);
    			String time = toNextMinute(e.t_date + " " + e.t_time);
    			
				chartData.setTime(time);
				chartData.setValue(e.water_index);
				legendType = "用水量";
    			
    			chartDatas[i] = chartData;
    		}
    		series.put(legendType, chartDatas);

            ChartModel chartModel = new ChartModel();
            chartModel.setTitle("");
            chartModel.setSubtitle("");
            chartModel.setSeries(series);
            chartModel.setyAxis_title(legendType+"(m3)");
            chartModel.setTooltip("m3");
            
            renderJSON(chartModel);
    	}
    }
    //泉州过去7天的数据
    public static void quanzhouPast7DaysData(){
    	String type = params.get("type");//测点参数：一二楼等室内外参数
    	
    	String[] tmp = new String[3];
    	tmp = setHighChartsyTitle(type);//根据测点参数类型，返回highCharts图标的y坐标标题和toolTip提示单位
    	
    	type = tmp[0];
    	String yTitle = tmp[1];//highCharts图标的y坐标标题
    	String toolTip = tmp[2];//toolTip提示单位
        
        DateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String nowDate = sdf.format(date);
        calendar.add(Calendar.DATE, -7);
        String pastDate = sdf.format(calendar.getTime());
        //选取过去七天的数据
        String jpql = "select t_date,max(" + type + ")" + " as max,min(" + type + ")" + " as min,avg("
                + type + ")" + " as average from EnergyMonitQuanzhou t where date(t_date) between '"+pastDate+"' and '"+nowDate+"' GROUP BY t_date ORDER BY date(t_date) ASC";
//        System.out.println(jpql+"\n"+nowDate);

        List list = JPA.em().createQuery(jpql).getResultList();
        int size = list.size();
        String[] time1 = new String[size];
        String[] max1 = new String[size];
        String[] min1 = new String[size];
        String[] avg1 = new String[size];
        
        if(list!=null){
        	Iterator iterator = list.iterator();
        	int i = 0;
        	while(iterator.hasNext()){
        		Object[] row = ( Object[])iterator.next();
        		time1[i] = row[0].toString();
        		max1[i] = row[1].toString();
        		min1[i] = row[2].toString();
        		avg1[i] = row[3].toString();
        		i++;
        	}
        }
        Map<String, CombinationChartData> series = new HashMap<>();
        series.put("平均值", new CombinationChartData("column", avg1));
        series.put("最大值", new CombinationChartData("scatter", max1));
        series.put("最小值", new CombinationChartData("scatter", min1));

        CombinationChartModel combinationChartModel = new CombinationChartModel();
        combinationChartModel.setTitle("");
        combinationChartModel.setSubtitle("");
        combinationChartModel.setCategories(time1);
        combinationChartModel.setSeries(series);
    	combinationChartModel.setyAxis_title(yTitle);
    	combinationChartModel.setTooltip(toolTip);

        renderJSON(combinationChartModel);
    }
    //泉州--逐月分析
    public static void quanzhouMonthData(){
    	String type = params.get("type");//测点参数：卧室温度、厨房温度等
    	
    	String[] tmp = new String[3];
    	tmp = setHighChartsyTitle(type);
    	
    	type = tmp[0];
    	String yTitle = tmp[1];
    	String toolTip = tmp[2];
    	
    	Calendar cal = Calendar.getInstance();
    	int year = cal.get(Calendar.YEAR);
        //选取每个月的最大最小平均值
        String jpql = "select CONCAT(YEAR(t_date),'-',MONTH (t_date)) as date,max(" + type + ")" + " as max,min(" + type + ")" + " as min,avg("
                + type + ")" + " as average from EnergyMonitQuanzhou t where SUBSTRING(t_date,1,4)='"+year+
        		"' GROUP BY CONCAT(YEAR(t_date),'-',MONTH (t_date)) ORDER BY MONTH(t_date) ASC";
//        System.out.println(jpql);

        List list = JPA.em().createQuery(jpql).getResultList();
        int size = list.size();
        String[] time1 = new String[size];
        String[] max1 = new String[size];
        String[] min1 = new String[size];
        String[] avg1 = new String[size];
        
        if(list!=null){
        	Iterator iterator = list.iterator();
        	int i = 0;
        	while(iterator.hasNext()){
        		Object[] row = ( Object[])iterator.next();
        		time1[i] = row[0].toString();
        		max1[i] = row[1].toString();
        		min1[i] = row[2].toString();
        		avg1[i] = row[3].toString();
        		i++;
        	}
        }
        Map<String, CombinationChartData> series = new HashMap<>();
        series.put("平均值", new CombinationChartData("column", avg1));
        series.put("最大值", new CombinationChartData("scatter", max1));
        series.put("最小值", new CombinationChartData("scatter", min1));

        CombinationChartModel combinationChartModel = new CombinationChartModel();
        combinationChartModel.setTitle("");
        combinationChartModel.setSubtitle("");
        combinationChartModel.setCategories(time1);
        combinationChartModel.setSeries(series);
    	combinationChartModel.setyAxis_title(yTitle);
    	combinationChartModel.setTooltip(toolTip);

        renderJSON(combinationChartModel);
    }
    
    
    //根据泉州测点参数类型，返回type,highCharts图标的y坐标标题和toolTip提示单位
    private static String[] setHighChartsyTitle(String type){
    	String yTitle=null,tooltip=null;
    	String[] result = new String[3];
    	
    	//tooltip
    	if(type.endsWith("temperature")){
    		tooltip = "°C";
    	}else if(type.endsWith("humidity")){
    		tooltip = "%";
    	}else if(type.endsWith("windspeed")){
    		tooltip = "m/s";
    	}
    	//一楼室内温湿度
    	if(type.startsWith("one")&&(type.endsWith("temperature") || type.endsWith("humidity"))){
    		if (type.equals("onekitchen_temperature")){
    			yTitle = "一楼厨房温度(°C)";
    		}else if (type.equals("onewashroom_temperature")){
    			yTitle = "一楼卫生间温度(°C)";
    		}else if (type.equals("onelyingeast_temperature")){
    			yTitle = "一楼东卧温度(°C)";
    		}else if (type.equals("oneaisle_temperature")){
    			yTitle = "一楼过道温度(°C)";
    		}
    		else if (type.equals("onekitchen_humidity")){
    			yTitle = "一楼厨房湿度(%)";
    		}else if (type.equals("onewashroom_humidity")){
    			yTitle = "一楼卫生间湿度(%)";
    		}else if (type.equals("onelyingeast_humidity")){
    			yTitle = "一楼东卧湿度(%)";
    		}else if (type.equals("oneaisle_humidity")){
    			yTitle = "一楼过道湿度(%)";
    		}
    	}
    	//二楼室内温湿度
    	if(type.startsWith("two")&&(type.endsWith("temperature") || type.endsWith("humidity"))){
    		if (type.equals("twoparlour_temperature")){
    			yTitle = "二楼客厅温度(°C)";
    		}else if (type.equals("twolyingeast_temperature")){
    			yTitle = "二楼东卧温度(°C)";
    		}else if (type.equals("twobalcony_temperature")){
    			yTitle = "二楼阳台温度(°C)";
    		}
    		else if (type.equals("twoparlour_humidity")){
    			yTitle = "二楼客厅湿度(°C)";
    		}else if (type.equals("twolyingeast_humidity")){
    			yTitle = "二楼东卧湿度(°C)";
    		}else if (type.equals("twobalcony_humidity")){
    			yTitle = "二楼阳台湿度(°C)";
    		}
    	}
    	//二楼壁面温度
    	if(type.startsWith("wall")){
    		tooltip = "°C";
    		if(type.equals("wall_east")){
    			yTitle = "东墙温度(°C)";
    			type = "twoeastwall_temperature";
    		}else if (type.equals("wall_west")){
    			type = "twowesternwall_temperature";
    			yTitle ="西墙温度(°C)";
    		}else if (type.equals("wall_sounth")){
    			type = "twosouthwall_temperature";
    			yTitle ="南墙温度(°C)";
    		}else if (type.equals("wall_north")){
    			type = "twonorthwall_temperature";
    			yTitle ="北墙温度(°C)";
    		}else if (type.equals("wall_ground")){
    			type = "twofloor_temperature";
    			yTitle ="地面温度(°C)";
    		}else if (type.equals("wall_roof")){
    			type = "twosouthwailimian_temperature";
    			yTitle ="屋顶温度(°C)";
    		}
    	}
    	//室内风速
    	if((type.startsWith("one")|| type.startsWith("two"))&&type.endsWith("speed")){
    		if(type.equals("onelyingeast_windspeed")){
    			yTitle = "一楼东卧风速";
    		}else if(type.equals("oneaisle_windspeed")){
    			yTitle = "一楼过道风速";
    		}if(type.equals("twolyingeast_windspeed")){
    			yTitle = "二楼东卧风速";
    		}if(type.equals("twolyingeast_windspeed")){
    			yTitle = "二楼过道风速";
    		}
    	}
    	//电能耗情况
    	if(type.contains("electric") || type.contains("power")){
    		if(type.equals("oneelectric_quantity")){
    			yTitle = "一楼电量";
    		}else if(type.equals("oneelectric_tension")){
    			yTitle = "一楼电压(V)";
    			tooltip = "V";
    		}else if(type.equals("oneelectric_current")){
    			yTitle = "一楼电流(A)";
    			tooltip = "A";
    		}else if(type.equals("onepower")){
    			type = "power";
        		yTitle = "一楼功率(W)";
        		tooltip = "W";
        	}else if(type.equals("twoelectric_quantity")){
    			yTitle = "二楼电量";
    		}else if(type.equals("twoelectric_tension")){
    			yTitle = "二楼电压(V)";
    			tooltip = "V";
    		}else if(type.equals("twoelectric_current")){
    			yTitle = "二楼电流(A)";
    			tooltip = "A";
    		}else if(type.equals("twopower")){
        		yTitle = "二楼功率(W)";
        		tooltip = "W";
        	}
    	}
    	
    	//室外参数 
    	if(type.startsWith("spec")){
    		type = type.substring(5);
    		if(type.equals("barometric")){
    			yTitle = "大气压力(KPa)";
    			tooltip = "KPa";
    		}else if(type.equals("total_radiation")){
    			yTitle = "总辐射(Ci)";
    			tooltip = "Ci";
    		}else if(type.equals("direct_radiation")){
    			yTitle = "直接辐射(Ci)";
    			tooltip = "Ci";
    		}else if(type.equals("wind_speed")){
    			yTitle = "风速(m/s)";
    			tooltip = "m/s";
    		}
    		
    	}
    	//用水量
    	if(type.equals("water_index")){
    		yTitle = "用水量(m3)";
    		tooltip = "m3";
    	}
    	
    	result[0] = type;result[1] = yTitle; result[2] = tooltip;
    	return result;
    }
   
    public static void export() {
        String start = params.get("start2");
        String end = params.get("end2");
        String location = params.get("location2");
        String arguments = params.get("arguments2");
        String type = params.get("type");
        ExportUtils.exportExcels(start, end, location, arguments, type, response);
    }
   /* public static void main(String[] args) {
    	DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String nowDate = sdf.format(date);
        calendar.add(Calendar.DATE, -7);
        String pastDate = sdf.format(calendar.getTime());
        
        System.out.println(nowDate+" "+pastDate);
	}*/
}
