# 🎓 RestAssured Flow - Complete Beginner's Guide

## 📖 Understanding: How One API Call is Divided into Multiple Classes

---

## 🤔 The Problem We're Solving

**Traditional Approach (BAD):**
```java
// Everything in ONE test class - NOT REUSABLE!
@Test
public void testLogin() {
    // Build request
    Map<String, String> body = new HashMap<>();
    body.put("email", "test@example.com");
    body.put("password", "12345");
    
    // Send request
    Response response = RestAssured.given()
        .header("Content-Type", "application/json")
        .body(body)
        .post("https://api.example.com/login");
    
    // Validate
    Assert.assertEquals(response.getStatusCode(), 200);
    String token = response.jsonPath().getString("token");
    Assert.assertNotNull(token);
}

@Test
public void testLoginAgainWithSameCode() {
    // DUPLICATE CODE! - Need to copy-paste everything again!
    Map<String, String> body = new HashMap<>();
    body.put("email", "test2@example.com");
    body.put("password", "67890");
    // ... same code repeated
}
```

**Problems:**
❌ Code duplication everywhere
❌ Hard to maintain (change API = update 100 places)
❌ Not reusable across different tests
❌ Messy and difficult to read

---

## ✅ The Solution: Page Object Model (POM)

We divide one API call into **4 separate classes**:

```
┌─────────────────────────────────────────────────────────┐
│         ONE API CALL = 4 CLASSES WORKING TOGETHER       │
└─────────────────────────────────────────────────────────┘

1. ConfigReader.java      → Stores credentials & URLs
2. LoginPage.java         → Makes the API call
3. BaseTest.java          → Common test setup
4. LoginTest.java         → Writes test scenarios
```

---

## 🎯 Real Example: MLX Login API Flow

Let me show you EXACTLY how your MLX Login API works step-by-step.

---

### **STEP 1: Configuration File (Data Storage)**

**File:** `src/main/resources/config-staging.properties`

```properties
# This is like a database for test data
baseURI=https://staging-api-mlx.labsquire.com
testEmail=uday.b@labsquire.com
testPassword=1234567
applicationType=web
```

**Purpose:** Store all changeable values in ONE place
- Change password? Update only this file!
- Switch to production? Just change baseURI!

---

### **STEP 2: ConfigReader Class (Data Reader)**

**File:** `src/main/java/com/mlx/api/utils/ConfigReader.java`

```java
package com.mlx.api.utils;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * ROLE: Reads configuration file and provides values to tests
 */
public class ConfigReader {
    private static Properties properties;
    
    // Load the file ONCE when class is first used
    static {
        properties = new Properties();
        FileInputStream file = new FileInputStream(
            "src/main/resources/config-staging.properties"
        );
        properties.load(file);
    }
    
    // Method to get any property value
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
```

**How to use it:**
```java
String email = ConfigReader.getProperty("testEmail");
// Returns: "uday.b@labsquire.com"

String password = ConfigReader.getProperty("testPassword");
// Returns: "1234567"
```

**Why separate class?**
✅ Used by ALL tests (reusable!)
✅ Change once, affects all tests
✅ No hardcoded values in code

---

### **STEP 3: LoginPage Class (API Call Logic)**

**File:** `src/main/java/com/mlx/api/pages/MLXLoginPage.java`

```java
package com.mlx.api.pages;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * ROLE: Handles ALL login API interactions
 * This is the CORE - it knows HOW to call the API
 */
public class MLXLoginPage {
    
    private Response response;
    private static final String LOGIN_ENDPOINT = "/users/login";
    
    // ========== METHOD 1: MAKE THE API CALL ==========
    public Response login(String email, String password, String appType) {
        
        // Step 1: Create JSON body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("application_type", appType);
        
        // Step 2: Build request with headers
        response = RestAssured.given()
            .header("Content-Type", "application/json")
            .header("accept", "application/json")
            .body(requestBody)
            .log().all()  // Print request details
        
        // Step 3: Send POST request to API
        .post(LOGIN_ENDPOINT);
        
        // Step 4: Log response
        response.then().log().all();
        
        return response;
    }
    
    // ========== METHOD 2: GET AUTH TOKEN ==========
    public String getAuthToken() {
        // Extract token from JSON response
        return response.jsonPath().getString("data.token");
    }
    
    // ========== METHOD 3: GET USER ID ==========
    public String getUserId() {
        return response.jsonPath().getString("data.user._id");
    }
    
    // ========== METHOD 4: GET EMAIL ==========
    public String getUserEmail() {
        return response.jsonPath().getString("data.user.email");
    }
    
    // ========== METHOD 5: GET STATUS CODE ==========
    public int getStatusCode() {
        return response.getStatusCode();
    }
    
    // ========== METHOD 6: CHECK IF LOGIN SUCCESSFUL ==========
    public boolean isLoginSuccessful() {
        return getStatusCode() == 200 && getAuthToken() != null;
    }
}
```

**Why separate class for API logic?**
✅ **Reusability:** Call `login()` from ANY test class
✅ **Maintainability:** API changes? Update only this file
✅ **Clean Tests:** Tests don't need to know API details
✅ **Helper Methods:** Extract data easily (`getAuthToken()`)

---

### **STEP 4: BaseTest Class (Common Setup)**

**File:** `src/test/java/com/mlx/api/base/BaseTest.java`

```java
package com.mlx.api.base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import com.mlx.api.utils.ConfigReader;

/**
 * ROLE: Setup code that runs BEFORE all tests
 */
public class BaseTest {
    
    @BeforeSuite
    public void setup() {
        // Set base URL from config file
        RestAssured.baseURI = ConfigReader.getProperty("baseURI");
        
        // Initialize reports, logging, etc.
        System.out.println("Test Suite Starting...");
        System.out.println("Base URI: " + RestAssured.baseURI);
    }
    
    @AfterSuite
    public void teardown() {
        System.out.println("Test Suite Completed!");
    }
}
```

**Why separate base class?**
✅ Common setup runs ONCE for all tests
✅ All test classes inherit this
✅ DRY principle (Don't Repeat Yourself)

---

### **STEP 5: LoginTest Class (Test Scenarios)**

**File:** `src/test/java/com/mlx/api/tests/MLXLoginTest.java`

```java
package com.mlx.api.tests;

import com.mlx.api.base.BaseTest;
import com.mlx.api.pages.MLXLoginPage;
import com.mlx.api.utils.ConfigReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * ROLE: Contains test scenarios (WHAT to test)
 */
public class MLXLoginTest extends BaseTest {  // Inherits setup
    
    private MLXLoginPage loginPage;  // Page object
    private String testEmail;
    private String testPassword;
    
    @BeforeClass
    public void setupLoginTests() {
        // Step 1: Create page object
        loginPage = new MLXLoginPage();
        
        // Step 2: Get test data from config
        testEmail = ConfigReader.getProperty("testEmail");
        testPassword = ConfigReader.getProperty("testPassword");
    }
    
    @Test(priority = 1)
    public void testValidLogin() {
        // ========== CALL THE API ==========
        Response response = loginPage.login(testEmail, testPassword, "web");
        
        // ========== VALIDATIONS ==========
        // Validation 1: Status code
        Assert.assertEquals(loginPage.getStatusCode(), 200);
        
        // Validation 2: Token exists
        String token = loginPage.getAuthToken();
        Assert.assertNotNull(token);
        
        // Validation 3: Email matches
        String returnedEmail = loginPage.getUserEmail();
        Assert.assertEquals(returnedEmail, testEmail);
        
        // Validation 4: Login successful
        Assert.assertTrue(loginPage.isLoginSuccessful());
        
        System.out.println("✅ Login Successful!");
        System.out.println("Token: " + token);
    }
    
    @Test(priority = 2)
    public void testInvalidPassword() {
        // Use same loginPage object, different data!
        Response response = loginPage.login(testEmail, "wrongpass", "web");
        
        // Should fail
        Assert.assertEquals(loginPage.getStatusCode(), 400);
        Assert.assertFalse(loginPage.isLoginSuccessful());
        
        System.out.println("✅ Invalid login correctly rejected!");
    }
}
```

**Why separate test class?**
✅ **Focus:** Only test logic, no API details
✅ **Readable:** Easy to understand test scenarios
✅ **Multiple Tests:** Add 100 tests using same page object

---

## 🔄 Complete Flow Visualization

```
┌─────────────────────────────────────────────────────────────┐
│                    TEST EXECUTION FLOW                      │
└─────────────────────────────────────────────────────────────┘

STEP 1: User runs test
   ↓
   mvn test

STEP 2: BaseTest.setup() runs FIRST
   ↓
   RestAssured.baseURI = "https://staging-api-mlx.labsquire.com"

STEP 3: MLXLoginTest.setupLoginTests() runs
   ↓
   loginPage = new MLXLoginPage()
   testEmail = ConfigReader.getProperty("testEmail")  → "uday.b@labsquire.com"
   testPassword = ConfigReader.getProperty("testPassword") → "1234567"

STEP 4: Test method testValidLogin() runs
   ↓
   Response response = loginPage.login(testEmail, testPassword, "web")
   
   ┌─────────────────────────────────────────────────┐
   │  INSIDE LoginPage.login() method:              │
   │  1. Create JSON: {"email": "...", "password"...}│
   │  2. Add headers: Content-Type, accept          │
   │  3. POST to: /users/login                      │
   │  4. Get response from API                      │
   └─────────────────────────────────────────────────┘
   
   ↓
   API returns:
   {
     "status": 200,
     "data": {
       "token": "eyJhbGc...",
       "user": {
         "_id": "123",
         "email": "uday.b@labsquire.com"
       }
     }
   }

STEP 5: Test extracts data using helper methods
   ↓
   int status = loginPage.getStatusCode()           → 200
   String token = loginPage.getAuthToken()          → "eyJhbGc..."
   String email = loginPage.getUserEmail()          → "uday.b@labsquire.com"

STEP 6: Test validates
   ↓
   Assert.assertEquals(status, 200)                 ✅ PASS
   Assert.assertNotNull(token)                      ✅ PASS
   Assert.assertEquals(email, testEmail)            ✅ PASS

STEP 7: Test completes
   ↓
   Generate report
```

---

## 🎯 Benefits of This Separation

### **1. Reusability Example:**

**Without POM (BAD):**
```java
// Test 1
@Test
public void test1() {
    Map<String, String> body = new HashMap<>();
    body.put("email", "test@test.com");
    body.put("password", "123");
    Response r = RestAssured.given().body(body).post("/login");
    // ... 50 lines of code
}

// Test 2
@Test
public void test2() {
    Map<String, String> body = new HashMap<>();  // DUPLICATE!
    body.put("email", "test2@test.com");
    body.put("password", "456");
    Response r = RestAssured.given().body(body).post("/login");  // DUPLICATE!
    // ... same 50 lines repeated
}
```

**With POM (GOOD):**
```java
// Test 1
@Test
public void test1() {
    loginPage.login("test@test.com", "123", "web");  // 1 line!
}

// Test 2
@Test
public void test2() {
    loginPage.login("test2@test.com", "456", "web");  // 1 line!
}
```

---

### **2. Maintainability Example:**

**Scenario:** API changes from `/login` to `/auth/login`

**Without POM:**
```
Need to update 50 test files! 😱
Find & Replace in ALL files
High chance of missing some
```

**With POM:**
```java
// Update ONLY LoginPage.java (1 file!)
private static final String LOGIN_ENDPOINT = "/auth/login";

// All 50 tests automatically fixed! ✅
```

---

### **3. Readability Example:**

**Without POM:**
```java
@Test
public void testUserCanLogin() {
    Map<String, String> body = new HashMap<>();
    body.put("email", "test@test.com");
    body.put("password", "123");
    Response r = RestAssured.given()
        .header("Content-Type", "application/json")
        .header("accept", "application/json")
        .body(body)
        .post("/login");
    int code = r.getStatusCode();
    Assert.assertEquals(code, 200);
    String token = r.jsonPath().getString("data.token");
    Assert.assertNotNull(token);
}
// Hard to understand WHAT we're testing!
```

**With POM:**
```java
@Test
public void testUserCanLogin() {
    loginPage.login(email, password, "web");
    Assert.assertTrue(loginPage.isLoginSuccessful());
}
// Crystal clear WHAT we're testing! ✅
```

---

## 📊 Class Responsibilities Summary

| Class | Responsibility | Contains |
|-------|---------------|----------|
| **config-staging.properties** | Store test data | Email, password, URLs |
| **ConfigReader.java** | Read config file | `getProperty()` method |
| **MLXLoginPage.java** | API interaction | `login()`, `getAuthToken()` |
| **BaseTest.java** | Common setup | `@BeforeSuite`, `@AfterSuite` |
| **MLXLoginTest.java** | Test scenarios | `@Test` methods with validations |

---

## 🔥 Simple Working Example

Let me create a **super simple example** from scratch:

### **Example: Testing a Weather API**

**API:** `GET https://api.weather.com/current?city=London`

**Response:**
```json
{
  "temperature": 20,
  "condition": "Sunny"
}
```

---

#### **Class 1: WeatherPage.java** (API Logic)

```java
public class WeatherPage {
    private Response response;
    
    // Method to call API
    public Response getWeather(String city) {
        response = RestAssured.given()
            .queryParam("city", city)
            .get("/current");
        return response;
    }
    
    // Helper to get temperature
    public int getTemperature() {
        return response.jsonPath().getInt("temperature");
    }
    
    // Helper to get condition
    public String getCondition() {
        return response.jsonPath().getString("condition");
    }
}
```

---

#### **Class 2: WeatherTest.java** (Tests)

```java
public class WeatherTest extends BaseTest {
    
    private WeatherPage weatherPage;
    
    @BeforeClass
    public void setup() {
        weatherPage = new WeatherPage();  // Create page object
    }
    
    @Test
    public void testLondonWeather() {
        // Call API
        weatherPage.getWeather("London");
        
        // Validate
        int temp = weatherPage.getTemperature();
        Assert.assertTrue(temp > 0, "Temperature should be positive");
        
        String condition = weatherPage.getCondition();
        Assert.assertNotNull(condition, "Condition should exist");
        
        System.out.println("London: " + temp + "°C, " + condition);
    }
    
    @Test
    public void testParisWeather() {
        // Same page object, different city!
        weatherPage.getWeather("Paris");
        
        int temp = weatherPage.getTemperature();
        String condition = weatherPage.getCondition();
        
        System.out.println("Paris: " + temp + "°C, " + condition);
    }
}
```

**See the benefit?**
- Called API 2 times with just `weatherPage.getWeather(city)`
- No duplicate code!
- Easy to add 100 more city tests

---

## 🎓 Key Takeaways

### **Why We Divide One API Call Into Multiple Classes:**

1. **ConfigReader** → Stores changeable data (email, password, URLs)
2. **LoginPage** → Knows HOW to call API (request logic)
3. **BaseTest** → Common setup for all tests
4. **LoginTest** → Knows WHAT to test (test scenarios)

### **Golden Rules:**

✅ **One Responsibility:** Each class does ONE thing well
✅ **Reusability:** Write once, use everywhere
✅ **Maintainability:** Change in one place affects all
✅ **Readability:** Clean, understandable code

---

## 🚀 Next Steps for You

1. **Study your MLXLoginPage.java** - See how it encapsulates API logic
2. **Study your MLXLoginTest.java** - See how it uses the page object
3. **Try creating a new API:** Register, Logout, etc.
4. **Follow the same pattern:** Create RegisterPage.java → RegisterTest.java

---

**Remember:** 
- **Page Object = API Call Logic** (How to call API)
- **Test Class = Test Scenarios** (What to test)
- **ConfigReader = Test Data** (What data to use)

This separation makes your code **professional, maintainable, and scalable**! 🎯

---

**Last Updated:** January 8, 2026
**Created For:** Understanding RestAssured & POM Pattern
