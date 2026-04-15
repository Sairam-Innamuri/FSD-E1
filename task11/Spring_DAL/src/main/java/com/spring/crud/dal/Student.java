package com.spring.crud.dal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity

@Data
@Table(name = "student")
public class Student {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="name", nullable = false, length = 100)
	private String name;
	
	@Column(name="email", length = 50)
	private String email;
	
	@Column(name="department", length = 50)
	private String department;
	
	@Column(name="age", length = 10)
	private Integer age;
	
	public Student() {
		
	}
	
	public Student(String name, String email, String department, Integer age) {
		
		this.name=name;
		this.email=email;
		this.department=department;
		this.age=age;
		
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	
	

}
