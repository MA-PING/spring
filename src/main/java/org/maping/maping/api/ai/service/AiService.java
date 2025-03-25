package org.maping.maping.api.ai.service;

import org.apache.http.HttpException;
import org.maping.maping.api.ai.dto.response.AiChatHistoryDetailResponse;
import org.maping.maping.api.ai.dto.response.AiChatResponse;
import org.maping.maping.api.ai.dto.response.NoticeSummaryResponse;
import org.maping.maping.common.response.BaseResponse;
import org.maping.maping.external.nexon.dto.notice.NoticeUpdateListDTO;
import org.maping.maping.model.ai.AiHistoryJpaEntity;
import org.maping.maping.api.ai.dto.response.AiHistoryResponse;
import reactor.core.publisher.Flux;
import java.util.Map;
import java.io.IOException;
import java.util.List;

public interface AiService {
    Flux<String> getAiStat(String ocid);
    Flux<String> getAiItem(String ocid) throws HttpException, IOException;
    Flux<String> getAiUnion(String ocid) throws HttpException, IOException;

    Flux<String> getAiArtifact(String ocid) throws HttpException, IOException;

    Flux<String> getAiSkill(String ocid) throws HttpException, IOException;

    Flux<String> getAiSymbol(String ocid) throws HttpException, IOException;

    List<NoticeSummaryResponse> getNoticeSummary();

    AiChatResponse getChat(Long userId, String chatId, String characterName, String type, String ocid, String text) throws HttpException, IOException;

    String getCharacterRecommend(String ocid) throws HttpException, IOException;

    String getUserRecommend(String ocid) throws HttpException, IOException;

    List<AiHistoryResponse> getHistory(Long userId);

    BaseResponse<AiChatHistoryDetailResponse> getHistory(Long userId, String chatId);

    BaseResponse<String> deleteHistory(Long userId, String chatId);

    String getGuestChat(String chatId, String characterName, String type, String ocid, String text) throws HttpException, IOException;

    Flux<Map<String, Object>> getStreamChat(Long userId, String chatId, String characterName, String type, String ocid, String text);
}
