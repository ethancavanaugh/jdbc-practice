package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Employee;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDaoImpl implements EmployeeDao {
    @Override
    public List<Employee> getAllEmployees() {
        return null;
    }

    @Override
    public Employee getEmployeeById(int id) {
        return null;
    }

    @Override
    public Employee addEmployee(Employee employee) {
        return null;
    }

    @Override
    public void updateEmployee(Employee employee) {

    }

    @Override
    public void deleteEmployeeById(int id) {

    }
}
