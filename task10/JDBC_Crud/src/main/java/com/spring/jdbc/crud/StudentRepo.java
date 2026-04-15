package com.spring.jdbc.crud;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepo extends JpaRepository<Student, Integer>{
	
	
	//List<Student> getByName(String name);
	
//	List<Student> getByEmail(String email);
	
	
//	List<Student> getByDepartment(String department);
	
	
//	List<Student> getByAge(Integer age);

}