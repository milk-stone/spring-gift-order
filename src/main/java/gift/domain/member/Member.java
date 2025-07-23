package gift.domain.member;

import gift.domain.wish.Wish;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String password;
    private String name;
    private RoleType role;

    @OneToMany(mappedBy = "member")
    private final List<Wish> wishList = new ArrayList<>();

    protected Member() {
    }

    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = RoleType.USER;
    }

    // Test 용 생성자
    public Member(String email, String password, String name, RoleType role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role.toString();
    }

    public List<Wish> getWishList() {
        return wishList;
    }

    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    public void update(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public boolean hasProductInWishList(Long productId) {
        return this.wishList.stream()
                .anyMatch(wish -> wish.getProduct().getId().equals(productId));
    }

    public boolean isAdmin() {
        return this.role.equals(RoleType.ADMIN);
    }

    public static Member createAdminForTest(String email) {
        return new Member(email, "testPassword", "관리자", RoleType.ADMIN);
    }
}
