package com.troila.cyberrangeai.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description  
 * @Author  liyandan
 * @Date 2020-08-28 15:50:52 
 */

@Entity
@IdClass(User.class)
@Table ( name ="user" , schema = "cyberrangeai")
public class User  implements Serializable {

	private static final long serialVersionUID =  3600959642955214291L;

	@Id
   	@Column(name = "userid" )
	private String userid;

	@Id
   	@Column(name = "scenarioid" )
	private String scenarioid;

   	@Column(name = "mail" )
	private String mail;

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getScenarioid() {
		return this.scenarioid;
	}

	public void setScenarioid(String scenarioid) {
		this.scenarioid = scenarioid;
	}

	public String getMail() {
		return this.mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String toString() {
		return "{" +
					"userid='" + userid + '\'' +
					"scenarioid='" + scenarioid + '\'' +
					"mail='" + mail + '\'' +
				'}';
	}

}
