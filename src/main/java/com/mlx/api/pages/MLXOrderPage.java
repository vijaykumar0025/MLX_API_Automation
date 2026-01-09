package com.mlx.api.pages;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Page Object Model for MLX Order API
 * Endpoint: https://staging-api-mlx.labsquire.com/orders/saveOrder
 */
public class MLXOrderPage extends BasePage {
    
    private static final Logger logger = LogManager.getLogger(MLXOrderPage.class);
    private Response response;
    
    // API Endpoint
    private static final String SAVE_ORDER_ENDPOINT = "/orders/saveOrder";
    
    /**
     * Create a standing order with all parameters
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing order details
     * @return Response object
     */
    public Response createOrder(String authToken, String userId, Map<String, Object> orderData) {
        logger.info("Creating order with order type: " + orderData.get("order_type"));
        
        // Build request with all headers from curl command
        RequestSpecification request = RestAssured.given()
            .header("accept", "application/json, text/plain, */*")
            .header("accept-language", "en-GB,en;q=0.9,en-US;q=0.8,en-IN;q=0.7")
            .header("authorization", "Bearer " + authToken)
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
            .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0");
        
        // Add user_id header only if not null
        if (userId != null && !userId.isEmpty()) {
            request.header("user_id", userId);
        }
        
        request.body(orderData)
            .log().all();
        
        // Send POST request
        response = request.post(SAVE_ORDER_ENDPOINT);
        
        // Log response
        logger.info("Response Status Code: " + response.getStatusCode());
        logger.info("Response Body: " + response.getBody().asString());
        response.then().log().all();
        
        return response;
    }
    
    /**
     * Build standing order request body
     */
    public Map<String, Object> buildStandingOrderRequest(
            String orderType,
            String facilityAccountNumber,
            String physicianNpi,
            Map<String, Object> patientData,
            List<String> services,
            List<String> orderCodes,
            List<String> icd10Codes,
            String standingStartDate,
            String standingEndDate,
            String standingFrequency,
            String dateOfService,
            Map<String, String> serviceAddress,
            String billingType,
            boolean isStat,
            boolean fasting,
            List<Map<String, Object>> tubeData,
            String serviceAddressString) {
        
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("order_type", orderType);
        orderData.put("facility_account_number", facilityAccountNumber);
        orderData.put("physician_npi", physicianNpi);
        orderData.put("patient_data", patientData);
        orderData.put("services", services);
        orderData.put("order_codes", orderCodes);
        orderData.put("icd_10_codes", icd10Codes);
        orderData.put("standing_start_date", standingStartDate);
        orderData.put("standing_end_date", standingEndDate);
        orderData.put("standing_frequency", standingFrequency);
        orderData.put("date_of_service", dateOfService);
        orderData.put("appointment_time", "");
        orderData.put("service_address", serviceAddress);
        orderData.put("billing_type", billingType);
        orderData.put("is_stat", isStat);
        orderData.put("fasting", fasting);
        orderData.put("tube_data", tubeData);
        orderData.put("service_address_string", serviceAddressString);
        
        return orderData;
    }
    
    /**
     * Build patient data
     */
    public Map<String, Object> buildPatientData(
            String firstName,
            String lastName,
            String dateOfBirth,
            String gender,
            String email,
            String mobileNumber,
            boolean homebound,
            String ethnicity,
            String race,
            boolean hardstick,
            List<Map<String, String>> addresses) {
        
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("first_name", firstName);
        patientData.put("last_name", lastName);
        patientData.put("date_of_birth", dateOfBirth);
        patientData.put("gender", gender);
        patientData.put("email", email);
        patientData.put("mobile_number1", mobileNumber);
        patientData.put("homebound", homebound);
        patientData.put("ethnicity", ethnicity);
        patientData.put("race", race);
        patientData.put("hardstick", hardstick);
        patientData.put("addresses", addresses);
        
        return patientData;
    }
    
    /**
     * Build address
     */
    public Map<String, String> buildAddress(String addressLine1, String city, String state, String zip) {
        Map<String, String> address = new HashMap<>();
        address.put("address_line_1", addressLine1);
        address.put("city", city);
        address.put("state", state);
        address.put("zip", zip);
        return address;
    }
    
    /**
     * Build tube data
     */
    public Map<String, Object> buildTubeData(String tubeName, int tubeCount) {
        Map<String, Object> tube = new HashMap<>();
        tube.put("tube_name", tubeName);
        tube.put("tube_count", tubeCount);
        return tube;
    }
    
    /**
     * Get the current response object
     */
    public Response getResponse() {
        return response;
    }
    
    /**
     * Validate response status code
     */
    public boolean validateStatusCode(int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        logger.info("Expected Status Code: " + expectedStatusCode + ", Actual: " + actualStatusCode);
        return actualStatusCode == expectedStatusCode;
    }
    
    /**
     * Validate response contains field
     */
    public boolean validateResponseContainsField(String fieldPath) {
        try {
            Object value = response.jsonPath().get(fieldPath);
            logger.info("Field '" + fieldPath + "' exists with value: " + value);
            return value != null;
        } catch (Exception e) {
            logger.error("Field '" + fieldPath + "' not found in response");
            return false;
        }
    }
    
    /**
     * Get field value from response
     */
    public Object getResponseField(String fieldPath) {
        return response.jsonPath().get(fieldPath);
    }
    
    /**
     * Validate response message
     */
    public boolean validateResponseMessage(String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        logger.info("Expected Message: " + expectedMessage + ", Actual: " + actualMessage);
        return actualMessage != null && actualMessage.equals(expectedMessage);
    }
    
    /**
     * Validate order ID is generated
     */
    public boolean validateOrderIdGenerated() {
        try {
            // For standing orders, check if orders array exists and has items
            List<Map<String, Object>> orders = response.jsonPath().getList("data.orders");
            if (orders != null && !orders.isEmpty()) {
                String firstOrderId = response.jsonPath().getString("data.orders[0].order_id");
                boolean isValid = firstOrderId != null && !firstOrderId.isEmpty();
                logger.info("Order ID validation: " + (isValid ? "PASSED - First Order ID: " + firstOrderId : "FAILED"));
                return isValid;
            }
            
            // For single orders, check data.order_id
            String orderId = response.jsonPath().getString("data.order_id");
            boolean isValid = orderId != null && !orderId.isEmpty();
            logger.info("Order ID validation: " + (isValid ? "PASSED - Order ID: " + orderId : "FAILED"));
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating order ID: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get order ID from response
     */
    public String getOrderId() {
        try {
            // For standing orders, get first order ID
            List<Map<String, Object>> orders = response.jsonPath().getList("data.orders");
            if (orders != null && !orders.isEmpty()) {
                return response.jsonPath().getString("data.orders[0].order_id");
            }
            
            // For single orders
            return response.jsonPath().getString("data.order_id");
        } catch (Exception e) {
            logger.error("Error getting order ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Create order with past date (Negative Test Case)
     * This method is used to verify that the system rejects orders with past dates
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing order details with past date
     * @return Response object
     */
    public Response createOrderWithPastDate(String authToken, String userId, Map<String, Object> orderData) {
        logger.info("Creating order with PAST DATE - Negative Test Case");
        logger.info("Order type: " + orderData.get("order_type"));
        logger.info("Date of service: " + orderData.get("date_of_service"));
        
        return createOrder(authToken, userId, orderData);
    }
    
    /**
     * Validate error response for past date
     * Checks if the response contains appropriate error message for past date submission
     */
    public boolean validatePastDateError() {
        try {
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString();
            
            logger.info("Validating past date error response...");
            logger.info("Status Code: " + statusCode);
            logger.info("Response Body: " + responseBody);
            
            // Check for error status codes (400, 422, etc.)
            boolean hasErrorStatusCode = (statusCode >= 400 && statusCode < 600);
            
            // Check for error message in response
            String message = response.jsonPath().getString("message");
            String error = response.jsonPath().getString("error");
            
            boolean hasErrorMessage = false;
            if (message != null) {
                hasErrorMessage = message.toLowerCase().contains("past") || 
                                 message.toLowerCase().contains("date") ||
                                 message.toLowerCase().contains("invalid") ||
                                 message.toLowerCase().contains("cannot");
            }
            if (error != null && !hasErrorMessage) {
                hasErrorMessage = error.toLowerCase().contains("past") || 
                                 error.toLowerCase().contains("date") ||
                                 error.toLowerCase().contains("invalid");
            }
            
            boolean isValid = hasErrorStatusCode || hasErrorMessage;
            logger.info("Past date error validation: " + (isValid ? "PASSED" : "FAILED"));
            
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating past date error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate that order was NOT created (for negative test cases)
     * Ensures no order ID is generated when validation should fail
     */
    public boolean validateOrderNotCreated() {
        try {
            int statusCode = response.getStatusCode();
            
            // Check if status code indicates failure
            if (statusCode >= 400 && statusCode < 600) {
                logger.info("Order not created - Error status code: " + statusCode);
                return true;
            }
            
            // Check if order ID is missing or null
            String orderId = null;
            try {
                List<Map<String, Object>> orders = response.jsonPath().getList("data.orders");
                if (orders != null && !orders.isEmpty()) {
                    orderId = response.jsonPath().getString("data.orders[0].order_id");
                } else {
                    orderId = response.jsonPath().getString("data.order_id");
                }
            } catch (Exception e) {
                // Exception means order_id field doesn't exist, which is expected
                logger.info("Order not created - No order_id in response");
                return true;
            }
            
            boolean notCreated = (orderId == null || orderId.isEmpty());
            logger.info("Order not created validation: " + (notCreated ? "PASSED" : "FAILED"));
            
            return notCreated;
        } catch (Exception e) {
            logger.error("Error validating order not created: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get error message from response
     * Useful for negative test case assertions
     */
    public String getErrorMessage() {
        try {
            String message = response.jsonPath().getString("message");
            if (message != null) {
                return message;
            }
            
            String error = response.jsonPath().getString("error");
            if (error != null) {
                return error;
            }
            
            // Try to get validation errors
            Object errors = response.jsonPath().get("errors");
            if (errors != null) {
                return errors.toString();
            }
            
            return "No error message found";
        } catch (Exception e) {
            logger.error("Error getting error message: " + e.getMessage());
            return "Unable to parse error message";
        }
    }
    
    // ========== Additional Negative Test Case Methods ==========
    
    /**
     * Create order with future date beyond allowed range (Negative Test Case)
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing order details with far future date
     * @return Response object
     */
    public Response createOrderWithFutureDate(String authToken, String userId, Map<String, Object> orderData) {
        logger.info("Creating order with FUTURE DATE - Negative Test Case");
        logger.info("Order type: " + orderData.get("order_type"));
        logger.info("Date of service: " + orderData.get("date_of_service"));
        
        return createOrder(authToken, userId, orderData);
    }
    
    /**
     * Create order with invalid/expired authentication token (Negative Test Case)
     * @param invalidToken Invalid or expired token
     * @param userId User ID
     * @param orderData Map containing order details
     * @return Response object
     */
    public Response createOrderWithInvalidAuth(String invalidToken, String userId, Map<String, Object> orderData) {
        logger.info("Creating order with INVALID AUTH TOKEN - Negative Test Case");
        logger.info("Token: " + (invalidToken != null ? invalidToken.substring(0, Math.min(20, invalidToken.length())) + "..." : "null"));
        
        return createOrder(invalidToken, userId, orderData);
    }
    
    /**
     * Create order with missing required fields (Negative Test Case)
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing incomplete order details
     * @param missingField Name of the field that is missing
     * @return Response object
     */
    public Response createOrderWithMissingField(String authToken, String userId, Map<String, Object> orderData, String missingField) {
        logger.info("Creating order with MISSING REQUIRED FIELD - Negative Test Case");
        logger.info("Missing field: " + missingField);
        
        return createOrder(authToken, userId, orderData);
    }
    
    /**
     * Create order with invalid data format (Negative Test Case)
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing order details with invalid format
     * @param invalidField Name of the field with invalid format
     * @return Response object
     */
    public Response createOrderWithInvalidFormat(String authToken, String userId, Map<String, Object> orderData, String invalidField) {
        logger.info("Creating order with INVALID DATA FORMAT - Negative Test Case");
        logger.info("Invalid field: " + invalidField);
        logger.info("Value: " + orderData.get(invalidField));
        
        return createOrder(authToken, userId, orderData);
    }
    
    /**
     * Create order with empty/null patient data (Negative Test Case)
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing order details with empty patient data
     * @return Response object
     */
    public Response createOrderWithEmptyPatientData(String authToken, String userId, Map<String, Object> orderData) {
        logger.info("Creating order with EMPTY PATIENT DATA - Negative Test Case");
        
        return createOrder(authToken, userId, orderData);
    }
    
    /**
     * Create order with invalid ICD-10 codes (Negative Test Case)
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing order details with invalid ICD-10 codes
     * @return Response object
     */
    public Response createOrderWithInvalidICD10(String authToken, String userId, Map<String, Object> orderData) {
        logger.info("Creating order with INVALID ICD-10 CODES - Negative Test Case");
        logger.info("ICD-10 codes: " + orderData.get("icd_10_codes"));
        
        return createOrder(authToken, userId, orderData);
    }
    
    /**
     * Create order with invalid facility account number (Negative Test Case)
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing order details with invalid facility account
     * @return Response object
     */
    public Response createOrderWithInvalidFacility(String authToken, String userId, Map<String, Object> orderData) {
        logger.info("Creating order with INVALID FACILITY ACCOUNT - Negative Test Case");
        logger.info("Facility account number: " + orderData.get("facility_account_number"));
        
        return createOrder(authToken, userId, orderData);
    }
    
    /**
     * Create order with invalid physician NPI (Negative Test Case)
     * @param authToken Bearer token for authorization
     * @param userId User ID
     * @param orderData Map containing order details with invalid physician NPI
     * @return Response object
     */
    public Response createOrderWithInvalidNPI(String authToken, String userId, Map<String, Object> orderData) {
        logger.info("Creating order with INVALID PHYSICIAN NPI - Negative Test Case");
        logger.info("Physician NPI: " + orderData.get("physician_npi"));
        
        return createOrder(authToken, userId, orderData);
    }
    
    /**
     * Validate authentication error response (401 Unauthorized)
     */
    public boolean validateAuthenticationError() {
        try {
            int statusCode = response.getStatusCode();
            logger.info("Validating authentication error...");
            logger.info("Status Code: " + statusCode);
            
            boolean isAuthError = (statusCode == 401 || statusCode == 403);
            logger.info("Authentication error validation: " + (isAuthError ? "PASSED" : "FAILED"));
            
            return isAuthError;
        } catch (Exception e) {
            logger.error("Error validating authentication error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate missing field error response (400 Bad Request)
     */
    public boolean validateMissingFieldError(String expectedFieldName) {
        try {
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString().toLowerCase();
            
            logger.info("Validating missing field error for: " + expectedFieldName);
            logger.info("Status Code: " + statusCode);
            
            boolean hasErrorCode = (statusCode == 400 || statusCode == 422);
            boolean mentionsField = responseBody.contains(expectedFieldName.toLowerCase()) ||
                                   responseBody.contains("required") ||
                                   responseBody.contains("missing");
            
            boolean isValid = hasErrorCode && mentionsField;
            logger.info("Missing field error validation: " + (isValid ? "PASSED" : "FAILED"));
            
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating missing field error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate invalid format error response
     */
    public boolean validateInvalidFormatError() {
        try {
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString().toLowerCase();
            
            logger.info("Validating invalid format error...");
            logger.info("Status Code: " + statusCode);
            
            boolean hasErrorCode = (statusCode >= 400 && statusCode < 500);
            boolean hasFormatError = responseBody.contains("invalid") ||
                                    responseBody.contains("format") ||
                                    responseBody.contains("validation");
            
            boolean isValid = hasErrorCode || hasFormatError;
            logger.info("Invalid format error validation: " + (isValid ? "PASSED" : "FAILED"));
            
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating invalid format error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate future date error response
     */
    public boolean validateFutureDateError() {
        try {
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString().toLowerCase();
            
            logger.info("Validating future date error response...");
            logger.info("Status Code: " + statusCode);
            
            boolean hasErrorStatusCode = (statusCode >= 400 && statusCode < 600);
            boolean hasFutureDateError = responseBody.contains("future") ||
                                        responseBody.contains("date") ||
                                        responseBody.contains("invalid") ||
                                        responseBody.contains("range");
            
            boolean isValid = hasErrorStatusCode || hasFutureDateError;
            logger.info("Future date error validation: " + (isValid ? "PASSED" : "FAILED"));
            
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating future date error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate general validation error (400/422 status codes)
     */
    public boolean validateValidationError() {
        try {
            int statusCode = response.getStatusCode();
            logger.info("Validating general validation error...");
            logger.info("Status Code: " + statusCode);
            
            boolean isValidationError = (statusCode == 400 || statusCode == 422);
            logger.info("Validation error check: " + (isValidationError ? "PASSED" : "FAILED"));
            
            return isValidationError;
        } catch (Exception e) {
            logger.error("Error validating validation error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all error details from response for comprehensive validation
     */
    public Map<String, Object> getAllErrorDetails() {
        Map<String, Object> errorDetails = new HashMap<>();
        
        try {
            errorDetails.put("status_code", response.getStatusCode());
            errorDetails.put("message", response.jsonPath().getString("message"));
            errorDetails.put("error", response.jsonPath().getString("error"));
            errorDetails.put("errors", response.jsonPath().get("errors"));
            errorDetails.put("response_body", response.getBody().asString());
            
            logger.info("Error Details: " + errorDetails);
        } catch (Exception e) {
            logger.error("Error getting error details: " + e.getMessage());
            errorDetails.put("error", "Unable to parse error details");
        }
        
        return errorDetails;
    }
    
    // ========== HTML Formatting Methods for Enhanced Reporting ==========
    
    /**
     * Format order number with HTML highlighting for SUCCESS scenarios
     * @param orderId The order ID to format
     * @return HTML formatted string with highlighted order number
     */
    public String formatOrderNumberSuccess(String orderId) {
        return "<div class='order-highlight-box-success'>" +
               "<span class='order-number-success'>✓ Order ID: " + orderId + "</span>" +
               "</div>";
    }
    
    /**
     * Format order number with HTML highlighting for FAILED scenarios
     * @param orderId The order ID to format
     * @return HTML formatted string with highlighted order number
     */
    public String formatOrderNumberFailed(String orderId) {
        return "<div class='order-highlight-box-failed'>" +
               "<span class='order-number-failed'>✗ Order ID: " + orderId + "</span>" +
               "</div>";
    }
    
    /**
     * Format multiple order numbers for standing orders (SUCCESS)
     * @param orderIds List of order IDs
     * @return HTML formatted string with all order numbers highlighted
     */
    public String formatMultipleOrderNumbersSuccess(List<String> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return "";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<div class='order-highlight-box-success'>");
        html.append("<strong>✓ Orders Created Successfully:</strong><br/>");
        
        for (int i = 0; i < orderIds.size(); i++) {
            html.append("<span class='order-number-success'>Order ")
                .append(i + 1)
                .append(": ")
                .append(orderIds.get(i))
                .append("</span><br/>");
        }
        
        html.append("</div>");
        return html.toString();
    }
    
    /**
     * Get all order IDs from standing order response
     * @return List of order IDs
     */
    public List<String> getAllOrderIds() {
        try {
            List<String> orderIds = new ArrayList<>();
            List<Map<String, Object>> orders = response.jsonPath().getList("data.orders");
            
            if (orders != null && !orders.isEmpty()) {
                for (int i = 0; i < orders.size(); i++) {
                    String orderId = response.jsonPath().getString("data.orders[" + i + "].order_id");
                    if (orderId != null && !orderId.isEmpty()) {
                        orderIds.add(orderId);
                    }
                }
            }
            
            return orderIds;
        } catch (Exception e) {
            logger.error("Error getting all order IDs: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Format order summary with details (for successful orders)
     * @param orderId The order ID
     * @param serviceDate Service date
     * @param patientName Patient name
     * @return HTML formatted summary
     */
    public String formatOrderSummarySuccess(String orderId, String serviceDate, String patientName) {
        return "<div class='order-highlight-box-success'>" +
               "<span class='order-number-success'>Order ID: " + orderId + "</span><br/>" +
               "<strong>Patient:</strong> " + patientName + "<br/>" +
               "<strong>Service Date:</strong> " + serviceDate +
               "</div>";
    }
    
    /**
     * Format order error information (for failed validations)
     * @param errorType Type of error
     * @param errorDetails Additional error details
     * @return HTML formatted error message
     */
    public String formatOrderError(String errorType, String errorDetails) {
        return "<div class='order-highlight-box-failed'>" +
               "<span class='order-number-failed'>✗ " + errorType + "</span><br/>" +
               "<strong>Details:</strong> " + errorDetails +
               "</div>";
    }
}
