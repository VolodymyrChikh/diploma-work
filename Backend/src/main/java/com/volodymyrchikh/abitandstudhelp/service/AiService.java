
package com.volodymyrchikh.abitandstudhelp.service;

import com.google.genai.Client;
import com.google.genai.types.*;
import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AiService {

    private final Client client;
    private GenerateContentConfig config;

    public AiService() {
        this.client = new Client();
    }

    @PostConstruct
    public void init() {
        String documentsContext = loadPdfDocuments();

        String finalSystemPrompt = """
            Ти — суворий цифровий асистент для студентів. Твоя робота — відповідати ТІЛЬКИ на теми освіти та документів.
            
            КРОКИ ОБРОБКИ ЗАПИТУ:
            1. ВИЗНАЧЕННЯ ТЕМИ: Якщо питання стосується побуту (рецепти, машини, політика, спорт), ТИ ПОВИНЕН ВІДМОВИТИ.
               Фраза: "Вибачте, я консультую лише з питань навчання та документів. Це питання поза моєю компетенцією."
            
            2. ПОШУК У БАЗІ ЗНАНЬ:
            =========================================
            %s
            =========================================
            
            3. РЕЗУЛЬТАТ ТА ФОРМАТУВАННЯ:
               - Відповідай українською мовою.
               - Використовуй Markdown розмітку для красивого вигляду.
               - **Жирний шрифт**: використовуй для назв документів, важливих дат та ключових термінів.
               - **Списки**: якщо в інструкції є перелік кроків або документів, ОБОВ'ЯЗКОВО використовуй марковані (•) або нумеровані (1.) списки.
               - **Заголовки**: використовуй ### для розділення логічних частин відповіді.
               - Розбивай текст на короткі абзаци для легкості читання.
            
            ПРАВИЛА ПОВЕДІНКИ:
            - Не вигадуй факти.
            - Якщо в базі НЕМАЄ відповіді: "На жаль, у моїх інструкціях немає інформації про це. Зверніться в деканат."
            """.formatted(documentsContext);

        this.config = GenerateContentConfig.builder()
                .temperature(0.3f)
                .systemInstruction(Content.builder()
                        .parts(List.of(Part.builder()
                                .text(finalSystemPrompt)
                                .build()))
                        .build())
                .build();

        System.out.println("AI Service initialized...");
    }

    public String getAnswer(String message) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    message,
                    config
            );
            return response.text();
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    private String loadPdfDocuments() {
        StringBuilder fullText = new StringBuilder();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources("classpath:documents/*.pdf");

            for (Resource resource : resources) {
                try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(resource.getInputStream()))) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);

                    fullText.append("\n--- Document: ").append(resource.getFilename()).append(" ---\n");
                    fullText.append(text).append("\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Didn't manage to load documents: " + e.getMessage());
        }
        return fullText.toString();
    }
}
