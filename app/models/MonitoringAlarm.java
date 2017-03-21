package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="t_monitoring_alarm")
public class MonitoringAlarm extends Model{

	@Required
	public String email;//邮箱

	public String username;//用户名

	public String emailTime;//邮件时间

	public String content;//告警内容

}
