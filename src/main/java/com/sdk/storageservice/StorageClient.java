package com.sdk.storageservice;

import com.sdk.storageservice.dto.ApiResponse;

import com.sdk.storageservice.dto.FileStorageDTO;

import com.sdk.storageservice.dto.FileUploadRequest;
import com.sdk.storageservice.exception.FileStorageException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class StorageClient {

    private final HttpClient client;
    private final String baseUrl;

    public StorageClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();
    }

    public ApiResponse<FileStorageDTO> uploadFile(Path filePath) {

        try {

            FileUploadRequest requestDto = FileUploadRequest.builder()
                    .fileName(filePath.getFileName().toString())
                    .build();

            byte[] fileBytes = Files.readAllBytes(filePath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/files"))
                    .header("Content-Type", "application/octet-stream")
                    .header("fileName", requestDto.getFileName())
                    .POST(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ApiResponse<FileStorageDTO> apiResponse = new ApiResponse<>();
            apiResponse.setSuccess(response.statusCode() == 200);
            apiResponse.setMessage("File uploaded successfully");

            return apiResponse;

        } catch (Exception e) {
            throw new FileStorageException("Failed to upload file");
        }
    }

    public ApiResponse<FileStorageDTO> getFileById(String fileId) {

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/files/" + fileId))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ApiResponse<FileStorageDTO> apiResponse = new ApiResponse<>();
            apiResponse.setSuccess(response.statusCode() == 200);
            apiResponse.setMessage("File fetched successfully");

            return apiResponse;

        } catch (Exception e) {
            throw new FileStorageException("Failed to fetch file");
        }
    }

    public ApiResponse<List<FileStorageDTO>> listFiles() {

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/files"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ApiResponse<List<FileStorageDTO>> apiResponse = new ApiResponse<>();
            apiResponse.setSuccess(response.statusCode() == 200);
            apiResponse.setMessage("Files fetched successfully");

            return apiResponse;

        } catch (Exception e) {
            throw new FileStorageException("Failed to list files");
        }
    }

    public ApiResponse<FileStorageDTO> updateFile(String fileId, String fileName) {

        try {

            String body = "{\"fileName\":\"" + fileName + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/files/" + fileId))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ApiResponse<FileStorageDTO> apiResponse = new ApiResponse<>();
            apiResponse.setSuccess(response.statusCode() == 200);
            apiResponse.setMessage("File updated successfully");

            return apiResponse;

        } catch (Exception e) {
            throw new FileStorageException("Failed to update file");
        }
    }

    public ApiResponse<String> deleteFile(String fileId) {

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/files/" + fileId))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ApiResponse<String> apiResponse = new ApiResponse<>();
            apiResponse.setSuccess(response.statusCode() == 200);
            apiResponse.setMessage("File deleted successfully");
            apiResponse.setData(response.body());

            return apiResponse;

        } catch (Exception e) {
            throw new FileStorageException("Failed to delete file");
        }
    }
}

