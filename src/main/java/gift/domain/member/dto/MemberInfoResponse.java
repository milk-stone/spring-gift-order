package gift.domain.member.dto;

import gift.domain.member.Member;

public record MemberInfoResponse(Long id, String email, String password, String name, String role) {
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(member.getId(), member.getEmail(), member.getPassword(), member.getName(), member.getRole());
    }
}
