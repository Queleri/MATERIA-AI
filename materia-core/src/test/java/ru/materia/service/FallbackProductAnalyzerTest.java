package ru.materia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.AiProductAnalyzer;
import ru.materia.service.ai.FallbackProductAnalyzer;
import ru.materia.service.ai.exception.AiApiException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FallbackProductAnalyzerTest {

    private AiProductAnalyzer primaryAnalyzer;
    private AiProductAnalyzer fallbackAnalyzer;
    private FallbackProductAnalyzer analyzer;

    private final String description = "Деревянный стол шириной 1200 мм";

    @BeforeEach
    void setUp() {
        primaryAnalyzer = mock(AiProductAnalyzer.class);
        fallbackAnalyzer = mock(AiProductAnalyzer.class);
        analyzer = new FallbackProductAnalyzer(primaryAnalyzer, fallbackAnalyzer);
    }

    @Test
    void shouldUsePrimaryAnalyzer() throws Exception {
        ProductSpecification specification = mock(ProductSpecification.class);
        when(primaryAnalyzer.analyze(description)).thenReturn(specification);

        ProductSpecification result = analyzer.analyze(description);

        assertSame(specification, result);
        verify(primaryAnalyzer).analyze(description);
        verifyNoInteractions(fallbackAnalyzer);
    }

    @Test
    void shouldSwitchWhenStatus429() throws Exception {
        ProductSpecification specification = mock(ProductSpecification.class);

        when(primaryAnalyzer.analyze(description))
                .thenThrow(new AiApiException(429, "Лимит закончился"));
        when(fallbackAnalyzer.analyze(description)).thenReturn(specification);

        ProductSpecification result = analyzer.analyze(description);

        assertSame(specification, result);
        verify(fallbackAnalyzer).analyze(description);
    }

    @Test
    void shouldSwitchWhenStatus503() throws Exception {
        ProductSpecification specification = mock(ProductSpecification.class);

        when(primaryAnalyzer.analyze(description))
                .thenThrow(new AiApiException(503, "Сервер недоступен"));
        when(fallbackAnalyzer.analyze(description)).thenReturn(specification);

        ProductSpecification result = analyzer.analyze(description);

        assertSame(specification, result);
        verify(fallbackAnalyzer).analyze(description);
    }

    @Test
    void shouldNotSwitchWhenStatus400() throws Exception {
        when(primaryAnalyzer.analyze(description))
                .thenThrow(new AiApiException(400, "Неверный запрос"));

        AiApiException exception = assertThrows(AiApiException.class,
                () -> analyzer.analyze(description));

        assertEquals(400, exception.getStatusCode());
        verifyNoInteractions(fallbackAnalyzer);
    }

    @Test
    void shouldNotSwitchWhenStatus401() throws Exception {
        when(primaryAnalyzer.analyze(description))
                .thenThrow(new AiApiException(401, "Ошибка авторизации"));

        AiApiException exception = assertThrows(AiApiException.class,
                () -> analyzer.analyze(description));

        assertEquals(401, exception.getStatusCode());
        verifyNoInteractions(fallbackAnalyzer);
    }

    @Test
    void shouldThrowWhenBothAnalyzersFail() throws Exception {
        when(primaryAnalyzer.analyze(description))
                .thenThrow(new AiApiException(429, "Лимит Gemini"));
        when(fallbackAnalyzer.analyze(description))
                .thenThrow(new AiApiException(503, "Ошибка Groq"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> analyzer.analyze(description));

        assertTrue(exception.getMessage().contains("429"));
        assertTrue(exception.getMessage().contains("503"));
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.analyze(""));
        assertThrows(IllegalArgumentException.class, () -> analyzer.analyze("   "));
        assertThrows(IllegalArgumentException.class, () -> analyzer.analyze(null));
    }

    @Test
    void shouldRejectNullAnalyzers() {
        assertThrows(IllegalArgumentException.class,
                () -> new FallbackProductAnalyzer(null, fallbackAnalyzer));

        assertThrows(IllegalArgumentException.class,
                () -> new FallbackProductAnalyzer(primaryAnalyzer, null));
    }
}

