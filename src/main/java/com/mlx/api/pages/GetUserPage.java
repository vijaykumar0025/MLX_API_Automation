package com.mlx.api.pages;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SIMPLE EXAMPLE: Get User Details API
 * 
 * API: GET https://staging-api-mlx.labsquire.com/users/{userId}
 * Purpose: Demonstrates how we reuse the same pattern for different APIs
 */
public class GetUserPage extends BasePage {
    
    private static final Logger logger = LogManager.getLogger(GetUserPage.class);
    private Response response;
    
    // API Endpoint
    private static final String GET_USER_ENDPOINT = "/users/{userId}";
    
    /**
     * METHOD 1: Call the API to get user details
     * @param authToken - JWT token from login
     * @param userId - User ID to fetch
     * @return Response object
     */
    public Response getUserDetails(String authToken, String userId) {
        logger.info("Fetching user details for User ID: " + userId);
        
        // Build the API request
        response = RestAssured.given()
            .header("Authorization", "Bearer " + authToken)  // Need auth token!
            .header("Content-Type", "application/json")
            .pathParam("userId", userId)  // Replace {userId} in URL
            .log().all()
        
        // Send GET request
        .get(GET_USER_ENDPOINT);
        
        // Log response
        logger.info("Response Status: " + response.getStatusCode());
        response.then().log().all();
        
        return response;
    }
    
    /**
     * METHOD 2: Get user's first name from response
     */
    public String getUserFirstName() {
        return response.jsonPath().getString("data.user.first_name");
    }
    
    /**
     * METHOD 3: Get user's email from response
     */
    public String getUserEmail() {
        return response.jsonPath().getString("data.user.email");
    }
    
    /**
     * METHOD 4: Get user's phone from response
     */
    public String getUserPhone() {
        return response.jsonPath().getString("data.user.phone");
    }
    
    /**
     * METHOD 5: Get status code
     */
    public int getStatusCode() {
        return response.getStatusCode();
    }
    
    /**
     * METHOD 6: Check if user exists
     */
    public boolean userExists() {
        return getStatusCode() == 200 && 
               response.jsonPath().get("data.user") != null;
    }
}
