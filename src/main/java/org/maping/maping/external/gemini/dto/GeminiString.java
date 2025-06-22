package org.maping.maping.external.gemini.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Component
@Service
@Configuration
public class GeminiString {
    public static final String SYSTEM_INSTRUCTION = """
            {
              "parts": [
                {
                  "text": "You are the AI assistant of the Maple Story game. Answer questions about MapleStory games."
                }
              ]
            }
            """;

    public static final String CONTENTS = """
            {
              "contents": [
                {
                  "parts": [
                    {"text": "%s"}
                  ]
                }
              ]
            }
            """;
    public static final String TOOLS = """
            {
              "tools": [
                {
                  "google_search": {}
                }
              ]
            }
            """;

    public String parts(String role ,String text) {
        return String.format("""
            {
              "role": "%s",
              "parts": [{
                "text": "%s"
              }]
            },
            """, role, text);
    }
    public String contents(String text) {
        return String.format("""
            {
            "system_instruction": {
              "parts": [
                {
                  "text": "You are the AI assistant of the Maple Story game. Answer questions about MapleStory games."
                }
              ]
            },
              "contents": [
                {
                  "parts": [
                    {"text": "%s"}
                  ]
                }
              ]
            }
            """, text);
    }
    public String content(String text) {
        return String.format("""
        {
            "system_instruction": {
              "parts": [
                {
                  "text": "너는 메이플스토리 게임의 AI 어시스턴트야. 메이플스토리 게임에 대한 질문에 대답해줘."
                }
              ]
            },
            "contents": [
              {
                "parts": [
                  {
                    "text": "%s"
                  }
                ]
              }
            ],
            "tools": [
                {
                    "google_search": {}
                }
            ]
          }
        """, text);
    }
    public String contents(List<String> text) {
        String parts = String.join("", text);
        return String.format("""
        "contents": [
                    %s
            ]
        """, parts);
    }
    public String googleSearch(String text) {
        return String.format("""
            {
        "system_instruction": {
              "parts": [
                {
                  "text": "너는 메이플스토리 게임의 AI 어시스턴트야. 메이플스토리 게임에 대한 질문에 대답해줘."
                }
              ]
            },
            %s,
            "tools": [
                {
                    "google_search": {}
                }
            ]
        }
        """, text);
    }
}
