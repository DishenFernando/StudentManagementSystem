package com.studentmanagement.system.dto;

import lombok.Data;

@Data
public class BulkUpdateClassRequest {
    private String fromClassName;   // e.g., "Grade 10"
    private String toClassName;     // e.g., "Grade 11"
}
