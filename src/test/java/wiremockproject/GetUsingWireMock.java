package wiremockproject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetUsingWireMock {

	private static final int iPort = 8080;
	private static final String sHost = "localhost";
	private static WireMockServer server = new WireMockServer(iPort);
	public static void main(String[] args)  {
	
		try {
		startServer();
		Response getRes = RestAssured.given()
				.when()
				.contentType("application/json")
				.get(new URI("http://localhost:8080/api/cars"));
		
		//System.out.println("addData response : " + getRes.asString() + " \n" + getRes.getStatusCode());
		getListOfBlueTesla(getRes);
		
		System.out.println("***********************************************************");
		
		getCarPrice(getRes);
		
		System.out.println("***********************************************************");
		
		getHighestRevenueCar(getRes);
		System.out.println("***********************************************************");
		} catch(Exception e) {
			 e.printStackTrace();
			stopServer();
		}
		stopServer();
	}
	
	public static void getListOfBlueTesla(Response response) {
		List<Map<String, String>> mapCars = response.jsonPath().getList("Car");
		List<Map<String, String>> mapMetadata = response.jsonPath().getList("Car.metadata");
		System.out.println("List of blue tesla cars and notes");
		System.out.println("---------------------------------");
		for(int i=0;i<mapCars.size();i++) {
			String sColor =  mapMetadata.get(i).get("Color");
			String sMake =   mapCars.get(i).get("make");
			String sNotes =  mapMetadata.get(i).get("Notes");
			if (   sColor.equalsIgnoreCase("Blue")
				&& sMake.equalsIgnoreCase("Tesla")) 
			{
				System.out.println(sMake + "\t || " + sColor + "\t || " + sNotes);
			}
		}
	}
	
	public static void getCarPrice(Response response)
	{
		System.out.println("Lowest rental cost per day");
		System.out.println("--------------------------");
		List<Map<String, String>> mapCars = response.jsonPath().getList("Car");
		List<Map<String, Integer>> mapRent = response.jsonPath().getList("Car.perdayrent");
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		ArrayList<Integer> priceListAfterDis = new ArrayList<Integer>();
		for(int i=0;i<mapRent.size();i++) {
			int iPrice = mapRent.get(i).get("Price");
			priceList.add(iPrice);
			int iDiscount = mapRent.get(i).get("Discount");
			priceListAfterDis.add((iPrice - iDiscount));
		}
		int index = priceList.indexOf(Collections.min(priceList));
		System.out.println("Lowest Price : " + mapCars.get(index).get("make") +"\t ||" + priceList.get(index));
		int dIndex = priceListAfterDis.indexOf(Collections.min(priceListAfterDis));
		System.out.println("After Discount : " + mapCars.get(dIndex).get("make") +"\t ||" + priceListAfterDis.get(dIndex));
	}
	
	public static void getHighestRevenueCar(Response response)
	{
		List<Map<String, String>> mapCars = response.jsonPath().getList("Car");
		List<Map<String, Float>> mapMetrics = response.jsonPath().getList("Car.metrics");
		ArrayList<Float> metricsList = new ArrayList<Float>();
		for(int i=0;i<mapMetrics.size();i++) {
			Float fMCost = mapMetrics.get(i).get("yoymaintenancecost");
			Float fDepriciation = mapMetrics.get(i).get("depreciation");
			metricsList.add(fMCost + fDepriciation);
		}
		int iPIndex = metricsList.indexOf(Collections. min(metricsList));
		System.out.println("Highest Profit Cars : " + mapCars.get(iPIndex).get("make") +"\t ||" + metricsList.get(iPIndex));
	}
	
	public static void startServer() {
		if (!server.isRunning())
			server.start();
		
		WireMock.configureFor(sHost, iPort);
		
		ResponseDefinitionBuilder responseBuilder = new ResponseDefinitionBuilder();
		responseBuilder.withStatus(200);
		responseBuilder.withHeader("Content-Type", "application/json");
		responseBuilder.withBody("{\"Car\":[{\"make\":\"Audi\",\"model\":\"A7\",\"vin\":\"09AGHY64352JITEG98K\",\"metadata\":{\"Color\":\"Black\","
				+ "\"Notes\":\"Scratches on the front side of the car\"},\"perdayrent\":{\"Price\":140,"
				+ "\"Discount\":15},\"metrics\":{\"yoymaintenancecost\":2190.76,\"depreciation\":2256.43,"
				+ "\"rentalcount\":{\"lastweek\":3,\"yeartodate\":221}}},{\"make\":\"Honda\",\"model\":\"Civic\",\"vin\":\"09AGHY64642JITEG98H\","
				+ "\"metadata\":{\"Color\":\"White\",\"Notes\":\"broken mirror\"},\"perdayrent\":{\"Price\""
				+ ":120,\"Discount\":10},\"metrics\":{\"yoymaintenancecost\":4190.06,\"depreciation\""
				+ ":1256.44,\"rentalcount\":{\"lastweek\":7,\"yeartodate\":225}}},{\"make\":\"Tesla\","
				+ "\"model\":\"S\",\"vin\":\"09AGHY64352KITEG98K\",\"metadata\":{\"Color\":\"Blue\","
				+ "\"Notes\":\"Scratches on back of the car\"},\"perdayrent\":{\"Price\":200,"
				+ "\"Discount\":5},\"metrics\":{\"yoymaintenancecost\":2790.45,\"depreciation\":3456.3,"
				+ "\"rentalcount\":{\"lastweek\":6,\"yeartodate\":228}}},{\"make\":\"Tesla\",\"model\":\"3\","
				+ "\"vin\":\"09AGHY64356JITEG98K\",\"metadata\":{\"Color\":\"Blue\",\"Notes\":\"2 Scratches in front, 3 scratches in back \"},"
				+ "\"perdayrent\":{\"Price\":200,\"Discount\":10},\"metrics\":{\"yoymaintenancecost\":2190.2,\"depreciation\":2256.43,"
				+ "\"rentalcount\":{\"lastweek\":4,\"yeartodate\":223}}},{\"make\":\"Tesla\",\"model\":\"3\",\"vin\":\"09AGHY64356JITEG98K\","
				+ "\"metadata\":{\"Color\":\"Black\",\"Notes\":\"Scratches on front of the car\"},\"perdayrent\":{\"Price\":200,\"Discount\":10},"
				+ "\"metrics\":{\"yoymaintenancecost\":4290.43,\"depreciation\":2756.3,\"rentalcount\":{\"lastweek\":6,\"yeartodate\":225}}},{\"make\":"
				+ "\"Ford\",\"model\":\"Escape\",\"vin\":\"09AGHY643345ITEG98K\",\"metadata\":{\"Color\":\"Grey\",\"Notes\":\"10 Scratches\"},"
				+ "\"perdayrent\":{\"Price\":160,\"Discount\":20},\"metrics\":{\"yoymaintenancecost\":1190.67,\"depreciation\":2056.03,\"rentalcount\":{\"lastweek\":2,\"yeartodate\":230}}}]}");
		//responseBuilder.withBodyFile("C:\\Users\\Sarayu.Naresh-HP\\Desktop\\tekarch training\\wiremock\\__files\\get_cars_response.json");
		
		WireMock.stubFor(WireMock.get("/api/cars")
				.willReturn(responseBuilder));
		
	}
	
	public static void stopServer() {
		if(server !=null && server.isRunning()) {
			server.shutdownServer();
		}
	}
}
