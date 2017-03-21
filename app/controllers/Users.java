package controllers;

import java.util.Date;
import java.util.List;

import models.Argument;
import models.User;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import utils.MD5Encrypt;

@CRUD.For(User.class)
public class Users extends CRUD {

    public static void index() {
        render();
    }

    public static void create() {
        String username = params.get("username");
        String nickname = params.get("nickname");
        String email = params.get("email");
        String password = params.get("password");
        User user = new User();
        user.username = username;
        user.nickname = nickname;
        user.email = email;
        user.password = MD5Encrypt.md5(password);
        user.permission = "admin_operator";
        user.isActived = true;
        user.save();
        redirect("systemmanage.users");
    }

    public static void admin_user_list(int page, String search, String searchFields, String orderBy,
            String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        order = "DESC";
        request.args.put("where", " permission like 'admin_%'");
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
    
    public static void bbs_user_list(int page, String search, String searchFields, String orderBy,
            String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        order = "DESC";
        request.args.put("where", " permission like 'bbs_%'");
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
