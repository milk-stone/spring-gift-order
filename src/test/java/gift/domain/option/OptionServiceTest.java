package gift.domain.option;

import gift.domain.product.Option;
import gift.domain.product.Product;
import gift.domain.product.dto.OptionRequest;
import gift.domain.product.dto.OptionResponse;
import gift.domain.product.repository.OptionRepository;
import gift.domain.product.repository.ProductRepository;
import gift.domain.product.service.OptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @InjectMocks
    private OptionService optionService;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ProductRepository productRepository;

    private Product product;

    @Test
    @DisplayName("특정 상품의 옵션 목록 조회 성공")
    void getProductOptions_Success() {
        Product product = new Product("테스트 상품", 10000L, "test.jpg");
        Option option1 = new Option("색상", 10, product);
        Option option2 = new Option("사이즈", 20, product);
        product.getOptions().addAll(List.of(option1, option2));

        List<OptionResponse> result = optionService.getProductOptions(product);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(OptionResponse::name) // DTO 리스트에서 name 필드만 추출
                .containsExactly("색상", "사이즈");
    }

    @Test
    @DisplayName("입력 받은 상품에 대한 옵션 생성 성공")
    void createOption_Success() {
        product = new Product("Test Product", 10000L, "test.jpg");
        Option option1 = new Option("Option1", 10, product);
        Option option2 = new Option("Option2", 5, product);
        product.getOptions().addAll(List.of(option1, option2));

        var newOptionRequest1 = new OptionRequest("색상", 100);
        var newOptionRequest2 = new OptionRequest("사이즈", 200);
        var optionRequests = List.of(newOptionRequest1, newOptionRequest2);

        given(optionRepository.existsByProductAndName(any(Product.class), anyString())).willReturn(false);

        optionService.createOption(product, optionRequests);

        verify(optionRepository, times(2)).save(any(Option.class));

        assertThat(product.getOptions()).hasSize(4);
    }

    @Test
    @DisplayName("옵션 생성 실패 케이스 - 1. 옵션 이름이 중복될 시 에러 발생")
    void createOption_DuplicateName() {
        product = new Product("Test Product", 10000L, "test.jpg");
        Option option1 = new Option("Option1", 10, product);
        Option option2 = new Option("Option2", 5, product);
        product.getOptions().addAll(List.of(option1, option2));

        var duplicateRequest = new OptionRequest("Option1", 100);
        var requests = List.of(duplicateRequest);

        given(optionRepository.existsByProductAndName(product, "Option1")).willReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            optionService.createOption(product, requests);
        });
    }

    @Test
    @DisplayName("옵션 생성 실패 케이스 - 2. 옵션 이름에 공백이 있을 시 에러 발생")
    void createOption_IncludeBlank() {
        product = new Product("Test Product", 10000L, "test.jpg");
        Option option1 = new Option("Option1", 10, product);
        Option option2 = new Option("Option2", 5, product);
        product.getOptions().addAll(List.of(option1, option2));

        var blankNameRequest = new OptionRequest("공백 옵션", 100);
        var requests = List.of(blankNameRequest);

        assertThrows(IllegalArgumentException.class, () -> {
            optionService.createOption(product, requests);
        });
    }

    @Test
    @DisplayName("옵션 생성 실패 케이스 - 3. 옵션 이름에 허용되지 않은 특수 문자가 포함되면 에러 발생")
    void createOption_IncludeNotPermittedCharacter() {
        product = new Product("Test Product", 10000L, "test.jpg");
        Option option1 = new Option("Option1", 10, product);
        Option option2 = new Option("Option2", 5, product);
        product.getOptions().addAll(List.of(option1, option2));

        var invalidCharRequest = new OptionRequest("잘못된@이름", 100);
        var requests = List.of(invalidCharRequest);

        assertThrows(IllegalArgumentException.class, () -> {
            optionService.createOption(product, requests);
        });
    }
}