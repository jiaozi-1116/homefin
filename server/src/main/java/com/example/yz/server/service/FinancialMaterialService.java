package com.example.yz.server.service;

import com.example.yz.server.controller.FinancialMaterialController;
import com.example.yz.server.exception.ResourceNotFoundException;
import com.example.yz.server.pojo.Family;
import com.example.yz.server.pojo.FinancialMaterial;
import com.example.yz.server.pojo.User;
import com.example.yz.server.repository.FinancialMaterialRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class FinancialMaterialService {
    private final FinancialMaterialRepository materialRepository;
    private final UserService userService;
    private final FamilyService familyService;
    private final FileStorageService fileStorageService;

    @Autowired
    public FinancialMaterialService(FinancialMaterialRepository materialRepository,
                                    UserService userService,
                                    FamilyService familyService,
                                    FileStorageService fileStorageService) {
        this.materialRepository = materialRepository;
        this.userService = userService;
        this.familyService = familyService;
        this.fileStorageService = fileStorageService;
    }

    public FinancialMaterial createMaterial(FinancialMaterialController.MaterialCreateDTO createDTO, MultipartFile file) {
        // 验证顾问是否存在
        User advisor = userService.findUserById(createDTO.getAdvisorID())
                .orElseThrow(() -> new ResourceNotFoundException("顾问不存在"));

        // 验证家庭（如果关联）
        Family family = null;
        if (createDTO.getFamilyID() != null) {
            family = familyService.getFamilyById(createDTO.getFamilyID())
                    .orElseThrow(() -> new ResourceNotFoundException("家庭不存在"));
        }

//        Optional<Family> family = familyService.getFamilyById(createDTO.getFamilyID());

        // 处理文件上传
        String filePath = null;
        if (file != null && !file.isEmpty()) {
            filePath = fileStorageService.storeFile(file);
        }

        // 创建实体
        FinancialMaterial material = new FinancialMaterial();
        material.setTitle(createDTO.getTitle());
        material.setContent(createDTO.getContent());
        material.setCategory(createDTO.getCategory());
        material.setAttachment(filePath);
        material.setAdvisor(advisor);
        material.setFamily(family);

        return materialRepository.save(material);
    }




    public List<FinancialMaterial> getMaterialsByFamilyAndCategory(Integer familyId, FinancialMaterial.MaterialCategory category) {
        return materialRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 添加 familyId 查询条件
            if (familyId != null) {
                predicates.add(cb.equal(root.get("family").get("familyID"), familyId));
            }

            // 添加 category 查询条件
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    // 其他业务方法...
}
