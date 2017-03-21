package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_communication_alarm")
public class CommunicationAlarm extends Model{

	@Required
	public String email;//邮箱

	public String username;//用户名

	public String emailTime;//邮件时间

	public String content;//告警内容

}
