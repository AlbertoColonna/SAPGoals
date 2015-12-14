package com.sap.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.sap.coe.sf.InsertResult;
import com.sap.coe.sf.QueryResult;
import com.sap.coe.sf.SFWebServiceFaultException_Exception;
import com.sap.coe.sf.UpdateResult;
import com.sap.coe.sf.UpsertResult;
import com.sap.domains.CGoal;
import com.sap.util.CConst;
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
	
	
	@POST	
	@Produces(MediaType.TEXT_PLAIN)
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})	
    public Response postGoals( CGoal[] goals) {
		
		//String userName;
		String output = "";
		boolean insert = true;
		String userId = "";
		
		List<SFObject> lSF = new ArrayList<SFObject>();	
		
		String userName = this.usrSrv.getUserId();
		
		//Only for testing
		userName = "D001684";		
				
		
		try {		
			
			QueryResult oRes = mConnector.getAPI().query(CQueries.queryGetUser + "'" + userName + "'", CConnectorService.SF_DEFAULT_PARAM);	
			
			Element field;		
	    	 
	    	//name,start,due,status	    	
	    	 
	        for(int i=0; i<oRes.getSfobject().size();i++) 
	        {        	   	
	        	userId = oRes.getSfobject().get(i).getId();	    
	        	LOG.error(userId);
	        	break;
	        }   						
			
			LOG.error("Anzahl:"+goals.length);
			
			for(int i=0; i<goals.length;i++)
			{
				SFObject oSF = new SFObject();
				oSF.setType("Goal$6");
				
				SOAPFactory factory = SOAPFactory.newInstance();
				SOAPElement element;						
				
				//if(!goals[i].getId().isEmpty())
				if(goals[i].getId() != null)				
				{
					oSF.setId(goals[i].getId());
					insert = false;
				}
				else
				{
					//userName
					element = factory.createElement("userName");	 
					element.addTextNode(goals[i].getUserName());	
					oSF.getAny().add(element);	
					
					//GUID
				    UUID guid = UUID.randomUUID();				
				    
				    String strGuid = guid.toString();
					element = factory.createElement("guid");	  
					element.addTextNode(strGuid);	
					oSF.getAny().add(element);	
					
					//Flag
					element = factory.createElement("flag");	 
					element.addTextNode("Public");	
					oSF.getAny().add(element);		
					
					//User-ID
					
					LOG.error("ID:"+userId);
					element = factory.createElement("userId");	 
					element.addTextNode(userId);	
					oSF.getAny().add(element);		
					
					//Category
					element = factory.createElement("category");	
					element.addTextNode("Customer");
					oSF.getAny().add(element);							
					
				}

				//Name
				element = factory.createElement("name");	
				element.addTextNode(goals[i].getName());
				oSF.getAny().add(element);						
					
				//Start
				element = factory.createElement("start");	
				element.addTextNode(goals[i].getStart());	
				oSF.getAny().add(element);		
					
				//Due
				element = factory.createElement("due");	
				element.addTextNode(goals[i].getDue());	
				oSF.getAny().add(element);		
					
				//Status
				element = factory.createElement("status");	
				element.addTextNode(goals[i].getStatus());	
				oSF.getAny().add(element);		
										
				
				LOG.error(goals[i].getId()+"/"+goals[i].getName()+"/"+goals[i].getStart()+"/"+goals[i].getDue()+"/"+goals[i].getStatus()+"/"+goals[i].getUserName()+"/")	;
				
				lSF.add(oSF);					
				
			}
			
			LOG.error("Update list:"+ lSF.size());
			if(insert == true)
			{
				UpsertResult ir = mConnector.getAPI().upsert(CConst.GOAL_TABLE, lSF, null);
				LOG.error("Result:"+ir.getJobStatus());			
				LOG.error("Result:"+ir.getMessage());					
			}
			else
			{
				UpdateResult ur = mConnector.getAPI().update(CConst.GOAL_TABLE, lSF, null);			
				LOG.error("Result:"+ur.getJobStatus());		
				LOG.error("Err:"+ur.getMessage());
			}

			
			
	} catch (SFWebServiceFaultException_Exception e) {
		// TODO Auto-generated catch block
		LOG.error("Upsert error"); 
		LOG.error("Exception:"+e.getMessage());
		return null;
		
	} catch (SOAPException e) {
		// TODO Auto-generated catch block
		LOG.error("SOAP Exception");
		e.printStackTrace();
	}			
	catch (Exception e){
		LOG.error("Exc:"+e.getMessage());
	}
			
	return Response.status(200).entity(output).build(); 				
		
	    
    }	


}
