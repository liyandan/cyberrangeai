package com.troila.cyberrangeai.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description  
 * @Author  liyandan
 * @Date 2020-08-28 11:46:34 
 */

@Entity
@IdClass(SurfingUrls.class)
@Table ( name ="surfing_urls" , schema = "cyberrangeai")
public class SurfingUrls  implements Serializable {

	private static final long serialVersionUID =  5444218533337722898L;

	@Id
   	@Column(name = "id" )
	private String id;

	@Id
   	@Column(name = "url" )
	private String url;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"url='" + url + '\'' +
				'}';
	}

}
