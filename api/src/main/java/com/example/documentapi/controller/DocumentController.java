package com.example.documentapi.controller;

import com.example.documentapi.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/document")
@AllArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * @return List of Document object
     */
    @GetMapping
    public ResponseEntity<List<?>> findAll(){
        return new ResponseEntity<>(documentService.getAllDocuments(), HttpStatus.OK);
    }

    /**
     * @param id
     * @return A message of successful deleting
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("id") Long id) {
        try {
            return new ResponseEntity<>(documentService.deleteFile(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param id
     * @param fileName
     * @return Document Object
     */
    @PutMapping
    public ResponseEntity<?> change(@RequestParam("id") Long id, @RequestParam("fileName") String fileName) {
        try {
            return new ResponseEntity<>(documentService.changeName(id, fileName), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param id
     * @return Document itself
     */
    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam("id") Long id) {
        try {

            Map<String, Object> response = documentService.download(id);

            return ResponseEntity //
                    .ok() //
                    .contentType(MediaType.parseMediaType("application/octet-stream")) //
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=utf-8''" + response.get("file-name")) //
                    .body(response.get("data"));

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param multipartFile
     * @return Document object
     */
    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile multipartFile){
        try{
            return new ResponseEntity<>(documentService.uploadFile(multipartFile),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
