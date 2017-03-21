package models;

import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 数据库备份还原操作
 */
@Entity
@Table(name = "t_backup_restore")
public class DataHandleLog extends Model {

    public enum ProcessMode {
        backup, restore
    }

    public enum BackupMethod {
        auto, manual
    }
    
    public Date date;

    public ProcessMode process_mode;
    
    public String path;
    
    public BackupMethod backup_method;
}
