package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;
/**
 * 其他展示 模块
 * @author twx
 *
 */
@Entity
@Table(name="t_other_display")
public class OtherDisplay extends Model{
	
	public String url;//页面位置
	
	public String clasify;//分类： 建筑、营造、适宜技术
	public String province;//省份： 内蒙、福建
	
	/** 正文 */
	@Lob
	@Expose
	public String content;
	
	public String description;//描述
	
	public OtherDisplay(String url) {
		this.url = url;
		this.content = "请编辑内容...";
		if(url.equals("dropConsume")){
			this.description = "降耗模块";
		}else if(url.equals("produce")){
			this.description = "产能模块";
		}else if(url.equals("storage")){
			this.description = "蓄能模块";
		}
		
		
		/*else if(url.endsWith("Fence")){
			this.description = "围护结构";
		}else if(url.equals("functionSpace")){
			this.description = "功能空间";
		}else if(url.equals("sysCrafts")){
			this.description = "体系工艺";
		}else if(url.endsWith("Space")){
			this.description = "空间环境";
		}else if(url.endsWith("House")){
			this.description = "住宅信息";
		}else if(url.endsWith("Entire")){
			this.description = "BIM模型-建筑整体展示";
		}else if(url.endsWith("Detail")){
			this.description = "BIM模型-建筑细节展示";
		}else if(url.endsWith("Creating")){
			this.description = "营建过程";
		}else if(url.endsWith("Animation")){
			this.description = "特色动画";
		}else if(url.endsWith("Light")){
			this.description = "光环境";
		}else if(url.endsWith("Wind")){
			this.description = "风环境";
		}else if(url.endsWith("Heat")){
			this.description = "热环境";
		}*/
	}

	public OtherDisplay() {
		// TODO Auto-generated constructor stub
	}
	
}
