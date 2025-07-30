package gift.domain.auth.service;

import gift.domain.auth.dto.KakaoTokenResponse;
import gift.domain.auth.dto.KakaoUserResponse;
import gift.domain.auth.properties.KakaoProperties;
import gift.global.exception.ExternalApiException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@Service
public class KakaoApiService {
    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;

    public KakaoApiService(RestTemplate restTemplate, KakaoProperties kakaoProperties) {
        this.restTemplate = restTemplate;
        this.kakaoProperties = kakaoProperties;
    }

    public KakaoTokenResponse getAccessToken(String authorizationCode) {
        String url = kakaoProperties.getAuthBaseUrl() + "/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        String redirectUri = kakaoProperties.getRedirectUri();

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);

        RequestEntity<LinkedMultiValueMap<String, String>> request = new RequestEntity<>(body,
                headers, HttpMethod.POST, URI.create(url));

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(request,
                KakaoTokenResponse.class);
        return response.getBody();
    }

    public KakaoUserResponse getUserInfo(String accessToken) {
        String baseUrl = kakaoProperties.getApiBaseUrl() + "/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]")
                .build()
                .toUri();

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(
                uri, HttpMethod.GET, request, KakaoUserResponse.class);

        if (Objects.requireNonNull(response.getBody()).kakaoAccount().email() == null) {
            throw new ExternalApiException("카카오 계정으로부터 전달받은 이메일이 없습니다.");
        }
        return response.getBody();
    }
}