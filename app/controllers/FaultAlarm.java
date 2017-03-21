package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.*;
import play.*;
import play.mvc.*;

import java.util.*;

import utils.DataTableSource;

@CRUD.For(User.class)
public class FaultAlarm extends CRUD {

    public static void monitorManage() {
        render("FaultAlarm/monitoringManage.html");
    }

    public static void monitorList() {
        render("FaultAlarm/monitoringAbnormal.html");
    }

    public static void communicationManage() {
        render("FaultAlarm/communicationManage.html");
    }

    public static void communicationList() {
        render("FaultAlarm/communicationAbnormal.html");
    }

    public static void getMonitoringUsers() {
        List<User> users = User.find("byPermissionLike", "admin%").fetch();
        JsonArray arr = new JsonArray();
        JsonObject obj = null;
        if (users != null && users.size() > 0) {
            for (User u : users) {
                obj = new JsonObject();
                obj.addProperty("id", u.id);
                obj.addProperty("email", u.email);
                obj.addProperty("username", u.username);
                obj.addProperty("permission", u.permission);
                if (u.sendEmailWhenDataAbnormal){
                    obj.addProperty("emailType", "<button class='btn btn-default btn-sm' onclick='setCancel("+u.id+")'>取消发送</button>");
                }else {
                    obj.addProperty("emailType", "<button class='btn btn-default btn-sm' onclick='setInsert("+u.id+")'>添加发送</button>");
                }
                arr.add(obj);
            }
        }
        renderJSON(new DataTableSource(request, arr));
    }

    public static void getMonitoringAlarm() {
        List<MonitoringAlarm> list = MonitoringAlarm.findAll();
        JsonArray arr = new JsonArray();
        JsonObject obj = null;
        if (list != null && list.size() > 0) {
            for (MonitoringAlarm c : list) {
                obj = new JsonObject();
                obj.addProperty("email", c.email);
                obj.addProperty("username", c.username);
                obj.addProperty("emailTime", c.emailTime);
                obj.addProperty("content", c.content);
                obj.addProperty("del", "<button class='btn btn-default btn-sm' onclick='deleteAlarm("+c.id+")'>删除</button>");
                arr.add(obj);
            }
        }
        renderJSON(new DataTableSource(request, arr));
    }

    public static void delMonitoring(Long id) {
        MonitoringAlarm monitoringAlarm = MonitoringAlarm.findById(id);
        if (monitoringAlarm != null){
            monitoringAlarm.delete();
        }
    }

    public static void getCommunicationUsers() {
        List<User> users = User.find("byPermissionLike", "admin%").fetch();
        JsonArray arr = new JsonArray();
        JsonObject obj = null;
        if (users != null && users.size() > 0) {
            for (User u : users) {
                obj = new JsonObject();
                obj.addProperty("id", u.id);
                obj.addProperty("email", u.email);
                obj.addProperty("username", u.username);
                obj.addProperty("permission", u.permission);
                if (u.sendEmailWhenCommFailure){
                    obj.addProperty("emailType", "<button class='btn btn-default btn-sm' onclick='setCancel("+u.id+")'>取消发送</button>");
                }else {
                    obj.addProperty("emailType", "<button class='btn btn-default btn-sm' onclick='setInsert("+u.id+")'>添加发送</button>");
                }
                arr.add(obj);
            }
        }
        renderJSON(new DataTableSource(request, arr));
    }

    public static void getCommunicationAlarm() {
        List<CommunicationAlarm> list = CommunicationAlarm.findAll();
        JsonArray arr = new JsonArray();
        JsonObject obj = null;
        if (list != null && list.size() > 0) {
            for (CommunicationAlarm c : list) {
                obj = new JsonObject();
                obj.addProperty("email", c.email);
                obj.addProperty("username", c.username);
                obj.addProperty("emailTime", c.emailTime);
                obj.addProperty("content", c.content);
                obj.addProperty("del", "<button class='btn btn-default btn-sm' onclick='deleteAlarm("+c.id+")'>删除</button>");
                arr.add(obj);
            }
        }
        renderJSON(new DataTableSource(request, arr));
    }

    public static void delCommunication(Long id) {
        CommunicationAlarm communicationAlarm = CommunicationAlarm.findById(id);
        if (communicationAlarm != null){
           communicationAlarm.delete();
        }
    }

    //取消邮件发送
    public static void setCancel(Long id, String type) {
        User user = User.findById(id);
        if (type.equals("com")) {
            user.sendEmailWhenCommFailure = false;
        } else if (type.equals("data")) {
            user.sendEmailWhenDataAbnormal = false;
        }
        user.save();
    }

    //添加邮件发送
    public static void setInsert(Long id, String type) {
        User user = User.findById(id);
        if (type.equals("com")) {
            user.sendEmailWhenCommFailure = true;
        } else if (type.equals("data")) {
            user.sendEmailWhenDataAbnormal = true;
        }
        user.save();
    }
}
