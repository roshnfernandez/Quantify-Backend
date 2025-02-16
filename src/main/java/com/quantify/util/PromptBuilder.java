package com.quantify.util;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Data
public class PromptBuilder {

    public String buildGraphAxesPrompt(List<String> columnNames){
        String concatColumnNames = concatListValues(columnNames);
        String graphTypes = concatListValues(PromptConstants.GRAPH_TYPES);
        return MessageFormat.format(
                PromptConstants.GET_GRAPH_AXES_PARAMS,
                concatColumnNames,
                graphTypes);
    }

    public Prompt buildGraphPrompt(List<String> xAxisValues, List<String> yAxisValues, String xAxis, String yAxis, String graphType){
        String concatXAxisValues = concatListValues(xAxisValues);
        String concatYAxisValues = concatListValues(yAxisValues);
        return optimizePrompt(MessageFormat.format(
          PromptConstants.GET_GRAPH,
          xAxis,
          yAxis,
          concatXAxisValues,
          concatYAxisValues,
          graphType
        ),PromptConstants.REACT_COMP_STRUCTURE);
    }

    private Prompt optimizePrompt(String userInput, String structure){
        return new PromptTemplate(PromptConstants.TEMPLATE_HOLDER).create(Map.of(
                "input", userInput,
                "structure", structure
        ));
    }

    private String concatListValues(List<String> values){
        return values.stream().reduce((val1,val2) -> val1 + ", " + val2).orElse("");
    }

    private String concatListValues(String[] values){
        return Arrays.stream(values).reduce((val1,val2) -> val1 + "," + val2).orElse("");
    }
}
