package gift.domain.auth.controller;

import gift.domain.auth.dto.LoginRequest;
import gift.domain.auth.dto.SignInRequest;
import gift.domain.auth.dto.TokenResponse;
import gift.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    private ResponseEntity<TokenResponse> signInAndLogin(@RequestBody @Valid SignInRequest signInRequest) {
        return new ResponseEntity<>(authService.signIn(signInRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    private ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }
}
