package model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * This represents one contributor to the database.
 * @author Ian Darwin, http://darwinsys.com/
 */
@Entity
public class Person {
	long id;
    String firstName;
    String lastName;
    String email;
	private String address1;
	private String address2;
	private String city;
	private String province;
	private String postCode;
	private String country;
	private String cellPhone;
	private String homePhone;
	private String busPhone;
	private Date creationDate, lastLogin;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Column(name = "address1", nullable = false)
	@NotNull
	public String getAddress1() {
		return this.address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	@Column(name = "address2")
	public String getAddress2() {
		return this.address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	@Column(name = "city", nullable = false, length = 20)
	@NotNull
	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@Column(name = "province", length = 20)
	public String getProvince() {
		return this.province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	@Column(name = "post_code", length = 15)
	public String getPostCode() {
		return this.postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	@Column(name = "country", nullable = false, length = 15)
	@NotNull
	public String getCountry() {
		return this.country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	@Temporal(TemporalType.DATE)
	@Column(name = "creation_date", length = 13)
	public Date getCreationDate() {
		return this.creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	@Column(name = "cell_phone")
	public String getCellPhone() {
		return this.cellPhone;
	}
	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}
	@Column(name = "home_phone", length = 30)
	public String getHomePhone() {
		return this.homePhone;
	}
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}
	@Column(name = "bus_phone", length = 30)
	public String getBusPhone() {
		return this.busPhone;
	}
	public void setBusPhone(String busPhone) {
		this.busPhone = busPhone;
	}
}
