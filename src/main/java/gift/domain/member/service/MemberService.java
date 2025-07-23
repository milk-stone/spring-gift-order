package gift.domain.member.service;

import gift.domain.auth.service.AuthService;
import gift.domain.member.Member;
import gift.domain.member.RoleType;
import gift.domain.member.dto.MemberInfoResponse;
import gift.domain.member.dto.MemberInfoUpdateRequest;
import gift.domain.member.repository.MemberRepository;
import gift.global.dto.CustomPageResponse;
import gift.global.exception.MemberNotFoundException;
import gift.global.exception.NotAdminException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository, AuthService authService) {
        this.memberRepository = memberRepository;
    }

    public MemberInfoResponse getMemberInfo(Long id, Member member) {
        if (!member.getRole().equalsIgnoreCase(RoleType.ADMIN.toString())) {
            throw new NotAdminException("MemberService : getMemberInfo() failed - member is not admin");
        }
        Member targetMember = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("MemberService : getMemberInfo() failed", id));
        return MemberInfoResponse.from(targetMember);
    }

    @Transactional
    public void updateMemberInfo(Long id, Member member, MemberInfoUpdateRequest req) {
        if (!member.getRole().equalsIgnoreCase(RoleType.ADMIN.toString())) {
            throw new NotAdminException("MemberService : updateMemberInfo() failed - member is not admin");
        }
        Member targetMember = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("MemberService : updateMemberInfo() failed", id));
        targetMember.update(req.email(), req.password(), req.name());
        memberRepository.save(targetMember);
    }

    @Transactional
    public void deleteMemberInfo(Long id, Member member) {
        if (!member.getRole().equalsIgnoreCase(RoleType.ADMIN.toString())) {
            throw new NotAdminException("MemberService : deleteMemberInfo() failed - member is not admin");
        }
        Member targetMember = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("MemberService : deleteMemberInfo() failed", id));
        memberRepository.delete(targetMember);
    }

    public CustomPageResponse<MemberInfoResponse> getMembers(Member member, Pageable pageable) {
        if (!member.getRole().equalsIgnoreCase(RoleType.ADMIN.toString())) {
            throw new NotAdminException("MemberService : getMembers() failed - member is not admin");
        }
        Page<Member> members = memberRepository.findAll(pageable);
        if (members.isEmpty()) {
            return null;
        }
        return CustomPageResponse.from(members.map(MemberInfoResponse::from));
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
