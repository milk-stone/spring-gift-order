package gift.domain.product.controller;

import gift.domain.annotation.LoginMember;
import gift.domain.member.Member;
import gift.domain.product.dto.*;
import gift.domain.product.service.ProductService;
import gift.global.dto.CustomPageRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        return new ResponseEntity<>(productService.addProduct(productRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.getProduct(productId), HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductUpdateRequest productUpdateRequest) {
        productService.updateProduct(productId, productUpdateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<ProductPageResponse> productList(
            @Valid @ModelAttribute CustomPageRequest customPageable
            ) {
        Pageable pageable = customPageable.toPageable();
        return new ResponseEntity<>(new ProductPageResponse(productService.getAllProducts(pageable)), HttpStatus.OK);
    }

    @GetMapping("/{productId}/options")
    public ResponseEntity<OptionListResponse> getOptionList(
            @PathVariable(name = "productId") Long productId
    ) {
        var options = productService.getProductOptions(productId);
        var responseBody = new OptionListResponse(options);
        return new ResponseEntity<>(responseBody, OK);
    }

    @PostMapping("/{productId}/options")
    public ResponseEntity<Void> addOptions(
            @PathVariable(name = "productId") Long productId,
            @RequestBody OptionListRequest optionListRequest,
            @LoginMember Member member
    ) {
        if (!member.isAdmin()) {
            return new ResponseEntity<>(FORBIDDEN);
        }
        productService.createProductOptions(productId, optionListRequest.options());
        return new ResponseEntity<>(CREATED);
    }
}
