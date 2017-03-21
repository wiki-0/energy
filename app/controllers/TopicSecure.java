package controllers;

import controllers.deadbolt.Deadbolt;
import models.Reply;
import models.Section;
import models.Topic;
import models.User;
import play.data.Upload;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.With;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.service.BootstrapServiceRegistryBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @Title: 受保护的控制器
 * @Description: 权限管理
 */
@With(Deadbolt.class)
public class TopicSecure extends Controller {
	//后台移动板块
	public static void moveSection(){
		Topic topic = Topic.findById(new Long(params.get("id")));
		topic.s_id = new Integer(params.get("sid"));
		topic.save();
        List<Topic> topics = Topic.findAll();
        List<Section> sections = Section.findAll();
        render("Topic/topic.html",topics,sections);
	}
	//后台置顶帖子
	public static void stickTopic(){
		Topic topic = Topic.findById(new Long(params.get("id")));
		topic.top = new Boolean(params.get("stick"));
		topic.save();
		List<Topic> topics = Topic.findAll();
        List<Section> sections = Section.findAll();
        render("Topic/topic.html",topics,sections);
	}
    //后台文章管理
    public static void topic() {
        List<Topic> topics = Topic.findAll();
        List<Section> sections = Section.findAll();
        render("Topic/topic.html",topics,sections);
    }
    //后台审核文章
    public static void showStatus() {
        Topic top = Topic.findById(new Long(params.get("id")));
        top.show_status = new Integer(params.get("show"));
        top.top = new Boolean(params.get("stick"));
        top.save();
        List<Topic> topics = Topic.findAll();
        List<Section> sections = Section.findAll();
        render("Topic/topic.html",topics,sections);
    }
    //后台删除帖子
    public static void adminDelete() {
        String topic_id = params.get("topic_id");
        Topic topic = Topic.findById(new Long(topic_id));
        topic.delete();
        List<Topic> topics = Topic.findAll();
        List<Section> sections = Section.findAll();
        render("Topic/topic.html",topics,sections);
    }
    //创建帖子
    public static void create() {
        List<Section> sections = Section.findAll();
        if (sections.size() == 0 ){
            Section s = new Section();
            s.name = "最新";
            s.tab = "news";
            s.show_status = false;
            s.display_index = 1;
            s.save();
            sections = Section.findAll();
            render("Topic/create.html",sections);
        }
        render("Topic/create.html",sections);
    }
    //保存帖子
    public static void save(Topic topic) {
        String user = Security.connected();
        String sid = params.get("sid");
        String title = params.get("title");
        String content = params.get("content");
        Date date = new Date();
        topic.title = title;
        User u = User.find("byUsername",user).first();
        topic.user = u;
        topic.s_id = new Integer(sid);
        topic.content = content;
        topic.in_time = date;
        topic.last_reply_time = date;
        topic.view = 0;
        topic.top = false;
        topic.isFeatured = false;
        topic.show_status = 0;
        topic.save();
        show(topic.id);
    }
    //保存编辑帖子
    public static void editSave() {
        String topic_id = params.get("sid");
        String content = params.get("content");
        Date date = new Date();
        Topic topic = Topic.findById(new Long(topic_id));
        topic.modify_time = date;
        topic.content = content;
        topic.save();
        show(topic.id);
    }
    //发帖人删除自己的帖子
    public static void delete() {
        String topic_id = params.get("topic_id");
        Topic topic = Topic.findById(new Long(topic_id));
        topic.delete();
        String tab = params.get("tab");
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
        List<Topic> topics = Topic.find("select t from Topic t where t.s_id=?",new Long(sec.id).intValue()).fetch();
        render("Topic/index.html",topics,sections,tab);
    }
    //发帖人编辑自己的帖子
    public static void edit() {
        String topic_id = params.get("topic_id");
        Topic topic = Topic.findById(new Long(topic_id));
        render("Topic/edit.html",topic);
    }
    //显示帖子及回复
    public static void show(Long id) {
        Topic topic = Topic.findById(id);
        if (topic != null) {
            User user = User.find("byUsername",Security.connected()).first();
            List<Reply> replies = Reply.find("select t from Reply t where t.tid=?",String.valueOf(id)).fetch();
            render("Topic/show.html",topic,replies,user);
        } else {
            renderText("您查询的话题不存在");
        }
        render("Topic/show.html",topic);
    }
    //回复处理
    public static void reply() {
        String tid = params.get("topic_id");
        String ru = params.get("reply_user");
        Date date = new Date();
        Topic topic = Topic.findById(new Long(tid));
        if (topic == null) {
            render("Topic/show.html");
        } else {
            // 回复数量+1
            topic.reply_count =  topic.reply_count + 1;
            Reply reply = new Reply();
            String content = params.get("content");
            reply.tid = tid;
            if (ru != null || "".equals(ru)){
                content = ru + content;
            }
            reply.floor = topic.reply_count;
            reply.content = content;
            reply.in_time = date;
            User user = User.find("byUsername",Security.connected()).first();
            reply.user = user;
            reply.save();
            //话题最后回复时间更新
//            topic.set("last_reply_time", date).set("last_reply_author_id", user.get("id")).update();
            topic.last_reply_time = date;
            topic.save();
            show(topic.id);
        }
        render("Topic/show.html");
    }
    //多图上传
    public static void upload(String abc) {
        List<Upload> uploadFiles = (List<Upload>) request.args.get("__UPLOADS");
        List<String> imgFiles = new ArrayList<String>();
        for(Upload uf: uploadFiles) {
            File f = uf.asFile();
            //生成唯一ID
            UUID uuid = UUID.randomUUID();
            String newName = uuid.toString();
            //不加后缀也可以自动识别
            File storeFile = new File("./public/pictures/" + newName);
            Files.copy(f, storeFile);
            imgFiles.add("/public/pictures/" + newName);
        }
        if(imgFiles.size() == 1) {
            renderText(imgFiles.get(0));
        } else {
            renderText(imgFiles.toString());
        }
    }
    //单个图片上传
    public static void pasteupload(String abc) {
        Upload uploadFile =(Upload) request.args.get("__UPLOADS");
        if(uploadFile != null) {
            File f = uploadFile.asFile();
            UUID uuid = UUID.randomUUID();
            String newName = uuid.toString();
            File storeFile = new File("./public/pictures/" + newName);
            Files.copy(f, storeFile);

            renderText("/public/pictures/" + newName);
        }
    }

    //设置加精
    public static void setIsFeatured(Long id) {
        Topic topic = Topic.findById(id);
        if (topic.isFeatured == true) {
            topic.isFeatured = false;
        } else {
            topic.isFeatured = true;
        }
        topic.save();
    }
    
    //文章分类
    public static void section() {
        List<Section> sections = Section.findAll();
        render("Topic/section.html",sections);
    }
    
    public static void newSection() {
        render("Topic/newSection.html");
    }
    
    public static void saveSection(String name,String tab, Integer display_index,Boolean show_status) {
        Section section = new Section();
        section.name = name;
        section.display_index = display_index;
        section.tab = tab;
        section.show_status = show_status;
        section.save();
        redirect("/TopicSecure/section");
    }
    
    public static void deleteSection(Long id) {
        Section section = Section.findById(id);
        section.delete();
    }
    
    public static void saveSectionShowStatus(Long id,Boolean show_status) {
        Section section = Section.findById(id);
        section.show_status = show_status;
        section.save();
    }

    public static void likeReply(Long id) {
        Reply reply = Reply.findById(id);
        User user =
                User.find("byUsername", Security.connected()).first();
        JsonObject jsonObject = new JsonObject();
        if (reply.like_users.contains(user)) {
            reply.like_users.remove(user);
            jsonObject.addProperty("status", "remove");
        } else {
            reply.like_users.add(user);
            jsonObject.addProperty("status", "add");
        }
        reply.save();
        jsonObject.addProperty("num", reply.like_users.size());
        renderJSON(jsonObject);
    }
}