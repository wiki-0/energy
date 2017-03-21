package models;

import com.google.gson.annotations.Expose;
import play.db.jpa.Model;
import models.User;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
	帖子
 */
@Entity
@Table(name = "t_topic")
public class Topic extends  Model {

	//版块id
	public int s_id;
	//浏览量
	public int view;
	//置顶
	public Boolean top;
	// 加精
    public Boolean isFeatured;
	//是否显示 默认为0 不显示审核后 1 显示
	public int show_status;
	//回复数量也是楼层
	public int reply_count;
	//标题
	public String title;
	//录入时间
	public Date in_time;
	//修改时间
	public Date modify_time;
	//最后回复话题时间，用于排序
	public Date last_reply_time;
	//最后回复话题的用户id
	public String last_reply_author_id;
	/** 论坛表外键 **/
	@ManyToOne
	@JoinColumn(name="user_id")
	public User user;
	/** 正文 */
	@Lob
	@Expose
	public String content;

	public static StringBuffer formatDateToZH(Date date) {
		Date now = new Date();
		long l = now.getTime() - date.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		StringBuffer sb = new StringBuffer();
		sb.append("发表于:");
		if (day == 0 && hour == 0 && min ==0 ){
			sb.append("刚刚");
		}else if (day == 0 && hour == 0 && min <=10){
			sb.append("片刻之前");
		}else if (day == 0 && hour == 0 && min >=0){
			sb.append(min+"分钟前");
		}else if (day == 0 && hour >= 0){
			sb.append(hour+"小时前");
		}else if (day == 1 && hour >= 0){
			sb.append("昨天");
		}else if (day >= 730){
			sb.append("很久以前");
		}else if (day >= 365){
			sb.append("1年以前");
		}else if (day >= 31){
			sb.append((int)day/30+"个月前");
		}else if (day >= 0){
			day++;
			sb.append(day+"天前");
		}
		return sb;
	}
	public static StringBuffer formatDate(Date date) {
		StringBuffer sb = new StringBuffer();
		if (date != null){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sb.append(df.format(date));
		}else {
			sb.append("无");
		}
		return sb;
	}
}
