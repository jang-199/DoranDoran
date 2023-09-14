package com.dorandoran.doranserver.global.util.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorReport {
    private String error;
    private String message;

    public ObjectNode makeErrorReport(String error, String message){
        return new ObjectMapper().createObjectNode()
                .put("error", error)
                .put("message", message);
    }
}
