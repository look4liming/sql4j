package test;

public class Department {
	
	private String pk;
	private String name;
	
	public Department() {
	}
	
	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Department[pk="+getPk()+", name="+getName()+"]";
	}

}
