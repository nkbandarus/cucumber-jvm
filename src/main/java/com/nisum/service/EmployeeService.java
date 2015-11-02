package com.nisum.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nisum.domain.Employee;

@Service
public class EmployeeService {
	
   List<Employee> employees = new ArrayList<Employee>();
   
   
   public void save(Employee employee) {
	   employees.add(employee);
   }
   
   

public Employee getEmployeeById(String empId) {
	   
	   for(Employee employee : employees) {
		   if(empId.equals(employee.getEmpId())) {
			   return employee;
		   }
	   }
	return null;
	   
   }
   
}