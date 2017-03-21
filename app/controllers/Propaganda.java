package controllers;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import controllers.deadbolt.Unrestricted;
import models.Reply;
import models.Section;
import play.data.Upload;
import play.libs.Files;
import play.mvc.*;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import models.Topic;
import models.User;

public class Propaganda extends Controller {

    public static void training() {
        render();
    }

    public static void bbs() {
        render();
    }

    public static void bbs_detail() {
        render();
    }

    //显示帖子及回复
    public static void show(Long id) {
        Topic topic = Topic.findById(id);
        String page = params.get("page");
        String limit = params.get("limit");
        if (page ==null ||new Integer(page)<1){
            page = "1";
        }
        if (limit ==null ||new Integer(limit)<1){
            limit = "10";
        }
        Integer p = new Integer(page) -1;
        if (topic != null) {
            User user = User.find("byUsername",Security.connected()).first();
            //赞的多优先显示
            List<Reply> replies = Reply.find("select t from Reply t where t.tid=? order by t.like_users.size desc,floor asc",String.valueOf(id)).from(p*new Integer(limit)).fetch(new Integer(limit));
            List<Reply> all = Reply.find("select t from Reply t where t.tid=?",String.valueOf(id)).fetch();
            Integer total = all.size();
            if (request.isNew) {
                topic.view += 1;
                topic.save();
            }
            render("Topic/show.html",topic,replies,user,total);
        } else {
            renderText("您查询的帖子不存在");
        }
        render("Topic/show.html",topic);
    }
    //主页显示
    public static void index() {
        String tab = params.get("tab");
        String page = params.get("page");
        String limit = params.get("limit");
        List<Section> sections = Section.findAll();
        if (sections.size() == 0 ){
            Section s = new Section();
            s.name = "最新";
            s.tab = "news";
            s.show_status = true;
            s.display_index = 1;
            s.save();
            sections = Section.findAll();
            List<Topic> topics = Topic.findAll();
            render("Topic/index.html",topics,sections);
        }
        if (tab == null || "".equals(tab))
        {
            tab = "news";
        }
        Section sec = Section.find("select t from Section t where t.tab=?",tab).first();
        if (page ==null ||new Integer(page)<1){
            page = "1";
        }
        if (limit ==null ||new Integer(limit)<1){
            limit = "5";
        }
        Integer p = new Integer(page) -1;
        List<Topic> topics = Topic.find("s_id is ?1 and show_status is ?2 order by top desc,in_time desc",new Long(sec.id).intValue(),1).from(p*new Integer(limit)).fetch(new Integer(limit));
        List<Topic> all = Topic.find("s_id is ?1 and show_status is ?2 order by in_time desc",new Long(sec.id).intValue(),1).fetch();
        //热门帖子 前10
        List<Topic> hotTS = Topic.find("s_id is ?1 and show_status is ?2 order by view desc, reply_count desc",new Long(sec.id).intValue(),1).fetch(10);
        //热门回复 前10
        List<Reply> hotRE = Reply.find("order by best desc").fetch(10);
        Integer total = all.size();
        if (Security.connected()!=null && !Security.connected().equals("")){
            User user = User.find("select t from User t where t.username =?",Security.connected()).first();
            List<Topic> mytopics = Topic.find("select t from Topic t where t.s_id=?1 and t.user.id=?2",new Long(sec.id).intValue(),user.id).fetch();
            render("Topic/index.html",topics,mytopics,sections,tab,total,hotTS,hotRE);
        }
        render("Topic/index.html",topics,sections,tab,total,hotTS,hotRE);
    }

}