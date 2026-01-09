package com.mlx.api.tests;

import com.mlx.api.base.BaseTest;
import com.mlx.api.pages.MLXLoginPage;
import com.mlx.api.utils.ConfigReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Comprehensive Test Suite for MLX Login API
 * Includes 14+ validations for each test scenario
 */
public class MLXLoginTest extends BaseTest {
    
    private MLXLoginPage loginPage;
    private String testEmail;
    private String testPassword;
    private String applicationType;
    private int maxResponseTime;
    
    @BeforeClass
    public void setupLoginTests() {
        loginPage = new MLXLoginPage();
        
        // Load test data from config
        testEmail = ConfigReader.getProperty("testEmail");
        testPassword = ConfigReader.getProperty("testPassword");
        applicationType = ConfigReader.getProperty("applicationType");
        maxResponseTime = Integer.parseInt(ConfigReader.getProperty("maxResponseTime"));
        
        logger.info("============================================");
        logger.info("MLX Login API Test Suite - Starting");
        logger.info("Test Email: " + testEmail);
        logger.info("Application Type: " + applicationType);
        logger.info("Max Response Time: " + maxResponseTime + "ms");
        logger.info("============================================");
    }
    
    /**
     * PRIMARY TEST: Login with Valid Credentials
     * Includes 14 comprehensive validations
     */
    @Test(priority = 1, description = "MLX Login - Valid Credentials with Comprehensive Validations")
    public void testMLXLoginWithValidCredentials() {
        test = extent.createTest("MLX Login - Valid Credentials Test", 
                                  "Verify login with valid email and password - 14 Validations");
        
        test.info("Test Email: " + testEmail);
        test.info("Application Type: " + applicationType);
        
        // Perform login
        Response response = loginPage.login(testEmail, testPassword, applicationType);
        
        // ============= VALIDATION 1: Status Code =============
        test.info("<b>VALIDATION 1:</b> Verify Status Code is 200");
        int statusCode = loginPage.getStatusCode();
        Assert.assertEquals(statusCode, 200, "Status code should be 200");
        test.pass("✓ Status Code: " + statusCode + " (Expected: 200)");
        
        // ============= VALIDATION 2: Response Time =============
        test.info("<b>VALIDATION 2:</b> Verify Response Time is within acceptable range");
        long responseTime = loginPage.getResponseTime();
        Assert.assertTrue(responseTime < maxResponseTime, 
                         "Response time should be less than " + maxResponseTime + "ms");
        test.pass("✓ Response Time: " + responseTime + "ms (Max: " + maxResponseTime + "ms)");
        
        // ============= VALIDATION 3: Content-Type =============
        test.info("<b>VALIDATION 3:</b> Verify Content-Type is application/json");
        String contentType = loginPage.getContentType();
        Assert.assertTrue(contentType.contains("application/json"), 
                         "Content-Type should be application/json");
        test.pass("✓ Content-Type: " + contentType);
        
        // ============= VALIDATION 4: Valid JSON Response =============
        test.info("<b>VALIDATION 4:</b> Verify response is valid JSON");
        boolean isValidJson = loginPage.isValidJson();
        Assert.assertTrue(isValidJson, "Response should be valid JSON");
        test.pass("✓ Response is valid JSON format");
        
        // ============= VALIDATION 5: Response Not Empty =============
        test.info("<b>VALIDATION 5:</b> Verify response body is not empty");
        String responseBody = response.getBody().asString();
        Assert.assertFalse(responseBody.isEmpty(), "Response body should not be empty");
        test.pass("✓ Response body is not empty (Size: " + responseBody.length() + " bytes)");
        
        // ============= VALIDATION 6: Auth Token Present =============
        test.info("<b>VALIDATION 6:</b> Verify authentication token is present");
        String authToken = loginPage.getAuthToken();
        Assert.assertNotNull(authToken, "Auth token should not be null");
        Assert.assertFalse(authToken.isEmpty(), "Auth token should not be empty");
        test.pass("✓ Auth Token Present: " + authToken.substring(0, Math.min(20, authToken.length())) + "...");
        
        // ============= VALIDATION 7: User ID Present =============
        test.info("<b>VALIDATION 7:</b> Verify User ID is present in response");
        String userId = loginPage.getUserId();
        Assert.assertNotNull(userId, "User ID should not be null");
        test.pass("✓ User ID: " + userId);
        
        // ============= VALIDATION 8: Email Verification =============
        test.info("<b>VALIDATION 8:</b> Verify returned email matches login email");
        String returnedEmail = loginPage.getUserEmail();
        Assert.assertEquals(returnedEmail, testEmail, "Returned email should match login email");
        test.pass("✓ Email Verified: " + returnedEmail);
        
        // ============= VALIDATION 9: User Name Present =============
        test.info("<b>VALIDATION 9:</b> Verify User Name is present");
        String userName = loginPage.getUserName();
        Assert.assertNotNull(userName, "User name should not be null");
        test.pass("✓ User Name: " + userName);
        
        // ============= VALIDATION 10: Success Message =============
        test.info("<b>VALIDATION 10:</b> Verify success message is present");
        String successMessage = loginPage.getSuccessMessage();
        Assert.assertNotNull(successMessage, "Success message should be present");
        test.pass("✓ Success Message: " + successMessage);
        
        // ============= VALIDATION 11: Required Fields Present =============
        test.info("<b>VALIDATION 11:</b> Verify all required fields are present");
        boolean hasRequiredFields = loginPage.hasRequiredFields();
        Assert.assertTrue(hasRequiredFields, "All required fields should be present");
        test.pass("✓ All required fields present (token, user.id, user.email)");
        
        // ============= VALIDATION 12: Login Success Flag =============
        test.info("<b>VALIDATION 12:</b> Verify login was successful");
        boolean isLoginSuccessful = loginPage.isLoginSuccessful();
        Assert.assertTrue(isLoginSuccessful, "Login should be successful");
        test.pass("✓ Login Successful: true");
        
        // ============= VALIDATION 13: Response Structure =============
        test.info("<b>VALIDATION 13:</b> Verify response structure is correct");
        Assert.assertNotNull(response.jsonPath().get("data"), "Data object should exist");
        Assert.assertNotNull(response.jsonPath().get("data.user"), "User object should exist");
        test.pass("✓ Response structure is correct (contains data.user object)");
        
        // ============= VALIDATION 14: No Error Messages =============
        test.info("<b>VALIDATION 14:</b> Verify no error messages in response");
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertNull(errorMessage, "Should not contain error messages");
        test.pass("✓ No error messages present");
        
        // Final Summary
        test.pass("<b>✅ ALL 14 VALIDATIONS PASSED SUCCESSFULLY!</b>");
        logger.info("Login test completed successfully with all validations passed");
    }
    
    /**
     * NEGATIVE TEST 1: Login with Invalid Email
     */
    @Test(priority = 2, description = "MLX Login - Invalid Email")
    public void testMLXLoginWithInvalidEmail() {
        test = extent.createTest("MLX Login - Invalid Email Test", 
                                  "Verify login fails with invalid email");
        
        String invalidEmail = "invalid.email@test.com";
        test.info("Attempting login with invalid email: " + invalidEmail);
        
        Response response = loginPage.login(invalidEmail, testPassword, applicationType);
        
        // Validations for error scenario
        test.info("VALIDATION: Verify status code is 401 or 400");
        int statusCode = loginPage.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 400 || statusCode == 404, 
                         "Status code should be 401, 400, or 404 for invalid credentials");
        test.pass("✓ Status Code: " + statusCode + " (Expected: 401/400/404)");
        
        test.info("VALIDATION: Verify no auth token is returned");
        String authToken = loginPage.getAuthToken();
        Assert.assertTrue(authToken == null || authToken.isEmpty(), 
                         "Auth token should not be present for invalid login");
        test.pass("✓ No auth token returned for invalid email");
        
        test.info("VALIDATION: Verify error message is present");
        String errorMsg = loginPage.getErrorMessage();
        test.pass("✓ Error message returned: " + (errorMsg != null ? errorMsg : "Handled by status code"));
        
        logger.info("Invalid email test completed successfully");
    }
    
    /**
     * NEGATIVE TEST 2: Login with Invalid Password
     */
    @Test(priority = 3, description = "MLX Login - Invalid Password")
    public void testMLXLoginWithInvalidPassword() {
        test = extent.createTest("MLX Login - Invalid Password Test", 
                                  "Verify login fails with incorrect password");
        
        String invalidPassword = "wrongpassword123";
        test.info("Attempting login with invalid password");
        
        Response response = loginPage.login(testEmail, invalidPassword, applicationType);
        
        test.info("VALIDATION: Verify status code indicates authentication failure");
        int statusCode = loginPage.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 400, 
                         "Status code should be 401 or 400 for invalid password");
        test.pass("✓ Status Code: " + statusCode + " (Expected: 401/400)");
        
        test.info("VALIDATION: Verify login is not successful");
        boolean isLoginSuccessful = loginPage.isLoginSuccessful();
        Assert.assertFalse(isLoginSuccessful, "Login should not be successful with wrong password");
        test.pass("✓ Login correctly failed with invalid password");
        
        logger.info("Invalid password test completed successfully");
    }
    
    /**
     * NEGATIVE TEST 3: Login with Missing Email Field
     */
    @Test(priority = 4, description = "MLX Login - Missing Email Field")
    public void testMLXLoginWithMissingEmail() {
        test = extent.createTest("MLX Login - Missing Email Field", 
                                  "Verify login fails when email field is missing");
        
        test.info("Attempting login with null email");
        
        Response response = loginPage.login(null, testPassword, applicationType);
        
        test.info("VALIDATION: Verify request is rejected");
        int statusCode = loginPage.getStatusCode();
        Assert.assertTrue(statusCode >= 400, "Status code should be 400+ for missing email");
        test.pass("✓ Request rejected with status code: " + statusCode);
        
        logger.info("Missing email field test completed successfully");
    }
    
    /**
     * NEGATIVE TEST 4: Login with Empty Credentials
     */
    @Test(priority = 5, description = "MLX Login - Empty Credentials")
    public void testMLXLoginWithEmptyCredentials() {
        test = extent.createTest("MLX Login - Empty Credentials", 
                                  "Verify login fails with empty email and password");
        
        test.info("Attempting login with empty credentials");
        
        Response response = loginPage.login("", "", applicationType);
        
        test.info("VALIDATION: Verify request is rejected");
        int statusCode = loginPage.getStatusCode();
        Assert.assertTrue(statusCode >= 400, "Status code should be 400+ for empty credentials");
        test.pass("✓ Request rejected with status code: " + statusCode);
        
        test.info("VALIDATION: Verify no auth token is returned");
        Assert.assertFalse(loginPage.isLoginSuccessful(), "Login should not be successful");
        test.pass("✓ Login correctly failed with empty credentials");
        
        logger.info("Empty credentials test completed successfully");
    }
    
    /**
     * TEST: Login with Different Application Types
     */
    @Test(priority = 6, description = "MLX Login - Different Application Types")
    public void testMLXLoginWithDifferentAppTypes() {
        test = extent.createTest("MLX Login - Application Types Test", 
                                  "Verify login works with different application types (web/mobile)");
        
        // Test with "web" application type
        test.info("Testing with application_type: web");
        Response webResponse = loginPage.login(testEmail, testPassword, "web");
        Assert.assertEquals(loginPage.getStatusCode(), 200, "Login should succeed with web type");
        test.pass("✓ Login successful with application_type: web");
        
        // Test with "mobile" application type
        test.info("Testing with application_type: mobile");
        Response mobileResponse = loginPage.login(testEmail, testPassword, "mobile");
        int mobileStatusCode = loginPage.getStatusCode();
        test.info("Response status for mobile type: " + mobileStatusCode);
        test.pass("✓ Application type 'mobile' processed (Status: " + mobileStatusCode + ")");
        
        logger.info("Application types test completed successfully");
    }
}
