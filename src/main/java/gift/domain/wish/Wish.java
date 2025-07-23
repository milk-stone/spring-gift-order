package gift.domain.wish;

import gift.domain.member.Member;
import gift.domain.product.Product;
import gift.global.exception.BadRequestException;
import jakarta.persistence.*;


@Entity
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    protected Wish() {
    }

    public Wish(Member member, Product product, int quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void updateQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be a positive number");
        }
    }
}
