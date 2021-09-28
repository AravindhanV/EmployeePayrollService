/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.bridgelabz.employeepayrollservice;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService {CONSOLE_IO,FILE_IO, DB_IO, REST_IO};
	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;
	
	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}
	
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}
	
	public List<EmployeePayrollData> readEmployeePayrollDBData(IOService ioService) throws SQLException{
		if(ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		return this.employeePayrollList;
	}
	
	public static void main(String args[]) {
		ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
		Scanner consoleInputReader = new Scanner(System.in);
		employeePayrollService.readEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
	}
	
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		else if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printData();
		}
		else if(ioService.equals(IOService.CONSOLE_IO)) {
			Scanner consoleInputReader = new Scanner(System.in);
			System.out.println("Enter employee ID:");
			int id = consoleInputReader.nextInt();
			System.out.println("Enter employee name :");
			String name  = consoleInputReader.next();
			employeePayrollList.add(new EmployeePayrollData(id, name));
		}
		return this.employeePayrollList;
	}
	
	public void writeEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.CONSOLE_IO))
		System.out.println("Writing employee payroll to console\n"+employeePayrollList);
		else if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
		}
	}
	
	public void printData(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printData();
		}
	}
	
	public long countEntries(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return 0;
	}

	public void updateEmployeeName(int id, String name) {
		int result = employeePayrollDBService.updateEmployeeData(id,name);
		if(result==0) {
			return;
		}
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(id);
		if(employeePayrollData != null) employeePayrollData.setName(name);
	}
	
	public EmployeePayrollData getEmployeePayrollData(int id) {
		return this.employeePayrollList.stream().filter(employeePayrollDataItem -> employeePayrollDataItem.getId() == id).findFirst().orElse(null);
	}
	
	

	public boolean checkEmployeePayrollInSyncWithDB(int id) {
		List<EmployeePayrollData> employeePayrollList = employeePayrollDBService.getEmployeePayrollData(id);
		System.out.println(employeePayrollList);
		return employeePayrollList.get(0).equals(getEmployeePayrollData(id));
	}

	public List<EmployeePayrollData> getEmployeesInGivenStartDateRange(String date) throws SQLException {
		List<EmployeePayrollData> employeesInGivenRange=employeePayrollDBService.getEmployeesInGivenStartRange(date);
		return employeesInGivenRange;
	}

	public double getSumBasedOnGender(char gender) {
		double sumOfSalaries=employeePayrollDBService.getSumBasedOnGender(gender);
		return sumOfSalaries;
	}

	public int addEmployee(String name,  LocalDate startDate, String gender) {
		EmployeePayrollData data = employeePayrollDBService.addEmployeeToPayroll(name,startDate,gender);
		employeePayrollList.add(data);
		return data.getId();
	}
}
