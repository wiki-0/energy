package models;

import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 参数
 */
@Entity
@Table(name = "t_argument", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Argument extends Model {

	public String name;
	
	public String value;
	
	@ManyToOne
	public User user;
	
	public Date date;
	
	public String description;

}
