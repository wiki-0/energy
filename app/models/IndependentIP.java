package models;

import play.db.jpa.Model;

import javax.persistence.*;

/*
 * 独立 IP 访问
 */
@Entity
@Table(name = "t_independent_ip")
public class IndependentIP extends Model {

    // 访问IP
    public String ip;

    // 访问时间
    public String querTime;

}
