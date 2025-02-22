package com.quantify.util;

public class PromptConstants {
    public static final String TEMPLATE_HOLDER = """
        You are an experienced React developer
        User Input : {input},
        Structure (Desired Structure) : {structure},
        Response (Provide code only, without markdown formatting or explanations) : 
        """;
    public static final String REACT_COMP_STRUCTURE = """
    const FunctionNameOfYourChoice = () => {
        const {...} = passed global import (window.React, etc.)
        //Other imports in same style
        //logic
        //return in JSX structure
    }
    export default FunctionNameOfYourChoice;
    """;
    public static final String JSON_STRUCTURE = """
            {
                "chartType1" : {
                    "xAxis" : [...],
                    "yAxis" : [...]
                },
                "chartType2" : {
                    "xAxis" : [...],
                    "yAxis" : [...]
                },
                ....
            }
            """;
    public static final String GET_GRAPH_AXES_PARAMS = "I have a dataset with the following columns: [{0}]. Based on this data, I want to identify the possible X-axis and Y-axis combinations for the following types of charts: [{1}]. Please provide the response in a JSON format where each chart type lists the possible X-axis and Y-axis as arrays of strings, using the column names. Please give me only the JSON with no additional texts or explanations";
    public static final String [] GRAPH_TYPES = {"lineChart","barChart","columnChart","pieChart"};
    public static final String GET_GRAPH = "Generate a React component using `react-google-charts`. Use 'window.Chart' for Chart import and use `window.React` for React imports (React, useState, useEffect). X-axis: [{0}], Y-axis: [{1}]. X values: [{2}], Y values: [{3}]. Sum duplicate X values. Chart type: [{4}]. Set width: '100%', height: '60vh' and background color as #2f2f2f. Make sure the other colors are in accordance with the background color";
}
