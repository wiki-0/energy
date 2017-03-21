package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

/*
	版块
 */
@Entity
@Table(name = "t_section")
public class Section extends  Model {

	//是否显示，0不显示1显示
	public Boolean show_status;
	//版块排序
	public int display_index;
	//版块名字
	public String name;
	//标签
	public String tab;
}
