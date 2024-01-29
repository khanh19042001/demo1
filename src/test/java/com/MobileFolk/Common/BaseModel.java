package com.MobileFolk.Common;

import com.MobileFolk.helpers.ModelMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class BaseModel {
    public ModelMapper tcName;
    public ModelMapper config;
    public ModelMapper expectedResult;
    public ModelMapper dataId;

    public BaseModel() {
        tcName = createModelMapperObj("TC_ID", null);
        config = createModelMapperObj("CONFIG", null);
        dataId = createModelMapperObj("DATA_ID", null);
        expectedResult = createModelMapperObj("EXPECTED_RESULT", null);
    }

    public ModelMapper createModelMapperObj(String name, String languageKey) {
        return ModelMapper.builder().devName(name).langProperty(languageKey).build();
    }

    public ModelMapper createModelMapperObj(String name, String msField, String languageKey) {
        return ModelMapper.builder().devName(name).msField(msField).langProperty(languageKey).build();
    }

    public ModelMapper createModelMapperObj(String name, boolean fill, String languageKey) {
        return ModelMapper.builder().devName(name).fill(fill).langProperty(languageKey).build();
    }
}
