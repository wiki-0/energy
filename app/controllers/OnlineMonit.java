package controllers;

import play.*;
import play.db.Model;
import play.mvc.*;
import utils.JsonUtil;
import utils.OnlineJsonData;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.*;
import utils.TimeFormatUtil;

public class OnlineMonit extends Controller {

    public static void reportModel() {
        render();
    }

    public static void monitoring() {
        render();
    }

    public static void monitor() {
        render();
    }

    public static void monitorFdc() {
        render("OnlineMonit/monitor_fdc.html");
    }

    public static void monitorHle() {
        render("OnlineMonit/monitor_hle.html");
    }

    public static void monitorQz() {
        render("OnlineMonit/monitor_qz.html");
    }

    public static void monitorMsc() {
        render("OnlineMonit/monitor_msc.html");
    }

    public static void energyFdc() {
        render("OnlineMonit/energy_fdc.html");
    }
    
    public static void energyMsc() {
        render("OnlineMonit/energy_msc.html");
    }
    
    public static void energyQz() {
        render("OnlineMonit/energy_qz.html");
    }
    

    public static void energyHle() {
        render("OnlineMonit/energy_hle.html");
    }

    public static void indoorHle() {
        render("OnlineMonit/indoor_hle.html");
    }

    public static void indoorQz() {
        render("OnlineMonit/indoor_qz.html");
    }

    public static void indoorFdc() {
        render("OnlineMonit/indoor_fdc.html");
    }

    public static void indoorMsc() {
        render("OnlineMonit/indoor_msc.html");
    }

    public static void outdoorHle() {
        render("OnlineMonit/outdoor_hle.html");
    }

    public static void outdoorQz() {
        render("OnlineMonit/outdoor_qz.html");
    }

    public static void outdoorFdc() {
        render("OnlineMonit/outdoor_fdc.html");
    }

    public static void outdoorMsc() {
        render("OnlineMonit/outdoor_msc.html");
    }

    //海拉尔-室内
    public static void getindoorHle() {
        String type = params.get("testType");
        List<EnergyMonitHailar> List = EnergyMonitHailar.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        if (List != null && List.size() == 2) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitHailar o : List) {
                obj = new JsonObject();
                obj.addProperty("time", o.t_date.trim() + " " + o.t_time.trim());
                if (type.equals("1")) {
                    obj.addProperty("title", "室内空气温度");
                    obj.addProperty("unit", "度");
                    obj.addProperty("date1_1", o.parlour_temperature);
                    obj.addProperty("date1_2", o.dining_temperature);
                    obj.addProperty("date2_1", o.bedroom_temperature);
                    obj.addProperty("date2_2", o.hall_temperature);
                    obj.addProperty("date3_1", o.washroom_temperature);
                    obj.addProperty("date3_2", o.kitchen_temperature);
                } else if (type.equals("2")) {
                    obj.addProperty("title", "室内空气湿度");
                    obj.addProperty("unit", "RH");
                    obj.addProperty("date1_1", o.parlour_humidity);
                    obj.addProperty("date1_2", o.dining_humidity);
                    obj.addProperty("date2_1", o.bedroom_humidity);
                    obj.addProperty("date2_2", o.hall_humidity);
                    obj.addProperty("date3_1", o.kitchen_humidity);
                    obj.addProperty("date3_2", o.outdoor_humidity);
                } else if (type.equals("3")) {
                    obj.addProperty("title", "内壁面温度");
                    obj.addProperty("unit", "度");
                    obj.addProperty("date1_1", o.east_wall_temperature);
                    obj.addProperty("date1_2", o.west_wall_temperature);
                    obj.addProperty("date2_1", o.south_wall_temperature);
                    obj.addProperty("date2_2", o.north_wall_temperature);
                    obj.addProperty("date3_1", o.roof_temperature);
                    obj.addProperty("date3_2", o.ground_temperature);
                }
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }

    //海拉尔-室内 最近6小时
    public static void indoorHleData() throws ParseException {

        String type = params.get("testType");
        String lastTime = params.get("lastTime");
        String sql = "from EnergyMonitHailar t where str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') between '"+TimeFormatUtil.hoursAgo(lastTime)+"' and '"+lastTime+"'";
        List<EnergyMonitHailar> List = EnergyMonitHailar.find(sql).fetch();
        Map<Object, ChartData[]> series=new HashMap<Object,ChartData[]>();
        int size = List.size();
        ChartData[] chartData1=new ChartData[size];
        ChartData[] chartData2=new ChartData[size];
        ChartData[] chartData3=new ChartData[size];
        ChartData[] chartData4=new ChartData[size];
        ChartData[] chartData5=new ChartData[size];
        ChartData[] chartData6=new ChartData[size];

        if (type.equals("1") && size > 0) {
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();
                ChartData Data3 = new ChartData();
                ChartData Data4 = new ChartData();
                ChartData Data5 = new ChartData();
                ChartData Data6 = new ChartData();

                Data1.setValue(o.parlour_temperature);
                Data1.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData1[i] = Data1;
                Data2.setValue(o.dining_temperature);
                Data2.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData2[i] = Data2;
                Data3.setValue(o.bedroom_temperature);
                Data3.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData3[i] = Data3;
                Data4.setValue(o.hall_temperature);
                Data4.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData4[i] = Data4;
                Data5.setValue(o.washroom_temperature);
                Data5.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData5[i] = Data5;
                Data6.setValue(o.kitchen_temperature);
                Data6.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData6[i] = Data6;

            }
            series.put("客厅温度", chartData1);
            series.put("饭厅温度", chartData2);
            series.put("卧室温度", chartData3);
            series.put("门厅温度", chartData4);
            series.put("卫生间温度", chartData5);
            series.put("厨房温度", chartData6);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室内空气温度");
            chartModel.setyAxis_title("Value (°C)");
            chartModel.setTooltip("°C");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (size > 0 && type.equals("2")) {
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();
                ChartData Data3 = new ChartData();
                ChartData Data4 = new ChartData();
                ChartData Data5 = new ChartData();
                ChartData Data6 = new ChartData();

                Data1.setValue(o.parlour_humidity);
                Data1.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData1[i] = Data1;
                Data2.setValue(o.dining_humidity);
                Data2.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData2[i] = Data2;
                Data3.setValue(o.bedroom_humidity);
                Data3.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData3[i] = Data3;
                Data4.setValue(o.hall_humidity);
                Data4.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData4[i] = Data4;
                Data5.setValue(o.washroom_humidity);
                Data5.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData5[i] = Data5;
                Data6.setValue(o.kitchen_humidity);
                Data6.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData6[i] = Data6;

            }
            series.put("客厅湿度", chartData1);
            series.put("饭厅湿度", chartData2);
            series.put("卧室湿度", chartData3);
            series.put("门厅湿度", chartData4);
            series.put("卫生间湿度", chartData5);
            series.put("厨房湿度", chartData6);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室内空气湿度");
            chartModel.setyAxis_title("Value (RH)");
            chartModel.setTooltip("RH");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (size > 0 && type.equals("3")) {
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();
                ChartData Data3 = new ChartData();
                ChartData Data4 = new ChartData();
                ChartData Data5 = new ChartData();
                ChartData Data6 = new ChartData();

                Data1.setValue(o.east_wall_temperature);
                Data1.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData1[i] = Data1;
                Data2.setValue(o.west_wall_temperature);
                Data2.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData2[i] = Data2;
                Data3.setValue(o.south_wall_temperature);
                Data3.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData3[i] = Data3;
                Data4.setValue(o.north_wall_temperature);
                Data4.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData4[i] = Data4;
                Data5.setValue(o.roof_temperature);
                Data5.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData5[i] = Data5;
                Data6.setValue(o.ground_temperature);
                Data6.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData6[i] = Data6;

            }
            series.put("东墙温度", chartData1);
            series.put("西墙温度", chartData2);
            series.put("南墙温度", chartData3);
            series.put("北墙温度", chartData4);
            series.put("屋顶温度", chartData5);
            series.put("地面温度", chartData6);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("内壁面温度");
            chartModel.setyAxis_title("Value (°C)");
            chartModel.setTooltip("°C");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        }
    }

    //席里村-室内
    public static void getindoorQz() {
        String type = params.get("testType");
        List<EnergyMonitQuanzhou> List = EnergyMonitQuanzhou.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        if (List != null && List.size() == 2) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitQuanzhou o : List) {
                obj = new JsonObject();
                obj.addProperty("time", o.t_date.trim() + " " + o.t_time.trim());
                if (type.equals("1")) {
                    obj.addProperty("title", "一层室内空气温度");
                    obj.addProperty("unit", "度");
                    obj.addProperty("date1_1", o.onelyingeast_temperature);
                    obj.addProperty("date1_2", o.onekitchen_temperature);
                    obj.addProperty("date2_1", o.oneaisle_temperature);
                    obj.addProperty("date2_2", o.onewashroom_temperature);
                } else if (type.equals("2")) {
                    obj.addProperty("title", "一层室内空气湿度");
                    obj.addProperty("unit", "RH");
                    obj.addProperty("date1_1", o.onelyingeast_humidity);
                    obj.addProperty("date1_2", o.onekitchen_humidity);
                    obj.addProperty("date2_1", o.oneaisle_humidity);
                    obj.addProperty("date2_2", o.onewashroom_humidity);
                } else if (type.equals("3")) {
                    obj.addProperty("title", "一层室内风速");
                    obj.addProperty("unit", "m/s");
                    obj.addProperty("date1_1", o.onelyingeast_windspeed);
                    obj.addProperty("date1_2", o.oneaisle_windspeed);
                } else if (type.equals("4")) {
                    obj.addProperty("title", "二层室内空气温度");
                    obj.addProperty("unit", "度");
                    obj.addProperty("date1_1", o.twolyingeast_temperature);
                    obj.addProperty("date1_2", o.twobalcony_temperature);
                    obj.addProperty("date2_1", o.twoparlour_temperature);
                } else if (type.equals("5")) {
                    obj.addProperty("title", "二层室内空气湿度");
                    obj.addProperty("unit", "RH");
                    obj.addProperty("date1_1", o.twolyingeast_humidity);
                    obj.addProperty("date1_2", o.twobalcony_humidity);
                    obj.addProperty("date2_1", o.twoparlour_humidity);
                } else if (type.equals("6")) {
                    obj.addProperty("title", "二层内壁面温度");
                    obj.addProperty("unit", "度");
                    obj.addProperty("date1_1", o.twoeastwall_temperature);
                    obj.addProperty("date1_2", o.twowesternwall_temperature);
                    obj.addProperty("date2_1", o.twosouthwailimian_temperature);
                    obj.addProperty("date2_2", o.twonorthwall_temperature);
                    obj.addProperty("date3_1", o.twosouthwailimian_temperature);
                    obj.addProperty("date3_2", o.twofloor_temperature);
                } else if (type.equals("7")) {
                    obj.addProperty("title", "二层室内风速");
                    obj.addProperty("unit", "m/s");
                    obj.addProperty("date1_1", o.twolyingeast_windspeed);
                    obj.addProperty("date1_2", o.twoaisle_windspeed);
                }
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }

    //席里村-室内 最近6小时
    public static void indoorQzData() throws ParseException {

        String type = params.get("testType");
        String lastTime = params.get("lastTime");
        String sql = "from EnergyMonitQuanzhou t where str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') between '"+TimeFormatUtil.hoursAgo(lastTime)+"' and '"+lastTime+"'";
        List<EnergyMonitQuanzhou> List = EnergyMonitQuanzhou.find(sql).fetch();
        Map<Object, ChartData[]> series=new HashMap<Object,ChartData[]>();
        int size = List.size();
        ChartData[] chartData1=new ChartData[size];
        ChartData[] chartData2=new ChartData[size];
        ChartData[] chartData3=new ChartData[size];
        ChartData[] chartData4=new ChartData[size];
        ChartData[] chartData5=new ChartData[size];
        ChartData[] chartData6=new ChartData[size];

        if (type.equals("1") && size > 0) {
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();
                ChartData Data3 = new ChartData();
                ChartData Data4 = new ChartData();
                Data1.setValue(o.onelyingeast_temperature);
                Data1.setTime(o.t_date + " " + o.t_time);
                chartData1[i] = Data1;
                Data2.setValue(o.onekitchen_temperature);
                Data2.setTime(o.t_date + " " + o.t_time);
                chartData2[i] = Data2;
                Data3.setValue(o.oneaisle_temperature);
                Data3.setTime(o.t_date + " " + o.t_time);
                chartData3[i] = Data3;
                Data4.setValue(o.onewashroom_temperature);
                Data4.setTime(o.t_date + " " + o.t_time);
                chartData4[i] = Data4;

            }
            series.put("东卧温度", chartData1);
            series.put("厨房温度", chartData2);
            series.put("过道温度", chartData3);
            series.put("卫生间温度", chartData4);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("第一层室内实时温度");
            chartModel.setyAxis_title("Value (°C)");
            chartModel.setTooltip("°C");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (size > 0 && type.equals("2")) {
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();
                ChartData Data3 = new ChartData();
                ChartData Data4 = new ChartData();

                Data1.setValue(o.onelyingeast_humidity);
                Data1.setTime(o.t_date + " " + o.t_time);
                chartData1[i] = Data1;
                Data2.setValue(o.onekitchen_humidity);
                Data2.setTime(o.t_date + " " + o.t_time);
                chartData2[i] = Data2;
                Data3.setValue(o.oneaisle_humidity);
                Data3.setTime(o.t_date + " " + o.t_time);
                chartData3[i] = Data3;
                Data4.setValue(o.onewashroom_humidity);
                Data4.setTime(o.t_date + " " + o.t_time);
                chartData4[i] = Data4;

            }
            series.put("东卧湿度", chartData1);
            series.put("厨房湿度", chartData2);
            series.put("过道湿度", chartData3);
            series.put("卫生间湿度", chartData4);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("第一层室内湿度");
            chartModel.setyAxis_title("Value (RH)");
            chartModel.setTooltip("RH");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (size > 0 && type.equals("3")) {
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();

                Data1.setValue(o.onelyingeast_windspeed);
                Data1.setTime(o.t_date + " " + o.t_time);
                chartData1[i] = Data1;
                Data2.setValue(o.oneaisle_windspeed);
                Data2.setTime(o.t_date + " " + o.t_time);
                chartData2[i] = Data2;
            }
            series.put("东卧风速", chartData1);
            series.put("过道风速", chartData2);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("第一层室内风速");
            chartModel.setyAxis_title("Value (m/s)");
            chartModel.setTooltip("m/s");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (size > 0 && type.equals("4")) {
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();
                ChartData Data3 = new ChartData();

                Data1.setValue(o.onelyingeast_temperature);
                Data1.setTime(o.t_date + " " + o.t_time);
                chartData1[i] = Data1;
                Data2.setValue(o.onekitchen_temperature);
                Data2.setTime(o.t_date + " " + o.t_time);
                chartData2[i] = Data2;
                Data3.setValue(o.oneaisle_temperature);
                Data3.setTime(o.t_date + " " + o.t_time);
                chartData3[i] = Data3;

            }
            series.put("东侧南卧温度", chartData1);
            series.put("阳台温度", chartData2);
            series.put("客厅温度", chartData3);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("第二层室内实时温度");
            chartModel.setyAxis_title("Value (°C)");
            chartModel.setTooltip("°C");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (size > 0 && type.equals("5")) {
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();
                ChartData Data3 = new ChartData();

                Data1.setValue(o.onelyingeast_humidity);
                Data1.setTime(o.t_date + " " + o.t_time);
                chartData1[i] = Data1;
                Data2.setValue(o.onekitchen_humidity);
                Data2.setTime(o.t_date + " " + o.t_time);
                chartData2[i] = Data2;
                Data3.setValue(o.oneaisle_humidity);
                Data3.setTime(o.t_date + " " + o.t_time);
                chartData3[i] = Data3;

            }
            series.put("东侧南卧湿度", chartData1);
            series.put("阳台湿度", chartData2);
            series.put("客厅湿度", chartData3);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("第二层室内湿度");
            chartModel.setyAxis_title("Value (RH)");
            chartModel.setTooltip("RH");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("6") && size > 0) {
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();
                ChartData Data3 = new ChartData();
                ChartData Data4 = new ChartData();
                ChartData Data5 = new ChartData();
                ChartData Data6 = new ChartData();

                Data1.setValue(o.twoeastwall_temperature);
                Data1.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData1[i] = Data1;
                Data2.setValue(o.twowesternwall_temperature);
                Data2.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData2[i] = Data2;
                Data3.setValue(o.twosouthwailimian_temperature);
                Data3.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData3[i] = Data3;
                Data4.setValue(o.twonorthwall_temperature);
                Data4.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData4[i] = Data4;
                Data5.setValue(o.twosouthwailimian_temperature);
                Data5.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData5[i] = Data5;
                Data6.setValue(o.twofloor_temperature);
                Data6.setTime(o.t_date.trim() + " " + o.t_time.trim());
                chartData6[i] = Data6;

            }
            series.put("东墙温度", chartData1);
            series.put("西墙温度", chartData2);
            series.put("南墙温度", chartData3);
            series.put("北墙温度", chartData4);
            series.put("屋顶温度", chartData5);
            series.put("地面温度", chartData6);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("第二层内壁面温度");
            chartModel.setyAxis_title("Value (°C)");
            chartModel.setTooltip("°C");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (size > 0 && type.equals("7")) {
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData Data1 = new ChartData();
                ChartData Data2 = new ChartData();

                Data1.setValue(o.twolyingeast_windspeed);
                Data1.setTime(o.t_date + " " + o.t_time);
                chartData1[i] = Data1;
                Data2.setValue(o.twoaisle_windspeed);
                Data2.setTime(o.t_date + " " + o.t_time);
                chartData2[i] = Data2;

            }
            series.put("阳台风速", chartData1);
            series.put("过道风速", chartData2);

            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("第二层室内风速");
            chartModel.setyAxis_title("Value (m/s)");
            chartModel.setTooltip("m/s");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        }
    }

    //海拉尔-室外
    public static void getoutdoorHle() {
        String type = params.get("testType");
        List<EnergyMonitHailar> List = EnergyMonitHailar.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        if (List != null && List.size() == 2) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitHailar o : List) {
                obj = new JsonObject();
                obj.addProperty("time", o.t_date + " " + o.t_time);
                if (type.equals("1")) {
                    obj.addProperty("title", "室外温度");
                    obj.addProperty("unit", "度");
                    obj.addProperty("date", o.outdoor_temperature);
                } else if (type.equals("2")) {
                    obj.addProperty("title", "室外湿度");
                    obj.addProperty("unit", "RH");
                    obj.addProperty("date", o.outdoor_humidity);
                } else if (type.equals("3")) {
                    obj.addProperty("title", "室外压力");
                    obj.addProperty("unit", "PA");
                    obj.addProperty("date", o.barometric);
                } else if (type.equals("4")) {
                    obj.addProperty("title", "室外风速");
                    obj.addProperty("unit", "m/s");
                    obj.addProperty("date", o.wind_speed);
                } else if (type.equals("5")) {
                    obj.addProperty("title", "总辐射");
                    obj.addProperty("unit", "SV");
                    obj.addProperty("date", o.total_radiation);
                } else if (type.equals("6")) {
                    obj.addProperty("title", "直接辐射");
                    obj.addProperty("unit", "SV");
                    obj.addProperty("date", o.direct_radiation);
                }
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }

    //海拉尔-室外 最近6小时
    public static void outdoorHleData() throws ParseException {
        String type = params.get("testType");
        String lastTime = params.get("lastTime");
        String sql = "from EnergyMonitHailar t where str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') between '"+TimeFormatUtil.hoursAgo(lastTime)+"' and '"+lastTime+"'";
        List<EnergyMonitHailar> List = EnergyMonitHailar.find(sql).fetch();
        Map<Object, ChartData[]> series=new HashMap<Object,ChartData[]>();
        int size = List.size();
        ChartData[] chartDatas=new ChartData[size];

        if (type.equals("1") && size > 0) {
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.outdoor_temperature);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("室外温度", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室外温度");
            chartModel.setyAxis_title("Value (°C)");
            chartModel.setTooltip("°C");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("2") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.outdoor_humidity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("室外湿度", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室外湿度");
            chartModel.setyAxis_title("Value (RH)");
            chartModel.setTooltip("RH");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("3") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.barometric);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("室外压力", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室外压力");
            chartModel.setyAxis_title("Value (PA)");
            chartModel.setTooltip("PA");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("4") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.wind_speed);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("室外风速", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室外风速");
            chartModel.setyAxis_title("Value ()");
            chartModel.setTooltip("m/s");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("5") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.total_radiation);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("总辐射", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("总辐射");
            chartModel.setyAxis_title("Value (SV)");
            chartModel.setTooltip("SV");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("6") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.direct_radiation);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("直接辐射", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("直接辐射");
            chartModel.setyAxis_title("Value (SV)");
            chartModel.setTooltip("SV");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        }
    }

    //席里村-室外
    public static void getoutdoorQz() {
        String type = params.get("testType");
        List<EnergyMonitQuanzhou> List = EnergyMonitQuanzhou.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        if (List != null && List.size() == 2) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitQuanzhou o : List) {
                obj = new JsonObject();
                obj.addProperty("time", o.t_date + " " + o.t_time);
                if (type.equals("1")) {
                    obj.addProperty("title", "室外温度");
                    obj.addProperty("unit", "度");
                    obj.addProperty("date", o.outdoor_temperature);
                } else if (type.equals("2")) {
                    obj.addProperty("title", "室外湿度");
                    obj.addProperty("unit", "RH");
                    obj.addProperty("date", o.outdoor_humidity);
                } else if (type.equals("3")) {
                    obj.addProperty("title", "室外压力");
                    obj.addProperty("unit", "PA");
                    obj.addProperty("date", o.barometric);
                } else if (type.equals("4")) {
                    obj.addProperty("title", "室外风速");
                    obj.addProperty("unit", "m/s");
                    obj.addProperty("date", o.wind_speed);
                } else if (type.equals("5")) {
                    obj.addProperty("title", "总辐射");
                    obj.addProperty("unit", "SV");
                    obj.addProperty("date", o.total_radiation);
                } else if (type.equals("6")) {
                    obj.addProperty("title", "直接辐射");
                    obj.addProperty("unit", "SV");
                    obj.addProperty("date", o.direct_radiation);
                }
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }

    //席里村-室外 最近6小时
    public static void outdoorQzData() throws ParseException {
        String type = params.get("testType");
        String lastTime = params.get("lastTime");
        String sql = "from EnergyMonitQuanzhou t where str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') between '"+TimeFormatUtil.hoursAgo(lastTime)+"' and '"+lastTime+"'";
        List<EnergyMonitQuanzhou> List = EnergyMonitQuanzhou.find(sql).fetch();
        Map<Object, ChartData[]> series=new HashMap<Object,ChartData[]>();
        int size = List.size();
        ChartData[] chartDatas=new ChartData[size];

        if (type.equals("1") && size > 0) {
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.outdoor_temperature);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("室外温度", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室外温度");
            chartModel.setyAxis_title("Value (°C)");
            chartModel.setTooltip("°C");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("2") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.outdoor_humidity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("室外湿度", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室外湿度");
            chartModel.setyAxis_title("Value (RH)");
            chartModel.setTooltip("RH");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("3") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.barometric);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("室外压力", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室外压力");
            chartModel.setyAxis_title("Value (PA)");
            chartModel.setTooltip("PA");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("4") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.wind_speed);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("室外风速", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("室外风速");
            chartModel.setyAxis_title("Value ()");
            chartModel.setTooltip("m/s");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("5") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.total_radiation);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("总辐射", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("总辐射");
            chartModel.setyAxis_title("Value (SV)");
            chartModel.setTooltip("SV");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        } else if (type.equals("6") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.direct_radiation);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
            }
            series.put("直接辐射", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("直接辐射");
            chartModel.setyAxis_title("Value (SV)");
            chartModel.setTooltip("SV");
            chartModel.setSeries(series);

            renderJSON(chartModel);
        }
    }

    //海拉尔-仪表实时温湿度
    public static void dataEneryMonitHle() {
        String type = params.get("testType");
        List<EnergyMonitHailar> List = EnergyMonitHailar.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(1);
        if (List != null && List.size() == 1) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitHailar o : List) {
                obj = new JsonObject();
                obj.addProperty("time", o.t_date + " " + o.t_time);
                obj.addProperty("dining_t", o.dining_temperature);
                obj.addProperty("dining_h", o.dining_humidity);
                obj.addProperty("parlour_t", o.parlour_temperature);
                obj.addProperty("parlour_h", o.parlour_humidity);
                obj.addProperty("bedroom_t", o.bedroom_temperature);
                obj.addProperty("bedroom_h", o.bedroom_humidity);
                obj.addProperty("hall_t", o.hall_temperature);
                obj.addProperty("hall_h", o.hall_humidity);
                obj.addProperty("washroom_t", o.washroom_temperature);
                obj.addProperty("washroom_h", o.washroom_humidity);
                obj.addProperty("kitchen_t", o.kitchen_temperature);
                obj.addProperty("kitchen_h", o.kitchen_humidity);
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }

    //海拉尔-仪表实时温湿度
    public static void dataEneryMonitQz() {
        String type = params.get("testType");
        List<EnergyMonitQuanzhou> List = EnergyMonitQuanzhou.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(1);
        if (List != null && List.size() == 1) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitQuanzhou o : List) {
                obj = new JsonObject();
                obj.addProperty("time", o.t_date + " " + o.t_time);
                if (type.equals("1")){
                    obj.addProperty("t1", "一层东卧");
                    obj.addProperty("lying_t1", o.onelyingeast_temperature);
                    obj.addProperty("lying_h1", o.onelyingeast_humidity);
                    obj.addProperty("t2", "一层厨房");
                    obj.addProperty("kitchen_t1", o.onekitchen_temperature);
                    obj.addProperty("kitchen_h1", o.onekitchen_humidity);
                    obj.addProperty("t3", "一层过道");
                    obj.addProperty("aisle_t1", o.oneaisle_temperature);
                    obj.addProperty("aisle_h1", o.oneaisle_humidity);
                    obj.addProperty("t4", "一层卫生间");
                    obj.addProperty("washroom_t1", o.onewashroom_temperature);
                    obj.addProperty("washroom_h1", o.onewashroom_humidity);
                }else {
                    obj.addProperty("t1", "二层东侧南卧");
                    obj.addProperty("lying_t1", o.twolyingeast_temperature);
                    obj.addProperty("lying_h1", o.twolyingeast_humidity);
                    obj.addProperty("t2", "二层阳台");
                    obj.addProperty("kitchen_t1", o.twobalcony_temperature);
                    obj.addProperty("kitchen_h1", o.twobalcony_humidity);
                    obj.addProperty("t3", "二层客厅");
                    obj.addProperty("aisle_t1", o.twoparlour_temperature);
                    obj.addProperty("aisle_h1", o.twoparlour_humidity);
                    obj.addProperty("t4", "二层屋顶");
                    obj.addProperty("washroom_t1", o.twosouthwailimian_temperature);
                    obj.addProperty("washroom_h1", o.twobalcony_humidity);
                }
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }
    
    //海拉尔-能耗
    public static void getkitchenHle() {
        String type = params.get("testType");
        List<EnergyMonitHailar> List = EnergyMonitHailar.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        if (List != null && List.size() == 2) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitHailar o : List) {
            	obj = new JsonObject();
                obj.addProperty("time", o.t_date + " " + o.t_time);
                if (type.equals("1")) {
                    obj.addProperty("title", "总电表");
                    obj.addProperty("unit", "KWH");
                    obj.addProperty("date", o.electric_quantity);
                } 
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }

    //席里村-能耗
    public static void getkitchenQz() {
        String type = params.get("testType");
        List<EnergyMonitQuanzhou> List = EnergyMonitQuanzhou.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        if (List != null && List.size() == 2) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitQuanzhou o : List) {
            	obj = new JsonObject();
                obj.addProperty("time", o.t_date + " " + o.t_time);
                if (type.equals("1")) {
                    obj.addProperty("title", "一层电表");
                    obj.addProperty("unit", "KWH");
                    obj.addProperty("date", o.oneelectric_quantity);
	            } else if (type.equals("3")) {
	                obj.addProperty("title", "二层电表");
	                obj.addProperty("unit", "KWH");
	                obj.addProperty("date", o.twoelectric_quantity);
                } else if (type.equals("4")) {
                    obj.addProperty("title", "总水表");
                    obj.addProperty("unit", "m³");
                    obj.addProperty("date", o.water_index);
                } 
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }
    
    //方顶村-能耗
    public static void getkitchenFdc() {
    	String type = params.get("testType");
        List<EnergyMonitHailar> List = EnergyMonitHailar.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        if (List != null && List.size() == 2) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitHailar o : List) {
            	obj = new JsonObject();
                obj.addProperty("time", o.t_date + " " + o.t_time);
                if (type.equals("1")) {
                    obj.addProperty("title", "厨房电表");
                    obj.addProperty("unit", "KWH");
                    obj.addProperty("date", o.electric_tension);
                } else if (type.equals("2")) {
                    obj.addProperty("title", "总电表");
                    obj.addProperty("unit", "KWH");
                    obj.addProperty("date", o.electric_quantity);
                } else if (type.equals("3")) {
                    obj.addProperty("title", "卫生间电表");
                    obj.addProperty("unit", "KWH");
                    obj.addProperty("date", o.roof_temperature);
                } else if (type.equals("4")) {
                    obj.addProperty("title", "总水表");
                    obj.addProperty("unit", "t/m³");
                    obj.addProperty("date", o.electric_quantity);
                } 
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }
     
    //庙上村-能耗
    public static void getkitchenMsc() {
    	String type = params.get("testType");
        List<EnergyMonitHailar> List = EnergyMonitHailar.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        if (List != null && List.size() == 2) {
            JsonArray arr = new JsonArray();
            JsonObject obj;
            for (EnergyMonitHailar o : List) {
            	obj = new JsonObject();
                obj.addProperty("time", o.t_date + " " + o.t_time);
                if (type.equals("1")) {
                    obj.addProperty("title", "厨房电表");
                    obj.addProperty("unit", "KWH");
                    obj.addProperty("date", o.electric_tension);
                } else if (type.equals("2")) {
                    obj.addProperty("title", "总电表");
                    obj.addProperty("unit", "KWH");
                    obj.addProperty("date", o.electric_quantity);
                } else if (type.equals("3")) {
                    obj.addProperty("title", "卫生间电表");
                    obj.addProperty("unit", "KWH");
                    obj.addProperty("date", o.roof_temperature);
                } else if (type.equals("4")) {
                    obj.addProperty("title", "总水表");
                    obj.addProperty("unit", "t/m³");
                    obj.addProperty("date", o.electric_quantity);
                } 
                arr.add(obj);
            }
            renderText(arr);
        }
        renderText(false);
    }
    
    //海拉尔-能耗 最近6小时
    public static void getkitchenHleData() throws ParseException
    {
    	String type = params.get("testType");
        String lastTime = params.get("lastTime");
        String sql = "from EnergyMonitHailar t where str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') between '"+TimeFormatUtil.hoursAgo(lastTime)+"' and '"+lastTime+"'";
        List<EnergyMonitHailar> List = EnergyMonitHailar.find(sql).fetch();
        Map<Object, ChartData[]> series=new HashMap<Object,ChartData[]>();
        int size = List.size();
        ChartData[] chartDatas=new ChartData[size];
        if (type.equals("1") && size > 0) {
        	for (int i = 0; i < size; i++) {
        		EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.electric_quantity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
         }
            series.put("总电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("总电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } 
    }

    //席里村-能耗 最近6小时
    public static void getkitchenQzData() throws ParseException
    {
    	String type = params.get("testType");
        String lastTime = params.get("lastTime");
        String sql = "from EnergyMonitQuanzhou t where str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') between '"+TimeFormatUtil.hoursAgo(lastTime)+"' and '"+lastTime+"'";
        List<EnergyMonitQuanzhou> List = EnergyMonitQuanzhou.find(sql).fetch();
        Map<Object, ChartData[]> series=new HashMap<Object,ChartData[]>();
        int size = List.size();
        ChartData[] chartDatas=new ChartData[size];
        if (type.equals("1") && size > 0) {
        	for (int i = 0; i < size; i++) {
        		EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.oneelectric_quantity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
         }
            series.put("一层电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("一层电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        }  else if (type.equals("3") && size > 0){
            for (int i = 0; i < size; i++) {
            	EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.twoelectric_quantity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
        }
            series.put("二层电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("二层电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } else if (type.equals("4") && size > 0){
            for (int i = 0; i < size; i++) {
            	EnergyMonitQuanzhou o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.water_index);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
           }
            series.put("总水表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("总水表");
            chartModel.setyAxis_title("Value (m³)");
            chartModel.setTooltip("m³");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } 
    }
    
    //方顶村-能耗 最近6小时
    public static void getkitchenFdcData() throws ParseException
    {
    	String type = params.get("testType");
        String lastTime = params.get("lastTime");
        String sql = "from EnergyMonitHailar t where str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') between '"+TimeFormatUtil.hoursAgo(lastTime)+"' and '"+lastTime+"'";
        List<EnergyMonitHailar> List = EnergyMonitHailar.find(sql).fetch();
        Map<Object, ChartData[]> series=new HashMap<Object,ChartData[]>();
        int size = List.size();
        ChartData[] chartDatas=new ChartData[size];
        if (type.equals("1") && size > 0) {
        	for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.electric_tension);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
         }
            series.put("厨房电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("厨房电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } else if (type.equals("2") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.electric_quantity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
           }
            series.put("总电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("总电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } else if (type.equals("3") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.roof_temperature);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
        }
            series.put("卫生间电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("卫生间电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } else if (type.equals("4") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.electric_quantity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
           }
            series.put("总水表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("总水表");
            chartModel.setyAxis_title("Value (t/m³)");
            chartModel.setTooltip("t/m³");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } 
    }

    //庙上村-能耗 最近6小时
    public static void getkitchenMscData() throws ParseException
    {
    	String type = params.get("testType");
        String lastTime = params.get("lastTime");
        String sql = "from EnergyMonitHailar t where str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') between '"+TimeFormatUtil.hoursAgo(lastTime)+"' and '"+lastTime+"'";
        List<EnergyMonitHailar> List = EnergyMonitHailar.find(sql).fetch();
        Map<Object, ChartData[]> series=new HashMap<Object,ChartData[]>();
        int size = List.size();
        ChartData[] chartDatas=new ChartData[size];
        if (type.equals("1") && size > 0) {
        	for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.electric_tension);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
         }
            series.put("厨房电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("厨房电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } else if (type.equals("2") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.electric_quantity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
           }
            series.put("总电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("总电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } else if (type.equals("3") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.roof_temperature);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
        }
            series.put("卫生间电表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("卫生间电表");
            chartModel.setyAxis_title("Value (KWH)");
            chartModel.setTooltip("KWH");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } else if (type.equals("4") && size > 0){
            for (int i = 0; i < size; i++) {
                EnergyMonitHailar o = List.get(i);
                ChartData chartData = new ChartData();
                chartData.setValue(o.electric_quantity);
                chartData.setTime(o.t_date + " " + o.t_time);
                chartDatas[i] = chartData;
           }
            series.put("总水表", chartDatas);
            ChartModel chartModel=new ChartModel();
            chartModel.setTitle("总水表");
            chartModel.setyAxis_title("Value (t/m³)");
            chartModel.setTooltip("t/m³");
            chartModel.setSeries(series);
            renderJSON(chartModel);
        } 
    }

    
}