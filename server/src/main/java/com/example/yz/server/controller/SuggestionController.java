package com.example.yz.server.controller;


import com.example.yz.server.pojo.Notice;
import com.example.yz.server.pojo.Suggestion;
import com.example.yz.server.service.SuggestionService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/suggestion")
@CrossOrigin(origins = "*")
public class SuggestionController {
    @Autowired
    private SuggestionService suggestionService;

    //获取member的建议
    @GetMapping("/member/{userId}")
    public List<Suggestion> getSuggestion(@PathVariable int userId) {
        return suggestionService.getSuggestionsByUserId(userId);
    }

    // 提交新的建议
    @PostMapping("/member")
    public Suggestion submitSuggestion(@RequestBody Suggestion suggestion) {
        if (suggestion.getFamilyId() == null) { System.out.println("FamilyID is null"); }
        return suggestionService.submitSuggestion(suggestion);
    }

    // 获取某个家庭的所有建议
    @GetMapping("/family/{familyId}")
    public List<Suggestion> getSuggestionsByFamilyId(@PathVariable int familyId) {
        return suggestionService.getSuggestionsByFamilyId(familyId);
    }

    // 更新建议状态
    @PutMapping("/{suggestionId}")
    public Suggestion updateSuggestionStatus(
            @PathVariable int suggestionId,
            @RequestParam String status) {
        return suggestionService.updateSuggestionStatus(suggestionId, status);
    }

    @GetMapping("/AISuggestion/{question}")
    public String AISuggestion(@PathVariable String question) {
        HttpResponse<String> response = Unirest.post("https://api.siliconflow.cn/v1/chat/completions")
                .header("Authorization", "Bearer "+"sk-uhawhdrowxgvbppubkibhxdauuhhedwlcehussqokgvvbbnt")
                .header("Content-Type", "application/json")
                .body("{\n  \"model\": \"Qwen/QwQ-32B\",\n  \"messages\": [\n    {\n      \"role\": \"user\",\n      \"content\": \"" + question + "\"\n    }\n  ],\n  \"stream\": false,\n  \"max_tokens\": 512,\n  \"stop\": null,\n  \"temperature\": 0.7,\n  \"top_p\": 0.7,\n  \"top_k\": 50,\n  \"frequency_penalty\": 0.5,\n  \"n\": 1,\n  \"response_format\": {\n    \"type\": \"text\"\n  },\n  \"tools\": [\n    {\n      \"type\": \"function\",\n      \"function\": {\n        \"description\": \"<string>\",\n        \"name\": \"<string>\",\n        \"parameters\": {},\n        \"strict\": false\n      }\n    }\n  ]\n}")
                .asString();
        System.out.println(response.getBody());
        return response.getBody();
    }

}
