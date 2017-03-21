package controllers;

import models.User;
import utils.MD5Encrypt;

public class Security extends Secure.Security{
	/**
	 * @Title: authenticate
	 * @Description: 登陆验证
	*/
	static boolean authenticate(String username, String password) {
        User user = User.find("byUsername", username).first();
        return user != null && user.password.equals(MD5Encrypt.md5(password));
    }
	
	
}
