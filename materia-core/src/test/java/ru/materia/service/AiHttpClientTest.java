

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.materia.service.ai.exception.AiApiException;
import ru.materia.service.ai.http.AiHttpClient;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AiHttpClientTest {

    private static final String URL = "https://example.com/api";
    private static final String JSON_BODY = "{\"message\":\"test\"}";
    private static final String API_KEY = "test-api-key";

    private HttpClient mockClient;
    private AiHttpClient aiHttpClient;

    @BeforeEach
    void setUp() {
        mockClient = mock(HttpClient.class);
        aiHttpClient = new AiHttpClient(mockClient);
    }

    @Test
    void shouldReturnBodyWhenStatusIs200() throws Exception {
        mockResponse(200, "{\"result\":\"success\"}");

        String result = aiHttpClient.sendPost(
                URL, JSON_BODY, API_KEY
        );

        assertEquals("{\"result\":\"success\"}", result);
    }

    @Test
    void shouldThrowAiApiExceptionWhenStatusIs400() throws Exception {
        mockResponse(400, "Bad Request");

        AiApiException exception = assertThrows(
                AiApiException.class,
                () -> aiHttpClient.sendPost(URL, JSON_BODY, API_KEY)
        );

        assertEquals(400, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Bad Request"));
    }

    @Test
    void shouldThrowAiApiExceptionWhenStatusIs429() throws Exception {
        mockResponse(429, "Too Many Requests");

        AiApiException exception = assertThrows(
                AiApiException.class,
                () -> aiHttpClient.sendPost(URL, JSON_BODY, API_KEY)
        );

        assertEquals(429, exception.getStatusCode());
    }

    @Test
    void shouldThrowAiApiExceptionWhenStatusIs503() throws Exception {
        mockResponse(503, "Service Unavailable");

        AiApiException exception = assertThrows(
                AiApiException.class,
                () -> aiHttpClient.sendPost(URL, JSON_BODY, API_KEY)
        );

        assertEquals(503, exception.getStatusCode());
    }

    @Test
    void shouldRejectNullUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> aiHttpClient.sendPost(null, JSON_BODY, API_KEY)
        );
    }

    @Test
    void shouldRejectBlankJsonBody() {
        assertThrows(
                IllegalArgumentException.class,
                () -> aiHttpClient.sendPost(URL, "   ", API_KEY)
        );
    }

    @Test
    void shouldRejectBlankApiKey() {
        assertThrows(
                IllegalArgumentException.class,
                () -> aiHttpClient.sendPost(URL, JSON_BODY, "   ")
        );
    }

    @SuppressWarnings("unchecked")
    private void mockResponse(int statusCode, String body) throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);

        when(mockClient.send(
                any(HttpRequest.class),
                org.mockito.ArgumentMatchers
                        .<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(response);
    }
}
