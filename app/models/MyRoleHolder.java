package models;

import java.util.Arrays;
import java.util.List;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;

public class MyRoleHolder implements RoleHolder{
	/*
		管理员 查看全部
		普通用户 除了在线监控+其他全部
		技术员 在线监控指定点+其他全部
	 */
	 public List<? extends Role> getRoles(){
		 return Arrays.asList(new MyRole("manager"),
                 new MyRole("member"),
                 new MyRole("operator"));
	 }

}
