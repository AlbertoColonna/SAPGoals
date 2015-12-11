package com.sap.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.sap.coe.sf.QueryResult;
import com.sap.coe.sf.SFWebServiceFaultException_Exception;
import com.sap.domains.CGoal;
import com.sap.util.CQueries;
import com.sap.coe.sf.SFObject;

@Path("/goals")
public class CGoalsService extends HttpServlet{
	
	private static final Logger LOG = LoggerFactory.getLogger(CGoalsService.class);		
	
	private CConnectorService mConnector;
	private CUserService usrSrv;
	
    @Context
    HttpServletRequest request;	
	
    @PostConstruct
    public void init() {
		
		LOG.error("GET method");
		
		mConnector = new CConnectorService();		
		usrSrv = new CUserService(request.getUserPrincipal());    	      
   			
    }	
	

	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	public List<CGoal> getGoals()
	{
		String id;
		String name;
		String start;
		String due;
		String status;
		String userName;
		
		List<CGoal> lGoals = new ArrayList();
		
		userName = this.usrSrv.getUserId();
		LOG.error("The User-ID is:" + userName);
		
		//Only for testing
		userName = "D001684";
		
		LOG.error("Query:"+CQueries.querySelectGoals + userName);
		LOG.error("API:"+mConnector.getAPI());
		
		
		try {
			QueryResult oRes = mConnector.getAPI().query(CQueries.querySelectGoals + "'" + userName + "'", CConnectorService.SF_DEFAULT_PARAM);
			 
	    	Element field;		
	    	
	    	//name,start,due,status	    	
	    	 
	        for(int i=0; i<oRes.getSfobject().size();i++) 
	        {        	 
	        	id       = "";
	        	name     = "";
	        	start    = "";
	        	due      = "";
	        	status   = "";
	        	
	        	id = oRes.getSfobject().get(i).getId();	        	
	        	
	        	for(int j=0; j<oRes.getSfobject().get(i).getAny().size();j++)
	        	{
	            	field = (Element) oRes.getSfobject().get(i).getAny().get(j);
	            	
	            	switch(j)
	            	{
	            		case 0: name = field.getTextContent();
	            				break;
	            		case 1: start = field.getTextContent();
        						break;	            				
	            		case 2: due = field.getTextContent();
        						break;	   
	            		case 3: status = field.getTextContent();
        						break;        				
	            	}
	            	
	            }    
	        	
	        	lGoals.add(new CGoal(id,name,start,due,status,userName));
	 
	        }   			
			
			return lGoals;
			
		} catch (SFWebServiceFaultException_Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Query error"); 
			return null;
		}		
	}


}
