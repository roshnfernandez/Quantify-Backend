package com.quantify;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Data
@Component
public class InMemoryObjectHolder {

    public static InputStream fileInputStream;
    public static Map<String,Object> graphAxesParam;
    public static String graphComp;

}
