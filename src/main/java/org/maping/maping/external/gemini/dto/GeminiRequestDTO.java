package org.maping.maping.external.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class GeminiRequestDTO {
    private String contents;

    public void setText(String text) {
        this.contents = String.format("""
{
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
}
