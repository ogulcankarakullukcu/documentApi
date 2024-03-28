package com.example.documentapi.service;

import com.example.documentapi.exception.DocumentServiceException;
import com.example.documentapi.exception.FileNotFoundException;
import com.example.documentapi.exception.InvalidFileNameException;
import com.example.documentapi.model.Document;
import com.example.documentapi.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class DocumentService {
    private static final String SPECIAL_FILE_EXTENSION_PATTERN = "docx|xlsx";
    private static final String BASE_DIR = System.getProperty("user.dir");
    private final DocumentRepository documentRepository;
    private final String filePath;
    private final String pattern;

    public DocumentService(DocumentRepository documentRepository, @Value("${document.filePath}") String filePath, @Value("${document.accepted.extension}") String pattern) {
        this.documentRepository = documentRepository;
        this.filePath = BASE_DIR + filePath;
        this.pattern = pattern;
    }

    /**
     * @return List of Document object
     */
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * @param id
     * @return Map of file name and byte array of document
     */
    public Map<String, Object> download(Long id) {
        Map<String, Object> response = new HashMap<>();

        Document doc = documentRepository.findById(id).orElseThrow(FileNotFoundException::new);

        response.put("file-name", doc.getFileName());
        response.put("data", getByteArray(searchFile(doc.getId())));

        return response;
    }

    private byte[] getByteArray(String filePath) {

        try {
            if (getFileExtension(filePath).matches(SPECIAL_FILE_EXTENSION_PATTERN)) {

                return getSpecialDocsByteArray(filePath, getFileExtension(filePath).equals("docx"));

            } else {

                return Files.readAllBytes(Paths.get(filePath));

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getSpecialDocsByteArray(String filePath, boolean isWordDocument) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (isWordDocument) {
                XWPFDocument wb = new XWPFDocument(Files.newInputStream(Paths.get(filePath)));
                wb.write(bos);
            } else {
                Workbook wb = WorkbookFactory.create(new File(filePath));
                wb.write(bos);
            }
        } catch (Exception e) {
            bos.close();
            throw new Exception(e);
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    /**
     * @param id
     * @param newFileName
     * @return Document Object
     */
    public Document changeName(Long id, String newFileName) {
        Document doc = documentRepository.findById(id).orElseThrow(FileNotFoundException::new);

        File file = new File(searchFile(doc.getId()));

        File newFilePath = new File(filePath + File.separator + doc.getId() + "#" + newFileName + "." + doc.getExtension());

        if (!file.renameTo(newFilePath)) {
            throw new DocumentServiceException("File Name cannot be changed");
        }

        doc.setFileName(newFileName + "." + doc.getExtension());
        return documentRepository.save(doc);
    }


    /**
     * @param id
     * @return A message of successful deleting
     */
    public String deleteFile(Long id) {
        Document doc = documentRepository.findById(id).orElseThrow(FileNotFoundException::new);

        if (!delete(searchFile(doc.getId()))) {
            throw new DocumentServiceException("File cannot be deleted");
        }

        documentRepository.deleteById(doc.getId());

        return "File deleted successfully";
    }

    private boolean delete(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }

    private String searchFile(Long id) {
        File directory = new File(filePath + File.separator);

        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(id + "#");
            }
        });

        if (Objects.isNull(files)) {
            throw new FileNotFoundException();
        }

        return files[0].getAbsolutePath();

    }

    /**
     * @param file
     * @return Document object
     */
    public Document uploadFile(MultipartFile file) {

        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setFilePath(filePath);
        doc.setFileSize(file.getSize());
        doc.setExtension(getFileExtension(doc.getFileName()));

        doc = documentRepository.save(doc);

        if (doc.getId() != null && !writeFileToServer(file, doc)) {
            documentRepository.deleteById(doc.getId());
            throw new DocumentServiceException("File cannot be uploaded!");
        }


        return doc;
    }

    private boolean writeFileToServer(MultipartFile multipartFile, Document doc) {

        try {

            Files.createDirectories(Paths.get(doc.getFilePath()));

            File file = new File(doc.getFilePath() + File.separator + doc.getId() + "#" + doc.getFileName());

            multipartFile.transferTo(file);

        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private String getFileExtension(String fileName) {
        String fileExtension = Optional.ofNullable(fileName).filter(f -> f.contains(".")).map(f -> f.substring(fileName.lastIndexOf(".") + 1)).orElseThrow(() -> new InvalidFileNameException("Invalid File Name"));

        if (!fileExtension.matches(pattern)) {
            throw new InvalidFileNameException("");
        }

        return fileExtension;
    }
}
