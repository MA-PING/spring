package org.maping.maping.external.gemini;

import com.google.common.collect.ImmutableList;
import com.google.genai.ResponseStream;
import com.google.genai.types.GenerateContentConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.maping.maping.api.ai.dto.response.AiChatHistoryDTO;
import org.maping.maping.external.gemini.dto.*;
import org.maping.maping.external.nexon.NEXONUtils;
import org.maping.maping.external.nexon.dto.notice.NoticeDetailDTO;
import org.maping.maping.external.nexon.dto.notice.NoticeUpdateListDTO;
import org.maping.maping.model.ai.NoticeJpaEntity;
import org.maping.maping.repository.ai.NoticeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GoogleSearch;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.time.OffsetDateTime;
import org.apache.http.HttpException;
import com.google.genai.types.Tool;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@Component
@Service
@Configuration
public class GEMINIUtils {

    private final String GEMINI_API_KEY;
    private final NEXONUtils nexonUtils;
    private final NoticeRepository noticeRepository;


    private final String Gemini2URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-pro-exp-02-05:generateContent?key="; //:streamGenerateContent?alt=sse
    private final String Gemini2StreamURL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-pro-exp-02-05:streamGenerateContent?alt=sse&key="; //:streamGenerateContent?alt=sse

    public GEMINIUtils(@Value("${spring.gemini.key}") String geminiApiKey, NEXONUtils nexonUtils, NoticeRepository noticeRepository) {
        GEMINI_API_KEY = geminiApiKey;
        this.nexonUtils = nexonUtils;
        this.noticeRepository = noticeRepository;
    }

    public GeminiSearchRequestDTO getGemini(String text) {

        log.info("GEMINI_API_KEY: {}", GEMINI_API_KEY);
        return null;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void setNotice() throws HttpException, IOException {
        NoticeUpdateListDTO noticeList = nexonUtils.getNoticeUpdateList();
        log.info(noticeList.toString());
        for(int i = noticeList.getNotice().size() -1; i >= 0 ; i--){
            NoticeJpaEntity entity = noticeRepository.findByNoticeUrl((noticeList.getNotice().get(i).getUrl()));
            if(entity == null){
                String version = getVersion(noticeList.getNotice().get(i).getTitle());
                String noticeString = noticeString(noticeList.getNotice().get(i).getNoticeId());
                String noticeSummary = getNoticeSummary(noticeString);
                NoticeJpaEntity notice = NoticeJpaEntity.builder()
                        .noticePart("패치노트")
                        .noticeTitle(noticeList.getNotice().get(i).getTitle())
                        .noticeDate(LocalDateTime.parse(noticeList.getNotice().get(i).getDate()))
                        .noticeSummary(noticeSummary)
                        .noticeUrl(noticeList.getNotice().get(i).getUrl())
                        .version(version)
                        .build();
                noticeRepository.save(notice);
            }else{
                log.info("이미 저장된 공지입니다. {}",i);
            }
        }
    }
    public String getNoticeSummary(String content) throws HttpException, IOException {
        String text = content + "\n" +
                "메이플 패치노트를 요약해서 알려줘.";
        return getGeminiResponse(text);
    }
    public String getVersion(String title) {
        String[] split = title.split(" ");
        String version = "";
        for(int i = 0; i < split.length; i++){
            if (split[i].equals("클라이언트")) {
                version = split[i + 1];
            }
        }
        log.info(version);
        return version;
    }

    public String noticeString(int noticeId) {
        NoticeDetailDTO notice = nexonUtils.getNoticeUpdateDetail(noticeId);
        String contents = notice.getContents();
        Document document = Jsoup.parse(contents);

        return document.text();
    }

//    public String getGeminiGoogleResponse(String text){
//        GeminiSearchRequestDTO geminiSearchRequestDTO = new GeminiSearchRequestDTO();
//        String fullURL = Gemini2URL + GEMINI_API_KEY;
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        geminiSearchRequestDTO.setText(text);
//        String requestBody = geminiSearchRequestDTO.getContents();
//
//        ResponseEntity<String> response = new RestTemplate().exchange(fullURL, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);
//        log.info(Objects.requireNonNull(response.getBody()).toString());
////        return Objects.requireNonNull(response.getBody()).getCandidates().getFirst().getContent().getParts().getFirst().getText();
//        return response.getBody();
//    }
    //제미나이 검색(구글 검색 X)
    public String getGeminiResponse(String text){
        GeminiRequestDTO geminiRequestDTO = new GeminiRequestDTO();
        String fullURL = Gemini2URL + GEMINI_API_KEY;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        geminiRequestDTO.setText(text);
        String requestBody = geminiRequestDTO.getContents();

        ResponseEntity<GeminiResponseDTO> response = new RestTemplate().exchange(fullURL, HttpMethod.POST, new HttpEntity<>(requestBody, headers), GeminiResponseDTO.class);

        return Objects.requireNonNull(response.getBody()).getCandidates().getFirst().getContent().getParts().getFirst().getText();
    }

    //제미나이 챗봇(구글 검색 O)
    public String getGeminiChatResponse(@NotNull List<AiChatHistoryDTO> history, String text){
        GeminiChatRequestDTO geminiChatRequestDTO = new GeminiChatRequestDTO();
        String fullURL = Gemini2URL + GEMINI_API_KEY;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String user = history.getLast().getQuestion();
        String model = history.getLast().getAnswer();

        geminiChatRequestDTO.setText(user, model, text);
        String requestBody = geminiChatRequestDTO.getContents();

        ResponseEntity<GeminiGoogleResponseDTO> response = new RestTemplate().exchange(fullURL, HttpMethod.POST, new HttpEntity<>(requestBody, headers), GeminiGoogleResponseDTO.class);
        return Objects.requireNonNull(response.getBody()).getCandidates().getFirst().getContent().getParts().getFirst().getText();
    }

    //제미나이 스트림 검색(구글 검색 O)
    public Flux<String> getGeminiStreamResponse(String text){
        GeminiSearchRequestDTO geminiRequestDTO = new GeminiSearchRequestDTO();

        geminiRequestDTO.setText(text);
        String requestBody = geminiRequestDTO.getContents();

        WebClient webClient = WebClient.builder()
                .baseUrl(Gemini2StreamURL + GEMINI_API_KEY)
                .build();

        Flux<GeminiGoogleResponseDTO> eventStream = webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(GeminiGoogleResponseDTO.class);

        return eventStream
                .map(response -> {
                    return response.getCandidates().getFirst().getContent().getParts().getFirst().getText();
                })
                .doOnComplete(() -> log.info("Stream completed"));
    }

//    //제미나이 스트림 검색(구글 검색 O)
//    public void getGeminiStreamResponse(String text) throws HttpException, IOException {
//        Client client = Client.builder().apiKey(GEMINI_API_KEY).build();
//
//        Tool googleSearchTool = Tool.builder().googleSearch(GoogleSearch.builder().build()).build();
//
//        GenerateContentConfig config =
//                GenerateContentConfig.builder()
//                        .tools(ImmutableList.of(googleSearchTool))
//                        .build();
//
//        ResponseStream<GenerateContentResponse> responseStream =
//                client.models.generateContentStream("gemini-2.0-pro-exp-02-05", text, config);
//        log.info(responseStream.toString());
//        responseStream.close();
//    }

    //제미나이 스트림 챗봇(구글 검색 O)
    public Flux<String> getGeminiStreamChatResponse(List<AiChatHistoryDTO> history, String text){
        GeminiChatRequestDTO geminiChatRequestDTO = new GeminiChatRequestDTO();

        String user = history.getLast().getQuestion();
        String model = history.getLast().getAnswer();

        geminiChatRequestDTO.setText(user, model, text);
        String requestBody = geminiChatRequestDTO.getContents();

        WebClient webClient = WebClient.builder()
                .baseUrl(Gemini2StreamURL + GEMINI_API_KEY)
                .build();

        Flux<GeminiGoogleResponseDTO> eventStream = webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(GeminiGoogleResponseDTO.class);

        return eventStream
                .map(response -> {
                    return response.getCandidates().getFirst().getContent().getParts().getFirst().getText();
                })
                .doOnComplete(() -> log.info("Stream completed"));
    }

    //제미나이 검색(구글 검색 O)
    public String getGeminiGoogleResponse(String text) throws HttpException, IOException {
        Client client = Client.builder().apiKey(GEMINI_API_KEY).build();

        Tool googleSearchTool = Tool.builder().googleSearch(GoogleSearch.builder().build()).build();

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .tools(ImmutableList.of(googleSearchTool))
                        .build();

        GenerateContentResponse response =
                client.models.generateContent("gemini-2.0-pro-exp-02-05", text, config);

        return response.text();
    }

//    public String getGeminiResponse(String text) throws IOException, HttpException{
//        Client client = Client.builder().apiKey(GEMINI_API_KEY).build();
//
//        GenerateContentResponse response =
//                client.models.generateContent("gemini-2.0-pro-exp-02-05", text, null);
//
//        return response.text();
//    }

//    public String getGeminiChatResponse(String text) throws IOException, HttpException {
//        Client client = Client.builder().apiKey(GEMINI_API_KEY).build();
//
//        GenerateContentResponse response =
//                client.models.generateContent("gemini-2.0-pro-exp-02-05", text, null);
//
//        return response.text();
//    }
}
