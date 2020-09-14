package wiremockproject;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ParsingUsersList {

	Response response;
	String sUrl = "https://jsonplaceholder.typicode.com/posts";
	

	@BeforeMethod
	public void sendRequest() throws Exception
	{
		response = RestAssured.given()
				.when()
				.contentType("application/json")
				.get(new URI(sUrl));

	}

	@Test
	public void parseResponse( )
	{
		//System.out.println("addData response : " + response.asString() + " \n" + response.getStatusCode());
		List<Map<String, Object>> usersList = response.jsonPath().getList("");
		boolean idFound = false;
		System.out.println("********************************************");
		for(Map<String, Object> userMap : usersList) {
			Integer userId = (Integer) userMap.get("userId");
			if (userId.equals(7)){
				idFound = true;
				System.out.println("-->" + userMap.get("title"));
				
			}
		}
		System.out.println("********************************************");
		if(idFound)
			Assert.assertSame(idFound, true, "userid 7 found");
	}

}
