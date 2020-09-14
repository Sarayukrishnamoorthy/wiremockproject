package wiremockproject;

import java.net.URI;
import java.util.List;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ParseResponseUsingWiremock {
	private static final int iPort = 8080;
	private static final String sHost = "localhost";
	private static WireMockServer server = null;
	
	
	public static void main(String[] args)  {
		try {			
			// using wiremockapi
			server = new WireMockServer(iPort);
			startServer();
			Response res = sendRequest("http://localhost:8080/api/user");
			parseWiremockResponse(res);
		
		} catch(Exception e) {
			 e.printStackTrace();
			stopServer();
		}
		stopServer();
	}
	
	public static void startServer() {
		if (!server.isRunning())
			server.start();
		
		WireMock.configureFor(sHost, iPort);
		
		ResponseDefinitionBuilder responseBuilder = new ResponseDefinitionBuilder();
		responseBuilder.withStatus(200);
		responseBuilder.withHeader("Content-Type", "application/json");
		responseBuilder.withBodyFile("response1.json");

		
		WireMock.stubFor(WireMock.get("/api/user")
				.willReturn(responseBuilder));
		
	}
	
	public static void stopServer() {
		if(server !=null && server.isRunning()) {
			server.shutdownServer();
		}
	}
	
	public static Response sendRequest(String sUrl) throws Exception
	{
		return   RestAssured.given()
				.when()
				.contentType("application/json")
				.get(new URI(sUrl));
			
	}
	
	public static void parseWiremockResponse(Response response) {
		//System.out.println("addData response : " + response.asString() + " \n" + response.getStatusCode());
		List<Map<String, Object>> studentsList = response.jsonPath().getList("students");
		System.out.println(studentsList.get(0).get("contact"));
		List<Integer> sSessionId = response.jsonPath().getList("sessionid");
		System.out.println("1.username : "+response.jsonPath().getString("username"));
		System.out.println("2.all SessionIds" +sSessionId.toString()+"\n3.sessionid last value : " + sSessionId.get(sSessionId.size() - 1));
		System.out.println("4.2nd stud, marks : " + studentsList.get(1).get("marks"));
		System.out.println("5.second state of first stud: "+ response.jsonPath().getString("students[0].adresss[1].state"));
		System.out.println("6.second student, second contact," + response.jsonPath().getString("students[1].contact[1]"));
		System.out.println("7.2nd stud, all cities "+ response.jsonPath().getList("students[1].adresss.city"));
		System.out.println("8.stud all contacts : " +response.jsonPath().getList("students.contact"));
		System.out.println("9.1st stud addr : " + studentsList.get(0).get("adresss"));
	}
}

