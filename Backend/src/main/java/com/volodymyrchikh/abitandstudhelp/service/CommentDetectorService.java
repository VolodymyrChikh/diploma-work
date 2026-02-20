package com.volodymyrchikh.abitandstudhelp.service;


import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Getter
public class CommentDetectorService {

    private final LanguageDetector detector;
    private final Set<Language> supportedLanguages;

    private final Set<String> curseWords = Set.of(
            "блядь", "хуй", "пизда", "сука", "ебать", "еблан", "пидор", "пидорас",
            "херня", "говно", "мудак", "урод", "идиот", "тупица", "долбоеб",
            "козел", "сволочь", "тварь", "пиздюк", "чмо", "блядина", "пиздострадалец",
            "ебланство", "хуета", "пиздец", "ебать тебя в рот", "сука блять", "ебать твою мать",
            "ебать в уши", "пиздеть", "ебать мозги", "хуйня", "ебать в рот", "сука ебать",
            "ебать в жопу", "пиздить", "ебать по лицу", "хуй сосать", "ебать в глаза",
            "сука блять ебать", "пиздеть в рот", "блять", "хуєта", "довбограй", "далбайоб",
            "ідіот", "дебіл", "пізда", "дурак", "тупий", "сволота", "тварина", "піздюк", "fuck",
            "fucking", "fuck you", "bitch", "moron", "mf", "motherfucker", "asshole", "ass",
            "dick"
    );

    public CommentDetectorService() {
        this.supportedLanguages = Set.of(
                Language.ENGLISH,
                Language.UKRAINIAN,
                Language.RUSSIAN
        );

        this.detector = LanguageDetectorBuilder.fromLanguages(
                supportedLanguages.toArray(new Language[0])
        ).build();
    }

    public Language detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Language.UNKNOWN;
        }

        Map<Language, Double> confidenceValues = detector.computeLanguageConfidenceValues(text);

        Optional<Map.Entry<Language, Double>> bestMatch = confidenceValues.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        if (bestMatch.isPresent() && bestMatch.get().getValue() > 0.6) {
            return bestMatch.get().getKey();
        } else {
            return Language.UNKNOWN;
        }
    }

    public Boolean containsCurseWords(String commentContent) {
        for (String curseWord : curseWords) {
            if (commentContent.toLowerCase().contains(curseWord)) {
                return true;
            }
        }

        return false;
    }
}