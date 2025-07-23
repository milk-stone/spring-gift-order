package gift.domain.member.dto;


import jakarta.validation.constraints.NotBlank;

public record MemberInfoUpdateRequest(
        @NotBlank(message = "이메일은 비워둘 수 없습니다.")
        String email,
        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        String password,
        @NotBlank(message = "이름은 비워둘 수 없습니다.")
        String name) {
}
