package com.troila.cyberrangeai.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description  
 * @Author  liyandan
 * @Date 2020-08-28 14:59:13 
 */

@Entity
@IdClass(Capibility.class)
@Table ( name ="capibility" , schema = "cyberrangeai")
public class Capibility  implements Serializable {

	private static final long serialVersionUID =  5886729892194274026L;

	@Id
   	@Column(name = "id" )
	private String id;

	@Id
   	@Column(name = "action" )
	private String action;

   	@Column(name = "weight" )
	private String weight;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getWeight() {
		return this.weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"action='" + action + '\'' +
					"weight='" + weight + '\'' +
				'}';
	}

}
