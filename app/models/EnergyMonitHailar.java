package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.jpa.GenericModel;
import play.db.jpa.Model;

/**
 * 能源监测表
 */
@Entity
@Table(name = "t_monit_hailar_indoor_1")
//@PersistenceUnit(name = "remote")
public class EnergyMonitHailar extends  GenericModel{

   @Id
   public double kitchen_temperature; //厨房温度
   @Id
   public double washroom_temperature; //卫生间温度
   @Id
   public double hall_temperature; //门厅温度
   @Id
   public double bedroom_temperature; //卧室温度
   @Id
   public double parlour_temperature;//客厅温度
   @Id
   public double dining_temperature;  //饭厅温度
   
   @Id
   public double ground_temperature;  //地面温度
   @Id
   public double east_wall_temperature; //东墙温度
   @Id
   public double west_wall_temperature; //西墙温度
   @Id
   public double south_wall_temperature; //南墙温度
   @Id
   public double north_wall_temperature; //北墙温度
   @Id
   public double roof_temperature; //屋顶温度
   @Id
   public double outdoor_temperature; //室外温度
   
   //湿度
   @Id
   public double kitchen_humidity; //厨房湿度
   @Id
   public double washroom_humidity; //卫生间湿度
   @Id
   public double hall_humidity; //门厅湿度
   @Id
   public double bedroom_humidity; //卧室湿度
   @Id
   public double parlour_humidity;//客厅湿度
   @Id
   public double dining_humidity;  //饭厅湿度
   @Id
   public double outdoor_humidity; //室外湿度

  
   
   @Id
   public double barometric; //大气压力
   @Id
   public double total_radiation; //总辐射
   @Id
   public double direct_radiation; //直接辐射
   @Id
   public double wind_speed; //风速
   
   //电能源
   @Id
   public double electric_quantity; //电量
   @Id
   public double electric_tension; //电压
   @Id
   public double electric_current; //电流
   @Id
   public double power; //功率
   
   //监测日期
   @Id
   public String t_date;
   
   //监测时间
   @Id
   public String t_time; 
   
}
