package models;
// Generated 2010-9-30 9:29:35 by Hibernate Tools 3.3.0.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Table;

import play.db.jpa.Model;

/**
 * PostsAttachments generated by hbm2java
 */
@Entity
@Table(name = "t_posts_attachments")
public class PostsAttachments extends Model {

	/**
	 * 
	 */
	private int postId;
	private String embedType;
	private int embedW;
	private int embedH;
	private String embedThumb;
	private String ifImageFilename;
	private String ifVideoSource;
	private String ifVideoHtml;
}
