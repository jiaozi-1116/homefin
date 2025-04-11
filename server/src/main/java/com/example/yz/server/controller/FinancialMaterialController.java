package com.example.yz.server.controller;

import com.example.yz.server.exception.FileStorageException;
import com.example.yz.server.exception.ResourceNotFoundException;
import com.example.yz.server.pojo.FinancialMaterial;
import com.example.yz.server.repository.FinancialMaterialRepository;
import com.example.yz.server.service.FileStorageService;
import com.example.yz.server.service.FinancialMaterialService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "*")
public class FinancialMaterialController {
    private final FinancialMaterialService materialService;
    @Autowired
    private  FileStorageService fileStorageService;

    @Autowired
    public FinancialMaterialController(FinancialMaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMaterial(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute MaterialCreateDTO createDTO) {
        try {
            FinancialMaterial created = materialService.createMaterial(createDTO, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("发布失败");
        }
    }

    // DTO定义
    @Data
    public static class MaterialCreateDTO {
        private String title;
        private String content;
        private FinancialMaterial.MaterialCategory category;
        private Integer advisorID;
        private Integer familyID;

        // Getters and Setters
    }

    // 新增文件上传接口
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> handleFileUpload(
            @RequestParam("file") MultipartFile file) {

        try {
            // 存储文件并获取文件名
            String storedFileName = fileStorageService.storeFile(file);

            // 构建文件访问URL
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(storedFileName)
                    .toUriString();

            // 返回响应
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("filename", storedFileName);
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);
        } catch (FileStorageException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @GetMapping
    public ResponseEntity<?> getMaterials(
            @RequestParam(required = false) Integer familyId,
            @RequestParam(required = false) String category) { // 使用 String 接收 category
        try {
            FinancialMaterial.MaterialCategory materialCategory = null;
            if (category != null) {
                materialCategory = FinancialMaterial.MaterialCategory.valueOf(category);
            }
            List<FinancialMaterial> materials = materialService.getMaterialsByFamilyAndCategory(familyId, materialCategory);
            return ResponseEntity.ok(materials);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("获取材料失败: " + ex.getMessage());
        }
    }
}
