package test;

public class Person {
	
	private String pk;
	private String username;
	private String password;
	private String departmentPk;
	private String departmentName;
	
	public Person() {
	}
	
	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDepartmentPk() {
		return departmentPk;
	}

	public void setDepartmentPk(String departmentPk) {
		this.departmentPk = departmentPk;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	@Override
	public String toString() {
		return "Person[pk=" + getPk() + 
				", username=" + getUsername() + 
				", password=" + getPassword() + 
				", departmentPk=" + getDepartmentPk() + 
				", departmentName=" + getDepartmentName() + "]";
	}
	
}
