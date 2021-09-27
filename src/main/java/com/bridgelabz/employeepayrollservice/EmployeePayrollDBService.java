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
	
	public static EmployeePayrollDBService getInstance() {
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
		String sql = "select * from employee";
		List<EmployeePayrollData> employeePayrollList= new ArrayList<>();
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(result);

		}catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(int id, String name) {
		return this.updateEmployeeDataUsingStatement(id,name);
	}
	
	private int updateEmployeeDataUsingStatement(int id, String name) {
		String sql = String.format("update employee set name='%s' where id=%d", name, id);
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
		try {
			employeePayrollDataStatement.setInt(1, id);
			ResultSet  resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while(result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				LocalDate startDate = result.getDate("start_date").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id,name, startDate));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "select * from employee where id=?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
