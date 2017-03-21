package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

/**
 * 能源监测表
 */
@Entity
@Table(name = "t_monit_quanzhou_indoor")
public class EnergyMonitQuanzhou extends GenericModel {
    @Id
    public double onelyingeast_temperature;//一楼东卧温度
    @Id
    public double onekitchen_temperature;//一楼厨房温度
    @Id
    public double oneaisle_temperature;//一楼过道温度
    @Id
    public double onewashroom_temperature;//一楼卫生间温度
    @Id
    public double twolyingeast_temperature;//二层东侧南卧温度
    @Id
    public double twobalcony_temperature;//二层阳台温度
    @Id
    public double twoparlour_temperature;//二楼客厅温度
    @Id
    public double onelyingeast_humidity;//一层东卧湿度
    @Id
    public double onekitchen_humidity;//一楼厨房湿度
    @Id
    public double oneaisle_humidity;//一楼过道湿度
    @Id
    public double onewashroom_humidity;//一楼卫生间湿度
    @Id
    public double twolyingeast_humidity;//二楼东卧湿度
    @Id
    public double twobalcony_humidity;//二楼阳台湿度
    @Id
    public double twoparlour_humidity;//二楼客厅湿度
    @Id
    public double twowesternwall_temperature;//二楼西墙温度
    @Id
    public double twosouthwall_temperature;//二楼南墙温度
    @Id
    public double twosouthwailimian_temperature;//？二楼屋顶温度
    @Id
    public double twoeastwall_temperature;//二楼东墙温度
    @Id
    public double twofloor_temperature;//二楼地面温度
    @Id
    public double twonorthwall_temperature;//二楼北墙温度
    @Id
    public double oneelectric_quantity;//一楼电量
    @Id
    public double oneelectric_tension;//一楼电压
    @Id
    public double oneelectric_current;//一楼电流
    @Id
    public double power;//一楼功率
    @Id
    public double twoelectric_quantity;//二楼电量
    @Id
    public double twoelectric_tension;//二楼电压
    @Id
    public double twoelectric_current;//二楼电流
    @Id
    public double twopower;//二楼功率
    @Id
    public double outdoor_temperature;//室外温度
    @Id
    public double outdoor_humidity;//室外湿度
    @Id
    public double barometric;//气压
    @Id
    public double total_radiation;//总辐射
    @Id
    public double direct_radiation;//直接辐射
    @Id
    public double wind_speed;//风速
    @Id
    public double twoaisle_windspeed;//二楼过道风速
    @Id
    public double twolyingeast_windspeed;//二楼东卧风速
    @Id
    public double onelyingeast_windspeed;//一楼东卧风速
    @Id
    public double oneaisle_windspeed;//一楼过道风速
    @Id
    public double water_index;//水量
    // 监测日期
    @Id
    public String t_date;
    // 监测时间
    @Id
    public String t_time;

}
