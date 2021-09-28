package com.bridgelabz.employeepayrollservice;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bridgelabz.employeepayrollservice.EmployeePayrollService.IOService;

import static com.bridgelabz.employeepayrollservice.EmployeePayrollService.IOService.FILE_IO;;

public class EmployeePayrollServiceTest {
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(1, "Jeff Bezos"),	
				new EmployeePayrollData(2, "Bill Gates"),
				new EmployeePayrollData(3, "Mark Zuckerberg")
		};
		
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(FILE_IO);
		employeePayrollService.printData(FILE_IO);

		long entries = employeePayrollService.countEntries(FILE_IO);
		Assert.assertEquals(3, entries);
	}
	
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData= employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		Assert.assertEquals(1, employeePayrollData.size());
	}
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch() throws SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		employeePayrollService.updateEmployeeName(1,"newName");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB(1);
		Assert.assertTrue(result);
	}
	
	@Test 
	public void givenDateRangeForEmployee_WhenRetrieved_shouldMatchGivenCount() throws SQLException {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDBData(IOService.DB_IO);
		String date="2021-01-01";
		List<EmployeePayrollData> listOfEmployeesInDateRange=employeePayrollService.getEmployeesInGivenStartDateRange(date);
		Assert.assertEquals(1, listOfEmployeesInDateRange.size());
	}
	
	@Test
	public void givenEmployeePayrollInDB_shouldReturnSumOfSalaryOfMaleEmployees() throws SQLException{
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		char gender='M';
		double sumOfSalary=employeePayrollService.getSumBasedOnGender(gender);
		Assert.assertEquals(123456789, sumOfSalary,0.0);
	}
	
	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		int id = employeePayrollService.addEmployee("Mark",LocalDate.now(),"M",50000);
		System.out.println("ID in test = "+id);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB(id);
		Assert.assertTrue(result);
	}
}
