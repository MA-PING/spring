package org.maping.maping.external.gemini;

import com.google.common.collect.ImmutableList;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.Tool;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.ResponseStream;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.http.HttpException;
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
    private final GenerateContentConfig contentConfig;
    private final GeminiString geminiString;
    private final Client client;
    private final String Gemini2URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="; //:streamGenerateContent?alt=sse
    private final String Gemini2StreamURL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:streamGenerateContent?alt=sse&key="; //:streamGenerateContent?alt=sse
    private final String modelName = "gemini-2.0-flash-001"; // 모델 이름

    public GEMINIUtils(@Value("${spring.gemini.key}") String geminiApiKey, NEXONUtils nexonUtils, NoticeRepository noticeRepository, List<Content> chatHistory, GeminiString geminiString) {
        GEMINI_API_KEY = geminiApiKey;
        this.geminiString = geminiString;
        this.client = Client.builder().apiKey(GEMINI_API_KEY).build();
        Tool googleSearchTool = Tool.builder()
                .googleSearch(GoogleSearch.builder().build())
                .build();
        this.contentConfig = GenerateContentConfig.builder()
                .tools(ImmutableList.of(googleSearchTool))
                .build();
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                        .noticeDate(LocalDateTime.parse(noticeList.getNotice().get(i).getDate().substring(0, 16)))
                        .noticeSummary(noticeSummary)
                        .noticeUrl(noticeList.getNotice().get(i).getUrl())
                        .version(version)
                        .build();
                noticeRepository.save(notice);
                log.info("공지 저장 성공. {}",i);
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
//        Content systemInstruction = Content.fromParts(Part.fromText("You're an expert on Nexon's MapleStory."));
        GenerateContentConfig config =
                GenerateContentConfig.builder()
//                        .systemInstruction(systemInstruction)
                        .build();
        GenerateContentResponse response =
                client.models.generateContent(modelName, text, config);

        return response.text();
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
        String requestBody = geminiString.content(text);
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
    public List<Part> setPart(String text){
        return ImmutableList.of(Part.builder().text(text).build());
    }

    //제미나이 스트림 검색(구글 검색 O)
    public ResponseStream<GenerateContentResponse> getNoLoginGeminiStreamResponse(List<Content> promptForModel) throws HttpException, IOException {
        log.info(String.valueOf(promptForModel));
        Content systemInstruction = Content.fromParts(Part.fromText("You're an expert on Nexon's MapleStory."));
        Tool googleSearchTool = Tool.builder().googleSearch(GoogleSearch.builder().build()).build();
        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .systemInstruction(systemInstruction)
                        .tools(ImmutableList.of(googleSearchTool))
                        .build();
        return client.models.generateContentStream(modelName, promptForModel, config);
    }

    //제미나이 스트림 챗봇(구글 검색 O)
    public Flux<String> getGeminiStreamChatResponse(List<AiChatHistoryDTO> history, String text){

        List<String> chatHistory = new ArrayList<>();
        int historySize = history.size();
        List<AiChatHistoryDTO> recentHistory;

        if (historySize > 10) {
            recentHistory = history.subList(historySize - 10, historySize);
        } else {
            recentHistory = history;
        }

        for (AiChatHistoryDTO chat : recentHistory) {
            chatHistory.add(geminiString.parts("user", chat.getQuestion()));
            chatHistory.add(geminiString.parts("model", chat.getAnswer()));
        }
        chatHistory.add(geminiString.parts("user", text));

        String contents = geminiString.contents(chatHistory);
        String googleSearch = geminiString.googleSearch(contents);
        log.info(googleSearch);
        WebClient webClient = WebClient.builder()
                .baseUrl(Gemini2StreamURL + GEMINI_API_KEY)
                .build();

        Flux<GeminiGoogleResponseDTO> eventStream = webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(googleSearch)
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
        Content systemInstruction = Content.fromParts(Part.fromText("You're an expert on Nexon's MapleStory."));
        Tool googleSearchTool = Tool.builder().googleSearch(GoogleSearch.builder().build()).build();

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .systemInstruction(systemInstruction)
                        .tools(ImmutableList.of(googleSearchTool))
                        .build();

        GenerateContentResponse response =
                client.models.generateContent("gemini-2.0-flash", text, config);

        return response.text();
    }

//    public String getGeminiResponse(String text) throws IOException, HttpException{
//        Client client = Client.builder().apiKey(GEMINI_API_KEY).build();
//
//        GenerateContentResponse response =
//                client.models.generateContent("gemini-2.0-flash", text, null);
//
//        return response.text();
//    }

//    public String getGeminiChatResponse(String text) throws IOException, HttpException {
//        Client client = Client.builder().apiKey(GEMINI_API_KEY).build();
//
//        GenerateContentResponse response =
//                client.models.generateContent("gemini-2.0-flash", text, null);
//
//        return response.text();
//    }
}
