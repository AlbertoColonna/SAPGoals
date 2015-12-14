package com.sap.domains;

public class CGoal {
	
	//attributes in json post have to have same name as in getter/setter methods
	
	private String id;
	private String name;
	private String start;
	private String due;
	private String status;
	private String userName;
	
	public CGoal()
	{
		
	}
    
    public CGoal(String goalId, String goalName, String goalStart, String goalDue, String goalStatus, String goalUserName)
    {
    	this.setId(goalId);
    	this.setName(goalName);
    	this.setStart(goalStart);
    	this.setDue(goalDue);
    	this.setStatus(goalStatus);
    	this.setUserName(goalUserName);
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getDue() {
		return due;
	}

	public void setDue(String due) {
		this.due = due;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

    
   
    

/*    @Override 
    public String toString() { 
        return new StringBuffer(" Date : ").append(this.datex) 
                .append(this.id).toString(); 
    } */
    	

}
