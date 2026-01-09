package com.mlx.api.pages;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Page Object Model for MLX Login API
 * Endpoint: https://staging-api-mlx.labsquire.com/users/login
 */
public class MLXLoginPage extends BasePage {
    
    private static final Logger logger = LogManager.getLogger(MLXLoginPage.class);
    private Response response;
    
    // API Endpoint
    private static final String LOGIN_ENDPOINT = "/users/login";
    
    /**
     * Perform login with email and password
     * @param email User email
     * @param password User password
     * @param applicationType Application type (web/mobile)
     * @return Response object
     */
    public Response login(String email, String password, String applicationType) {
        logger.info("Attempting login with email: " + email + " and application type: " + applicationType);
        
        // Create request payload
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("application_type", applicationType);
        
        // Build request with all headers from curl command
        RequestSpecification request = RestAssured.given()
            .header("accept", "application/json, text/plain, */*")
            .header("accept-language", "en-GB,en;q=0.9,en-US;q=0.8,en-IN;q=0.7")
            .header("content-type", "application/json")
            .header("origin", "https://staging-mlx.labsquire.com")
            .header("priority", "u=1, i")
            .header("referer", "https://staging-mlx.labsquire.com/")
            .header("sec-ch-ua", "\"Microsoft Edge\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
            .header("sec-ch-ua-mobile", "?0")
            .header("sec-ch-ua-platform", "\"Windows\"")
            .header("sec-fetch-dest", "empty")
            .header("sec-fetch-mode", "cors")
            .header("sec-fetch-site", "same-site")
            .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0")
            .body(requestBody)
            .log().all();
        
        // Send POST request
        response = request.post(LOGIN_ENDPOINT);
        
        // Log response
        logger.info("Response Status Code: " + response.getStatusCode());
        logger.info("Response Body: " + response.getBody().asString());
        response.then().log().all();
        
        return response;
    }
    
    /**
     * Login with default application type (web)
     */
    public Response login(String email, String password) {
        return login(email, password, "web");
    }
    
    /**
     * Get the current response object
     */
    public Response getResponse() {
        return response;
    }
    
    // ============= Response Validation Methods =============
    
    /**
     * Check if login was successful
     */
    public boolean isLoginSuccessful() {
        try {
            return response.getStatusCode() == 200 && 
                   response.jsonPath().getString("data.token") != null;
        } catch (Exception e) {
            logger.error("Error checking login success: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get authentication token from response
     */
    public String getAuthToken() {
        try {
            return response.jsonPath().getString("data.token");
        } catch (Exception e) {
            logger.error("Error extracting auth token: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get user ID from response
     */
    public String getUserId() {
        try {
            return response.jsonPath().getString("data.user._id");
        } catch (Exception e) {
            logger.error("Error extracting user ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get user email from response
     */
    public String getUserEmail() {
        try {
            return response.jsonPath().getString("data.user.email");
        } catch (Exception e) {
            logger.error("Error extracting user email: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get user name from response
     */
    public String getUserName() {
        try {
            String firstName = response.jsonPath().getString("data.user.first_name");
            String lastName = response.jsonPath().getString("data.user.last_name");
            return firstName + " " + lastName;
        } catch (Exception e) {
            logger.error("Error extracting user name: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get success message from response
     */
    public String getSuccessMessage() {
        try {
            return response.jsonPath().getString("message");
        } catch (Exception e) {
            logger.error("Error extracting success message: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get error message from response
     */
    public String getErrorMessage() {
        try {
            return response.jsonPath().getString("error");
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Verify if response contains required fields
     */
    public boolean hasRequiredFields() {
        try {
            return getAuthToken() != null &&
                   getUserId() != null &&
                   getUserEmail() != null;
        } catch (Exception e) {
            logger.error("Error checking required fields: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get response time in milliseconds
     */
    public long getResponseTime() {
        return response.getTime();
    }
    
    /**
     * Get status code
     */
    public int getStatusCode() {
        return response.getStatusCode();
    }
    
    /**
     * Get content type
     */
    public String getContentType() {
        return response.getContentType();
    }
    
    /**
     * Validate response is JSON
     */
    public boolean isValidJson() {
        try {
            response.jsonPath().prettify();
            return true;
        } catch (Exception e) {
            logger.error("Invalid JSON response: " + e.getMessage());
            return false;
        }
    }
}
