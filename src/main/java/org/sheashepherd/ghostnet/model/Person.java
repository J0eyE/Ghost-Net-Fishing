package org.sheashepherd.ghostnet.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "person", uniqueConstraints = @UniqueConstraint(name = "uk_person_name_phone", columnNames = { "name",
		"phone" }))
public class Person implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100, nullable = false)
	private String name;

	@Column(length = 30)
	private String phone;

	public Person() {
	}

	public Person(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Person))
			return false;
		Person that = (Person) o;
		return Objects.equals(name, that.name) && Objects.equals(phone, that.phone);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, phone);
	}
}
