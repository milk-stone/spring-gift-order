package gift.domain.wish;


import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.member.Member;
import gift.domain.resolver.LoginMemberArgumentResolver;
import gift.domain.wish.controller.WishController;
import gift.domain.wish.dto.WishPageResponse;
import gift.domain.wish.dto.WishRequest;
import gift.domain.wish.dto.WishUpdateRequest;
import gift.domain.wish.service.WishService;
import gift.global.dto.CustomPageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishController.class)
public class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WishService wishService;

    @MockitoBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    private Member testMember;

    @BeforeEach
    void setUp() throws Exception {
        testMember = new Member("test@example.com", "password", "tester");
        when(loginMemberArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(testMember);
    }

    @Test
    @DisplayName("POST /api/wishes - 위시리스트 생성 성공")
    void createWish_Success() throws Exception {
        WishRequest request = new WishRequest(101L, 1);
        doNothing().when(wishService).createWish(any(WishRequest.class), any(Member.class));
        mockMvc.perform(post("/api/wishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT /api/wishes/{id} - 위시리스트 수정 성공")
    void updateWish_Success() throws Exception {
        Long wishId = 1L;
        WishUpdateRequest request = new WishUpdateRequest(5);
        doNothing().when(wishService).updateWish(anyLong(), any(WishUpdateRequest.class), any(Member.class));
        mockMvc.perform(put("/api/wishes/{id}", wishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/wishes/{id} - 위시리스트 삭제 성공")
    void deleteWish_Success() throws Exception {
        Long wishId = 1L;
        doNothing().when(wishService).deleteWish(anyLong(), any(Member.class));
        mockMvc.perform(delete("/api/wishes/{id}", wishId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/wishes - 위시리스트 목록 페이지네이션 조회 성공")
    void getWishes_Success() throws Exception {
        WishPageResponse fakeResponse = new WishPageResponse(new CustomPageResponse<>(Collections.emptyList(), 0, 10, 0, 0, true, true));
        when(wishService.getWishes(any(Member.class), any(Pageable.class))).thenReturn(fakeResponse.wishResponses());

        // when & then
        mockMvc.perform(get("/api/wishes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.wishResponses.pageNumber").value(0))
                .andExpect(jsonPath("$.wishResponses.pageSize").value(10));
    }
}
