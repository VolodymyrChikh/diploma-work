package com.volodymyrchikh.abitandstudhelp.service;

import com.google.genai.Client;
import com.google.genai.types.*;
import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.volodymyrchikh.abitandstudhelp.dto.ChatMessageDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        String documentsContext = loadDocuments();

        String finalSystemPrompt = """
                        Ти — розумний, впевнений та приязний ШІ-агент — цифровий помічник факультету.
                Твоя робота — консультувати студентів з питань навчання, структури факультету та документів.
                
                ПРАВИЛА СПІЛКУВАННЯ ТА СТИЛЮ (КРИТИЧНО ВАЖЛИВО):
                1. ГОВОРИ ВІД СЕБЕ ЯК ЕКСПЕРТ: Формулюй відповіді так, наче ти особисто знаєш усю інформацію про факультет.
                   НІКОЛИ не використовуй фрази: "згідно з наданими документами", "у знайдених файлах", "у моїх інструкціях", "згадується в базі знань".
                   Замість "У документах написано, що він доцент" кажи прямо: "Він працює доцентом...". Приховуй від користувача сам факт існування текстової бази знань.
                
                2. ЧІТКЕ І ВВІЧЛИВЕ ЗАПЕРЕЧЕННЯ: Якщо користувач припускає хибний факт (наприклад, плутає посаду чи людину), спочатку чітко, але ввічливо запереч це, а потім дай правильну інформацію.
                   Приклад відповіді: "Ні, Сергій Адамович Ярошко не є деканом. Він очолює кафедру програмування, а також..."
                
                КРОКИ ОБРОБКИ ЗАПИТУ:
                1. ВИЗНАЧЕННЯ ТЕМИ: Якщо питання стосується побуту (рецепти, машини, політика, спорт), ТИ ПОВИНЕН ВІДМОВИТИ.
                   Фраза: "Вибачте, я консультую лише з питань навчання та документів. Це питання поза моєю компетенцією."
                
                2. ТВОЯ БАЗА ЗНАНЬ ПРО ФАКУЛЬТЕТ:
                =========================================
                %s
                =========================================
                
                3. РЕЗУЛЬТАТ ТА ФОРМАТУВАННЯ:
                   - Відповідай українською мовою.
                   - Використовуй Markdown розмітку для красивого вигляду.
                   - **Жирний шрифт**: використовуй для назв документів, посад, важливих дат та ключових термінів.
                   - **Списки**: якщо у відповіді є перелік кроків або посад, ОБОВ'ЯЗКОВО використовуй марковані (•) або нумеровані (1.) списки.
                   - **Заголовки**: використовуй ### для розділення логічних частин відповіді.
                   - Розбивай текст на короткі абзаци для легкості читання.
                
                ПРАВИЛА ПОВЕДІНКИ ПРИ ВІДСУТНОСТІ ФАКТІВ:
                - Не вигадуй факти, яких немає у твоїй базі знань.
                - Якщо інформації про запит взагалі немає у блоці вище, відповідай: "На жаль, я не маю точної інформації про це. Зверніться, будь ласка, до деканату."
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

    public String getAnswer(List<ChatMessageDto> messages) {
        try {
            if (messages == null || messages.isEmpty()) {
                return "Exception: Chat history is empty";
            }

            List<Content> contents = new ArrayList<>();
            for (ChatMessageDto msg : messages) {
                if (msg == null || msg.getText() == null || msg.getText().isBlank()) {
                    continue;
                }
                contents.add(Content.builder()
                        .role(msg.getRole() != null ? msg.getRole() : "user")
                        .parts(List.of(Part.builder().text(msg.getText()).build()))
                        .build());
            }

            if (contents.isEmpty()) {
                return "Exception: Chat history has no usable messages";
            }

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    contents,
                    config
            );
            return response.text();
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    public String getAnswer(String message) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRole("user");
        dto.setText(message);
        return getAnswer(List.of(dto));
    }

    private String loadDocuments() {
        StringBuilder fullText = new StringBuilder();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources("classpath:documents/*.*");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null) continue;
                String lowerName = filename.toLowerCase();
                try {
                    String text;
                    if (lowerName.endsWith(".pdf")) {
                        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(resource.getInputStream()))) {
                            PDFTextStripper stripper = new PDFTextStripper();
                            text = stripper.getText(document);
                        }
                    } else if (lowerName.endsWith(".docx")) {
                        try (var is = resource.getInputStream();
                             XWPFDocument doc = new XWPFDocument(is);
                             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                            text = extractor.getText();
                        }
                    } else if (lowerName.endsWith(".txt")) {
                        try (var is = resource.getInputStream()) {
                            text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        }
                    } else {
                        continue;
                    }

                    fullText.append("\n--- Document: ").append(filename).append(" ---\n");
                    fullText.append(text).append("\n");
                } catch (Exception ex) {
                    System.err.println("Error reading file " + filename + ": " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Didn't manage to load documents: " + e.getMessage());
        }
        return fullText.toString();
    }
}
