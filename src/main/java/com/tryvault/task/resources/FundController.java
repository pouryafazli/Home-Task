package com.tryvault.task.resources;


import com.tryvault.task.Response.FundLoadResponse;
import com.tryvault.task.service.FundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/funds")
public class FundController {

    private final FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @PostMapping("/load")
    public ResponseEntity<List<FundLoadResponse>> loadFunds(@RequestParam("file") MultipartFile file) throws IOException {
        File inputFile = convertMultipartFileToFile(file);
        List<FundLoadResponse> responses = fundService.loadFunds(inputFile);
        return ResponseEntity.ok(responses);
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        // Use the original filename without spaces
        String originalFilename = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String sanitizedFilename = originalFilename.replaceAll("\\s", "_");
        File convertedFile = new File("/tmp/" + sanitizedFilename);
        multipartFile.transferTo(convertedFile);
        return convertedFile;
    }


}