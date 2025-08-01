package gift.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.auth.dto.KakaoTalkMessage;
import gift.domain.auth.dto.KakaoTalkResponse;
import gift.domain.auth.dto.KakaoTokenResponse;
import gift.domain.auth.dto.KakaoUserResponse;
import gift.domain.auth.properties.KakaoProperties;
import gift.global.exception.ExternalApiException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@Service
public class KakaoApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KakaoProperties kakaoProperties;

    public KakaoApiService(RestTemplate restTemplate, ObjectMapper objectMapper, KakaoProperties kakaoProperties) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.kakaoProperties = kakaoProperties;
    }

    public String buildAuthUrl() {
        return UriComponentsBuilder.fromUriString(kakaoProperties.getAuthBaseUrl())
                .path("/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoProperties.getClientId())
                .queryParam("redirect_uri", kakaoProperties.getRedirectUri())
                .build()
                .toUriString();
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
        String baseUrl = kakaoProperties.getApiBaseUrl() + "/user/me";
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

    public KakaoTalkResponse postSelfKakaoTalk(String accessToken, KakaoTalkMessage message) {
        String baseUrl = kakaoProperties.getApiBaseUrl() + "/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        String templateJson;
        try {
            templateJson = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 중 오류가 발생했습니다.", e);
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        URI uri = UriComponentsBuilder.fromUriString(baseUrl).build().toUri();

        ResponseEntity<KakaoTalkResponse> response = restTemplate.exchange(
                uri, HttpMethod.POST, request, KakaoTalkResponse.class
        );

        if (Objects.requireNonNull(response.getBody().responseCode() != 0)){
            throw new ExternalApiException("카카오톡 전송 도중 문제가 발생했습니다.");
        }
        return response.getBody();
    }
}
