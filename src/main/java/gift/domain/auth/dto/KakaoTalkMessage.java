package gift.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTalkMessage(
        @JsonProperty("object_type")
        String objectType,

        @JsonProperty("text")
        String text,

        @JsonProperty("link")
        KakaoLink link,

        @JsonProperty("button_title")
        String buttonTitle
) {
}
