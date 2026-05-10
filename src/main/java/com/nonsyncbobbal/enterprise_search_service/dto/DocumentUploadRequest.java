package com.nonsyncbobbal.enterprise_search_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentUploadRequest {

    private String title;
    private String content;
}
