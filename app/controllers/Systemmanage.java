package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.EnergyMonitHailar;

@With(Secure.class)
public class Systemmanage extends Controller {

    public static void index() {
        render();
    }
    
    public static void users(){
    	render("Systemmanage/users.html");
    }
    
    public static void usersbbs(){
    	render("Systemmanage/usersbbs.html");
    }
    
    public static void setting(){
    	render("Systemmanage/setting.html");
    }
    
    public static void data(){
    	render("Systemmanage/data.html");
    }
}