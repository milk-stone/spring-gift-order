package gift.domain.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.is4xxClientError() || statusCode.is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        String body = new String(response.getBody().readAllBytes());

        if (statusCode.is4xxClientError()) {
            log.error("클라이언트 에러 - URL: {}, Method: {}, Status: {} - ResponseBody: {}", url, method, statusCode, body);
            throw new HttpClientErrorException(statusCode, response.getStatusText(),
                    response.getHeaders(), body.getBytes(), null);
        }

        if (statusCode.is5xxServerError()) {
            log.error("서버 에러 - URL: {}, Method: {}, Status: {} - ResponseBody: {}", url, method, statusCode, body);
            throw new HttpServerErrorException(statusCode, response.getStatusText(),
                    response.getHeaders(), body.getBytes(), null);
        }
    }

}
