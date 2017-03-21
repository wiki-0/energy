package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

@Entity
@Table(name="t_demon_display")
public class DemonDisplay extends GenericModel{
	@Id
	public String url;//页面位置
	
	public String clasify;//分类： 建筑、营造、适宜技术
	public String province;//省份： 内蒙、福建
	/** 正文 */
	@Lob
	@Expose
	public String content;
	
	public String description;//描述
	
}
