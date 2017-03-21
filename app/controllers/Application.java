package controllers;

import com.google.gson.JsonObject;

import models.*;
import play.*;
import play.mvc.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Application extends Controller {

    public static void index() {
    	User user = User.find("byUsername", Security.connected()).first();
        if (user != null){
            session.put("permission", user.permission);
        }
        render();
    }

    public static void baiduMap() {
        if (params.get("location")!=null && params.get("location").equals("msc")){
            //庙上村 先用泉州数据
            EnergyMonitQuanzhou quanzhou = EnergyMonitQuanzhou.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").first();
            if (quanzhou != null){
               JsonObject obj = new JsonObject();
               StringBuffer sb = new StringBuffer();
               sb.append("<div class='list-group'><a class='list-group-item active'>庙上村-室外环境实时数据</a><br>");
               sb.append("<p class='list-group-item-text'>采集时间 "+ quanzhou.t_date + " " + quanzhou.t_time + "</p>");
               sb.append("<p class='list-group-item-text'>室外温度 "+ quanzhou.outdoor_temperature + "℃</p>");
               sb.append("<p class='list-group-item-text'>室外湿度 "+ quanzhou.outdoor_humidity + "w/m²</p>");
               sb.append("<p class='list-group-item-text'>大气压力 "+ quanzhou.barometric + "hpa</p>");
               sb.append("<p class='list-group-item-text'>风速 "+ quanzhou.wind_speed + "m/s</p>");
               sb.append("<p class='list-group-item-text'>总辐射 "+ quanzhou.total_radiation + "w/m²</p>");
               sb.append("<p class='list-group-item-text'>直接辐射 "+ quanzhou.direct_radiation + "w/m²</p>");
               sb.append("<a href='/onlinemonit/outdoormsc'>进入详细页面</a></div>");
               obj.addProperty("content", sb.toString());
               renderJSON(obj);
           }else {
                JsonObject obj = new JsonObject();
                StringBuffer sb = new StringBuffer();
                sb.append("<div class='list-group'><a class='list-group-item active'>庙上村-室外环境实时数据</a><br>");
                sb.append("<p class='list-group-item-text'>无数据</p>");
                sb.append("<a href='/onlinemonit/outdoorhmsc'>进入详细页面</a></div>");
                obj.addProperty("content", sb.toString());
                renderJSON(obj);
            }
        }else {
            EnergyMonitHailar hailar = EnergyMonitHailar.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").first();
            if (hailar != null){
                JsonObject obj = new JsonObject();
                StringBuffer sb = new StringBuffer();
                sb.append("<div class='list-group'><a class='list-group-item active'>海拉尔-室外环境实时数据</a><br>");
                sb.append("<p class='list-group-item-text'>采集时间 "+ hailar.t_date + " " + hailar.t_time + "</p>");
                sb.append("<p class='list-group-item-text'>室外温度 "+ hailar.outdoor_temperature + "℃</p>");
                sb.append("<p class='list-group-item-text'>室外湿度 "+ hailar.outdoor_humidity + "w/m²</p>");
                sb.append("<p class='list-group-item-text'>大气压力 "+ hailar.barometric + "hpa</p>");
                sb.append("<p class='list-group-item-text'>风速 "+ hailar.wind_speed + "m/s</p>");
                sb.append("<p class='list-group-item-text'>总辐射 "+ hailar.total_radiation + "w/m²</p>");
                sb.append("<p class='list-group-item-text'>直接辐射 "+ hailar.direct_radiation + "w/m²</p>");
                sb.append("<a href='/onlinemonit/outdoorhle'>进入详细页面</a></div>");
                obj.addProperty("content", sb.toString());
                renderJSON(obj);
            }else {
                JsonObject obj = new JsonObject();
                StringBuffer sb = new StringBuffer();
                sb.append("<div class='list-group'><a class='list-group-item active'>海拉尔-室外环境实时数据</a><br>");
                sb.append("<p class='list-group-item-text'>无数据</p>");
                sb.append("<a href='/onlinemonit/outdoorhle'>进入详细页面</a></div>");
                obj.addProperty("content", sb.toString());
                renderJSON(obj);
            }
        }
    }
    public static void doGet()
    {
        Argument argument = Argument.find("byName","totalIP").first();
        if (argument == null){
            Argument argument1 = new Argument();
            argument1.date = new Date();
            argument1.description = "独立IP访问量统计值";
            argument1.name = "totalIP";
            argument1.value = "0";
            argument1.user = User.find("byUsername","admin").first();
            argument1.save();
            argument = argument1;
        }
        //分析IP访问统计
        if (request.remoteAddress!=null && !request.remoteAddress.equals("")){
            IndependentIP ip = IndependentIP.find("byIp",request.remoteAddress).first();
            if (ip == null){
                IndependentIP ip1 = new IndependentIP();
                ip1.ip = request.remoteAddress;
                ip1.querTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                ip1.save();
                int arg = Integer.valueOf(argument.value) + 1;
                argument.value = Integer.toString(arg);
                argument.save();
            }else {
                String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                if (ip.querTime.equals(nowTime)){
                    renderText(argument.value);
                }else {
                    ip.querTime = nowTime;
                    ip.save();
                    int arg = Integer.valueOf(argument.value) + 1;
                    argument.value = Integer.toString(arg);
                    argument.save();
                }
            }
            renderText(argument.value);
        }
    }

}