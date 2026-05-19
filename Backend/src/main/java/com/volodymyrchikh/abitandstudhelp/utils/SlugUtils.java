package com.volodymyrchikh.abitandstudhelp.utils;

import com.ibm.icu.text.Transliterator;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final String CYRILLIC_TO_LATIN = "Any-Latin; Latin-ASCII";
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s+]+");

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        Transliterator transliterator = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        String result = transliterator.transliterate(input);

        result = result.toLowerCase(Locale.ENGLISH);

        result = WHITESPACE.matcher(result).replaceAll("-");

        result = NONLATIN.matcher(result).replaceAll("");

        result = result.replaceAll("-{2,}", "-").replaceAll("^-|-$", "");

        return result;
    }
}