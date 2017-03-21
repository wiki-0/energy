package controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.hibernate.service.spi.ServiceException;

import models.User;
import play.mvc.Controller;
import utils.EmailSender;
import utils.MD5Encrypt;

public class Register extends Controller{
	
	public static void index(){
		render();
	}
	public static void success(){
		String email = params.get("email");
		render("register/success.html",email);
	}
	
	public static  void reg(){
		String userName = params.get("reg_username");
		String nickName = params.get("reg_nickname");
		String email = params.get("reg_email");
		String password = params.get("reg_password");
		String logo = params.get("reg_logo");
		
		if(isBlank(nickName) || isBlank(password) || isBlank(userName)){
			error(500, "请完善注册信息");
		}else{
			User user1 = User.find("byUsername", userName).first();
			User user2 = User.find("byNickname", nickName).first();
			User user3 = User.find("byEmail", email).first();
			if(user2!=null){
				error("昵称已经被注册，请更换其他昵称");
			}else if(user1 != null){
				error("用户名已经被注册，请更换其他用户名");
			}else if(user3 !=null){
				error("邮箱已经被注册，请更换其他邮箱");
			}
			else{
				User newUser = new User();
				newUser.username = userName;
				newUser.nickname = nickName;
				newUser.email = email;
				newUser.password = MD5Encrypt.md5(password);
				newUser.permission = "bbs_member";
				newUser.logo = logo;
				
				newUser.isActived = false;
				newUser.validateCode = MD5Encrypt.encode2hex(email);
			    newUser.registerTime = new Date();
				newUser.save();
				
				renderText(email);;
			}
		}
	}
	
	public static void sendEmail() throws UnknownHostException{
		String email = params.get("email");
		User user = User.find("byEmail", email).first();
		
		InetAddress addr = InetAddress.getLocalHost();//获取本机ip
		String ip = addr.getHostAddress();
//		System.out.println(ip);
		StringBuffer sb = new StringBuffer("点击下面链接激活账号，48小时生效，否则重新注册账号，链接只能使用一次，请尽快激活！</br>");
		sb.append("<a href=\"http://"+ip+":8888/register/activate?email=")
			.append(email)
			.append("&valiCode=")
			.append(MD5Encrypt.encode2hex(email))
			.append("\">http://"+ip+":8888/register/activate?email=")
			.append(email)
			.append("&valiCode=")
			.append(MD5Encrypt.encode2hex(email))
			.append("</a>");
//		System.out.println(sb.toString());
		EmailSender.send(email, sb.toString(),"<能源自维持>注册邮箱激活");
	}
	/**
	 * 处理激活
	 */
	public static void activate(String email,String valiCode){
		User user = User.find("select t from User t where email= ?", email).first();
		//验证用户是否存在  
        if(user != null) {  
            //验证用户状态  
            if(user.isActived == false) {
            	Date currentTime = new Date();
            	//验证链接是否过期  
                if(currentTime.after(user.registerTime)){
                	//验证激活码是否正确  
                    if(valiCode.equals(user.validateCode)) {
                    	//激活成功
                    	user.isActived = true;
                    	user.save();
                    	render();
                    }else{
                    	throw new ServiceException("激活码不正确");
                    }
                }else{
                	throw new ServiceException("激活码已过期");
                }
            }else{
            	throw new ServiceException("邮箱已激活，请登入");
            }
        }else{
        	throw new ServiceException("该邮箱未注册(邮箱地址不存在)");
        }
	}
	
	/**
	 * 字符串为 null 或者为  "" 时返回 true
	 */
	private static boolean isBlank(String str) {
		return str == null || "".equals(str.trim());
	}
	
	/* public static void main(String[] args) throws Exception {
	        InetAddress addr = InetAddress.getLocalHost(); 
	        System.out.println(addr.getHostAddress());
	    }*/
}
