package controllers;

import play.db.Model;
import play.exceptions.TemplateNotFoundException;

import java.util.List;

public class EnergyMonitQuanzhous extends CRUD {

    public static void index() {
        render();
    }

    public static void list(int page, String search, String searchFields, String orderBy, String order ) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        order = "DESC";
        //区别一层二层水电表
        String slist = params.get("type");
        if(slist!=null){
            session.put("slist", slist);
        }else {
            slist= session.get("slist");
        }
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order ,slist);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order ,slist);
        }
    }

}