package gift.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.regex.Pattern;

@Entity
@Table(name = "option", uniqueConstraints = {
        @UniqueConstraint(
                name = "UNIQUE_PRODUCT_OPTION_NAME",
                columnNames = {"product_id", "name"}
        )
})
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;
    @NotBlank
    @Column(length = 50)
    private String name;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private static final Pattern ALLOWED_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣()\\[\\]+\\-&/_]*$");

    protected Option() {
    }

    public Option(String name, Integer quantity, Product product) {
        validateName(name);
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 비워둘 수 없습니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("이름은 50 글자 이내여야 합니다.");
        }
        if (!ALLOWED_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("옵션 이름에 허용되지 않는 특수문자가 포함되어 있습니다.");
        }
    }

    public void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("옵션 수량은 1개 이상 이어야 합니다.");
        }
        if (quantity > 100_000_000) {
            throw new IllegalArgumentException("옵션 수량은 1억개 미만 이어야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void subtractQuantity(int quantity) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
            return;
        }
        this.quantity = 0;
    }
}
