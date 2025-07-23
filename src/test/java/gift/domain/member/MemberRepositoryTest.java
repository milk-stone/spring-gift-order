package gift.domain.member;

import gift.domain.member.repository.MemberRepository;
import gift.global.dto.CustomPageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원을 저장하고 ID로 조회하면, 저장된 회원이 반환되어야 한다.")
    void saveAndFindById() {
        // given
        Member newMember = new Member("test@example.com", "password123", "테스트유저");

        // when
        Member savedMember = memberRepository.save(newMember);
        Optional<Member> foundMemberOptional = memberRepository.findById(savedMember.getId());

        // then
        assertThat(foundMemberOptional).isPresent();
        Member foundMember = foundMemberOptional.get();
        assertThat(foundMember.getId()).isNotNull();
        assertThat(foundMember.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("이메일로 회원을 조회하면, 해당 이메일을 가진 회원이 반환되어야 한다.")
    void findByEmail() {
        // given
        Member member = new Member("find@example.com", "password123", "찾을유저");
        memberRepository.save(member);

        // when
        Optional<Member> foundMemberOptional = memberRepository.findByEmail("find@example.com");
        Optional<Member> notFoundMemberOptional = memberRepository.findByEmail("notfound@example.com");

        // then
        assertThat(foundMemberOptional).isPresent();
        assertThat(foundMemberOptional.get().getName()).isEqualTo("찾을유저");
        assertThat(notFoundMemberOptional).isEmpty();
    }

    @Test
    @DisplayName("이메일 존재 여부를 확인하면, 정확한 boolean 값을 반환해야 한다.")
    void existsByEmail() {
        // given
        Member member = new Member("exists@example.com", "password123", "존재유저");
        memberRepository.save(member);

        // when
        boolean shouldBeTrue = memberRepository.existsByEmail("exists@example.com");
        boolean shouldBeFalse = memberRepository.existsByEmail("notexists@example.com");

        // then
        assertThat(shouldBeTrue).isTrue();
        assertThat(shouldBeFalse).isFalse();
    }

    @Test
    @DisplayName("모든 회원을 조회하면, 저장된 모든 회원 리스트가 반환되어야 한다. 모든 회원을 반환할 때 페이지네이션이 적용 되었는지도 확인한다.")
    void findAllMembers() {
        // given
        Member member1 = new Member("user1@test.com", "pass1", "유저1");
        Member member2 = new Member("user2@test.com", "pass2", "유저2");
        memberRepository.saveAll(List.of(member1, member2));
        CustomPageRequest pageRequest = new CustomPageRequest(0, 5, null);
        Pageable pageable = pageRequest.toPageable();

        // when
        Page<Member> members = memberRepository.findAll(pageable);
        List<Member> memberList = members.getContent();

        // then
        assertThat(memberList).hasSize(2);
        assertThat(memberList).extracting(Member::getEmail)
                .containsExactlyInAnyOrder("user1@test.com", "user2@test.com");
    }

    @Test
    @DisplayName("회원을 삭제하면, 더 이상 해당 회원이 조회되지 않아야 한다.")
    void deleteMember() {
        // given
        Member member = new Member("delete@example.com", "password123", "삭제될유저");
        Member savedMember = memberRepository.save(member);
        Long memberId = savedMember.getId();

        // when
        memberRepository.delete(savedMember);

        // then
        Optional<Member> foundMemberOptional = memberRepository.findById(memberId);
        assertThat(foundMemberOptional).isEmpty();
    }
}
