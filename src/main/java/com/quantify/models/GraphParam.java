package com.quantify.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GraphParam {
    private String type;
    private String xAxis;
    private String yAxis;
}
