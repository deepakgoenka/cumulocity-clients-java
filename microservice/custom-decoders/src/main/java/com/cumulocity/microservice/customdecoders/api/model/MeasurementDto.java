package com.cumulocity.microservice.customdecoders.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.svenson.JSONTypeHint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class MeasurementDto implements Serializable {
    private String type;
    private String series;
    private DateTime time;
    private String[] fragmentsToCopyFromSourceDevice;
    private String deviceFragmentPrefix;
    private String deviceNameFragment;
    private boolean includeDeviceName;
    private Map<String, String> additionalProperties = new HashMap<>();
    private List<MeasurementValueDto> values = new ArrayList<>();

    @JSONTypeHint(MeasurementValueDto.class)
    public List<MeasurementValueDto> getValues() {
        return values;
    }
}
