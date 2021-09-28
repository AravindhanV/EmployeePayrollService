package com.bridgelabz.employeepayrollservice;

import java.sql.Connection;
import java.sql.Date;
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

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "user1";
		String password = "pass";
		Connection connection;
		System.out.println("Connecting to database" + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection is successfull" + connection);
		return connection;
	}

	public List<EmployeePayrollData> readData() {
		String sql = "select * from employee";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(result);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(int id, String name) {
		return this.updateEmployeeDataUsingStatement(id, name);
	}

	private int updateEmployeeDataUsingStatement(int id, String name) {
		String sql = String.format("update employee set name='%s' where id=%d", name, id);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<EmployeePayrollData> getEmployeePayrollData(int id) {
		List<EmployeePayrollData> employeePayrollList = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setInt(1, id);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				LocalDate startDate = result.getDate("start_date").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public List<EmployeePayrollData> getEmployeesInGivenStartRange(String date) throws SQLException {
		String sql = String
				.format("select * from employee where start_date between cast('%s' as date) and date(now());", date);
		List<EmployeePayrollData> listOfEmployees = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			listOfEmployees = this.getEmployeePayrollData(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOfEmployees;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "select * from employee where id=?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public double getSumBasedOnGender(char gender) {
		String sql = String.format("select sum(phone) as total,gender from employee where gender='%c' group by gender;",
				gender);
		double sum = 0.0;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				sum = result.getDouble("total");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sum;
	}

	public EmployeePayrollData addEmployeeToPayroll(String name, LocalDate startDate, String gender, double salary) {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData= null;
		Connection connection = null;
		try {
		connection = this.getConnection();
		connection.setAutoCommit(false);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		String sql = String.format("insert into employee (name,gender,start_date) values ('%s','%s','%s')",name,gender,Date.valueOf(startDate));
		try(Statement statement = connection.createStatement();) {
			int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(employeeId,name,startDate);
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		String sql2 = String.format("insert into payroll (basic_pay,deductions,taxable_income,tax,net_pay,emp_id) values (%.2f,%.2f,%.2f,%.2f,%.2f,%d)",salary,0.2*salary,0.8*salary,0.08*salary,0.92*salary,employeeId);
		try(Statement statement = connection.createStatement();) {
			int rowAffected = statement.executeUpdate(sql2,statement.RETURN_GENERATED_KEYS);
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return employeePayrollData;
	}

	public int removeEmployee(int id) {
		EmployeePayrollData employeePayrollData= null;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate("update employee set isActive=false where id="+id);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
