package com.mlx.api.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Extent Report Manager for generating HTML reports
 */
public class ExtentReportManager {
    
    private static ExtentReports extent;
    private static String reportPath;
    
    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }
    
    private static ExtentReports createInstance() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        reportPath = "test-output/ExtentReports/MLX_Order_API_Report_" + timestamp + ".html";
        
        // Create directory if it doesn't exist
        File reportDir = new File("test-output/ExtentReports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("MLX Order API Automation Report");
        sparkReporter.config().setReportName("MLX Order Creation API Test Results");
        sparkReporter.config().setEncoding("UTF-8");
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        
        // Add custom CSS for highlighting order numbers
        sparkReporter.config().setCss(
            ".order-number { " +
            "  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); " +
            "  color: white; " +
            "  padding: 8px 16px; " +
            "  border-radius: 6px; " +
            "  font-weight: bold; " +
            "  font-size: 14px; " +
            "  display: inline-block; " +
            "  margin: 4px 0; " +
            "  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3); " +
            "  font-family: 'Courier New', monospace; " +
            "} " +
            ".order-number-success { " +
            "  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); " +
            "  color: white; " +
            "  padding: 8px 16px; " +
            "  border-radius: 6px; " +
            "  font-weight: bold; " +
            "  font-size: 14px; " +
            "  display: inline-block; " +
            "  margin: 4px 0; " +
            "  box-shadow: 0 2px 8px rgba(56, 239, 125, 0.4); " +
            "  font-family: 'Courier New', monospace; " +
            "} " +
            ".order-number-failed { " +
            "  background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%); " +
            "  color: white; " +
            "  padding: 8px 16px; " +
            "  border-radius: 6px; " +
            "  font-weight: bold; " +
            "  font-size: 14px; " +
            "  display: inline-block; " +
            "  margin: 4px 0; " +
            "  box-shadow: 0 2px 8px rgba(235, 51, 73, 0.4); " +
            "  font-family: 'Courier New', monospace; " +
            "} " +
            ".order-highlight-box { " +
            "  background: #f8f9fa; " +
            "  border-left: 4px solid #667eea; " +
            "  padding: 12px; " +
            "  margin: 8px 0; " +
            "  border-radius: 4px; " +
            "} " +
            ".order-highlight-box-success { " +
            "  background: #e8f5e9; " +
            "  border-left: 4px solid #38ef7d; " +
            "  padding: 12px; " +
            "  margin: 8px 0; " +
            "  border-radius: 4px; " +
            "} " +
            ".order-highlight-box-failed { " +
            "  background: #ffebee; " +
            "  border-left: 4px solid #eb3349; " +
            "  padding: 12px; " +
            "  margin: 8px 0; " +
            "  border-radius: 4px; " +
            "}"
        );
        
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application", "Mobile Lab Express (MLX)");
        extent.setSystemInfo("Environment", "Staging");
        extent.setSystemInfo("Base URL", "https://staging-api-mlx.labsquire.com");
        extent.setSystemInfo("Test Module", "Order Creation API");
        extent.setSystemInfo("Framework", "RestAssured + TestNG");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        
        return extent;
    }
    
    public static void flush() {
        if (extent != null) {
            extent.flush();
            System.out.println("\n========================================");
            System.out.println("HTML Report Generated Successfully!");
            System.out.println("Report Location: " + reportPath);
            System.out.println("========================================\n");
        }
    }
    
    public static String getReportPath() {
        return reportPath;
    }
}
