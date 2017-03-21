package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import controllers.CRUD.ObjectType;
import models.DataHandleLog;
import models.DataHandleLog.BackupMethod;
import models.DataHandleLog.ProcessMode;
import play.Play;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import utils.DataUtil;

@CRUD.For(DataHandleLog.class)
public class DataHandle extends CRUD {

    public static void index() {
        render();
    }

    public static void list(int page, String search, String searchFields, String orderBy,
            String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        order = "DESC";
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order,
                (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
    
    public static void delete(String id) throws Exception {
        DataHandleLog dataHandleLog = DataHandleLog.findById(new Long(id));
        String path = dataHandleLog.path;
        try {
            dataHandleLog._delete();
            Files.deleteIfExists(Paths.get(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void backup() {
        Path path = Paths.get(Play.applicationPath.getPath() + "/backup");
        // if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                // fail to create directory
                e.printStackTrace();
            }
        }
        Date date = new Date();
        String savePath = path + "\\backup_" + date.getTime() + ".sql";
        String user = Play.configuration.getProperty("db.default.user");
        String pass = Play.configuration.getProperty("db.default.pass");
        String url = Play.configuration.getProperty("db.default.url");
        String db_name;
        if (url.contains("?")) {
            db_name = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        } else {
            db_name = url.substring(url.lastIndexOf("/") + 1);
        }
        if(DataUtil.backupData(user, pass, db_name, savePath)){
            DataHandleLog dataHandleLog = new DataHandleLog();
            dataHandleLog.date = date;
            dataHandleLog.backup_method = BackupMethod.manual;
            dataHandleLog.process_mode = ProcessMode.backup;
            dataHandleLog.path = savePath;
            dataHandleLog.save();
        }
    }
    
    public static void restore() {
        Long id = new Long(params.get("id"));
        DataHandleLog dataHandleLog = DataHandleLog.findById(id);
        String user = Play.configuration.getProperty("db.default.user");
        String pass = Play.configuration.getProperty("db.default.pass");
        String url = Play.configuration.getProperty("db.default.url");
        String db_name;
        if (url.contains("?")) {
            db_name = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        } else {
            db_name = url.substring(url.lastIndexOf("/") + 1);
        }
        String targerFile = dataHandleLog.path;
        if(DataUtil.restoreData(user, pass, db_name, targerFile)){
            DataHandleLog newDataHandleLog = new DataHandleLog();
            newDataHandleLog.date = new Date();
            newDataHandleLog.process_mode = ProcessMode.restore;
            newDataHandleLog.save();
        }
    }
}
