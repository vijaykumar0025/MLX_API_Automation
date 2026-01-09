# 🎨 Visual API Flow Diagram

## 📊 How One API Call is Divided Into Multiple Classes

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         TRADITIONAL APPROACH (BAD)                           │
│                         Everything in ONE class                              │
└─────────────────────────────────────────────────────────────────────────────┘

LoginTest.java (500 lines!)
├── testLogin1() {
│   ├── Build request body
│   ├── Add headers
│   ├── Send POST /login
│   ├── Parse response
│   └── Validate
│   }
├── testLogin2() {
│   ├── Build request body (DUPLICATE!)
│   ├── Add headers (DUPLICATE!)
│   ├── Send POST /login (DUPLICATE!)
│   ├── Parse response (DUPLICATE!)
│   └── Validate
│   }
└── ... 50 more tests with duplicate code

❌ Problems: Code duplication, hard to maintain, messy


┌─────────────────────────────────────────────────────────────────────────────┐
│                         PAGE OBJECT MODEL (GOOD)                             │
│                    Divided into 4 specialized classes                        │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│ config-staging.      │ ◄─── Stores test data
│ properties           │      • baseURI
│                      │      • testEmail
│ Data Storage         │      • testPassword
└──────────┬───────────┘
           │
           │ reads
           ▼
┌──────────────────────┐
│ ConfigReader.java    │ ◄─── Reads configuration
│                      │      
│ getProperty(key)     │      Returns values when needed
└──────────┬───────────┘
           │
           │ provides data to
           ▼
┌──────────────────────┐
│ MLXLoginPage.java    │ ◄─── API interaction logic
│                      │      
│ Methods:             │      • login(email, password, type)
│ • login()            │      • getAuthToken()
│ • getAuthToken()     │      • getUserId()
│ • getUserId()        │      • getStatusCode()
│ • getEmail()         │      
│                      │      (All API logic HERE!)
└──────────┬───────────┘
           │
           │ used by
           ▼
┌──────────────────────┐
│ BaseTest.java        │ ◄─── Common test setup
│                      │      
│ @BeforeSuite         │      • Sets base URI
│ setup()              │      • Initializes reports
│                      │      • Runs ONCE for all tests
└──────────┬───────────┘
           │
           │ inherited by
           ▼
┌──────────────────────┐
│ MLXLoginTest.java    │ ◄─── Test scenarios ONLY
│                      │      
│ @Test                │      • testValidLogin()
│ testValidLogin() {   │      • testInvalidPassword()
│   loginPage.login()  │      • testMissingEmail()
│   Assert.assertEquals│      
│ }                    │      (No API logic, just tests!)
└──────────────────────┘

✅ Benefits: Reusable, maintainable, clean, scalable
```

---

## 🔄 Real Execution Flow - Step by Step

### **Scenario: User wants to test MLX Login API**

```
┌─────────────────────────────────────────────────────────────────┐
│ STEP 1: User Runs Test                                         │
└─────────────────────────────────────────────────────────────────┘
    
    Command: mvn test
    
    ↓

┌─────────────────────────────────────────────────────────────────┐
│ STEP 2: BaseTest.setup() Executes                              │
└─────────────────────────────────────────────────────────────────┘

    @BeforeSuite
    public void setup() {
        // Read from ConfigReader
        RestAssured.baseURI = ConfigReader.getProperty("baseURI");
        // Result: baseURI = "https://staging-api-mlx.labsquire.com"
    }
    
    ↓

┌─────────────────────────────────────────────────────────────────┐
│ STEP 3: MLXLoginTest.setupLoginTests() Executes                │
└─────────────────────────────────────────────────────────────────┘

    @BeforeClass
    public void setupLoginTests() {
        loginPage = new MLXLoginPage();  // Create page object
        
        // Get test data from ConfigReader
        testEmail = ConfigReader.getProperty("testEmail");
        // Result: "uday.b@labsquire.com"
        
        testPassword = ConfigReader.getProperty("testPassword");
        // Result: "1234567"
    }
    
    ↓

┌─────────────────────────────────────────────────────────────────┐
│ STEP 4: Test Method testMLXLoginWithValidCredentials() Runs    │
└─────────────────────────────────────────────────────────────────┘

    @Test
    public void testMLXLoginWithValidCredentials() {
        
        // Call the page object method
        Response response = loginPage.login(testEmail, testPassword, "web");
        
        ┌───────────────────────────────────────────────────┐
        │ INSIDE MLXLoginPage.login() method:              │
        │                                                   │
        │ 1. Create JSON body:                             │
        │    {                                             │
        │      "email": "uday.b@labsquire.com",           │
        │      "password": "1234567",                     │
        │      "application_type": "web"                  │
        │    }                                             │
        │                                                   │
        │ 2. Add headers:                                  │
        │    - Content-Type: application/json             │
        │    - accept: application/json                   │
        │                                                   │
        │ 3. Send POST request:                            │
        │    POST https://staging-api-mlx.labsquire.com/  │
        │         users/login                              │
        │                                                   │
        │ 4. Receive response                              │
        └───────────────────────────────────────────────────┘
    }
    
    ↓

┌─────────────────────────────────────────────────────────────────┐
│ STEP 5: API Response Received                                  │
└─────────────────────────────────────────────────────────────────┘

    {
        "status": 200,
        "success": true,
        "message": "Login successful",
        "data": {
            "user": {
                "_id": "695d3d09217c7f22cb54a7ae",
                "first_name": "Testing",
                "last_name": "Team",
                "email": "uday.b@labsquire.com"
            },
            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        }
    }
    
    ↓

┌─────────────────────────────────────────────────────────────────┐
│ STEP 6: Test Extracts Data Using Page Object Methods           │
└─────────────────────────────────────────────────────────────────┘

    // Use helper methods from LoginPage
    int statusCode = loginPage.getStatusCode();
    // Returns: 200
    
    String token = loginPage.getAuthToken();
    // Returns: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    
    String email = loginPage.getUserEmail();
    // Returns: "uday.b@labsquire.com"
    
    ↓

┌─────────────────────────────────────────────────────────────────┐
│ STEP 7: Test Performs Validations                              │
└─────────────────────────────────────────────────────────────────┘

    Assert.assertEquals(statusCode, 200);          ✅ PASS
    Assert.assertNotNull(token);                   ✅ PASS
    Assert.assertEquals(email, testEmail);         ✅ PASS
    
    ↓

┌─────────────────────────────────────────────────────────────────┐
│ STEP 8: Generate Reports                                       │
└─────────────────────────────────────────────────────────────────┘

    ExtentReportManager creates HTML report
    TestNG creates XML report
    
    ✅ Test Result: PASSED
```

---

## 💡 Reusability Demonstration

### **Example: Using Same Page Object in Multiple Tests**

```java
public class MLXLoginTest extends BaseTest {
    
    private MLXLoginPage loginPage;  // ONE page object
    
    @BeforeClass
    public void setup() {
        loginPage = new MLXLoginPage();  // Create ONCE
    }
    
    @Test
    public void test1_ValidLogin() {
        loginPage.login("user1@test.com", "pass1", "web");  // Use it
        Assert.assertEquals(loginPage.getStatusCode(), 200);
    }
    
    @Test
    public void test2_InvalidPassword() {
        loginPage.login("user1@test.com", "wrong", "web");  // Reuse it
        Assert.assertEquals(loginPage.getStatusCode(), 400);
    }
    
    @Test
    public void test3_MobileApp() {
        loginPage.login("user1@test.com", "pass1", "mobile");  // Reuse again
        Assert.assertEquals(loginPage.getStatusCode(), 200);
    }
}
```

**See?** 
- Created page object ONCE
- Used it 3 times
- No code duplication!

---

## 🎯 Multiple APIs Working Together

```
┌──────────────────────────────────────────────────────────────┐
│  Example: Login, then Get User Details                      │
└──────────────────────────────────────────────────────────────┘

public class APIFlowTest extends BaseTest {
    
    private MLXLoginPage loginPage;      // Page object for Login API
    private GetUserPage getUserPage;     // Page object for GetUser API
    
    @Test
    public void testCompleteFlow() {
        
        // ========== API 1: LOGIN ==========
        loginPage.login(email, password, "web");
        String token = loginPage.getAuthToken();  // Get token
        String userId = loginPage.getUserId();    // Get user ID
        
        // ========== API 2: GET USER (uses token from API 1!) ==========
        getUserPage.getUserDetails(token, userId);
        String firstName = getUserPage.getUserFirstName();
        String userEmail = getUserPage.getUserEmail();
        
        // Validate
        Assert.assertEquals(userEmail, email);
    }
}
```

**Flow:**
```
1. Call Login API          → loginPage.login()
2. Get token from response → loginPage.getAuthToken()
3. Use token in next API   → getUserPage.getUserDetails(token, userId)
4. Extract user details    → getUserPage.getUserFirstName()
5. Validate                → Assert.assertEquals()
```

---

## 📋 Quick Comparison Chart

| Aspect | Without POM | With POM |
|--------|-------------|----------|
| **Code Reuse** | ❌ Copy-paste everywhere | ✅ Write once, use anywhere |
| **Maintenance** | ❌ Update 50 files | ✅ Update 1 file |
| **Readability** | ❌ 100 lines per test | ✅ 10 lines per test |
| **API Changes** | ❌ Find & replace nightmare | ✅ Change in one place |
| **Test Focus** | ❌ Mixed with API logic | ✅ Pure test logic only |
| **Learning Curve** | ✅ Easy to start | ⚠️ Need to understand POM |
| **Professional** | ❌ Not industry standard | ✅ Industry best practice |

---

## 🔑 Key Concept Summary

### **The 4-Layer Architecture:**

```
┌─────────────────────────────────────┐
│  Layer 1: DATA                      │  config-staging.properties
│  What data to use                   │  (Email, password, URLs)
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Layer 2: DATA ACCESS               │  ConfigReader.java
│  How to read data                   │  (getProperty method)
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Layer 3: API LOGIC                 │  MLXLoginPage.java
│  How to call API                    │  (login, getToken methods)
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Layer 4: TEST LOGIC                │  MLXLoginTest.java
│  What to test                       │  (@Test methods)
└─────────────────────────────────────┘
```

Each layer has ONE responsibility!

---

## 🎓 Remember These Points:

1. **Page Object = API Call Wrapper**
   - Encapsulates API interaction
   - Provides helper methods
   - Hides complexity

2. **Test Class = Test Scenarios**
   - Uses page objects
   - Contains validations
   - Readable and clean

3. **ConfigReader = Data Provider**
   - Single source of truth
   - Easy to change
   - No hardcoded values

4. **BaseTest = Common Setup**
   - Runs once for all tests
   - Shared configuration
   - DRY principle

---

**Bottom Line:** 
One API call is divided to make code **reusable**, **maintainable**, and **professional**! 🚀
