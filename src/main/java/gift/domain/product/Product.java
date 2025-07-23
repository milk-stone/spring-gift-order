package gift.domain.product;

import gift.domain.wish.Wish;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    private Long price;
    private String imageUrl;

    @OneToMany(mappedBy = "product")
    private final List<Option> options = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private final List<Wish> wishList = new ArrayList<>();

    private static final int MAX_NAME_LENGTH = 15;
    private static final Pattern ALLOWED_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣\\s\\(\\)\\[\\]\\+\\-\\&\\/_]*$");

    protected Product() {
    }

    public Product(String name, Long price, String imageUrl) {
        validateName(name);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Wish> getWishList() {
        return wishList;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void update(String name, Long price, String imageUrl) {
        validateName(name);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비워둘 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("상품 이름은 공백을 포함하여 최대 " + MAX_NAME_LENGTH + "자까지 입력할 수 있습니다.");
        }
        if (!ALLOWED_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("상품 이름에 허용되지 않는 특수문자가 포함되어 있습니다.");
        }
        if (name.contains("카카오")) {
            throw new IllegalArgumentException("상품 이름에 '카카오'를 사용할 수 없습니다. MD에게 문의하세요.");
        }
    }
}
