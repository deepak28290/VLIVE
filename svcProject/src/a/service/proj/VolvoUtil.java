package a.service.proj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class VolvoUtil {

	
	public static String getCoordFromLoc(String loc){
		 Connection connection = null;
			Statement stmt = null;
			String lat="";
			String longt="";
			String coord;
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
				String getSql = "SELECT * from volvo_bus_routes WHERE busstop_name='"+loc+"'";
				//String getSql2 = "INSERT INTO volvo_table VALUES('"+userid+"','"+boardingpoint+"','"+droppoint+"','100,100','120,120','"+busnumb+"','0',null)";
			ResultSet rs=	stmt.executeQuery(getSql);
			if (rs.next()) {
			lat=rs.getString("latitude");
			longt=rs.getString("longitude");
				
			}
				// STEP 5: Extract data from result set
//				System.out.println(memcached.get(""));
			
						
					}catch(Exception e){
						e.printStackTrace();
						return "fail";
					}
		loc=lat+","+longt;
		return loc;
	}
	public static void main(String[] args){
		System.out.println(getCoordFromLoc("Majestic"));
	}
	
}
