package gift.domain.member.controller;

import gift.domain.annotation.LoginMember;
import gift.domain.member.Member;
import gift.domain.member.dto.MemberInfoPageResponse;
import gift.domain.member.dto.MemberInfoResponse;
import gift.domain.member.dto.MemberInfoUpdateRequest;
import gift.domain.member.service.MemberService;
import gift.global.dto.CustomPageRequest;
import gift.global.exception.TokenExpiredException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(
            @PathVariable Long id,
            @LoginMember Member member) throws TokenExpiredException {
        return new ResponseEntity<>(memberService.getMemberInfo(id, member), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMemberInfo(
            @PathVariable Long id,
            @RequestBody MemberInfoUpdateRequest request,
            @LoginMember Member member) throws TokenExpiredException {
        memberService.updateMemberInfo(id, member, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberInfo(
            @PathVariable Long id,
            @LoginMember Member member) throws TokenExpiredException {
        memberService.deleteMemberInfo(id, member);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<MemberInfoPageResponse> getMemberInfos(
            @LoginMember Member member,
            @Valid @ModelAttribute CustomPageRequest customPageable) {
        Pageable pageable = customPageable.toPageable();
        return new ResponseEntity<>(new MemberInfoPageResponse(memberService.getMembers(member, pageable)), HttpStatus.OK);
    }
}
