package models;

import org.hibernate.annotations.AccessType;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Lob;
import java.util.HashMap;

@Entity
@Table(name = "t_alarm_threshold")
@AccessType("field")
public class AlarmThreshold extends Model {

    @Required
    public String location;//位置

    public String lastTime;//最新时间

    public int sameTime; //时间相同

    public int emailTotal; //邮件次数

    @Lob
    public HashMap<String, Integer> humitureMap = new HashMap<String, Integer>();//温湿度 相同计数
}
