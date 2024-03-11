package com.example.helloworld.controller;

import com.syncfusion.ej2.wordprocessor.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;




@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FusionController {

    @PostMapping("/api/wordeditor/Import")
    public ResponseEntity<?> processDocument(@RequestParam("fileLink") String fileLink) throws Exception {
        try {
            // Download the document content from the provided link
            byte[] documentContent = downloadDocumentContent(fileLink);

            // Process the downloaded document content
            String processedContent = WordProcessorHelper.load(new ByteArrayInputStream(documentContent), FormatType.Docx);

            // Return the processed content
            return ResponseEntity.ok(processedContent);
        } catch (IOException e) {
            // Handle any exceptions and return an error response
            String errorMessage = "Error downloading or processing the document: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(errorMessage));
        }
    }

    // Method to download document content from a URL
    private byte[] downloadDocumentContent(String fileLink) throws IOException {
        URL url = new URL(fileLink);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // Check if the request was successful (HTTP status code 200)
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Read the document content from the input stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return outputStream.toByteArray();
        } else {
            throw new IOException("Failed to download document. HTTP status code: " + connection.getResponseCode());
        }
    }

    // Custom error response object
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}