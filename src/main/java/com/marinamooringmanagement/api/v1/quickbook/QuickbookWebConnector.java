package com.marinamooringmanagement.api.v1.quickbook;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RestController
@RequestMapping("/api/quickbooks")
public class QuickbookWebConnector {

    private static final String REQUEST_XML_PATH = "C:\\Users\\prana\\OneDrive\\Desktop\\qb_xml\\request.xml";
    private static final String RESPONSE_XML_PATH = "C:\\Users\\prana\\OneDrive\\Desktop\\qb_xml\\response.xml";
    private static final String QB_API_URL = "http://localhost:8082/api/quickbooks"; // Adjust as needed

    @PostMapping
    public String handleQBRequest(@RequestBody String request, HttpServletRequest httpRequest) {
        System.out.println("Received request: " + request);

        if (request.contains("authenticate")) {
            return authenticateResponse();
        } else if (request.contains("sendRequestXML")) {
            String customerQueryRequest = createCustomerQueryRequest();
            return wrapInQBXML(customerQueryRequest);
        } else if (request.contains("receiveResponseXML")) {
            return processResponseXML(request);
        } else {
            return wrapInQBXML("<Error>Unsupported request</Error>");
        }
    }

    // Authentication response for the Web Connector
    private String authenticateResponse() {
        return "<?xml version=\"1.0\"?>"
                + "<QBXML>"
                + "<QBXMLMsgsRs>"
                + "<AuthenticateRs>"
                + "<AuthReturn>"
                + "<UserName>pranav212salaria@gmail.com</UserName>"
                + "<SessionTicket>SESSION123</SessionTicket>"
                + "</AuthReturn>"
                + "</AuthenticateRs>"
                + "</QBXMLMsgsRs>"
                + "</QBXML>";
    }

    // Create a Customer Query Request in QBXML format
    private String createCustomerQueryRequest() {
        return "<CustomerQueryRq>"
                + "<MaxReturned>100</MaxReturned>"
                + "</CustomerQueryRq>";
    }

    // Process the response XML from QuickBooks
    private String processResponseXML(String responseXML) {
        System.out.println("Received response from QuickBooks: " + responseXML);

        // Save the response XML to a file
        saveXmlToFile(responseXML, RESPONSE_XML_PATH);

        // Check for customer data in the response
        if (responseXML.contains("<CustomerQueryRs>") && !responseXML.contains("<Error>")) {
            // Parse the response to extract customer data
            return "Customers found"; // or return actual customer data
        } else {
            return "No customers found or error in the response.";
        }
    }

    // Wrap any inner XML with QBXML format
    private String wrapInQBXML(String innerXML) {
        return "<?xml version=\"1.0\"?>"
                + "<QBXML>"
                + "<QBXMLMsgsRq onError=\"stopOnError\">"
                + innerXML
                + "</QBXMLMsgsRq>"
                + "</QBXML>";
    }

    // Fetch customers from QuickBooks Desktop via the Web Connector
    @GetMapping("/customers")
    public String fetchCustomers() {
        String requestXML = wrapInQBXML(createCustomerQueryRequest());
        String responseXML = sendRequestToQuickBooks(requestXML);

        if (responseXML == null) {
            return "Failed to fetch customers.";
        }
        return responseXML;
    }

    // Send a QBXML request to QuickBooks Web Connector
    private String sendRequestToQuickBooks(String requestXML) {
        // Save the request XML to a file
        saveXmlToFile(requestXML, REQUEST_XML_PATH);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(QB_API_URL);
            post.setEntity(new StringEntity(requestXML));
            post.setHeader("Content-Type", "application/xml");

            try (CloseableHttpResponse response = client.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    System.err.println("Failed with HTTP error code: " + statusCode);
                    return null;
                }

                // Convert response to string
                String responseXML = EntityUtils.toString(response.getEntity());

                // Save the response XML to a file
                saveXmlToFile(responseXML, RESPONSE_XML_PATH);

                return responseXML;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the error instead
            return null;
        }
    }

    // Helper method to save XML content to a file
    private void saveXmlToFile(String xmlContent, String filePath) {
        File file = new File(filePath);
        try {
            // Create parent directories if they don't exist
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(xmlContent);
                System.out.println("XML saved to: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log the error if saving fails
        }
    }
}


