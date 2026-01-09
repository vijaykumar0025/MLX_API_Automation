# Order Number Highlighting in HTML Reports - Implementation Guide

## Overview
The MLX API Automation framework now features **enhanced HTML reporting** with visually highlighted order numbers for better readability and quick identification of test results.

## What Has Been Enhanced

### 1. Custom CSS Styling (ExtentReportManager.java)
Added custom CSS classes to the Extent Reports for highlighting order numbers:

#### Success Styling (Green Gradient)
- **Class:** `order-number-success`
- **Visual:** Green gradient background (teal to lime)
- **Use Case:** Successful order creation
- **Features:** 
  - Bold white text
  - Rounded corners
  - Box shadow for depth
  - Monospace font for order IDs

#### Failed Styling (Red Gradient)
- **Class:** `order-number-failed`
- **Visual:** Red gradient background
- **Use Case:** Failed validations or rejected orders
- **Features:**
  - Bold white text
  - Rounded corners
  - Box shadow for emphasis
  - Monospace font for order IDs

#### Highlight Boxes
- **Success Box:** Light green background with green left border
- **Failed Box:** Light red background with red left border
- **Purpose:** Group related order information together

### 2. Helper Methods in MLXOrderPage.java

#### `formatOrderNumberSuccess(String orderId)`
Formats a single order ID with success highlighting.
```java
String formattedOrder = orderPage.formatOrderNumberSuccess("ORD123456");
test.pass(formattedOrder);
```
**Output:** Green highlighted order number with checkmark (✓)

#### `formatOrderNumberFailed(String orderId)`
Formats a single order ID with failed/error highlighting.
```java
String formattedOrder = orderPage.formatOrderNumberFailed("ORD123456");
test.fail(formattedOrder);
```
**Output:** Red highlighted order number with cross mark (✗)

#### `formatMultipleOrderNumbersSuccess(List<String> orderIds)`
Formats multiple order IDs (for standing orders) with success highlighting.
```java
List<String> allOrderIds = orderPage.getAllOrderIds();
test.pass(orderPage.formatMultipleOrderNumbersSuccess(allOrderIds));
```
**Output:** Green box containing all order IDs with numbering

#### `getAllOrderIds()`
Retrieves all order IDs from a standing order response.
```java
List<String> orderIds = orderPage.getAllOrderIds();
// Returns: ["ORD001", "ORD002", "ORD003", "ORD004"]
```

#### `formatOrderSummarySuccess(String orderId, String serviceDate, String patientName)`
Creates a comprehensive order summary with highlighting.
```java
String summary = orderPage.formatOrderSummarySuccess(
    "ORD123456", 
    "01-15-2026", 
    "John Doe"
);
test.pass(summary);
```

#### `formatOrderError(String errorType, String errorDetails)`
Formats error messages for failed order scenarios.
```java
String error = orderPage.formatOrderError(
    "PAST DATE ERROR", 
    "Orders cannot be created with past dates"
);
test.fail(error);
```

### 3. Updated Test Cases (MLXCreateOrderTest.java)

#### Positive Test Cases - Success Highlighting
When orders are created successfully:
```java
// Single order
test.pass(orderPage.formatOrderNumberSuccess(orderId));

// Multiple orders (standing orders)
List<String> allOrderIds = orderPage.getAllOrderIds();
test.pass(orderPage.formatMultipleOrderNumbersSuccess(allOrderIds));
```

#### Negative Test Cases - Failed Highlighting
When orders should be rejected (e.g., past dates):
```java
// Highlight failed orders that should have been rejected
test.fail(orderPage.formatOrderNumberFailed(orderId) + 
         "<br/><strong>Service Date:</strong> " + serviceDate + 
         " (PAST DATE - SHOULD BE REJECTED)");
```

## Visual Examples

### Success Scenario (Green Highlighting)
```
┌─────────────────────────────────────────────────┐
│  ✓ Orders Created Successfully:                 │
│  ┌──────────────────────────────────────────┐  │
│  │  Order 1: MLX-2026-001234              │  │
│  └──────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────┐  │
│  │  Order 2: MLX-2026-001235              │  │
│  └──────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────┐  │
│  │  Order 3: MLX-2026-001236              │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
(Green gradient boxes with white text)
```

### Failed Scenario (Red Highlighting)
```
┌─────────────────────────────────────────────────┐
│  ✗ API ACCEPTED PAST DATES                       │
│  Details: The API should have rejected orders   │
│  with past dates but accepted them              │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │  ✗ Order ID: MLX-2026-001237           │  │
│  └──────────────────────────────────────────┘  │
│  Service Date: 12-28-2025 (PAST DATE)          │
└─────────────────────────────────────────────────┘
(Red gradient boxes with white text)
```

## Benefits

### 1. **Quick Visual Identification**
- Order IDs stand out immediately in the report
- Color coding (green/red) provides instant status recognition
- No need to read through text to find order numbers

### 2. **Professional Appearance**
- Gradient backgrounds with shadow effects
- Consistent styling across all test reports
- Modern, clean design

### 3. **Better Bug Tracking**
- Failed order numbers are immediately visible in red
- Easy to copy order IDs for further investigation
- Clear visual separation between success and failure

### 4. **Enhanced Reporting for Stakeholders**
- Non-technical stakeholders can quickly understand results
- Order IDs are prominently displayed
- Professional HTML reports for client presentations

## How to Use in Your Tests

### Example 1: Successful Order Creation
```java
@Test
public void testCreateOrder() {
    // ... create order logic ...
    
    if (orderPage.validateOrderIdGenerated()) {
        List<String> orderIds = orderPage.getAllOrderIds();
        test.pass(orderPage.formatMultipleOrderNumbersSuccess(orderIds));
    }
}
```

### Example 2: Failed Order Validation
```java
@Test
public void testPastDateRejection() {
    // ... send request with past date ...
    
    if (!orderPage.validatePastDateError()) {
        String orderId = orderPage.getOrderId();
        test.fail(orderPage.formatOrderError(
            "VALIDATION BUG",
            "Past date was accepted when it should be rejected"
        ));
        test.fail(orderPage.formatOrderNumberFailed(orderId));
    }
}
```

### Example 3: Order Summary
```java
String summary = orderPage.formatOrderSummarySuccess(
    orderId,
    "01-15-2026",
    "John Doe"
);
test.pass(summary);
```

## CSS Classes Reference

| Class Name | Purpose | Background | Text Color |
|------------|---------|------------|------------|
| `order-number-success` | Success badge | Green gradient | White |
| `order-number-failed` | Failed badge | Red gradient | White |
| `order-highlight-box-success` | Success container | Light green | Inherited |
| `order-highlight-box-failed` | Failed container | Light red | Inherited |

## Report Generation

The HTML report is automatically generated after test execution:
- **Location:** `test-output/ExtentReports/MLX_Order_API_Report_TIMESTAMP.html`
- **Auto-opens:** System prints report location in console
- **Styling:** All custom CSS is embedded in the report

## Best Practices

1. **Use formatMultipleOrderNumbersSuccess()** for standing orders with multiple IDs
2. **Use formatOrderNumberSuccess()** for single order scenarios
3. **Use formatOrderNumberFailed()** when highlighting bugs or validation failures
4. **Use formatOrderError()** to create detailed error boxes with context
5. **Always include order IDs** in test.pass() or test.fail() for traceability

## Future Enhancements

Possible additions:
- Order status badges (PENDING, ACTIVE, CANCELLED)
- Clickable order IDs (with links to order details)
- Timeline visualization for standing orders
- Export order IDs to CSV functionality

## Troubleshooting

### Issue: Styling not showing in report
**Solution:** Ensure ExtentReportManager.getInstance() is called before creating tests

### Issue: Order IDs not formatted
**Solution:** Use the formatting methods from MLXOrderPage class

### Issue: Multiple formats mixing
**Solution:** Use consistent formatting - either plain text OR HTML, not both in same message

---

**Created:** January 2026  
**Framework:** MLX API Automation  
**Version:** 1.0  
**Author:** Test Automation Team
