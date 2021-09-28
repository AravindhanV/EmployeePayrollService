package com.bridgelabz.employeepayrollservice;

import java.time.LocalDate;

public class EmployeePayrollData {
	private int id;
	private String name;
	private LocalDate date;
	private boolean isActive;
	
	public EmployeePayrollData(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public EmployeePayrollData(int id, String name, LocalDate startDate) {
		this.id = id;
		this.name = name;
		this.date = startDate;
	}
	
	public EmployeePayrollData(int id, String name, LocalDate startDate, boolean isActive) {
		this.id = id;
		this.name = name;
		this.date = startDate;
		this.isActive = isActive;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate startDate) {
		this.date = startDate;
	}
	
	public String toString() {
		return "id="+id+", name="+name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean getActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(o==null||getClass() !=o.getClass()) return false;
		EmployeePayrollData that = (EmployeePayrollData) o;
		return id==that.id &&
				name.equals(that.name);
	}
}
