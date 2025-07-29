package gift.domain.auth.service;

import gift.domain.auth.dto.*;
import gift.domain.auth.jwt.JwtProvider;
import gift.domain.member.Member;
import gift.domain.member.repository.MemberRepository;
import gift.global.exception.BadRequestException;
import gift.global.exception.LoginFailedException;
import gift.global.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final KakaoApiService kakaoApiService;

    public AuthService(MemberRepository memberRepository, JwtProvider jwtProvider, KakaoApiService kakaoApiService) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
        this.kakaoApiService = kakaoApiService;
    }

    public TokenResponse signIn(SignInRequest signInRequest) {
        String email = signInRequest.email();
        if (memberRepository.existsByEmail(email)) {
            throw new BadRequestException("AuthService : signIn() failed - Already registered member");
        }
        Member member = new Member(email, signInRequest.password(), signInRequest.name());
        memberRepository.save(member);
        if (!memberRepository.existsByEmail(email)) {
            throw new MemberNotFoundException("AuthService : signIn() failed - Member not saved");
        }
        Member member1 = memberRepository.findByEmail(member.getEmail()).orElseThrow(() -> new MemberNotFoundException("AuthService : signIn() failed - Member not found"));
        String accessToken = jwtProvider.generateToken(member1);
        return new TokenResponse(accessToken);
    }

    public TokenResponse login(LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();
        if (!memberRepository.existsByEmail(email)) {
            throw new MemberNotFoundException("AuthService : login() failed - 404 Not Found Error");
        }
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("AuthService : login() failed - Member not found"));
        if (!member.verifyPassword(password)) {
            throw new LoginFailedException("AuthService : login() failed - Wrong Password Error");
        }
        String accessToken = jwtProvider.generateToken(member);
        return new TokenResponse(accessToken);
    }

    public Optional<Member> getMemberByToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtProvider.extractEmailFromAccessToken(token);
        if (!memberRepository.existsByEmail(email)) {
            throw new MemberNotFoundException("MemberService : validateToken() failed - Member with email " + email + " not found");
        }
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public TokenResponse kakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = kakaoApiService.getAccessToken(code);
        KakaoUserResponse userResponse = kakaoApiService.getUserInfo(tokenResponse.accessToken());

        String email = userResponse.kakaoAccount().email();
        String nickname = userResponse.kakaoAccount().profile().nickname();

        if (!memberRepository.existsByEmail(email)) {
            Member member = new Member(email, "password", nickname);
            memberRepository.save(member);
        }
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException("AuthService : kakaoLogin() failed - Member not found"));

        String accessToken = jwtProvider.generateToken(member);

        return new TokenResponse(accessToken);

    }
}
