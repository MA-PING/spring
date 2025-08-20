package org.maping.maping.api.ai.service;

import com.google.genai.ResponseStream;
import com.google.genai.types.GenerateContentResponse;
import org.apache.http.HttpException;
import org.maping.maping.api.ai.dto.response.AiChatHistoryDetailResponse;
import org.maping.maping.api.ai.dto.response.AiChatResponse;
import org.maping.maping.api.ai.dto.response.NoticeSummaryResponse;
import org.maping.maping.common.response.BaseResponse;
import org.maping.maping.api.ai.dto.response.AiHistoryResponse;
import reactor.core.publisher.Flux;
import java.util.Map;
import java.io.IOException;
import java.util.List;

public interface AiService {
    BaseResponse<String> getAiStat(String ocid) throws HttpException, IOException;
    BaseResponse<String> getAiItem(String ocid) throws HttpException, IOException;
    BaseResponse<String> getAiUnion(String ocid) throws HttpException, IOException;

    BaseResponse<String> getAiArtifact(String ocid) throws HttpException, IOException;

    BaseResponse<String> getAiSkill(String ocid) throws HttpException, IOException;

    BaseResponse<String> getAiLinkSkill(String ocid) throws HttpException, IOException;

    BaseResponse<String> getAiSymbol(String ocid) throws HttpException, IOException;

    BaseResponse<String> getAiLevel(String ocid) throws HttpException, IOException;

    List<NoticeSummaryResponse> getNoticeSummary();

    AiChatResponse getChat(Long userId, String chatId, String characterName, String type, String ocid, String text) throws HttpException, IOException;

    List<String> getCharacterRecommend(String ocid, Long userId) throws HttpException, IOException;

    String[] getUserRecommend() throws HttpException, IOException;

    List<AiHistoryResponse> getHistory(Long userId);

    BaseResponse<AiChatHistoryDetailResponse> getHistory(Long userId, String chatId);

    BaseResponse<String> deleteHistory(Long userId, String chatId);

    Flux<Map<String, Object>> getGuestChat(String chatId, String text) throws HttpException, IOException;

    Flux<Map<String, Object>> getStreamChat(Long userId, String chatId, String characterName, String type, String ocid, String text);
}
