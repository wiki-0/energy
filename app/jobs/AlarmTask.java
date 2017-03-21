package jobs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.*;
import play.jobs.Job;
import play.jobs.On;
import utils.EmailSender;
import utils.TimeFormatUtil;

/**
 * 故障告警 每10分钟运行一次取最新2条数据
 */
@On("0 */10 * * * ?")
public class AlarmTask extends Job {

    @Override
    public void doJob() throws Exception {
        Argument communicationThreshold = Argument.find("byName","communicationThreshold").first();
        Argument humidityThreshold = Argument.find("byName","humidityThreshold").first();
        Argument directRadiationThreshold = Argument.find("byName","directRadiationThreshold").first();
        Argument totalRadiationThreshold = Argument.find("byName","totalRadiationThreshold").first();
        int communication = 6;
        int humidity = 6;
        int direct = 50;
        int total = 50;
        if (communicationThreshold != null){
            communication = Integer.valueOf(communicationThreshold.value) * 6;
        }
        if (humidityThreshold != null){
            humidity = Integer.valueOf(humidityThreshold.value) * 6;
        }
        if (directRadiationThreshold != null){
            direct = Integer.valueOf(directRadiationThreshold.value);
        }
        if (totalRadiationThreshold != null){
            total = Integer.valueOf(totalRadiationThreshold.value);
        }
        //海拉尔
        List<EnergyMonitHailar> hailarList = EnergyMonitHailar.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        AlarmThreshold hailar = AlarmThreshold.find("byLocation", "hailar").first();
        //判断通信异常
        String time = hailarList.get(0).t_date + " " + hailarList.get(0).t_time;

        if (hailar.lastTime.equals(time)) {
            hailar.sameTime = hailar.sameTime + 1;
        } else {
            //更新最新时间和重置时间次数
            hailar.lastTime = time;
            hailar.sameTime = 0;

            //检查各仪器有无数值变化 -2 去掉了仅有的 String 类型的 t_date t_time 剩下是double类型 反射获得 值
            Field[] fields = hailarList.get(0).getClass().getDeclaredFields();
            for (int i = 0; i < fields.length - 2; i++) {
                String name = fields[i].getName();
                name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                Method method = hailarList.get(0).getClass().getMethod("get" + name);
                Double valueOne = (Double) method.invoke(hailarList.get(0));
                Double valueTwo = (Double) method.invoke(hailarList.get(1));
                if (valueOne != null && valueTwo != null) {
                    if (valueOne.equals(valueTwo)) {
                        if (hailar.humitureMap.containsKey(name)) {
                            hailar.humitureMap.put(name, hailar.humitureMap.get(name) + 1);
                        } else {
                            hailar.humitureMap.put(name, 1);
                        }
                    } else {
                        if (hailar.humitureMap.containsKey(name)) {
                            hailar.humitureMap.remove(name);
                        }
                    }
                }
            }
        }
        /*
            告警邮件处理
         */
        int divhl = hailar.sameTime / communication;
        int remhl = hailar.sameTime % communication;
        //通信异常 邮件 最多发3次 时间间隔 阈值communicationThreshold 分钟
        if (divhl >= 1 && remhl == 0 && divhl <= 3) {
            List<User> UserList = User.findAll();
            if (UserList != null && UserList.size() >0){
                for (User u : UserList) {
                    if (u.sendEmailWhenCommFailure){
                        String content = "告警：海拉尔表" + hailar.sameTime * 10 + "分钟未更新数据";
                        EmailSender.send(u.email,content,"海拉尔通信异常");
                        CommunicationAlarm alarm = new CommunicationAlarm();
                        alarm.content = content;
                        alarm.email = u.email;
                        alarm.username = u.username;
                        Date now = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                        alarm.emailTime = sdf.format(now);
                        alarm.save();
                    }
                }
            }
        }
        divhl = hailar.sameTime / humidity;
        remhl = hailar.sameTime % humidity;
        //温湿度异常
        Iterator ithl = hailar.humitureMap.keySet().iterator();
        while (ithl.hasNext()) {
            String key = (String) ithl.next();
            // 提醒3次 间隔阈值时间
            if (divhl >= 1 && remhl == 0 && divhl <= 3) {
                List<User> UserList = User.findAll();
                if (UserList != null && UserList.size() >0){
                    for (User u : UserList) {
                        if (u.sendEmailWhenDataAbnormal){
                            String content = "告警：海拉尔表中属性" + key + " 已经有" + hailar.humitureMap.get(key) * 10 + "分钟 未更新数据";
                            EmailSender.send(u.email,content,"海拉尔数据异常");
                            CommunicationAlarm alarm = new CommunicationAlarm();
                            alarm.content = content;
                            alarm.email = u.email;
                            alarm.username = u.username;
                            Date now = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                            alarm.emailTime = sdf.format(now);
                            alarm.save();
                        }
                    }
                }
            }
        }
        //检查 夜间20：00-05：00 辐射是否低于阈值
        if (TimeFormatUtil.isInDate(time, "20:00:00", "05:00:00")) {
            if (hailarList.get(0).total_radiation < total && hailar.emailTotal <=3){
                List<User> UserList = User.findAll();
                if (UserList != null && UserList.size() >0){
                    for (User u : UserList) {
                        if (u.sendEmailWhenDataAbnormal){
                            String content = "告警：海拉尔表中总辐射" + hailarList.get(0).total_radiation + " 低于阈值 " + total;
                            EmailSender.send(u.email,content,"海拉尔数据异常");
                            CommunicationAlarm alarm = new CommunicationAlarm();
                            alarm.content = content;
                            alarm.email = u.email;
                            alarm.username = u.username;
                            Date now = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                            alarm.emailTime = sdf.format(now);
                            alarm.save();
                        }
                    }
                }
                hailar.emailTotal = hailar.emailTotal +1;
            }
            if (hailarList.get(0).direct_radiation < direct && hailar.emailTotal <=3){
                List<User> UserList = User.findAll();
                if (UserList != null && UserList.size() >0){
                    for (User u : UserList) {
                        if (u.sendEmailWhenDataAbnormal){
                            String content = "告警：海拉尔表中直接辐射" + hailarList.get(0).direct_radiation + " 低于阈值 " + direct;
                            EmailSender.send(u.email,content,"海拉尔数据异常");
                            CommunicationAlarm alarm = new CommunicationAlarm();
                            alarm.content = content;
                            alarm.email = u.email;
                            alarm.username = u.username;
                            Date now = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                            alarm.emailTime = sdf.format(now);
                            alarm.save();
                        }
                    }
                }
                hailar.emailTotal = hailar.emailTotal +1;
            }
        }

        hailar.save();
        hailar.refresh();

        /*
            席里村
         */
        List<EnergyMonitQuanzhou> quanzhouList = EnergyMonitQuanzhou.find("order by str_to_date(concat(t_date,' ',t_Time),'%Y-%m-%d %H:%i:%s') desc").fetch(2);
        AlarmThreshold quanzhou = AlarmThreshold.find("byLocation", "quanzhou").first();
        //判断通信异常
        String timeqz = quanzhouList.get(0).t_date + " " + quanzhouList.get(0).t_time;
        if (quanzhou.lastTime.equals(timeqz)) {
            quanzhou.sameTime = quanzhou.sameTime + 1;
        } else {
            quanzhou.lastTime = timeqz;
            quanzhou.sameTime = 0;

            Field[] fields = quanzhouList.get(0).getClass().getDeclaredFields();
            for (int i = 0; i < fields.length - 2; i++) {
                String name = fields[i].getName();
                name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                Method method = quanzhouList.get(0).getClass().getMethod("get" + name);
                Double valueOne = (Double) method.invoke(quanzhouList.get(0));
                Double valueTwo = (Double) method.invoke(quanzhouList.get(1));
                if (valueOne != null && valueTwo != null) {
                    if (valueOne.equals(valueTwo)) {
                        if (quanzhou.humitureMap.containsKey(name)) {
                            quanzhou.humitureMap.put(name, quanzhou.humitureMap.get(name) + 1);
                        } else {
                            quanzhou.humitureMap.put(name, 1);
                        }
                    } else {
                        if (quanzhou.humitureMap.containsKey(name)) {
                            quanzhou.humitureMap.remove(name);
                        }
                    }
                }
            }
        }
        int divqz = quanzhou.sameTime / communication;
        int remqz = quanzhou.sameTime % communication;
        //通信异常 邮件 最多发3次 时间间隔 阈值communicationThreshold 分钟
        if (divqz >= 1 && remqz == 0 && divqz <= 3) {
            List<User> UserList = User.findAll();
            if (UserList != null && UserList.size() >0){
                for (User u : UserList) {
                    if (u.sendEmailWhenCommFailure){
                        String content = "告警：席里村表" + quanzhou.sameTime * 10 + "分钟未更新数据";
                        EmailSender.send(u.email,content,"席里村通信异常");
                        CommunicationAlarm alarm = new CommunicationAlarm();
                        alarm.content = content;
                        alarm.email = u.email;
                        alarm.username = u.username;
                        Date now = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                        alarm.emailTime = sdf.format(now);
                        alarm.save();
                    }
                }
            }
        }
        divqz = quanzhou.sameTime / humidity;
        remqz = quanzhou.sameTime % humidity;
        //温湿度异常
        Iterator itqz = quanzhou.humitureMap.keySet().iterator();
        while (itqz.hasNext()) {
            String key = (String) itqz.next();
            // 提醒3次 间隔阈值时间
            if (divhl >= 1 && remhl == 0 && divhl <= 3) {
                List<User> UserList = User.findAll();
                if (UserList != null && UserList.size() >0){
                    for (User u : UserList) {
                        if (u.sendEmailWhenDataAbnormal){
                            String content = "告警：席里村表中属性" + key + " 已经有" + quanzhou.humitureMap.get(key) * 10 + "分钟 未更新数据";
                            EmailSender.send(u.email,content,"席里村数据异常");
                            CommunicationAlarm alarm = new CommunicationAlarm();
                            alarm.content = content;
                            alarm.email = u.email;
                            alarm.username = u.username;
                            Date now = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                            alarm.emailTime = sdf.format(now);
                            alarm.save();
                        }
                    }
                }
            }
        }

        //检查 夜间20：00-05：00 辐射是否低于阈值
        if (TimeFormatUtil.isInDate(time, "20:00:00", "05:00:00")) {
            if (quanzhouList.get(0).total_radiation < total && quanzhou.emailTotal <=3){
                List<User> UserList = User.findAll();
                if (UserList != null && UserList.size() >0){
                    for (User u : UserList) {
                        if (u.sendEmailWhenDataAbnormal){
                            String content = "告警：席里村表中总辐射" + quanzhouList.get(0).total_radiation + " 低于阈值 " + total;
                            EmailSender.send(u.email,content,"席里村数据异常");
                            CommunicationAlarm alarm = new CommunicationAlarm();
                            alarm.content = content;
                            alarm.email = u.email;
                            alarm.username = u.username;
                            Date now = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                            alarm.emailTime = sdf.format(now);
                            alarm.save();
                        }
                    }
                }
                quanzhou.emailTotal = quanzhou.emailTotal +1;
            }
            if (quanzhouList.get(0).direct_radiation < direct && quanzhou.emailTotal <=3){
                List<User> UserList = User.findAll();
                if (UserList != null && UserList.size() >0){
                    for (User u : UserList) {
                        if (u.sendEmailWhenDataAbnormal){
                            String content = "告警：席里村表中直接辐射" + quanzhouList.get(0).direct_radiation + " 低于阈值 " + direct;
                            EmailSender.send(u.email,content,"席里村数据异常");
                            CommunicationAlarm alarm = new CommunicationAlarm();
                            alarm.content = content;
                            alarm.email = u.email;
                            alarm.username = u.username;
                            Date now = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                            alarm.emailTime = sdf.format(now);
                            alarm.save();
                        }
                    }
                }
                quanzhou.emailTotal = quanzhou.emailTotal +1;
            }
        }
        quanzhou.save();
        quanzhou.refresh();

        //释放
        communicationThreshold.refresh();
        humidityThreshold.refresh();
        directRadiationThreshold.refresh();
        totalRadiationThreshold.refresh();
    }

}