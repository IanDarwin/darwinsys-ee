package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A role that a Person can be in
 */
@Entity
@Table(name = "customerrole", schema = "public")
public class PersonRole implements java.io.Serializable {

	private static final long serialVersionUID = -4752893119223861882L;
	private int id;
	private String name;

	public PersonRole() {
	}

	public PersonRole(int id) {
		this.id = id;
	}
	public PersonRole(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name", length = 20)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
