package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.OtherDisplay;
import models.User;
import play.data.Upload;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.With;

public class Other extends Controller {

    public static void index() {
        render();
    }
    /**
     * 展示模块
     */
    public static void show(String url){
    	User user = User.find("byUsername",Security.connected()).first();
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(other == null){
    		other = new OtherDisplay(url);
    		other.save();
    	}
    	/*if(url.endsWith("Fence")){
    		
    	}*/
    	render("Other/show.html",user,other);
    }

	public static void three(){
		render("Other/three.html");
	}
    
    /**
     * 编辑模块
     */
    public static void edit(){
    	String url = params.get("url");
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	render("Other/edit.html",other);
    }
  
    /**
     * 保存功能
     */
    public static void save(){
    	String url = params.get("url");
    	String content = params.get("content");
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	other.content = content;
    	other.save();
    	if(url.endsWith("Fence")){
    		fence(url);
    	}else if(url.endsWith("FcSpace")){
    		functionSpace(url);
    	}else if(url.endsWith("Sys")){
    		sysCrafts(url);
    	}else if(url.endsWith("Space") || url.endsWith("House")){
    		building(url);
    	}else if(url.endsWith("Entire") || url.endsWith("Detail") || url.endsWith("Creating") || url.endsWith("Animation")){
    		display(url);
    	}else if(url.endsWith("Light") || url.endsWith("Wind") || url.endsWith("Heat")){
    		simulation(url);
    	}
    	else{
    		show(url);
    	}
    }
    
    /**
     * 多图上传
     * @param abc
     */
    public static void upload(String abc) {
        List<Upload> uploadFiles = (List<Upload>) request.args.get("__UPLOADS");
        List<String> imgFiles = new ArrayList<String>();
        for(Upload uf: uploadFiles) {
            File f = uf.asFile();
            UUID uuid = UUID.randomUUID();
            String newName = uuid.toString();
            File storeFile = new File("./public/img/other" + newName);
            Files.copy(f, storeFile);
            imgFiles.add("/public/img/other" + newName);
        }
        if(imgFiles.size() == 1) {
            renderText(imgFiles.get(0));
        } else {
            renderText(imgFiles.toString());
        }
    }
    /**
     * 围护结构
     */
    public static void fence(String url){
    	User user = User.find("byUsername",Security.connected()).first();
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(other == null){
    		other = new OtherDisplay(url);
    		other.description = "围护结构";
    		other.save();
    	}
    	render("Other/fence.html",user,other);
    }
    
    /**
     * 功能空间
     * @param url
     */
    public static void functionSpace(String url){
    	User user = User.find("byUsername",Security.connected()).first();
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(other == null){
    		other = new OtherDisplay(url);
    		other.description = "功能空间";
    		other.save();
    	}
    	render("Other/functionSpace.html",user,other);
    }
    /**
     * 体系工艺
     * @param url
     */
    public static void sysCrafts(String url){
    	User user = User.find("byUsername",Security.connected()).first();
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(other == null){
    		other = new OtherDisplay(url);
    		other.description = "体系工艺";
    		other.save();
    	}
    	render("Other/sysCrafts.html",user,other);
    }
    /**
     * 建筑信息
     */
    public static void building(String url){
    	User user = User.find("byUsername",Security.connected()).first();
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(url.equals("random")){
    		other = new OtherDisplay();
    		other.content = " ";
    	}
    	render("Other/building.html",user,other);
    }
    
    /**
     * 模型展示
     */
    public static void display(String url){
    	User user = User.find("byUsername",Security.connected()).first();
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(url.equals("random")){
    		other = new OtherDisplay();
    		other.content = " ";
    	}
    	render("Other/display.html",user,other);
    }
    
    /**
     * 模拟分析
     */
    public static void simulation(String url){
    	User user = User.find("byUsername",Security.connected()).first();
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(url.equals("random")){
    		other = new OtherDisplay();
    		other.content = " ";
    	}
    	render("Other/simulation.html",user,other);
    }
    
    /**
     * 空间环境
     */
    public static void loadSpace(){
    	String url = params.get("url");
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(other == null){
    		other = new OtherDisplay(url);
    		other.description = "空间环境";
    		other.save();
    	}
    	renderJSON(other);
    }
    /**
     * 住宅信息
     */
    public static void loadHouse(){
    	String url = params.get("url");
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(other == null){
    		other = new OtherDisplay(url);
    		other.description = "住宅信息";
    		other.save();
    	}
    	renderJSON(other);
    }
    
    /**
     * 模型展示 详细
     */
    public static void loadDisplay(){
    	String url = params.get("url");
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(other == null){
    		other = new OtherDisplay(url);
    		other.save();
    	}
    	renderJSON(other);
    }
    /**
     * 模拟分析 详细
     */
    public static void loadSimulation(){
    	String url = params.get("url");
    	OtherDisplay other = OtherDisplay.find("byUrl",url).first();
    	if(other == null){
    		other = new OtherDisplay(url);
    		other.save();
    	}
    	renderJSON(other);
    }
  

}