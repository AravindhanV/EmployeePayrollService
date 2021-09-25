package com.bridgelabz.employeepayrollservice;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DBDemo {
	public static void main(String[] args) {
		String jdbcURL="jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName="user1";
		String password="pass";
		Connection connection;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver loaded!");
		}catch(ClassNotFoundException e) {
			throw new IllegalStateException("",e);
		}
		listDrivers();
		try {
			System.out.println("Connecting DB: "+jdbcURL);
			connection = DriverManager.getConnection(jdbcURL,userName,password);
			System.out.println("Connection Successful");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while(driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println(""+driverClass.getClass().getName());
		}
	}
}
