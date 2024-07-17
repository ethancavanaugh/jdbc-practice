package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Employee;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeDaoImpl implements EmployeeDao {
    private JdbcTemplate jdbc;

    public EmployeeDaoImpl (JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Employee> getAllEmployees() {
        String sql = "SELECT * FROM employee";
        return jdbc.query(sql, new EmployeeMapper());
    }

    @Override
    public Employee getEmployeeById(int id) {
        try {
            String sql = "SELECT * FROM employee WHERE id = ?";
            return jdbc.queryForObject(sql, new EmployeeMapper(), id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Employee addEmployee(Employee employee) {
        String sql = "INSERT INTO employee(firstName, lastName) VALUES (?,?)";
        jdbc.update(sql, employee.getFirstName(), employee.getLastName());

        @SuppressWarnings("DataFlowIssue")
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        employee.setId(newId);
        return employee;
    }

    @Override
    public void updateEmployee(Employee employee) {
        String sql = "UPDATE employee SET firstName = ?, lastName = ? WHERE id = ?";
        jdbc.update(sql, employee.getFirstName(), employee.getLastName(), employee.getId());
    }

    @Override
    @Transactional
    public void deleteEmployeeById(int id) {
        String delEmployeeFromMeetings = "DELETE FROM meeting_employee WHERE employeeId = ?";
        jdbc.update(delEmployeeFromMeetings, id);

        String delEmployee = "DELETE FROM employee WHERE id = ?";
        jdbc.update(delEmployee, id);
    }

    public static final class EmployeeMapper implements RowMapper<Employee> {
        @Override
        public Employee mapRow(ResultSet rs, int index) throws SQLException {
            Employee emp = new Employee();
            emp.setId(rs.getInt("id"));
            emp.setFirstName(rs.getString("firstName"));
            emp.setLastName(rs.getString("lastName"));
            return emp;
        }
    }
}
