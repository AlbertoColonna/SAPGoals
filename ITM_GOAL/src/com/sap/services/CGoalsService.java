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

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sap.coe.sf.InsertResult;
import com.sap.coe.sf.QueryResult;
import com.sap.coe.sf.SFWebServiceFaultException_Exception;
import com.sap.coe.sf.UpdateResult;
import com.sap.coe.sf.UpsertResult;
import com.sap.domains.CGoal;
import com.sap.domains.CUser;
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
		
		
		LOG.error("Test1");
		mConnector = new CConnectorService();
		LOG.error(request.getUserPrincipal().toString());
		usrSrv = new CUserService(request.getUserPrincipal());    	      
   			 
    }	
	

	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)	
	//public List<CGoal> getGoals()
	public Response getGoals()	
	{
		String id;
		String name;
		String start;
		String due;
		String status;
		String userName; 
		
		List<CGoal> lGoals = new ArrayList();
		
		userName = this.usrSrv.getUserId();
		//LOG.error("The User-ID is:" + userName);
		
		//Only for testing
		//userName = "D001684";
		
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
	        
			return Response.ok().entity(lGoals).build();	        
			
			//return lGoals; 
			
		} catch (SFWebServiceFaultException_Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Query error"); 
			return null; 
		}		
	} 
	
	
	@POST	
	@Produces(MediaType.TEXT_PLAIN)
    @Consumes({MediaType.APPLICATION_JSON})	
    public Response postGoals( CGoal[] goals) {
		
		//String userName;
		String output = "";
		boolean insert = true;
		String userId = "";
		String strGuid = "";
		
		List<SFObject> lSFInsert = new ArrayList<SFObject>();	
		List<SFObject> lSFUpdate = new ArrayList<SFObject>();			
		
		String userName = this.usrSrv.getUserId();
		
		//Only for testing
		//userName = "D001684";		
			
        LOG.error("Test1");
		
		try {		
			
			QueryResult oRes = mConnector.getAPI().query(CQueries.queryGetUser + "'" + userName + "'", CConnectorService.SF_DEFAULT_PARAM);	
			
			Element field;		
	    	 
	    	//name,start,due,status	   
			
	        LOG.error("Test2");			
	    	 
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
					LOG.error(goals[i].getId());
				}
				else
				{
					insert = true;
/*					LOG.error("Factory"+factory);
					
					//userName
					element = factory.createElement("userName");	 
					
					LOG.error("Element"+element);	
					LOG.error("UsrN"+goals[i].getUserName());
					element.addTextNode(goals[i].getUserName());	
					oSF.getAny().add(element);	
					LOG.error("UserName"+element.getTextContent() + "");*/
					
					//GUID
				    UUID guid = UUID.randomUUID();				
				    
				    strGuid = "";
				    strGuid = guid.toString();
					element = factory.createElement("guid");	  
					element.addTextNode(strGuid);	
					oSF.getAny().add(element);	
					LOG.error("GUID:"+strGuid);
					 
					//Flag
					element = factory.createElement("flag");	 
					element.addTextNode("Public");	
					oSF.getAny().add(element);		
					LOG.error("Flag"+element.getTextContent());					
					
					//User-ID
					
					LOG.error("ID:"+userId);
					element = factory.createElement("userId");	 
					element.addTextNode(userId);	
					oSF.getAny().add(element);		
					LOG.error("USER-ID"+element.getTextContent());							
					
					//Category
					element = factory.createElement("category");	
					element.addTextNode("Customer");
					oSF.getAny().add(element);			
					LOG.error("Category:"+element.getTextContent());							
					
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
				element = factory.createElement("state");	
				element.addTextNode(goals[i].getState());	
				oSF.getAny().add(element);		
										
				
				if(insert == true)
				{
					lSFInsert.add(oSF);
				}
				else
				{
					lSFUpdate.add(oSF);					
				}
				
			}
			
			//LOG.error("Update list:"+ lSF.size());
			if(insert == true)
			{
				UpsertResult ir = mConnector.getAPI().upsert(CConst.GOAL_TABLE, lSFInsert, null);
				LOG.error("Result:"+ir.getJobStatus());			
				LOG.error("Result:"+ir.getMessage());	
				
				if(ir.getJobStatus().equals("ERROR") )
				{
					return Response.status(500).entity(output).build();						
				}				
			}
			else 
			{
				UpdateResult ur = mConnector.getAPI().update(CConst.GOAL_TABLE, lSFUpdate, null);			
				LOG.error("Result:"+ur.getJobStatus());		
				LOG.error("Err:"+ur.getMessage());
				
				if(ur.getJobStatus().equals("ERROR") )
				{
					return Response.status(500).entity(output).build();						
				}
			}

			
			
	} catch (SFWebServiceFaultException_Exception e) {
		// TODO Auto-generated catch block
		LOG.error("Upsert error"); 
		LOG.error("Exception:"+e.getMessage());
		return Response.status(500).entity(e.getMessage()).build();		
		
	} catch (SOAPException e) {
		// TODO Auto-generated catch block
		LOG.error("SOAP Exception");
		e.printStackTrace();
		return Response.status(500).entity(e.getMessage()).build();	
	}			
	catch (Exception e){
		LOG.error("Exc:"+e.getMessage());
		e.printStackTrace();
		return Response.status(500).entity(e.getMessage()).build();				
	}
			
		return Response.ok().entity("Hallooooo").build();	 				
		
	    
    }	
	
	@GET
	@Path("user")	
	@Produces(MediaType.APPLICATION_JSON)	
	public Response getUser()
	{
		
		String userName = this.usrSrv.getUserId();	
		
		//userName = "D001684";				
		
		CUser user = new CUser(userName);		
		
		return Response.ok().entity(user).build();	 
		
	}
	
	
	@GET
	@Path("count")	
	@Produces(MediaType.APPLICATION_JSON)	
	public Response getNoOfGoals()
	{
		
		JSONObject jo = new JSONObject();

		String output   	 = "";
        int noOfGoals   	 = 0;		
        int noAchieved  	 = 0;
        int noOnTrack   	 = 0;
        int noOffTarget		 = 0;
        int noLongerValid    = 0;
		
		String userName = this.usrSrv.getUserId();
		
		//Only for testing
		//userName = "D001684";		
		
		try {		
			
			QueryResult oRes = mConnector.getAPI().query(CQueries.querySelectNoOfGoals + "'" + userName + "'", CConnectorService.SF_DEFAULT_PARAM);	
			
			Element field;
			
	        for(int i=0; i<oRes.getSfobject().size();i++) 
	        {        	         	
	        	
	        	for(int j=0; j<oRes.getSfobject().get(i).getAny().size();j++)
	        	{
	            	field = (Element) oRes.getSfobject().get(i).getAny().get(j);
	            	
	                if(field.getTextContent().equals(CConst.STATUS_ACHIEVED))
	                {
	                	noAchieved++;
	                }
	                else if(field.getTextContent().equals(CConst.STATUS_ON_TRACK))
	                {
	                	noOnTrack++;
	                }
	                else if(field.getTextContent().equals(CConst.STATUS_OFF_TARGET))
	                {
	                	noOffTarget++;
	                }
	                else if(field.getTextContent().equals(CConst.STATUS_NO_LONGER_VALID))
	                {
	                	noLongerValid++;
	                }	                
	            	
	            }    
	 
	        }   		
					
			noOfGoals = noAchieved + noOnTrack + noOffTarget;//oRes.getSfobject().size();
			
			jo.put("count",    noOfGoals);		
			jo.put("achieved", noAchieved);
			jo.put("ontrack",  noOnTrack);
			jo.put("offtarget",noOffTarget);		
			jo.put("nolongervalid",noLongerValid);				
			
	
		} catch (SFWebServiceFaultException_Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Upsert error"); 
			LOG.error("Exception:"+e.getMessage());
			return Response.status(500).entity(e.getMessage()).build();		
		}			
		catch (Exception e){
			LOG.error("Exc:"+e.getMessage());
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();				
		}
				
		
		return Response.ok().entity(jo).build();	 	
			
	}	


}
