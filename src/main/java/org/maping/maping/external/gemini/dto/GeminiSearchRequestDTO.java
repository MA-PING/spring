package org.maping.maping.external.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
//import org.maping.maping.common.utills.gemini.dto.request.content;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class GeminiSearchRequestDTO {

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
    ],
    "tools": [
        {
            "google_search": {}
        }
    ]
}
""", text);
        }
}
