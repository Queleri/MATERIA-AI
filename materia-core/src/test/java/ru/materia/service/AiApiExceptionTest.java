package ru.materia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.materia.service.ai.exception.AiApiException;
import static org.junit.jupiter.api.Assertions.*;


public class AiApiExceptionTest {
private AiApiException aiApiException;
    @BeforeEach
    void setUp(){
        aiApiException = new AiApiException(400, "Bad Request");
    }

    @Test
    void shouldStoreStatusCode() {
        assertEquals(400, aiApiException.getStatusCode());
    }

    @Test
    void shouldStoreErrorMessage() {
        assertEquals("Bad Request", aiApiException.getMessage());
    }

    @Test
    void shouldBeRuntimeException() {
        assertTrue(aiApiException instanceof RuntimeException, "Api exception должнен наслежоваться от RuntimeException");
    }
}
