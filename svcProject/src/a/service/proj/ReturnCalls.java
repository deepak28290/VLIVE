package a.service.proj;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jettison.json.JSONArray;




// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@XmlRootElement
@Path("/volvolive")
public class ReturnCalls {
	 @GET
	  @Path("/busList")
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public String getAllBusesByBusNumb(@QueryParam(value = "busnum") String busnum) throws ClientProtocolException, IOException {
			System.out.println(busnum);	
		
			return busnum;
	 }
	 
	 @GET
	  @Path("/firstUser")
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public String crtUserData(@QueryParam(value = "userid") String userid,@QueryParam(value = "boardingpoint") String boardingpoint,@QueryParam(value = "droppoint") String droppoint,@QueryParam(value = "busnumb") String busnumb,@QueryParam(value = "currentcoord") String currentcoord) throws ClientProtocolException, IOException {
		String boardcoord;
		String dropcoord;
		boardcoord=VolvoUtil.getCoordFromLoc(boardingpoint);
		dropcoord=VolvoUtil.getCoordFromLoc(droppoint);
		
		 Connection connection = null;
			Statement stmt = null;
			
			String sql = new String();
		 try {
				// the sql server driver string
				Class.forName("com.mysql.jdbc.Driver");

				// the sql server url
				String url = "jdbc:mysql://db4free.net:3306/volvolive";
				
				// get the sql server database connection
				connection = DriverManager.getConnection(url, "deepak111", "deepak");

				stmt = connection.createStatement();
				String up=VolvoUtil.isUpStream(boardingpoint, droppoint);
				System.out.println(sql);
				//String getSql = "INSERT INTO volvo_table VALUES('deepakId','Marathalli','Kadugodi','100,100','120,120','111,111','22E','0','','')";
				String getSql = "INSERT INTO volvo_table VALUES('"+userid+"','"+boardingpoint+"','"+droppoint+"','"+boardcoord+"','"+dropcoord+"','"+currentcoord+"','"+busnumb+"','0',null,'"+up+"')";
				System.out.println(getSql);
				stmt.executeUpdate(getSql);
				// STEP 5: Extract data from result set
//				System.out.println(memcached.get(""));
			
						
					}catch(Exception e){
						e.printStackTrace();
						return "fail";
					}
		 System.out.println(userid);	
		  	 return userid;
	 }
	 
	 @GET
	  @Path("/updateCoords")
	  @Produces(MediaType.APPLICATION_JSON)
	  @Consumes(MediaType.APPLICATION_JSON)
	  public String updCurrCoord(@QueryParam(value = "coord") String coord,@QueryParam(value = "userid") String userid) throws ClientProtocolException, IOException {
			
			 Connection connection = null;
				Statement stmt = null;
				
				String sql = new String();
			 try {
					// the sql server driver string
					Class.forName("com.mysql.jdbc.Driver");

					// the sql server url
					String url = "jdbc:mysql://db4free.net:3306/volvolive";
					
					// get the sql server database connection
					connection = DriverManager.getConnection(url, "deepak111", "deepak");

					stmt = connection.createStatement();
					
					System.out.println(sql);
					String getSql = "UPDATE volvo_table SET current_coord='"+coord+"' WHERE user_id='"+userid+"'";
					//String getSql2 = "INSERT INTO volvo_table VALUES('"+userid+"','"+boardingpoint+"','"+droppoint+"','100,100','120,120','"+busnumb+"','0',null)";
					stmt.executeUpdate(getSql);
					// STEP 5: Extract data from result set
//					System.out.println(memcached.get(""));
				
							
						}catch(Exception e){
							e.printStackTrace();
							return "fail";
						}
			 
			 System.out.println(userid);	
		  	 return userid;
	 }
	  
}