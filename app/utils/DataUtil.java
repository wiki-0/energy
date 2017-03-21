package utils;

import java.io.IOException;
import java.nio.file.Path;

import play.Logger;

public class DataUtil {

    public static Boolean backupData(String dbUser, String dbPass, String dbName, String savePath) {
        String[] execCMD = new String[] {"mysqldump", "-u" + dbUser, "-p" + dbPass, dbName,
                "t_monit_hailar_indoor", "t_monit_quanzhou_indoor", "-r" + savePath};
        Process process;
        try {
            process = Runtime.getRuntime().exec(execCMD);
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                Logger.info("备份成功.");
            } else {
                Logger.info("备份数据库失败.");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Boolean restoreData(String dbUser, String dbPass, String dbName,
            String targetFile) {
        String[] execCMD = new String[] {"mysql", "-u" + dbUser, "-p" + dbPass, dbName, "-e",
                " source " + targetFile};
        Process process;
        try {
            process = Runtime.getRuntime().exec(execCMD);
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                Logger.info("还原成功.");
            } else {
                Logger.info("还原数据库失败.");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
