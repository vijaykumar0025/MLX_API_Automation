# MLX Login API - Comprehensive Test Guide

## 📋 API Details

**Endpoint:** `POST https://staging-api-mlx.labsquire.com/users/login`

**Headers:**
```
accept: application/json, text/plain, */*
content-type: application/json
origin: https://staging-mlx.labsquire.com
referer: https://staging-mlx.labsquire.com/
```

**Request Body:**
```json
{
  "email": "uday.b@labsquire.com",
  "password": "1234567",
  "application_type": "web"
}
```

**Expected Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful",
  "data": {
    "user": {
      "id": "12345",
      "email": "uday.b@labsquire.com",
      "name": "Uday",
      "role": "admin"
    }
  }
}
```

---

## ✅ 14 Comprehensive Validations

### Test: `testMLXLoginWithValidCredentials`

#### **VALIDATION 1: Status Code**
- **Check:** Response status code equals 200
- **Why:** Confirms API is accessible and request was successful
- **Expected:** 200 OK

#### **VALIDATION 2: Response Time**
- **Check:** Response time < 5000ms (5 seconds)
- **Why:** Ensures API performance meets acceptable standards
- **Expected:** < 5000ms

#### **VALIDATION 3: Content-Type**
- **Check:** Response header Content-Type is application/json
- **Why:** Verifies response is in expected JSON format
- **Expected:** application/json

#### **VALIDATION 4: Valid JSON Response**
- **Check:** Response body is valid, parseable JSON
- **Why:** Ensures response can be processed by client applications
- **Expected:** Valid JSON structure

#### **VALIDATION 5: Response Not Empty**
- **Check:** Response body is not empty
- **Why:** Confirms API returned data
- **Expected:** Non-empty response body

#### **VALIDATION 6: Auth Token Present**
- **Check:** Authentication token exists and is not empty
- **Why:** Required for subsequent authenticated API calls
- **Expected:** Non-null, non-empty token string

#### **VALIDATION 7: User ID Present**
- **Check:** User ID exists in response data.user.id
- **Why:** Essential for user identification in the system
- **Expected:** Non-null user ID

#### **VALIDATION 8: Email Verification**
- **Check:** Returned email matches the login email
- **Why:** Confirms correct user account was authenticated
- **Expected:** Email matches input email

#### **VALIDATION 9: User Name Present**
- **Check:** User name exists in response
- **Why:** Verifies complete user data is returned
- **Expected:** Non-null user name

#### **VALIDATION 10: Success Message**
- **Check:** Success message exists in response
- **Why:** Confirms positive authentication result
- **Expected:** Non-null success message

#### **VALIDATION 11: Required Fields Present**
- **Check:** All required fields (token, user.id, user.email) exist
- **Why:** Ensures response completeness for client use
- **Expected:** All required fields present

#### **VALIDATION 12: Login Success Flag**
- **Check:** Combined validation of status=200 + token present
- **Why:** Overall login success confirmation
- **Expected:** true

#### **VALIDATION 13: Response Structure**
- **Check:** Response contains data.user object hierarchy
- **Why:** Validates correct JSON structure
- **Expected:** Proper nested object structure

#### **VALIDATION 14: No Error Messages**
- **Check:** No error fields in response
- **Why:** Confirms no errors occurred during processing
- **Expected:** error field is null

---

## 🧪 Test Scenarios Covered

### 1. **Positive Test - Valid Credentials**
- Tests successful login with correct email and password
- Validates all 14 checkpoints
- Priority: 1 (runs first)

### 2. **Negative Test - Invalid Email**
- Tests login with non-existent email
- Expected: 401/400/404 status code
- Validates error handling

### 3. **Negative Test - Invalid Password**
- Tests login with incorrect password
- Expected: 401/400 status code
- Validates authentication failure

### 4. **Negative Test - Missing Email**
- Tests login with null email field
- Expected: 400+ status code
- Validates required field enforcement

### 5. **Negative Test - Empty Credentials**
- Tests login with empty strings
- Expected: 400+ status code
- Validates input validation

### 6. **Functional Test - Application Types**
- Tests both "web" and "mobile" application types
- Validates application_type parameter handling

---

## 🚀 How to Run Tests

### **Option 1: Using TestNG XML (Recommended)**
```bash
cd C:\Users\VIJAY\eclipse-workspace\MLX_API_Automation
mvn clean test -DsuiteXmlFile=testng-mlx-login.xml
```

### **Option 2: Run Single Test Method**
```bash
mvn clean test -Dtest=MLXLoginTest#testMLXLoginWithValidCredentials
```

### **Option 3: Run All Tests in Class**
```bash
mvn clean test -Dtest=MLXLoginTest
```

### **Option 4: From Eclipse**
1. Right-click on `testng-mlx-login.xml`
2. Select "Run As" → "TestNG Suite"

### **Option 5: Using Batch File**
```bash
run-mlx-login-tests.bat
```

---

## 📊 Expected Results

### **Console Output:**
```
[INFO] Running MLX Login API Test Suite
[INFO] ============================================
[INFO] MLX Login API Test Suite - Starting
[INFO] Test Email: uday.b@labsquire.com
[INFO] Application Type: web
[INFO] Max Response Time: 5000ms
[INFO] ============================================

Test: testMLXLoginWithValidCredentials
✓ VALIDATION 1: Status Code = 200
✓ VALIDATION 2: Response Time = 1234ms
✓ VALIDATION 3: Content-Type = application/json
✓ VALIDATION 4: Valid JSON = true
✓ VALIDATION 5: Response Not Empty = true
✓ VALIDATION 6: Auth Token Present = true
✓ VALIDATION 7: User ID = 12345
✓ VALIDATION 8: Email Matches = true
✓ VALIDATION 9: User Name Present = true
✓ VALIDATION 10: Success Message = "Login successful"
✓ VALIDATION 11: Required Fields = true
✓ VALIDATION 12: Login Successful = true
✓ VALIDATION 13: Response Structure = valid
✓ VALIDATION 14: No Errors = true

✅ ALL 14 VALIDATIONS PASSED!

Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

### **HTML Report:**
Location: `test-output/ExtentReports/MLX_API_Report_<timestamp>.html`

Features:
- Visual pass/fail indicators
- Detailed validation results
- Request/response logs
- Response time metrics
- Timestamp for each test

---

## 📁 Project Structure

```
MLX_API_Automation/
├── src/
│   ├── main/
│   │   ├── java/com/mlx/api/
│   │   │   ├── pages/
│   │   │   │   ├── BasePage.java
│   │   │   │   └── MLXLoginPage.java     ← Login API POM
│   │   │   └── utils/
│   │   │       ├── ConfigReader.java
│   │   │       ├── ExtentReportManager.java
│   │   │       └── TestListener.java
│   │   └── resources/
│   │       ├── config.properties          ← Main config
│   │       └── config-staging.properties  ← Staging config
│   └── test/
│       └── java/com/mlx/api/
│           └── tests/
│               └── MLXLoginTest.java      ← Test class
├── testng-mlx-login.xml                   ← TestNG suite
├── pom.xml                                ← Maven dependencies
└── MLX_LOGIN_TEST_GUIDE.md               ← This file
```

---

## 🔧 Configuration

**File:** `src/main/resources/config-staging.properties`

```properties
# Base URL
baseURI=https://staging-api-mlx.labsquire.com

# Test Credentials
testEmail=uday.b@labsquire.com
testPassword=1234567

# Application Settings
applicationType=web

# Performance Thresholds
maxResponseTime=5000
```

---

## 🐛 Troubleshooting

### Issue: Connection Timeout
**Solution:** Check network connectivity to staging server
```bash
ping staging-api-mlx.labsquire.com
```

### Issue: 401 Unauthorized with Valid Credentials
**Solution:** Verify credentials in config file are current

### Issue: Tests Not Running
**Solution:** Ensure Maven dependencies are downloaded
```bash
mvn clean install -U
```

### Issue: No Report Generated
**Solution:** Check TestListener is configured in testng.xml
```xml
<listener class-name="com.mlx.api.utils.TestListener"/>
```

---

## 📝 Notes

- All tests use staging environment credentials
- Response time threshold set to 5000ms (configurable)
- Tests run sequentially (preserve-order="true")
- Extent Reports generated after each run
- Logs available in `logs/mlx-api-automation.log`

---

## 🔐 Security

**Important:** 
- Never commit credentials to version control
- Use environment variables for sensitive data in CI/CD
- Rotate passwords regularly
- Use separate credentials for automation testing

---

## 📞 Support

For issues or questions:
1. Check logs in `logs/` folder
2. Review Extent Report for detailed failure info
3. Verify API endpoint is accessible
4. Confirm credentials are valid

---

**Last Updated:** January 2026  
**Version:** 1.0  
**Maintained By:** MLX QA Team
