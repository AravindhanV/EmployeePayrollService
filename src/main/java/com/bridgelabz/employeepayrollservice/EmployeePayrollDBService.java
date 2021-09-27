package com.bridgelabz.employeepayrollservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

	private PreparedStatement employeePayrollDataStatement;
	private static EmployeePayrollDBService employeePayrollDBService;
	private EmployeePayrollDBService() {}
	
	private static EmployeePayrollDBService getInstance() {
		if(employeePayrollDBService==null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL="jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName="user1";
		String password="pass";
		Connection connection;
		System.out.println("Connecting to database"+ jdbcURL);
		connection=DriverManager.getConnection(jdbcURL,userName,password);
		System.out.println("Connection is successfull"+ connection);
		return connection;
	}

	public List<EmployeePayrollData> readData() throws SQLException{
		String sql = "select p.emp_id, e.name, p.basic_pay "
				+ " from employee e, payroll p"
				+ " where e.id=p.emp_id";
		List<EmployeePayrollData> employeePayrollList= new ArrayList<>();
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				int id=result.getInt("emp_id");
				String name = result.getString("name");
				Double salary=result.getDouble("basic_pay");
				LocalDate startDate = result.getDate("start_date").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary,startDate));
			}

		}catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(int id, double salary) {
		return this.updateEmployeeDataUsingStatement(id,salary);
	}
	
	private int updateEmployeeDataUsingStatement(int id, double salary) {
		String sql = String.format("update payroll set salary=%.2f where emp_id=%d", salary, id);
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public List<EmployeePayrollData> getEmployeePayrollData(int id) {
		List<EmployeePayrollData> employeePayrollList = null;
		if(this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
	}

}
