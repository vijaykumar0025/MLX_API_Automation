package com.mlx.api.pages;

import com.mlx.api.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base Page class for all Page Objects
 * Contains common methods used across all pages
 */
public class BasePage {
    
    protected static final Logger logger = LogManager.getLogger(BasePage.class); // what is this for?
    protected Response response;
    
    /**
     * Get property from config file
     */
    protected String getConfigProperty(String key) {
        return ConfigReader.getProperty(key);
    }
    
    /**
     * Set base URI
     */
    protected void setBaseURI(String baseURI) {
        RestAssured.baseURI = baseURI;					// 
    }
    
    /**
     * Get base URI
     */
    protected String getBaseURI() {
        return RestAssured.baseURI;
    }
}
