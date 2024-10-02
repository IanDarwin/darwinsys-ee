package model;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

/**
 * This represents one contributor to the database.
 * @author Ian Darwin, http://darwinsys.com/
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="PType",
    discriminatorType=DiscriminatorType.CHAR)
@DiscriminatorValue(value="P")
public class Person {
	Long id;
    String firstName;
    String lastName;
    // Login data
    String email;
 	private String loginName;
 	private String passPhrase;
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
	private Set<PersonRole> roles;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@NotNull
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	@NotNull
	public String getPassPhrase() {
		return passPhrase;
	}
	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}
	@OneToMany
	public Set<PersonRole> getRoles() {
		return roles;
	}
	public void setRoles(Set<PersonRole> roles) {
		this.roles = roles;
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
	@Transient
	public String getName() {
		return getFirstName() + " " + getLastName();
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
