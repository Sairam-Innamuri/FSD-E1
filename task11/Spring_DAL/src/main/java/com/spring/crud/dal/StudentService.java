package com.spring.crud.dal;

import java.util.List;

import org.springframework.stereotype.Service;


@Service
public class StudentService {
	
	
	
	private final StudentRepo repo;
	
	
	public StudentService(StudentRepo repo) {
		this.repo=repo;
				
	}
	
	public Student saveStudent(Student student) {
		return repo.save(student);
	}
	
	public List<Student> findStudents() {
		
		return repo.findAll();
		
	}
	
	public Student findByID(Integer id) {
		
		return repo.findById(id).orElse(null);
		
	}
	
	public Student updateStudent(Student student) {
		return repo.save(student);
	}
	
	public void deleteByID(Integer id) {
		
		repo.deleteById(id);
		
	}
	
	public List<Student> getName(String name) {
		return repo.getByName(name);
	}
	
	
	public List<Student> getDepartment(String department) {
		return repo.getByDepartment(department);
	}
	
	
	
	public List<Student> getAge(Integer age){
		return repo.getByAge(age);
		
	}
	
}
