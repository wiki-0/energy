package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import controllers.deadbolt.Deadbolt;
import models.DemonDisplay;
import models.User;
import play.Play;
import play.data.Upload;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.With;

public class Demonstration extends Controller {

    private static void init() {
    	init_building();
    	init_creating();
    	init_suitable();
    }
    
    private static void init_building(){
    	DemonDisplay building1 = new DemonDisplay();
    	building1.url = "/neimeng/space";
    	building1.clasify = "building";
    	building1.description = "内蒙--空间信息";
    	building1.province = "neimeng";
    	building1.content = "未找到数据";
    	building1.save();
    	
    	DemonDisplay building2 = new DemonDisplay();
    	building2.url = "/neimeng/house";
    	building2.clasify = "building";
    	building2.description = "内蒙--住宅信息";
    	building2.province = "neimeng";
    	building2.content = "未找到数据";
    	building2.save();
    	
    	DemonDisplay building3 = new DemonDisplay();
    	building3.url = "/fujian/house";
    	building3.clasify = "building";
    	building3.description = "福建--空间信息";
    	building3.province = "fujian";
    	building3.content = "未找到数据";
    	building3.save();
    	
    	DemonDisplay building4 = new DemonDisplay();
    	building4.url = "/fujian/space";
    	building4.clasify = "building";
    	building4.description = "福建--住宅信息";
    	building4.province = "fujian";
    	building4.content = "未找到数据";
    	building4.save();
    }
    private static void init_creating(){
    	DemonDisplay creating1 = new DemonDisplay();
    	creating1.url = "/neimeng/creating";
    	creating1.clasify = "creating";
    	creating1.description = "内蒙--营建过程";
    	creating1.province = "neimeng";
    	creating1.content = "未找到数据";
    	creating1.save();
    	
    	DemonDisplay creating2 = new DemonDisplay();
    	creating2.url = "/fujian/entire";
    	creating2.clasify = "creating";
    	creating2.description = "福建--BIM模型-建筑整体展示";
    	creating2.province = "fujian";
    	creating2.content = "未找到数据";
    	creating2.save();
    	
    	DemonDisplay creating3 = new DemonDisplay();
    	creating3.url = "/fujian/detail";
    	creating3.clasify = "creating";
    	creating3.description = "福建--BIM模型-建筑细节展示";
    	creating3.province = "fujian";
    	creating3.content = "未找到数据";
    	creating3.save();
    	
    	DemonDisplay creating4 = new DemonDisplay();
    	creating4.url = "/fujian/creating";
    	creating4.clasify = "creating";
    	creating4.description = "福建--营建过程";
    	creating4.province = "福建";
    	creating4.content = "未找到数据";
    	creating4.save();
    }
    private static void init_suitable(){
    	DemonDisplay suitable1 = new DemonDisplay();
    	suitable1.url = "/neimeng/climate";
    	suitable1.clasify = "suitable";
    	suitable1.description = "内蒙--建筑气候区域特征";
    	suitable1.province = "neimeng";
    	suitable1.content = "未找到数据";
    	suitable1.save();
    	
    	DemonDisplay suitable2 = new DemonDisplay();
    	suitable2.url = "/neimeng/suitable";
    	suitable2.clasify = "suitable";
    	suitable2.description = "内蒙--适宜技术";
    	suitable2.province = "neimeng";
    	suitable2.content = "未找到数据";
    	suitable2.save();
    	
    	
    	DemonDisplay suitable3 = new DemonDisplay();
    	suitable3.url = "/fujian/climate";
    	suitable3.clasify = "suitable";
    	suitable3.description = "福建--建筑气候区域特征";
    	suitable3.province = "fujian";
    	suitable3.content = "未找到数据";
    	suitable3.save();
    	
    	DemonDisplay suitable4 = new DemonDisplay();
    	suitable4.url = "/fujian/suitable";
    	suitable4.clasify = "suitable";
    	suitable4.description = "福建--适宜技术";
    	suitable4.province = "fujian";
    	suitable4.content = "未找到数据";
    	suitable4.save();
    }
    public static void building(){
    	if(DemonDisplay.findAll().isEmpty()){
    		init();
    	}
    	render("Demonstration/building.html");
    }
    public static void building_neimeng(){
    	render("Demonstration/building_neimeng.html");
    }
  
    public static void building_fujian(){
    	render("Demonstration/building_fujian.html");
    }

    public static void creating_neimeng(){
    	render("Demonstration/creating_neimeng.html");
    }
    public static void creating_fujian(){
    	render("Demonstration/creating_fujian.html");
    }
    public static void simulation(){
    	render("Demonstration/simulatioAnnalysis.html");
    }
    
    public static void suitable_neimeng(){
    	render("Demonstration/suitable_neimeng.html");
    }
    public static void suitable_fujian(){
    	render("Demonstration/suitable_fujian.html");
    }
    
    public static void save(){
    	String content = params.get("content");
    	String position = params.get("position");
    	DemonDisplay demon = DemonDisplay.findById(position);
    	
    	demon.content = content;
    	demon.save();
    	
    	show(position);
    }
    
    public static void show(String url){
    	User user = User.find("select t from User t where t.username =?",Security.connected()).first();
    	DemonDisplay demon = DemonDisplay.findById(url);
    	render("demonstration/show.html",user,demon);
    }
    
    public static void edit(){
    	String position = params.get("position");
    	DemonDisplay demon = DemonDisplay.findById(position);
    	render("demonstration/edit.html",demon);
    }

    //多图上传
    public static void upload(String abc) {
        List<Upload> uploadFiles = (List<Upload>) request.args.get("__UPLOADS");
        List<String> imgFiles = new ArrayList<String>();
        for(Upload uf: uploadFiles) {
            File f = uf.asFile();
            UUID uuid = UUID.randomUUID();
            String newName = uuid.toString();
            File storeFile = new File("./public/img/" + newName);
            Files.copy(f, storeFile);
            imgFiles.add("/public/img/" + newName);
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
            File storeFile = new File("./public/images/" + newName);
            Files.copy(f, storeFile);

            renderText("/public/images/" + newName);
        }
    }
    
    public static void fileupload(File attachment){  
        if(request.method.equalsIgnoreCase("GET")){  
            render();  
        }else{  
            Files.copy(attachment, Play.getFile("public/img/"+attachment.getName()));  
            renderText("public/img/"+attachment.getName());
        }  
    }  
}