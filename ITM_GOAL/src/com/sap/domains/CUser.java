package com.sap.domains;

public class CUser {
	
	//attributes in json post have to have same name as in getter/setter methods
	
	private String userName;
	
	public CUser()
	{
		
	}
    
    public CUser(String goalUserName)
    {
    	this.setUserName(goalUserName);

    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


    	

}
