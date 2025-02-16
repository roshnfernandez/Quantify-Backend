package com.quantify.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantify.InMemoryObjectHolder;
import com.quantify.models.GraphParam;
import com.quantify.util.PromptBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;


@Data
@Slf4j
@Service
public class FileService {

    //Constants
    public static final String X_AXIS_VALUES = "xAxisValues";
    public static final String Y_AXIS_VALUES = "yAxisValues";

    //Members
    private XSSFWorkbook workbook;
    @Autowired
    private PromptBuilder promptBuilder;
    @Autowired
    private ChatService chatService;

    public Map<String,Object> processFileToGetAxesParams(MultipartFile multipartFile){
        List <String> columnNames = new ArrayList<>();
        Map<String,Object> graphParams = new HashMap<>();
        String prompt = "";
        String resp = "";
        if (loadFileInMemory(multipartFile)) fetchColumnNames(columnNames);
        if (columnNames.size()>2) prompt = promptBuilder.buildGraphAxesPrompt(columnNames);
        if(prompt.length() > 10) resp = chatService.getResponse(prompt);
        try {
            graphParams = new ObjectMapper().readValue(resp.substring(resp.indexOf('{'), resp.lastIndexOf('}') + 1), new TypeReference<>() {});
        }catch (Exception e){
            log.error("Exception while parsing the response, {}", e.getMessage());
        }
        InMemoryObjectHolder.graphAxesParam = graphParams;
        return graphParams;
    }

    private boolean loadFileInMemory(MultipartFile multipartFile){
        try {
            this.workbook  = new XSSFWorkbook(multipartFile.getInputStream());
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private void fetchColumnNames(List<String> columnNames){
        if(workbook != null){
            try {
                XSSFSheet sheet = this.workbook.getSheetAt(0);
                XSSFRow row = sheet.getRow(0);
                Iterator<Cell> cellIterator = row.cellIterator();
                while(cellIterator.hasNext()){
                    Cell currenctCell = cellIterator.next();
                    if(!(currenctCell.getCellType().equals(CellType.BLANK))){
                        columnNames.add(currenctCell.getStringCellValue());
                    }
                }
            }catch (Exception e){
                log.error("Exception occurred while reading the file , {}",e.getMessage());
            }
        }
    }

    public String getGraphFromAI(GraphParam graphParam){
        Map <String, List<String>> graphValues = new HashMap<>();
        graphValues.put(X_AXIS_VALUES,new ArrayList<>());
        graphValues.put(Y_AXIS_VALUES,new ArrayList<>());
        try {
            getGraphValues(graphParam.getXAxis(), graphParam.getYAxis(), graphValues);
            Prompt prompt = promptBuilder.buildGraphPrompt(graphValues.get(X_AXIS_VALUES), graphValues.get(Y_AXIS_VALUES), graphParam.getXAxis(), graphParam.getYAxis(), graphParam.getType());
            String graphComp = InMemoryObjectHolder.graphComp == null ? chatService.getResponse(prompt) : InMemoryObjectHolder.graphComp;
            graphComp = graphComp.replaceAll("```[a-zA-Z]*", "").replaceAll("```", "").trim();
//            InMemoryObjectHolder.graphComp = graphComp;
            return graphComp;
        }catch (Exception e){
            log.error("Exception while getting graph values {}", e.getMessage());
        }
        return "";
    }

    private void getGraphValues(String xAxis, String yAxis, Map <String, List<String>> graphValues) throws Exception{
        int xAxisIndex= -1;
        int yAxisIndex= -1;
        if(this.workbook == null) throw new Exception("Missing Workbook");
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        while(rowIterator.hasNext()) {
            Row currRow = rowIterator.next();
            Iterator<Cell> cellIterator = currRow.cellIterator();
            if (currRow.getRowNum() == 0) {
                while (cellIterator.hasNext()) {
                    Cell currCell = cellIterator.next();
                    if (currCell.getStringCellValue().equals(xAxis)) xAxisIndex = currCell.getColumnIndex();
                    if (currCell.getStringCellValue().equals(yAxis)) yAxisIndex = currCell.getColumnIndex();
                }
            }
            graphValues.get(X_AXIS_VALUES).add(getCellValue(currRow.getCell(xAxisIndex)));
            graphValues.get(Y_AXIS_VALUES).add(getCellValue(currRow.getCell(yAxisIndex)));
        }
    }

    private String getCellValue(Cell cell){
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> Double.toString(cell.getNumericCellValue());
            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
