package gift.domain.member.dto;

import gift.global.dto.CustomPageResponse;

public record MemberInfoPageResponse(CustomPageResponse<MemberInfoResponse> memberInfoResponses) {
}
