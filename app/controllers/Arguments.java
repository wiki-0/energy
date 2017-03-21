package controllers;

import java.util.Date;
import java.util.List;

import models.Argument;
import models.User;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

@CRUD.For(Argument.class)
public class Arguments extends CRUD {

    public static void index() {
        render();
    }

    public static void create() {
        String name = params.get("name");
        String value = params.get("value");
        String description = params.get("description");
        Argument argument = new Argument();
        User user = User.find("username", Security.connected()).first();
        argument.user = user == null
                ? (User) (User.find("username", "admin").first()) : user;
        argument.date = new Date();
        argument.name = name;
        argument.value = value;
        argument.description = description;
        argument.save();
        redirect("systemmanage.setting");
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

}
