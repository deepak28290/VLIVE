package a.service.proj;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import a.service.data.LocationDetails;
import a.service.data.PotentialGuideResponse;
import a.service.data.ResponseStatus;

public class BusServiceHelper{

	public static PotentialGuideResponse getAllNearestBuses(String busNumber, String currentStop,String destinationStop) throws SQLException{

		Set<String> potentialGuideSet = null;
		List<LocationDetails> locationDetailsList = new ArrayList<LocationDetails>();
		Boolean upstream = getUpstreamValue(currentStop,destinationStop);
		if(upstream == null)
			return null;

		try{
			potentialGuideSet = DBConnectionHelper.getPotentialGuides(upstream == true ? "1" : "0", busNumber);
			for(String string: potentialGuideSet){
				LocationDetails location = new LocationDetails();
				List<String> coOrdinateList = Arrays.asList(string.split(","));
				location.setLatitude(Double.valueOf(coOrdinateList.get(0)));
				location.setLongitude(Double.valueOf(coOrdinateList.get(1)));
				locationDetailsList.add(location);
			}

		}catch(Exception e){
			return null;
		}

		if(!locationDetailsList.isEmpty()){

			LocationDetails fromLocation = DBConnectionHelper.setLocationFromArea(currentStop);
			LocationDetails toLocation = DBConnectionHelper.setLocationFromArea(destinationStop);

			if(fromLocation != null && toLocation != null){
				return sortAndReturnNearest(locationDetailsList, fromLocation, toLocation);
			}			
		}

		return null;
	}


	private static PotentialGuideResponse sortAndReturnNearest(List<LocationDetails> locationDetailsList, LocationDetails fromLocation, LocationDetails toLocation){
	
		PotentialGuideResponse response = new PotentialGuideResponse();
		List<LocationDetails> finalLocationDetails = new ArrayList<LocationDetails>();
		
		for(LocationDetails location: locationDetailsList){

			Long directDistance = getDistance(location, fromLocation, toLocation,true);
			if(directDistance != null){
				Long viaWayPointDistance = getDistance(location, fromLocation, toLocation,false);
				if( directDistance >= viaWayPointDistance){
					location.setDistance(directDistance);
					finalLocationDetails.add(location);
				}
			}			
		}		
		response.setResponseList(finalLocationDetails);	
		response.setResponseStatus(ResponseStatus.SUCCESS);
		return response;
	}
	
	private static Long getDistance(LocationDetails busLocation, LocationDetails currentStop, LocationDetails destinationStop, boolean isDirect){

		Long distance = null;
		String urlString = "";
		if(isDirect){
			urlString = "http://maps.googleapis.com/maps/api/directions/xml?sensor=false&origin="+busLocation.getLatitude() + "+" +busLocation.getLongitude()
					+"&destination="+destinationStop.getLatitude() + "+" + destinationStop.getLongitude();
		}else{
			urlString = "http://maps.googleapis.com/maps/api/directions/xml?sensor=false&origin="+busLocation.getLatitude() + "+" +busLocation.getLongitude()
					+"&destination="+destinationStop.getLatitude() + "+" + destinationStop.getLongitude() + "&waypoints=" +currentStop.getLatitude() + "+" + currentStop.getLongitude();
		}

		try{
			URL urlGoogleDirService = new URL(urlString);

			HttpURLConnection urlGoogleDirCon = (HttpURLConnection)urlGoogleDirService.openConnection();

			urlGoogleDirCon.setAllowUserInteraction( false );
			urlGoogleDirCon.setDoInput( true );
			urlGoogleDirCon.setDoOutput( false );
			urlGoogleDirCon.setUseCaches( true );
			urlGoogleDirCon.setRequestMethod("GET");
			urlGoogleDirCon.connect();

			DocumentBuilderFactory factoryDir = DocumentBuilderFactory.newInstance();
			DocumentBuilder parserDirInfo = factoryDir.newDocumentBuilder();
			Document docDir = parserDirInfo.parse(urlGoogleDirCon.getInputStream());
			urlGoogleDirCon.disconnect();

			NodeList nodeList = docDir.getElementsByTagName("distance");
			if(nodeList != null && nodeList.getLength() > 0){
				Element element = (Element) nodeList.item(nodeList.getLength() - 1);
				if(element != null){
					NodeList n1 = element.getElementsByTagName("text");

					if(n1 != null && n1.getLength() > 0) {
						Element el = (Element)n1.item(0);
						distance = getDistanceFromNode(el.getFirstChild().getNodeValue());
					}
				}
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

		return distance;
	}
	
	private static Long getDistanceFromNode(String nodeValue){
		Long distance = null;
		
		if(nodeValue != null){			
			try{
				distance = (long) Double.parseDouble(Arrays.asList(nodeValue.split(" ")).get(0));
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		return distance;
	}
	
	private static Boolean getUpstreamValue(String currentStop,String destinationStop) throws SQLException{
		Boolean upstream = null;

		Integer currentStopNumber = DBConnectionHelper.getStopNumber(currentStop);
		Integer destinationStopNumber = DBConnectionHelper.getStopNumber(destinationStop);

		if(currentStopNumber != null && destinationStopNumber != null){
			return (currentStopNumber - destinationStopNumber > 0 ? true: false);
		}
		return upstream;
	}
}