package org.sheashepherd.ghostnet.model;

import jakarta.persistence.*;

@Entity
@Table(name = "account", uniqueConstraints = @UniqueConstraint(name = "uq_account_last_name", columnNames = "last_name"))
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	@Column(name = "company", length = 120)
	private String company;

	@Column(name = "phone", nullable = false, length = 30)
	private String phone;

	@Column(name = "salt", nullable = false, length = 255)
	private String salt;

	@Column(name = "pwd_hash", nullable = false, length = 255)
	private String passwordHash;

	// getters/setters …
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
}
