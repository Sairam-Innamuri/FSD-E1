package com.spring.jdbc.crud;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity               // Marks this class as a JPA entity
@Table(name="students") // Maps to student table
public class Student {
	

	    @Id                          // Primary Key
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "student_id") // Maps to column student_id
	    private int id;

	    @Column(name = "name", nullable = false, length = 100)
	    private String name;

	    //@Column(name = "email", length = 100)
	    //private String email;

	    @Column(name = "department", length = 50)
	    private String department;

	    @Column(name = "age")
	    private int age;

	    // 🔹 Default Constructor (Required by JPA)
	    public Student() {
	    }

	    // 🔹 Parameterized Constructor
	    public Student(String name, String department, int age) {
	        this.name = name;
	        this.department = department;
	        this.age = age;
	    }

	    // 🔹 Getters and Setters
	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }


	    public String getDepartment() {
	        return department;
	    }

	    public void setDepartment(String department) {
	        this.department = department;
	    }

	    public int getAge() {
	        return age;
	    }

	    public void setAge(int age) {
	        this.age = age;
	    }


}