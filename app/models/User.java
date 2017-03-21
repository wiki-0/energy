package models;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;
import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;
import models.Topic;

/**
 * 用户
 */
@Entity
@Table(name = "t_user", uniqueConstraints = @UniqueConstraint(columnNames = "username") )
public class User extends Model implements RoleHolder {

    @Required
    public String username;

    public String nickname;

    public String logo;

    @Password
    public String password;

    @Email
    public String email;

    @Required
    public String permission;

    public Boolean isActived = false;

    //通信异常发送邮件
    public Boolean sendEmailWhenCommFailure = false;

    //数据异常发送邮件
    public Boolean sendEmailWhenDataAbnormal = false;
    
    public String validateCode;// 激活码

    public Date registerTime;// 注册时间

    // 用户维护文章 可以删除用户关联所有文章
    @OneToMany(mappedBy = "user", targetEntity = Topic.class, cascade = {CascadeType.ALL},
            orphanRemoval = true)
    public Set<Topic> topics;

    public static User connect(String username, String password) {
        return find("byUsernameAndPassword", username, password).first();
    }

    public static User getByName(String username) {
        return find("byUsername", username).first();
    }

    public List<? extends Role> getRoles() {
        return Arrays.asList(new MyRole(permission));
    }
}
