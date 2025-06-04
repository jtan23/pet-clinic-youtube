package com.bw.petclinic.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class CustomPageImpl<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomPageImpl(@JsonProperty("content") List<T> content,
                          @JsonProperty("pageable") JsonNode pageable,
                          @JsonProperty("totalElements") int totalElements,
                          @JsonProperty("number") int number,
                          @JsonProperty("size") int size) {
        super(content, PageRequest.of(number, size), totalElements);
    }

}
