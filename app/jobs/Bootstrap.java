package jobs;


import models.AlarmThreshold;
import models.Argument;
import models.User;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.MD5Encrypt;

import java.util.Date;
import java.util.List;

/**
 * 程序初始化
 */
@OnApplicationStart
public class Bootstrap extends Job {


    public void doJob() {
        Logger.info("start to init the system table...");
        this.initSuperManagerData();
        this.initAlarmData();
        Logger.info("IStorM Cloud init successful...");
    }

    //初始化 管理员
    private void initSuperManagerData() {
        List<User> user = User.findAll();
        if (user.size() == 0) {
            User u1 = new User();
            u1.username = "admin";
            u1.logo = "/public/images/logo/man_head01.png";
            u1.password = MD5Encrypt.md5("123456");
            u1.email = "737352058@qq.com";
            u1.nickname = "管理员";
            u1.isActived = true;
            u1.permission = "admin_manager";
            User u2 = new User();
            u2.username = "bbs_admin";
            u2.logo = "/public/images/logo/man_head01.png";
            u2.password = MD5Encrypt.md5("123456");
            u2.email = "737352058@qq.com";
            u2.nickname = "BBS管理员";
            u2.isActived = true;
            u2.permission = "bbs_manager";
            try {
                u1.save();
                u2.save();
            } catch (Exception e) {
                Logger.info("Init the superUser error");
            }
        }
        Logger.info("Init the User table successful...");
    }

    //初始化 故障告警设置
    private void initAlarmData() {
        List<AlarmThreshold> list = AlarmThreshold.findAll();
        if (list == null || list.size() == 0) {
            AlarmThreshold hailer = new AlarmThreshold();
            hailer.location = "hailar";
            hailer.sameTime = 0; // 时间相同 次数
            hailer.lastTime = "2016-4-17 22:19:30";
            hailer.emailTotal = 0;
            hailer.save();

            AlarmThreshold qz = new AlarmThreshold();
            qz.location = "quanzhou";
            qz.sameTime = 0; // 时间相同 次数
            qz.lastTime = "2016-4-17 22:19:30";
            qz.emailTotal = 0;
            qz.save();

            Logger.info("Init the AlarmData successful...");
        }
        User user = User.find("byUsername","admin").first();
        List<Argument> argumentList = Argument.findAll();
        if (argumentList  == null || argumentList.size() == 0) {
            Argument com = new Argument();
            com.date = new Date();
            com.description = "通信异常阈值（单位：小时）";
            com.name = "communicationThreshold";
            com.value = "1";
            com.user = user;
            com.save();

            Argument hum = new Argument();
            hum.date = new Date();
            hum.description = "温湿度异常阈值（单位：小时）";
            hum.name = "humidityThreshold";
            hum.value = "1";
            hum.user = user;
            hum.save();

            Argument direct = new Argument();
            direct.date = new Date();
            direct.description = "直接辐射最低阈值";
            direct.name = "directRadiationThreshold";
            direct.value = "50";
            direct.user = user;
            direct.save();

            Argument total = new Argument();
            total.date = new Date();
            total.description = "总辐射最低阈值";
            total.name = "totalRadiationThreshold";
            total.value = "50";
            total.user = user;
            total.save();
        }
    }
}
