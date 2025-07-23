package gift.domain.product.service;

import gift.domain.product.Product;
import gift.domain.product.dto.*;
import gift.domain.product.repository.ProductRepository;
import gift.global.dto.CustomPageResponse;
import gift.global.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OptionService optionService;

    public ProductService(ProductRepository productRepository, OptionService optionService) {
        this.productRepository = productRepository;
        this.optionService = optionService;
    }

    @Transactional
    public ProductResponse addProduct(ProductRequest req) {
        Product product = new Product(req.name(), req.price(), req.imageUrl());
        productRepository.save(product);
        optionService.createOption(product, req.options());
        return ProductResponse.from(product);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("ProductService : getProduct() failed", id));
        return ProductResponse.from(product);
    }

    @Transactional
    public void updateProduct(Long id, ProductUpdateRequest req) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("ProductService : updateProduct() failed", id));
        product.update(req.name(), req.price(), req.imageUrl());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("ProductService : deleteProduct() failed", id));
        productRepository.delete(product);
    }

    public CustomPageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return CustomPageResponse.from(products.map(ProductResponse::from));
    }

    public List<OptionResponse> getProductOptions(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("ProductService : getProductOptions() failed", productId));
        return optionService.getProductOptions(product);
    }

    public void createProductOptions(Long productId, List<OptionRequest> options) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("ProductService : createProductOptions() failed", productId));
        optionService.createOption(product, options);
    }
}
