package gift.domain.product.service;

import gift.domain.product.Option;
import gift.domain.product.Product;
import gift.domain.product.dto.OptionRequest;
import gift.domain.product.dto.OptionResponse;
import gift.domain.product.repository.OptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionService {
    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public List<OptionResponse> getProductOptions(Product product) {
        return product.getOptions()
                .stream()
                .map(OptionResponse::from)
                .toList();
    }

    public void createOption(Product product, List<OptionRequest> optionRequests) {
        for (OptionRequest optionRequest : optionRequests) {
            if (optionRepository.existsByProductAndName(product, optionRequest.name())){
                throw new IllegalArgumentException("이미 존재하는 옵션 이름입니다.");
            }
            Option option = new Option(optionRequest.name(), optionRequest.quantity(), product);
            optionRepository.save(option);
            product.getOptions().add(option);
        }
    }
}
