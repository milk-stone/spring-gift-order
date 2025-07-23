package gift.domain.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.member.Member;
import gift.domain.product.controller.ProductController;
import gift.domain.product.dto.*;
import gift.domain.product.service.OptionService;
import gift.domain.product.service.ProductService;
import gift.domain.resolver.LoginMemberArgumentResolver;
import gift.global.dto.CustomPageRequest;
import gift.global.dto.CustomPageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private OptionService optionService;

    @MockitoBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    private Member adminMember;

    @BeforeEach
    void setUp() throws Exception {
        // 관리자 권한이 필요한 테스트를 위한 Mock 설정
        adminMember = Member.createAdminForTest("admin@test.com");
        given(loginMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(adminMember);
    }

    @Test
    @DisplayName("POST /api/products - 상품 생성 성공")
    void createProduct() throws Exception {
        // given
        var optionRequest = new OptionRequest("기본", 100);
        var request = new ProductRequest("테스트 상품", 10000L, "test.jpg", List.of(optionRequest));
        var response = new ProductResponse(1L, "테스트 상품", 10000L, "test.jpg");

        given(productService.addProduct(any(ProductRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("테스트 상품"));
    }

    @Test
    @DisplayName("GET /api/products/{productId} - 특정 상품 조회 성공")
    void getProduct() throws Exception {
        // given
        Long productId = 1L;
        var response = new ProductResponse(productId, "테스트 상품", 10000L, "test.jpg");
        given(productService.getProduct(productId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("테스트 상품"));
    }

    @Test
    @DisplayName("PUT /api/products/{productId} - 상품 수정 성공")
    void updateProduct() throws Exception {
        // given
        Long productId = 1L;
        var request = new ProductUpdateRequest("수정된 상품", 12000L, "edited.jpg");
        willDoNothing().given(productService).updateProduct(eq(productId), any(ProductUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/api/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(productService).updateProduct(eq(productId), any(ProductUpdateRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/products/{productId} - 상품 삭제 성공")
    void deleteProduct() throws Exception {
        // given
        Long productId = 1L;
        willDoNothing().given(productService).deleteProduct(productId);

        // when & then
        mockMvc.perform(delete("/api/products/{productId}", productId))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productId);
    }

    @Test
    @DisplayName("GET /api/products - 상품 목록 페이지 조회 성공")
    void productList() throws Exception {
        // given
        var productResponse = new ProductResponse(1L, "테스트 상품", 10000L, "test.jpg");
        var pageable = new CustomPageRequest(0, 10, null).toPageable();

        var productPage = new CustomPageResponse<>(List.of(productResponse), 0, 10, 1, 1, true, false);

        given(productService.getAllProducts(pageable)).willReturn(productPage);

        // when & then
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productResponseList.content").isArray())
                .andExpect(jsonPath("$.productResponseList.content[0].name").value("테스트 상품"));
    }

    @Test
    @DisplayName("POST /api/products/{productId}/options - 관리자가 옵션 추가 성공")
    void addOptions() throws Exception {
        // given
        Long productId = 1L;
        var optionRequests = List.of(new OptionRequest("NewOption", 100));
        var optionListRequest = new OptionListRequest(optionRequests);

        willDoNothing().given(productService).createProductOptions(eq(productId), any());

        // when & then
        mockMvc.perform(post("/api/products/{productId}/options", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(optionListRequest)))
                .andExpect(status().isCreated());

        verify(productService).createProductOptions(eq(productId), eq(optionRequests));
    }

    @Test
    @DisplayName("GET /api/products/{productId}/options - 옵션 목록 조회 성공")
    void getOptionList() throws Exception {
        // given
        Long productId = 1L;
        List<OptionResponse> response = List.of(new OptionResponse(1L, "Option 1", 10));
        given(productService.getProductOptions(productId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/products/{productId}/options", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options[0].name").value("Option 1"));
    }
}
