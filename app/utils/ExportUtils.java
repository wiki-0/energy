package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import org.jxls.template.SimpleExporter;

import models.EnergyMonitHailar;
import models.EnergyMonitQuanzhou;
import play.mvc.Http.Response;

public class ExportUtils {
    public static void exportExcels(String start, String end, String location, String arguments,
            String type, Response response) {
        String str1 = "";
        String str2 = "";
        if (location.equals("dining")) {
            str1 = "饭厅";
        } else if (location.equals("parlour")) {
            str1 = "客厅";
        } else if (location.equals("bedroom")) {
            str1 = "卧室";
        } else if (location.equals("hall")) {
            str1 = "门斗";
        } else if (location.equals("washroom")) {
            str1 = "卫生间";
        } else if (location.equals("kitchen")) {
            str1 = "厨房";
        } else if (location.equals("onekitchen")) {
            str1 = "一楼厨房";
        } else if (location.equals("onewashroom")) {
            str1 = "一楼卫生间";
        } else if (location.equals("onelyingeast")) {
            str1 = "一楼东卧";
        } else if (location.equals("oneaisle")) {
            str1 = "一楼过道";
        } else if (location.equals("twoparlour")) {
            str1 = "二楼客厅";
        } else if (location.equals("twolyingeast")) {
            str1 = "二楼东卧";
        } else if (location.equals("twobalcony")) {
            str1 = "二楼阳台";
        } else if (location.equals("twoparlour")) {
            str1 = "二楼客厅";
        } else if (location.equals("twoeastwall") || location.equals("east_wall")) {
            str1 = "东墙";
        } else if (location.equals("twowesternwall") || location.equals("west_wall")) {
            str1 = "西墙";
        } else if (location.equals("twosouthwall") || location.equals("south_wall")) {
            str1 = "南墙";
        } else if (location.equals("twonorthwall") || location.equals("north_wall")) {
            str1 = "北墙";
        } else if (location.equals("ground")) {
            str1 = "地面";
        } else if (location.equals("roof")) {
            str1 = "屋顶";
        } else if (location.equals("twofloor")) {
            str1 = "二楼地面";
        } else if (location.equals("twosouthwailimian")) {
            str1 = "二楼屋顶";
        } else if (location.equals("oneelectric")) {
            str1 = "一楼电能耗";
        } else if (location.equals("twoelectric")) {
            str1 = "二楼电能耗";
        } else if (location.equals("spec")) {
            str1 = "室外参数";
        } else if (location.equals("electric")) {
            str1 = "电能耗";
        } else if (location.equals("water_index")) {
            str1 = "用水量";
        }
        if (arguments.equals("temperature")) {
            str2 = "空气温度";
        } else if (arguments.equals("humidity")) {
            str2 = "相对湿度";
        } else if (arguments.equals("windspeed")) {
            str2 = "风速";
        } else if (arguments.equals("wind_speed")) {
            str2 = "风速";
        } else if (arguments.equals("quantity")) {
            str2 = "电量";
        } else if (arguments.equals("tension")) {
            str2 = "电压";
        } else if (arguments.equals("current")) {
            str2 = "电流";
        } else if (arguments.equals("power")) {
            str2 = "功率";
        } else if (arguments.equals("barometric")) {
            str2 = "大气压力";
        } else if (arguments.equals("total_radiation")) {
            str2 = "总辐射";
        } else if (arguments.equals("direct_radiation")) {
            str2 = "直接辐射";
        } else if (arguments.equals("water_index")) {
            str2 = "用水量";
        }
        List<String> headers = Arrays.asList("Date", "Time", "Value");
        OutputStream os = response.out;// 取得输出流
        response.setContentTypeIfNotSet("application/ms-excel;charset=UTF-8");
        List list = null;
        if (type.equals("hailaer")) {
            list = EnergyMonitHailar.find("date(t_date) between date(?1) and date(?2)", start, end)
                    .fetch();
        } else if (type.equals("quanzhou")) {
            list = EnergyMonitQuanzhou
                    .find("date(t_date) between date(?1) and date(?2)", start, end).fetch();
        }
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=".concat(String.valueOf(
                            URLEncoder.encode((type.equals("hailaer") ? "淘海牧场" : "席里村") + "_" + str1
                                    + "_" + str2 + "_" + start + "--" + end + ".xls", "UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String objectProps = "";
        if (location.equals("spec") || location.equals("water_index")
                || (location.equals("electric") && arguments.equals("power"))) {
            objectProps = "t_date, t_time, " + arguments;
        } else {
            objectProps = "t_date, t_time, " + location + "_" + arguments;
        }
        SimpleExporter exporter = new SimpleExporter();
        exporter.gridExport(headers, list, objectProps, os);
    }
}
