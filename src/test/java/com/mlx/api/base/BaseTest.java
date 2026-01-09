package com.mlx.api.base;

import com.mlx.api.utils.ConfigReader;
import com.mlx.api.utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Base Test class for all API tests
 * Contains common setup and teardown methods
 */
public class BaseTest {
    
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected static ExtentReports extent;
    protected static ExtentTest test;
    
    @BeforeSuite
    public void setup() {
        logger.info("========================================");
        logger.info("Starting API Test Suite");
        logger.info("========================================");
        
        // Initialize Extent Reports
        extent = ExtentReportManager.getInstance();
        
        // Set base URI from config
        RestAssured.baseURI = ConfigReader.getProperty("baseURI");
        
        logger.info("Base URI set to: " + RestAssured.baseURI);
        logger.info("Test suite setup completed");
    }
    
    @AfterSuite
    public void tearDown() {
        logger.info("========================================");
        logger.info("Test Suite Execution Completed");
        logger.info("========================================");
        
        // Flush Extent Reports
        if (extent != null) {
            extent.flush();
        }
    }
}
