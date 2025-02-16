package com.quantify.controllers;


import com.quantify.InMemoryObjectHolder;
import com.quantify.models.GraphParam;
import com.quantify.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("file")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    public Map<String,Object> fetchGraphParamsFromFile(@RequestBody MultipartFile file){
        return InMemoryObjectHolder.graphAxesParam == null ? fileService.processFileToGetAxesParams(file) : InMemoryObjectHolder.graphAxesParam;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateGraph(@RequestBody GraphParam graphParam){
        log.info("This is the value {}", graphParam);
        return ResponseEntity.ok().contentType(MediaType.valueOf("application/javascript")).body(fileService.getGraphFromAI(graphParam));
    }
}
