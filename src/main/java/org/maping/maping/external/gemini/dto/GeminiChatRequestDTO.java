package org.maping.maping.external.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class GeminiChatRequestDTO {
    private String contents;

    public void setText(String user, String model, String text) {
        this.contents = String.format("""
{
    "system_instruction": {
      "parts": [
        {
          "text": "You are the AI assistant of the Maple Story game. Answer questions about MapleStory games."
        }
      ]
    },
    "contents": [
        {"role": "user",
         "parts": [{
           "text": "%s"}]},
        {"role": "model",
         "parts": [{
           "text": "%s"}]},
        {"role": "user",
         "parts": [{
           "text": "%s"}]},
    ],
    "tools": [
        {
            "google_search": {}
        }
    ]
}
""", user, model, text);
    }
}
