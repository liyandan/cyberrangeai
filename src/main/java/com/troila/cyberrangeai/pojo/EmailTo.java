package com.troila.cyberrangeai.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description  
 * @Author  liyandan
 * @Date 2020-08-26 18:13:34 
 */

@Entity
@IdClass(EmailTo.class)
@Table ( name ="email_to" , schema = "cyberrangeai")
public class EmailTo  implements Serializable {

	private static final long serialVersionUID =  7193569215795110289L;

	@Id
   	@Column(name = "id" )
	private String id;

   	@Column(name = "fullname" )
	private String fullname;

	@Id
   	@Column(name = "email" )
	private String email;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"fullname='" + fullname + '\'' +
					"email='" + email + '\'' +
				'}';
	}

}
