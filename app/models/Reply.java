package models;

import com.google.gson.annotations.Expose;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/*
 * 回复
 */
@Entity
@Table(name = "t_reply")
public class Reply extends Model {

    // 录入时间
    public Date in_time;
    // 赞
    public int best;
    // 是否删除0 默认 1删除
    public int isdelete;
    //楼层
    public int floor;
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
    // 回复文章id
    public String tid;
    /** 回复内容 */
    @Lob
    @Expose
    public String content;
    @ManyToMany
    @JoinTable(name = "t_reply_like_users")
    public Set<User> like_users;
}
