package com.sdk.storageservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FileUploadRequest {
    private String fileName;
    private UUID clientId;
}
