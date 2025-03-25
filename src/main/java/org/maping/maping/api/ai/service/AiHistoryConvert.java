package org.maping.maping.api.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.maping.maping.api.ai.dto.response.AiChatHistoryDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Component
public class AiHistoryConvert {
    public List<AiChatHistoryDTO> setAiHistoryConvert(String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<AiChatHistoryDTO> dtoList = new ArrayList<>();
        try {
            // JSON 배열을 List로 변환
            dtoList = objectMapper.readValue(content, new TypeReference<List<AiChatHistoryDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리
        }
        return dtoList; // 리스트를 반환
    }

    public String getHistoryJson(List<AiChatHistoryDTO> dtoList) {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = "";
        try {
            content = objectMapper.writeValueAsString(dtoList); // DTO 리스트를 JSON 문자열로 변환
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리
        }
        return content; // JSON 문자열 반환
    }
}
