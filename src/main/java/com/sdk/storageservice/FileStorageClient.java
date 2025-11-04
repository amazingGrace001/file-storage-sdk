package com.sdk.storageservice;


import com.sdk.storageservice.dto.ApiResponse;
import com.sdk.storageservice.dto.FileStorageDTO;
import com.sdk.storageservice.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class FileStorageClient {

    private static final Logger log = LoggerFactory.getLogger(FileStorageClient.class);

    private final String baseUrl;
    private final String apiKey;
    private final RestTemplate restTemplate;

    public FileStorageClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    /** Upload file as binary */
    public FileStorageDTO uploadFile(String fileName, UUID clientId, byte[] fileBytes) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", apiKey);
            headers.set("fileName", fileName);
            headers.set("clientId", clientId.toString());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            HttpEntity<byte[]> entity = new HttpEntity<>(fileBytes, headers);

            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    baseUrl + "/api/v1/files",
                    HttpMethod.POST,
                    entity,
                    ApiResponse.class
            );

            ApiResponse body = response.getBody();
            if (body == null || !body.isSuccess()) {
                throw new FileStorageException("Failed to upload file");
            }
            return (FileStorageDTO) body.getData();

        } catch (RestClientException e) {
            throw new FileStorageException("Upload failed: " + e.getMessage(), e);
        }
    }

    /** Retrieve file by ID */
    public FileStorageDTO getFileById(UUID fileId, UUID clientId) {
        try {
            String url = String.format("%s/api/v1/%s?clientId=%s", baseUrl, fileId, clientId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", apiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ApiResponse.class
            );

            ApiResponse body = response.getBody();
            if (body == null || !body.isSuccess()) {
                throw new FileStorageException("File not found or request failed");
            }
            return (FileStorageDTO) body.getData();

        } catch (RestClientException e) {
            throw new FileStorageException("Error retrieving file: " + e.getMessage(), e);
        }
    }
}





