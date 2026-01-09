package com.mlx.api.tests;

import com.mlx.api.base.BaseTest;
import com.mlx.api.pages.MLXLoginPage;
import com.mlx.api.pages.GetUserPage;
import com.mlx.api.utils.ConfigReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * EXAMPLE: How Multiple APIs Work Together
 * 
 * This test demonstrates:
 * 1. Login API (get token)
 * 2. Use that token to call Get User API
 * 
 * Shows how Page Objects are REUSABLE!
 */
public class APIFlowExampleTest extends BaseTest {
    
    // Create page objects for BOTH APIs
    private MLXLoginPage loginPage;
    private GetUserPage getUserPage;
    
    // Store data that flows between APIs
    private String authToken;
    private String userId;
    
    @BeforeClass
    public void setupTest() {
        // Initialize page objects
        loginPage = new MLXLoginPage();
        getUserPage = new GetUserPage();
        
        logger.info("===========================================");
        logger.info("API Flow Example Test - Starting");
        logger.info("===========================================");
    }
    
    /**
     * TEST 1: Complete API Flow Example
     * Step 1: Login → Get Token
     * Step 2: Use Token → Get User Details
     */
    @Test(priority = 1, description = "Complete API Flow: Login → Get User")
    public void testCompleteAPIFlow() {
        test = extent.createTest("API Flow Example", 
                                  "Demonstrates how multiple APIs work together using Page Objects");
        
        // ========== STEP 1: CALL LOGIN API ==========
        test.info("STEP 1: Calling Login API to get authentication token");
        
        String email = ConfigReader.getProperty("testEmail");
        String password = ConfigReader.getProperty("testPassword");
        
        // Use LoginPage object to call login API
        Response loginResponse = loginPage.login(email, password, "web");
        
        // Validate login was successful
        Assert.assertEquals(loginPage.getStatusCode(), 200, "Login should succeed");
        test.pass("✓ Login API called successfully");
        
        // Extract token and userId from login response
        authToken = loginPage.getAuthToken();
        userId = loginPage.getUserId();
        
        Assert.assertNotNull(authToken, "Auth token should be present");
        Assert.assertNotNull(userId, "User ID should be present");
        
        test.pass("✓ Auth Token extracted: " + authToken.substring(0, 20) + "...");
        test.pass("✓ User ID extracted: " + userId);
        
        // ========== STEP 2: USE TOKEN TO CALL GET USER API ==========
        test.info("STEP 2: Using auth token to call Get User Details API");
        
        // Use GetUserPage object to call get user API
        Response getUserResponse = getUserPage.getUserDetails(authToken, userId);
        
        // Validate get user was successful
        Assert.assertEquals(getUserPage.getStatusCode(), 200, "Get User should succeed");
        test.pass("✓ Get User API called successfully");
        
        // Extract user details
        String firstName = getUserPage.getUserFirstName();
        String userEmail = getUserPage.getUserEmail();
        String phone = getUserPage.getUserPhone();
        
        test.pass("✓ User First Name: " + firstName);
        test.pass("✓ User Email: " + userEmail);
        test.pass("✓ User Phone: " + phone);
        
        // Validate email matches login email
        Assert.assertEquals(userEmail, email, "Email should match login email");
        test.pass("✓ Email verification successful");
        
        // ========== SUMMARY ==========
        test.pass("<b>✅ API FLOW COMPLETED SUCCESSFULLY!</b>");
        test.info("Login API → Token → Get User API → User Details");
        
        logger.info("API flow test completed successfully");
    }
    
    /**
     * TEST 2: Reusing the same Page Objects with different data
     */
    @Test(priority = 2, description = "Demonstrates Page Object Reusability")
    public void testPageObjectReusability() {
        test = extent.createTest("Page Object Reusability Demo", 
                                  "Shows how we reuse the same page objects multiple times");
        
        test.info("Using the SAME loginPage object again!");
        
        // First login
        String email1 = ConfigReader.getProperty("testEmail");
        String password1 = ConfigReader.getProperty("testPassword");
        
        loginPage.login(email1, password1, "web");
        Assert.assertEquals(loginPage.getStatusCode(), 200);
        test.pass("✓ First login call successful");
        
        // Second login with same object, different app type
        loginPage.login(email1, password1, "mobile");
        Assert.assertEquals(loginPage.getStatusCode(), 200);
        test.pass("✓ Second login call successful (different app type)");
        
        test.pass("<b>✅ SAME PAGE OBJECT USED MULTIPLE TIMES!</b>");
        test.info("No code duplication - just reuse loginPage.login() method");
        
        logger.info("Reusability test completed");
    }
    
    /**
     * TEST 3: Shows what happens without Page Objects (BAD example)
     */
    @Test(priority = 3, description = "Without Page Objects - Code Duplication Example")
    public void testWithoutPageObjects_BAD_EXAMPLE() {
        test = extent.createTest("WITHOUT Page Objects (Bad Practice)", 
                                  "Shows how messy code gets without POM");
        
        test.info("❌ This is how code looks WITHOUT Page Object Model:");
        
        // Without page objects, you'd write this EVERY time:
        /*
        Map<String, String> body1 = new HashMap<>();
        body1.put("email", "test@test.com");
        body1.put("password", "123");
        Response r1 = RestAssured.given()
            .header("Content-Type", "application/json")
            .body(body1)
            .post("/users/login");
        int status1 = r1.getStatusCode();
        String token1 = r1.jsonPath().getString("data.token");
        
        // Need to login again? DUPLICATE all this code!
        Map<String, String> body2 = new HashMap<>();
        body2.put("email", "test2@test.com");
        body2.put("password", "456");
        Response r2 = RestAssured.given()
            .header("Content-Type", "application/json")
            .body(body2)
            .post("/users/login");
        int status2 = r2.getStatusCode();
        String token2 = r2.jsonPath().getString("data.token");
        
        // Imagine doing this 50 times! 😱
        */
        
        test.info("Problems:");
        test.info("• Code duplication everywhere");
        test.info("• Hard to maintain");
        test.info("• Difficult to read");
        test.info("• Error-prone");
        
        test.info("✅ With Page Objects:");
        test.info("• loginPage.login(email, password, 'web') - ONE LINE!");
        test.info("• Reuse anywhere");
        test.info("• Easy to maintain");
        test.info("• Clean and readable");
        
        test.pass("Understanding: Page Objects save time and effort!");
    }
}
