package com.example.yz.server.controller;

import com.example.yz.server.entity.PurchaseImage;
import com.example.yz.server.entity.PurchaseRequest;
import com.example.yz.server.mapper.PurchaseImageMapper;
import com.example.yz.server.service.FileStorageService;
import com.example.yz.server.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase")
@CrossOrigin(origins = "*")
@Validated
public class PurchaseController {


    @Autowired
    private final PurchaseService purchaseService;
    @Autowired
    private final FileStorageService fileStorageService;
    @Autowired
    private PurchaseImageMapper imageMapper;

    public PurchaseController(PurchaseService purchaseService, FileStorageService fileStorageService) {
        this.purchaseService = purchaseService;
        this.fileStorageService = fileStorageService;
    }


    @PostMapping("/request")
    public PurchaseRequest createRequest(@RequestBody PurchaseRequest request) {
        purchaseService.createRequest(request);
        return request;
    }

    @GetMapping("/my-requests/{userId}")
    public List<PurchaseRequest> getMyRequests(@PathVariable Integer userId) {
        return purchaseService.getUserRequests(userId);
    }

    @PutMapping("/approve/{requestId}")
    public boolean approveRequest(@PathVariable Integer requestId) {
        purchaseService.approveRequest(requestId);
        return true;
    }

    @PutMapping("/reject/{requestId}")
    public boolean rejectRequest(@PathVariable Integer requestId) {
        purchaseService.rejectRequest(requestId);
        return true;
    }

    @GetMapping("/pending/{familyId}")
    public List<Map<String, Object>> getMembersRequests(@PathVariable Integer familyId) {
        return purchaseService.getMembersRequests(familyId);
    }

    @PostMapping("/upload/{requestId}")
    public PurchaseImage uploadImage(
            @PathVariable Integer requestId,
            @RequestParam("file") MultipartFile file
    ) {
        String filePath = fileStorageService.storeFile(file);
        PurchaseImage image = new PurchaseImage();
        image.setRequestId(requestId);
        image.setImagePath(filePath);
        imageMapper.insert(image);
        return image;
    }
}
