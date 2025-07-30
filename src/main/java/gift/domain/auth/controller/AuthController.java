package gift.domain.auth.controller;

import gift.domain.auth.dto.LoginRequest;
import gift.domain.auth.dto.SignInRequest;
import gift.domain.auth.dto.TokenResponse;
import gift.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/members/register")
    private ResponseEntity<TokenResponse> signInAndLogin(@RequestBody @Valid SignInRequest signInRequest) {
        return new ResponseEntity<>(authService.signIn(signInRequest), HttpStatus.CREATED);
    }

    @PostMapping("/api/members/login")
    private ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }

    @GetMapping("/auth/kakao")
    public ResponseEntity<Void> redirectToKakao() {
        String kakaoAuthUrl = authService.buildAuthUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(kakaoAuthUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestParam("code") String code) {
        TokenResponse tokenResponse = authService.kakaoLogin(code);
        return ResponseEntity.ok().body(tokenResponse);
    }
}
