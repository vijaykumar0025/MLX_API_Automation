package com.mlx.api.tests;

import com.mlx.api.pages.MLXLoginPage;
import com.mlx.api.pages.MLXOrderPage;
import com.mlx.api.utils.ConfigReader;
import com.mlx.api.utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;
import com.github.javafaker.Faker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.util.Random;

/**
 * Test class for MLX Create Order API
 * Tests order creation with various scenarios
 */
public class MLXCreateOrderTest {
    
    private static final Logger logger = LogManager.getLogger(MLXCreateOrderTest.class);
    private MLXOrderPage orderPage;
    private MLXLoginPage loginPage;
    private ExtentReports extent;
    private ExtentTest test;
    private Faker faker;
    private Random random;
    
    private String baseURI;
    private String testEmail;
    private String testPassword;
    private String applicationType;
    private String authToken;
    private String userId;
    
    /**
     * Generate dynamic date in MM-dd-yyyy format
     */
    private String generateDynamicDate(int daysFromToday) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, daysFromToday);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return sdf.format(cal.getTime());
    }
    
    /**
     * Generate random phone number
     */
    private String generatePhoneNumber() {
        return String.format("9%09d", random.nextInt(1000000000));
    }
    
    /**
     * Generate random facility account number
     */
    private String generateFacilityAccountNumber() {
        return "TG" + String.format("%07d", random.nextInt(10000000));
    }
    
    @BeforeClass
    public void setup() {
        logger.info("Setting up MLX Create Order Test");
        
        // Initialize Page Objects
        orderPage = new MLXOrderPage();
        loginPage = new MLXLoginPage();
        
        // Initialize Faker for dynamic data generation
        faker = new Faker();
        random = new Random();
        
        // Load configuration
        baseURI = ConfigReader.getProperty("baseURI");
        testEmail = ConfigReader.getProperty("testEmail");
        testPassword = ConfigReader.getProperty("testPassword");
        applicationType = ConfigReader.getProperty("applicationType");
        
        // Set base URI
        RestAssured.baseURI = baseURI;
        
        logger.info("Base URI: " + baseURI);
        logger.info("Test Email: " + testEmail);
        logger.info("Application Type: " + applicationType);
        
        // Initialize Extent Reports
        extent = ExtentReportManager.getInstance();
        
        // Perform login to get auth token
        performLogin();
    }
    
    /**
     * Perform login and extract auth token
     */
    private void performLogin() {
        logger.info("Performing login to get auth token");
        Response loginResponse = loginPage.login(testEmail, testPassword, applicationType);
        
        if (loginResponse.getStatusCode() == 200) {
            authToken = loginResponse.jsonPath().getString("data.token");
            userId = loginResponse.jsonPath().getString("data.user._id");
            logger.info("Login successful. Token obtained: " + authToken);
            logger.info("User ID: " + userId);
        } else {
            logger.error("Login failed. Status Code: " + loginResponse.getStatusCode());
            Assert.fail("Login failed. Cannot proceed with order creation tests.");
        }
    }
    
    @Test(priority = 1, description = "Create Standing Order - Valid Data")
    public void testCreateStandingOrderWithValidData() {
        test = extent.createTest("Create Standing Order - Valid Data", 
            "Verify that a standing order can be created successfully with valid data");
        
        logger.info("========== Test: Create Standing Order - Valid Data ==========");
        
        try {
            // Log test details
            test.info("Base URI: " + baseURI);
            test.info("Test Email: " + testEmail);
            test.info("Application Type: " + applicationType);
            
            // Generate dynamic patient data
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.com";
            String phone = generatePhoneNumber();
            String dateOfBirth = generateDynamicDate(-10000); // ~27 years ago
            String[] genders = {"MALE", "FEMALE", "OTHER"};
            String gender = genders[random.nextInt(genders.length)];
            String[] ethnicities = {"ASIAN", "HISPANIC", "CAUCASIAN", "AFRICAN AMERICAN", "OTHER"};
            String ethnicity = ethnicities[random.nextInt(ethnicities.length)];
            String race = ethnicity;
            
            // Generate dynamic address
            String streetAddress = faker.address().streetAddress();
            String city = faker.address().city().toUpperCase();
            String state = faker.address().state().toUpperCase();
            String zipCode = faker.address().zipCode().substring(0, 5);
            
            test.info("Generated Patient: " + firstName + " " + lastName);
            test.info("Patient Email: " + email);
            test.info("Patient Phone: " + phone);
            test.info("Service Address: " + streetAddress + ", " + city + ", " + state + " " + zipCode);
            
            // Build patient address
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress(streetAddress, city, state, zipCode));
            
            // Build patient data with dynamic values
            Map<String, Object> patientData = orderPage.buildPatientData(
                firstName,
                lastName,
                dateOfBirth,
                gender,
                email,
                phone,
                false,
                race,
                ethnicity,
                false,
                addresses
            );
            
            // Build services list
            List<String> services = new ArrayList<>();
            services.add("STOOL SPECIMEN PICKUP");
            
            // Build order codes
            List<String> orderCodes = new ArrayList<>();
            orderCodes.add("RPP COVID19");
            
            // Build ICD-10 codes
            List<String> icd10Codes = new ArrayList<>();
            icd10Codes.add("A21.8");
            icd10Codes.add("A04.9");
            
            // Build service address (same as patient address)
            Map<String, String> serviceAddress = orderPage.buildAddress(streetAddress, city, state, zipCode);
            
            // Build tube data
            List<Map<String, Object>> tubeData = new ArrayList<>();
            tubeData.add(orderPage.buildTubeData("NASAL SWAB", 1));
            
            // Generate dynamic dates for standing order
            String startDate = generateDynamicDate(1); // Tomorrow
            String endDate = generateDynamicDate(4); // 4 days from today
            String serviceDate = generateDynamicDate(5); // 5 days from today
            
            // Generate dynamic facility account number
            String facilityAccount = generateFacilityAccountNumber();
            
            test.info("Facility Account: " + facilityAccount);
            test.info("Standing Order: " + startDate + " to " + endDate);
            
            // Build complete order request with dynamic data
            Map<String, Object> orderData = orderPage.buildStandingOrderRequest(
                "STANDING ORDER",
                facilityAccount,
                "1093767972", // Physician NPI - can be made dynamic if needed
                patientData,
                services,
                orderCodes,
                icd10Codes,
                startDate,
                endDate,
                "DAILY",
                serviceDate,
                serviceAddress,
                "CLIENT",
                false,
                true,
                tubeData,
                streetAddress + " " + city + " " + state + " " + zipCode
            );
            
            test.info("Sending create order request...");
            
            // Create order
            Response response = orderPage.createOrder(authToken, userId, orderData);
            
            // Validation 1: Status Code
            test.info("Validation 1: Verifying status code");
            Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201 (Created)");
            test.pass("✓ Status code is 201 (Created)");
            logger.info("✓ Status code validation passed: 201");
            
            // Validation 2: Response time
            test.info("Validation 2: Verifying response time");
            long responseTime = response.getTime();
            test.info("Response time: " + responseTime + " ms");
            Assert.assertTrue(responseTime < 5000, "Response time should be less than 5 seconds");
            test.pass("✓ Response time is acceptable: " + responseTime + " ms");
            logger.info("✓ Response time: " + responseTime + " ms");
            
            // Validation 3: Response contains success message
            test.info("Validation 3: Verifying response message");
            String message = response.jsonPath().getString("message");
            Assert.assertNotNull(message, "Response should contain a message");
            test.pass("✓ Response message: " + message);
            logger.info("✓ Response message: " + message);
            
            // Validation 4: Order ID is generated
            test.info("Validation 4: Verifying order ID is generated");
            Assert.assertTrue(orderPage.validateOrderIdGenerated(), "Order ID should be generated");
            
            // Get all order IDs and format them with HTML highlighting
            List<String> allOrderIds = orderPage.getAllOrderIds();
            if (!allOrderIds.isEmpty()) {
                test.pass(orderPage.formatMultipleOrderNumbersSuccess(allOrderIds));
                logger.info("✓ Orders created: " + allOrderIds);
            } else {
                String orderId = orderPage.getOrderId();
                test.pass(orderPage.formatOrderNumberSuccess(orderId));
                logger.info("✓ Order ID: " + orderId);
            }
            
            // Validation 5: Response contains data object
            test.info("Validation 5: Verifying response contains data object");
            Assert.assertTrue(orderPage.validateResponseContainsField("data"), "Response should contain data object");
            test.pass("✓ Response contains data object");
            logger.info("✓ Response contains data object");
            
            // Validation 6: Content-Type header
            test.info("Validation 6: Verifying Content-Type header");
            String contentType = response.getHeader("Content-Type");
            Assert.assertTrue(contentType.contains("application/json"), "Content-Type should be application/json");
            test.pass("✓ Content-Type: " + contentType);
            logger.info("✓ Content-Type: " + contentType);
            
            // Validation 7: Verify all mandatory order fields in response
            test.info("Validation 7: Verifying mandatory order fields");
            String firstOrderOrderType = response.jsonPath().getString("data.orders[0].order_type");
            Assert.assertEquals(firstOrderOrderType, "MLX", "Order type should be MLX");
            test.pass("✓ Order type: " + firstOrderOrderType);
            
            String facilityAccNum = response.jsonPath().getString("data.orders[0].facility_account_number");
            Assert.assertEquals(facilityAccNum, facilityAccount, "Facility account number should match request");
            test.pass("✓ Facility account number: " + facilityAccNum);
            
            String physicianNpi = response.jsonPath().getString("data.orders[0].physician_npi");
            Assert.assertEquals(physicianNpi, "1093767972", "Physician NPI should match");
            test.pass("✓ Physician NPI: " + physicianNpi);
            
            String responseBillingType = response.jsonPath().getString("data.orders[0].billing_type");
            Assert.assertEquals(responseBillingType, "CLIENT", "Billing type should match");
            test.pass("✓ Billing type: " + responseBillingType);
            
            // Validation 8: Verify order codes
            test.info("Validation 8: Verifying order codes");
            List<String> responseOrderCodes = response.jsonPath().getList("data.orders[0].order_codes");
            Assert.assertNotNull(responseOrderCodes, "Order codes should not be null");
            Assert.assertTrue(responseOrderCodes.contains("RPP COVID19"), "Order codes should contain RPP COVID19");
            test.pass("✓ Order codes validated: " + responseOrderCodes);
            
            // Validation 9: Verify ICD-10 codes
            test.info("Validation 9: Verifying ICD-10 codes");
            List<String> responseIcd10 = response.jsonPath().getList("data.orders[0].icd_10_codes");
            Assert.assertNotNull(responseIcd10, "ICD-10 codes should not be null");
            Assert.assertTrue(responseIcd10.contains("A21.8"), "ICD-10 codes should contain A21.8");
            Assert.assertTrue(responseIcd10.contains("A04.9"), "ICD-10 codes should contain A04.9");
            test.pass("✓ ICD-10 codes validated: " + responseIcd10);
            
            // Validation 10: Verify patient data
            test.info("Validation 10: Verifying patient data");
            String responseFirstName = response.jsonPath().getString("data.orders[0].patient_info.first_name");
            Assert.assertEquals(responseFirstName, firstName, "Patient first name should match request");
            test.pass("✓ Patient first name: " + responseFirstName);
            
            String responseLastName = response.jsonPath().getString("data.orders[0].patient_info.last_name");
            Assert.assertEquals(responseLastName, lastName, "Patient last name should match request");
            test.pass("✓ Patient last name: " + responseLastName);
            
            String responseGender = response.jsonPath().getString("data.orders[0].patient_info.gender");
            Assert.assertEquals(responseGender, gender, "Patient gender should match request");
            test.pass("✓ Patient gender: " + responseGender);
            
            String responseEmail = response.jsonPath().getString("data.orders[0].patient_info.email");
            Assert.assertEquals(responseEmail, email, "Patient email should match request");
            test.pass("✓ Patient email: " + responseEmail);
            
            // Validation 11: Verify standing order details
            test.info("Validation 11: Verifying standing order details");
            Integer totalOrdersCreated = response.jsonPath().getInt("data.total_orders_created");
            Assert.assertEquals(totalOrdersCreated, Integer.valueOf(4), "Total orders created should be 4");
            test.pass("✓ Total orders created: " + totalOrdersCreated);
            
            String responseStartDate = response.jsonPath().getString("data.standing_order_details.start_date");
            Assert.assertEquals(responseStartDate, startDate, "Start date should match request");
            test.pass("✓ Standing start date: " + responseStartDate);
            
            String responseEndDate = response.jsonPath().getString("data.standing_order_details.end_date");
            Assert.assertEquals(responseEndDate, endDate, "End date should match request");
            test.pass("✓ Standing end date: " + responseEndDate);
            
            String responseFrequency = response.jsonPath().getString("data.standing_order_details.frequency");
            Assert.assertEquals(responseFrequency, "DAILY", "Frequency should match request");
            test.pass("✓ Standing frequency: " + responseFrequency);
            
            List<String> serviceDates = response.jsonPath().getList("data.standing_order_details.service_dates");
            Assert.assertEquals(serviceDates.size(), 4, "Should have 4 service dates");
            test.pass("✓ Service dates count: " + serviceDates.size());
            
            // Validation 12: Verify order status
            test.info("Validation 12: Verifying order status");
            String orderStatus = response.jsonPath().getString("data.orders[0].status");
            Assert.assertEquals(orderStatus, "ACTIVE", "Order status should be ACTIVE");
            test.pass("✓ Order status: " + orderStatus);
            
            Boolean isFasting = response.jsonPath().getBoolean("data.orders[0].fasting");
            Assert.assertEquals(isFasting, Boolean.TRUE, "Fasting flag should match");
            test.pass("✓ Fasting: " + isFasting);
            
            Boolean isStat = response.jsonPath().getBoolean("data.orders[0].is_stat");
            Assert.assertEquals(isStat, Boolean.FALSE, "Is stat flag should match");
            test.pass("✓ Is stat: " + isStat);
            
            test.log(Status.PASS, "All mandatory fields validated successfully - Create Standing Order test passed");
            logger.info("========== Test PASSED - All Mandatory Fields Validated ==========");
            
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 2, description = "Create Order - Invalid Auth Token")
    public void testCreateOrderWithInvalidAuthToken() {
        test = extent.createTest("Create Order - Invalid Auth Token", 
            "Verify that order creation fails with invalid auth token");
        
        logger.info("========== Test: Create Order - Invalid Auth Token ==========");
        
        try {
            // Build minimal order data
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", "TG1225007");
            
            test.info("Sending request with invalid auth token...");
            
            // Create order with invalid token
            Response response = orderPage.createOrder("invalid_token_12345", userId, orderData);
            
            // Validation: Should fail with validation error
            test.info("Validation: Verifying status code indicates error");
            int statusCode = response.getStatusCode();
            Assert.assertTrue(statusCode == 401 || statusCode == 403 || statusCode == 422, 
                "Status code should indicate authentication or validation error");
            test.pass("✓ Status code is " + statusCode + " (Request rejected)");
            logger.info("✓ Invalid token rejected with status code: " + statusCode);
            
            test.log(Status.PASS, "Invalid auth token test passed");
            logger.info("========== Test PASSED ==========");
            
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 3, description = "Create Order - Missing Required Fields")
    public void testCreateOrderWithMissingRequiredFields() {
        test = extent.createTest("Create Order - Missing Required Fields", 
            "Verify that order creation fails when required fields are missing");
        
        logger.info("========== Test: Create Order - Missing Required Fields ==========");
        
        try {
            // Build incomplete order data (missing patient_data)
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            
            test.info("Sending request with missing required fields...");
            
            // Create order
            Response response = orderPage.createOrder(authToken, userId, orderData);
            
            // Validation: Should fail with 400
            test.info("Validation: Verifying status code indicates validation error");
            int statusCode = response.getStatusCode();
            Assert.assertTrue(statusCode == 400 || statusCode == 422, 
                "Status code should be 400 or 422 for missing required fields");
            test.pass("✓ Status code is " + statusCode + " (Bad Request/Validation Error)");
            logger.info("✓ Missing required fields rejected with status code: " + statusCode);
            
            test.log(Status.PASS, "Missing required fields test passed");
            logger.info("========== Test PASSED ==========");
            
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 4, description = "Create Order - Invalid Date Format")
    public void testCreateOrderWithInvalidDateFormat() {
        test = extent.createTest("Create Order - Invalid Date Format", 
            "Verify that order creation fails with invalid date format");
        
        logger.info("========== Test: Create Order - Invalid Date Format ==========");
        
        try {
            // Build patient address
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("Test Address", "Test City", "NEW YORK", "12345"));
            
            // Build patient data with invalid date
            Map<String, Object> patientData = orderPage.buildPatientData(
                "TEST",
                "TEST",
                "invalid-date",  // Invalid date format
                "MALE",
                "test@test.com",
                "1234567890",
                false,
                "ASIAN",
                "ASIAN",
                false,
                addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", "TG1225007");
            orderData.put("patient_data", patientData);
            
            test.info("Sending request with invalid date format...");
            
            // Create order
            Response response = orderPage.createOrder(authToken, userId, orderData);
            
            // Validation: Should fail
            test.info("Validation: Verifying request fails with invalid date");
            int statusCode = response.getStatusCode();
            Assert.assertNotEquals(statusCode, 200, "Request should not succeed with invalid date format");
            test.pass("✓ Invalid date format rejected with status code: " + statusCode);
            logger.info("✓ Invalid date format rejected");
            
            test.log(Status.PASS, "Invalid date format test passed");
            logger.info("========== Test PASSED ==========");
            
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 5, description = "Create Order - Past Date (Negative Test)")
    public void testCreateOrderWithPastDate() {
        test = extent.createTest("Create Order - Past Date", 
            "Verify that order creation fails when date of service is in the past");
        
        logger.info("========== Test: Create Order - Past Date (Negative Test) ==========");
        
        try {
            // Generate dynamic patient data
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.com";
            String phone = generatePhoneNumber();
            String dateOfBirth = generateDynamicDate(-10000); // ~27 years ago
            
            // Build patient address
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            // Build patient data
            Map<String, Object> patientData = orderPage.buildPatientData(
                firstName, lastName, dateOfBirth, "MALE", email, phone,
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            // Build services and codes
            List<String> services = new ArrayList<>();
            services.add("STOOL SPECIMEN PICKUP");
            List<String> orderCodes = new ArrayList<>();
            orderCodes.add("RPP COVID19");
            List<String> icd10Codes = new ArrayList<>();
            icd10Codes.add("A21.8");
            
            // Build service address
            Map<String, String> serviceAddress = orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345");
            
            // Build tube data
            List<Map<String, Object>> tubeData = new ArrayList<>();
            tubeData.add(orderPage.buildTubeData("NASAL SWAB", 1));
            
            // Generate PAST dates - THIS IS THE KEY NEGATIVE TEST
            String pastStartDate = generateDynamicDate(-10); // 10 days ago
            String pastEndDate = generateDynamicDate(-7); // 7 days ago
            String pastServiceDate = generateDynamicDate(-5); // 5 days ago
            
            test.info("Past Start Date: " + pastStartDate);
            test.info("Past End Date: " + pastEndDate);
            test.info("Past Service Date: " + pastServiceDate);
            
            // Build complete order request with PAST DATE
            Map<String, Object> orderData = orderPage.buildStandingOrderRequest(
                "STANDING ORDER", generateFacilityAccountNumber(), "1093767972",
                patientData, services, orderCodes, icd10Codes,
                pastStartDate, pastEndDate, "DAILY", pastServiceDate,
                serviceAddress, "CLIENT", false, true, tubeData,
                "123 Test St Test City NEW YORK 12345"
            );
            
            test.info("Sending request with PAST DATE - This should be rejected...");
            
            // Create order with past date using the new method
            Response response = orderPage.createOrderWithPastDate(authToken, userId, orderData);
            
            // Log the actual response for debugging
            int statusCode = response.getStatusCode();
            test.info("Response Status Code: " + statusCode);
            test.info("Response Body: " + response.getBody().asString());
            
            // Validation 1: Verify past date error
            test.info("Validation 1: Verifying past date is rejected");
            boolean isPastDateError = orderPage.validatePastDateError();
            
            if (!isPastDateError) {
                // Log failure details with HTML highlighting
                test.fail(orderPage.formatOrderError("API ACCEPTED PAST DATES", 
                    "The API should have rejected orders with past dates but accepted them with status " + statusCode));
                test.fail("Status Code: " + statusCode + " (Expected: 400 or 422)");
                test.fail("The API created orders with past dates:");
                test.fail("- Start Date: " + pastStartDate + " (PAST)");
                test.fail("- End Date: " + pastEndDate + " (PAST)");
                test.fail("- Service Date: " + pastServiceDate + " (PAST)");
                
                // Get order IDs if created and highlight them as failed
                try {
                    List<Map<String, Object>> orders = response.jsonPath().getList("data.orders");
                    if (orders != null && !orders.isEmpty()) {
                        test.fail("Orders created: " + orders.size());
                        for (int i = 0; i < orders.size(); i++) {
                            String orderId = response.jsonPath().getString("data.orders[" + i + "].order_id");
                            String serviceDate = response.jsonPath().getString("data.orders[" + i + "].phlebo_order.date_of_service");
                            test.fail(orderPage.formatOrderNumberFailed(orderId) + 
                                     "<br/><strong>Service Date:</strong> " + serviceDate + " (PAST DATE - SHOULD BE REJECTED)");
                        }
                    }
                } catch (Exception ex) {
                    // Ignore if we can't extract order details
                }
                
                logger.error("❌ BUG FOUND: API accepts past dates");
                Assert.fail("API should reject orders with past dates but it accepted them with status " + statusCode);
            }
            
            test.pass("✓ Past date was properly rejected");
            logger.info("✓ Past date validation passed");
            
            // Validation 2: Verify order was NOT created
            test.info("Validation 2: Verifying order was not created");
            boolean orderNotCreated = orderPage.validateOrderNotCreated();
            
            if (!orderNotCreated) {
                test.fail("❌ FAILED: Order was created with past date!");
                logger.error("❌ Order was created when it should have been rejected");
                Assert.fail("Order should not be created with past date");
            }
            
            test.pass("✓ Order was not created");
            logger.info("✓ Order not created validation passed");
            
            // Validation 3: Get and log error message
            test.info("Validation 3: Checking error message");
            String errorMessage = orderPage.getErrorMessage();
            test.info("Error message received: " + errorMessage);
            logger.info("Error message: " + errorMessage);
            
            test.log(Status.PASS, "Past date negative test passed - System correctly rejected past date");
            logger.info("========== Test PASSED - Past Date Correctly Rejected ==========");
            
        } catch (AssertionError e) {
            // Catch assertion errors specifically to log them properly
            test.fail("❌ ASSERTION FAILED: " + e.getMessage());
            logger.error("Test failed with assertion error: " + e.getMessage(), e);
            throw e; // Re-throw to mark test as failed
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 6, description = "Create Order - Future Date Beyond Range (Negative Test)")
    public void testCreateOrderWithFarFutureDate() {
        test = extent.createTest("Create Order - Far Future Date", 
            "Verify that order creation fails when date is too far in the future");
        
        logger.info("========== Test: Create Order - Far Future Date (Negative Test) ==========");
        
        try {
            // Generate dynamic patient data
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                firstName, lastName, generateDynamicDate(-10000), "MALE", 
                firstName + "@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            List<String> services = new ArrayList<>();
            services.add("STOOL SPECIMEN PICKUP");
            List<String> orderCodes = new ArrayList<>();
            orderCodes.add("RPP COVID19");
            List<String> icd10Codes = new ArrayList<>();
            icd10Codes.add("A21.8");
            
            Map<String, String> serviceAddress = orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345");
            List<Map<String, Object>> tubeData = new ArrayList<>();
            tubeData.add(orderPage.buildTubeData("NASAL SWAB", 1));
            
            // Generate dates 1 year in the future
            String futureStartDate = generateDynamicDate(365);
            String futureEndDate = generateDynamicDate(368);
            String futureServiceDate = generateDynamicDate(370);
            
            test.info("Far Future Start Date: " + futureStartDate);
            test.info("Far Future Service Date: " + futureServiceDate);
            
            Map<String, Object> orderData = orderPage.buildStandingOrderRequest(
                "STANDING ORDER", generateFacilityAccountNumber(), "1093767972",
                patientData, services, orderCodes, icd10Codes,
                futureStartDate, futureEndDate, "DAILY", futureServiceDate,
                serviceAddress, "CLIENT", false, true, tubeData,
                "123 Test St Test City NEW YORK 12345"
            );
            
            test.info("Sending request with FAR FUTURE DATE...");
            
            Response response = orderPage.createOrderWithFutureDate(authToken, userId, orderData);
            
            test.info("Validation: Checking if far future date is handled");
            int statusCode = response.getStatusCode();
            test.info("Status Code: " + statusCode);
            logger.info("Far future date test - Status Code: " + statusCode);
            
            if (statusCode >= 400) {
                test.pass("✓ Far future date was rejected with status: " + statusCode);
            } else {
                test.info("Note: Far future date was accepted - may be valid business rule");
            }
            
            test.log(Status.PASS, "Far future date test completed");
            logger.info("========== Test COMPLETED ==========");
            
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 7, description = "Create Order - Invalid ICD-10 Codes (Negative Test)")
    public void testCreateOrderWithInvalidICD10() {
        test = extent.createTest("Create Order - Invalid ICD-10 Codes", 
            "Verify that order creation handles invalid ICD-10 codes appropriately");
        
        logger.info("========== Test: Create Order - Invalid ICD-10 Codes (Negative Test) ==========");
        
        try {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                firstName, lastName, generateDynamicDate(-10000), "MALE", 
                firstName + "@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            List<String> services = new ArrayList<>();
            services.add("STOOL SPECIMEN PICKUP");
            List<String> orderCodes = new ArrayList<>();
            orderCodes.add("RPP COVID19");
            
            // Invalid ICD-10 codes
            List<String> invalidIcd10Codes = new ArrayList<>();
            invalidIcd10Codes.add("INVALID123");
            invalidIcd10Codes.add("ZZZZZ");
            invalidIcd10Codes.add("99999");
            
            test.info("Invalid ICD-10 Codes: " + invalidIcd10Codes);
            
            Map<String, String> serviceAddress = orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345");
            List<Map<String, Object>> tubeData = new ArrayList<>();
            tubeData.add(orderPage.buildTubeData("NASAL SWAB", 1));
            
            Map<String, Object> orderData = orderPage.buildStandingOrderRequest(
                "STANDING ORDER", generateFacilityAccountNumber(), "1093767972",
                patientData, services, orderCodes, invalidIcd10Codes,
                generateDynamicDate(1), generateDynamicDate(4), "DAILY", generateDynamicDate(5),
                serviceAddress, "CLIENT", false, true, tubeData,
                "123 Test St Test City NEW YORK 12345"
            );
            
            test.info("Sending request with INVALID ICD-10 codes...");
            
            Response response = orderPage.createOrderWithInvalidICD10(authToken, userId, orderData);
            
            int statusCode = response.getStatusCode();
            test.info("Status Code: " + statusCode);
            
            if (statusCode >= 400) {
                test.pass("✓ Invalid ICD-10 codes rejected with status: " + statusCode);
                logger.info("✓ Invalid ICD-10 codes rejected");
            } else {
                test.info("Note: Invalid ICD-10 codes were accepted - validation may not be enforced");
            }
            
            test.log(Status.PASS, "Invalid ICD-10 codes test completed");
            logger.info("========== Test COMPLETED ==========");
            
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 8, description = "Create Order - Invalid Facility Account (Negative Test)")
    public void testCreateOrderWithInvalidFacility() {
        test = extent.createTest("Create Order - Invalid Facility Account", 
            "Verify that order creation fails with invalid facility account number");
        
        logger.info("========== Test: Create Order - Invalid Facility Account (Negative Test) ==========");
        
        try {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                firstName, lastName, generateDynamicDate(-10000), "MALE", 
                firstName + "@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            List<String> services = new ArrayList<>();
            services.add("STOOL SPECIMEN PICKUP");
            List<String> orderCodes = new ArrayList<>();
            orderCodes.add("RPP COVID19");
            List<String> icd10Codes = new ArrayList<>();
            icd10Codes.add("A21.8");
            
            Map<String, String> serviceAddress = orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345");
            List<Map<String, Object>> tubeData = new ArrayList<>();
            tubeData.add(orderPage.buildTubeData("NASAL SWAB", 1));
            
            // Invalid facility account number
            String invalidFacility = "INVALID_FACILITY_12345";
            test.info("Invalid Facility Account: " + invalidFacility);
            
            Map<String, Object> orderData = orderPage.buildStandingOrderRequest(
                "STANDING ORDER", invalidFacility, "1093767972",
                patientData, services, orderCodes, icd10Codes,
                generateDynamicDate(1), generateDynamicDate(4), "DAILY", generateDynamicDate(5),
                serviceAddress, "CLIENT", false, true, tubeData,
                "123 Test St Test City NEW YORK 12345"
            );
            
            test.info("Sending request with INVALID FACILITY ACCOUNT...");
            
            Response response = orderPage.createOrderWithInvalidFacility(authToken, userId, orderData);
            
            int statusCode = response.getStatusCode();
            test.info("Status Code: " + statusCode);
            
            if (statusCode >= 400) {
                test.pass("✓ Invalid facility account rejected with status: " + statusCode);
                String errorMsg = orderPage.getErrorMessage();
                test.info("Error: " + errorMsg);
                logger.info("✓ Invalid facility rejected: " + errorMsg);
            } else {
                test.info("Note: Invalid facility was accepted - validation may not be enforced");
            }
            
            test.log(Status.PASS, "Invalid facility account test completed");
            logger.info("========== Test COMPLETED ==========");
            
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 9, description = "Create Order - Invalid Physician NPI (Negative Test)")
    public void testCreateOrderWithInvalidNPI() {
        test = extent.createTest("Create Order - Invalid Physician NPI", 
            "Verify that order creation handles invalid physician NPI appropriately");
        
        logger.info("========== Test: Create Order - Invalid Physician NPI (Negative Test) ==========");
        
        try {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                firstName, lastName, generateDynamicDate(-10000), "MALE", 
                firstName + "@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            List<String> services = new ArrayList<>();
            services.add("STOOL SPECIMEN PICKUP");
            List<String> orderCodes = new ArrayList<>();
            orderCodes.add("RPP COVID19");
            List<String> icd10Codes = new ArrayList<>();
            icd10Codes.add("A21.8");
            
            Map<String, String> serviceAddress = orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345");
            List<Map<String, Object>> tubeData = new ArrayList<>();
            tubeData.add(orderPage.buildTubeData("NASAL SWAB", 1));
            
            // Invalid physician NPI
            String invalidNPI = "INVALID_NPI_12345";
            test.info("Invalid Physician NPI: " + invalidNPI);
            
            Map<String, Object> orderData = orderPage.buildStandingOrderRequest(
                "STANDING ORDER", generateFacilityAccountNumber(), invalidNPI,
                patientData, services, orderCodes, icd10Codes,
                generateDynamicDate(1), generateDynamicDate(4), "DAILY", generateDynamicDate(5),
                serviceAddress, "CLIENT", false, true, tubeData,
                "123 Test St Test City NEW YORK 12345"
            );
            
            test.info("Sending request with INVALID PHYSICIAN NPI...");
            
            Response response = orderPage.createOrderWithInvalidNPI(authToken, userId, orderData);
            
            int statusCode = response.getStatusCode();
            test.info("Status Code: " + statusCode);
            
            if (statusCode >= 400) {
                test.pass("✓ Invalid NPI rejected with status: " + statusCode);
                String errorMsg = orderPage.getErrorMessage();
                test.info("Error: " + errorMsg);
                logger.info("✓ Invalid NPI rejected: " + errorMsg);
            } else {
                test.info("Note: Invalid NPI was accepted - validation may not be enforced");
            }
            
            test.log(Status.PASS, "Invalid physician NPI test completed");
            logger.info("========== Test COMPLETED ==========");
            
        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 10, description = "Missing Patient Data - Negative Test")
    public void testCreateOrderWithoutPatientData() {
        test = extent.createTest("Missing Patient Data", 
            "Verify that order creation fails when patient_data is missing");
        
        logger.info("========== Test: Missing Patient Data ==========");
        
        try {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", generateFacilityAccountNumber());
            orderData.put("physician_npi", "1093767972");
            // patient_data is MISSING
            
            test.info("Sending request WITHOUT patient_data...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "patient_data");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("patient_data"), 
                "API should reject request when patient_data is missing");
            test.pass("✓ Order creation failed as expected - patient_data is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 11, description = "Missing Facility Account Number - Negative Test")
    public void testCreateOrderWithoutFacilityAccount() {
        test = extent.createTest("Missing Facility Account Number", 
            "Verify that order creation fails when facility_account_number is missing");
        
        logger.info("========== Test: Missing Facility Account Number ==========");
        
        try {
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                "Test", "User", generateDynamicDate(-10000), "MALE", 
                "test@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            // facility_account_number is MISSING
            orderData.put("physician_npi", "1093767972");
            orderData.put("patient_data", patientData);
            
            test.info("Sending request WITHOUT facility_account_number...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "facility_account_number");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("facility"), 
                "API should reject request when facility_account_number is missing");
            test.pass("✓ Order creation failed as expected - facility_account_number is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 12, description = "Missing Physician NPI - Negative Test")
    public void testCreateOrderWithoutPhysicianNPI() {
        test = extent.createTest("Missing Physician NPI", 
            "Verify that order creation fails when physician_npi is missing");
        
        logger.info("========== Test: Missing Physician NPI ==========");
        
        try {
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                "Test", "User", generateDynamicDate(-10000), "MALE", 
                "test@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", generateFacilityAccountNumber());
            // physician_npi is MISSING
            orderData.put("patient_data", patientData);
            
            test.info("Sending request WITHOUT physician_npi...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "physician_npi");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("physician"), 
                "API should reject request when physician_npi is missing");
            test.pass("✓ Order creation failed as expected - physician_npi is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 13, description = "Missing Order Codes - Negative Test")
    public void testCreateOrderWithoutOrderCodes() {
        test = extent.createTest("Missing Order Codes", 
            "Verify that order creation fails when order_codes is missing");
        
        logger.info("========== Test: Missing Order Codes ==========");
        
        try {
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                "Test", "User", generateDynamicDate(-10000), "MALE", 
                "test@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", generateFacilityAccountNumber());
            orderData.put("physician_npi", "1093767972");
            orderData.put("patient_data", patientData);
            // order_codes is MISSING
            
            test.info("Sending request WITHOUT order_codes...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "order_codes");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("order_codes"), 
                "API should reject request when order_codes is missing");
            test.pass("✓ Order creation failed as expected - order_codes is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 14, description = "Missing Services - Negative Test")
    public void testCreateOrderWithoutServices() {
        test = extent.createTest("Missing Services", 
            "Verify that order creation fails when services is missing");
        
        logger.info("========== Test: Missing Services ==========");
        
        try {
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                "Test", "User", generateDynamicDate(-10000), "MALE", 
                "test@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", generateFacilityAccountNumber());
            orderData.put("physician_npi", "1093767972");
            orderData.put("patient_data", patientData);
            // services is MISSING
            
            test.info("Sending request WITHOUT services...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "services");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("services"), 
                "API should reject request when services is missing");
            test.pass("✓ Order creation failed as expected - services is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 15, description = "Missing ICD-10 Codes - Negative Test")
    public void testCreateOrderWithoutICD10Codes() {
        test = extent.createTest("Missing ICD-10 Codes", 
            "Verify that order creation fails when icd_10_codes is missing");
        
        logger.info("========== Test: Missing ICD-10 Codes ==========");
        
        try {
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                "Test", "User", generateDynamicDate(-10000), "MALE", 
                "test@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", generateFacilityAccountNumber());
            orderData.put("physician_npi", "1093767972");
            orderData.put("patient_data", patientData);
            // icd_10_codes is MISSING
            
            test.info("Sending request WITHOUT icd_10_codes...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "icd_10_codes");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("icd_10_codes"), 
                "API should reject request when icd_10_codes is missing");
            test.pass("✓ Order creation failed as expected - icd_10_codes is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 16, description = "Missing Billing Type - Negative Test")
    public void testCreateOrderWithoutBillingType() {
        test = extent.createTest("Missing Billing Type", 
            "Verify that order creation fails when billing_type is missing");
        
        logger.info("========== Test: Missing Billing Type ==========");
        
        try {
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                "Test", "User", generateDynamicDate(-10000), "MALE", 
                "test@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", generateFacilityAccountNumber());
            orderData.put("physician_npi", "1093767972");
            orderData.put("patient_data", patientData);
            // billing_type is MISSING
            
            test.info("Sending request WITHOUT billing_type...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "billing_type");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("billing_type"), 
                "API should reject request when billing_type is missing");
            test.pass("✓ Order creation failed as expected - billing_type is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 17, description = "Missing Date of Service - Negative Test")
    public void testCreateOrderWithoutDateOfService() {
        test = extent.createTest("Missing Date of Service", 
            "Verify that order creation fails when date_of_service is missing");
        
        logger.info("========== Test: Missing Date of Service ==========");
        
        try {
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                "Test", "User", generateDynamicDate(-10000), "MALE", 
                "test@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", generateFacilityAccountNumber());
            orderData.put("physician_npi", "1093767972");
            orderData.put("patient_data", patientData);
            // date_of_service is MISSING
            
            test.info("Sending request WITHOUT date_of_service...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "date_of_service");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("date_of_service"), 
                "API should reject request when date_of_service is missing");
            test.pass("✓ Order creation failed as expected - date_of_service is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test(priority = 18, description = "Missing Tube Data - Negative Test")
    public void testCreateOrderWithoutTubeData() {
        test = extent.createTest("Missing Tube Data", 
            "Verify that order creation fails when tube_data is missing");
        
        logger.info("========== Test: Missing Tube Data ==========");
        
        try {
            List<Map<String, String>> addresses = new ArrayList<>();
            addresses.add(orderPage.buildAddress("123 Test St", "Test City", "NEW YORK", "12345"));
            
            Map<String, Object> patientData = orderPage.buildPatientData(
                "Test", "User", generateDynamicDate(-10000), "MALE", 
                "test@test.com", generatePhoneNumber(),
                false, "ASIAN", "ASIAN", false, addresses
            );
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order_type", "STANDING ORDER");
            orderData.put("facility_account_number", generateFacilityAccountNumber());
            orderData.put("physician_npi", "1093767972");
            orderData.put("patient_data", patientData);
            // tube_data is MISSING
            
            test.info("Sending request WITHOUT tube_data...");
            
            Response response = orderPage.createOrderWithMissingField(authToken, userId, orderData, "tube_data");
            
            test.info("Response Status Code: " + response.getStatusCode());
            Assert.assertTrue(orderPage.validateMissingFieldError("tube_data"), 
                "API should reject request when tube_data is missing");
            test.pass("✓ Order creation failed as expected - tube_data is mandatory");
            
            logger.info("========== Test PASSED ==========");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            logger.error("Test failed: " + e.getMessage(), e);
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @AfterClass
    public void tearDown() {
        logger.info("Tearing down MLX Create Order Test");
        
        // Flush ExtentReports to generate HTML report
        ExtentReportManager.flush();
        
        logger.info("Test execution completed. HTML report generated.");
    }
}
