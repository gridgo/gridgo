package io.gridgo.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.gridgo.utils.exception.RuntimeIOException;
import lombok.NonNull;

public final class StringUtils {

    private StringUtils() {
        // just prevent other can create new instance...
    }

    public static List<String> getAllMatches(String text, String regex) {
        List<String> matches = new ArrayList<String>();
        Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
        while (m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }

    public static final String upperCaseFirstLetter(String inputString) {
        if (inputString == null || inputString.isEmpty())
            return inputString;
        return Character.toUpperCase(inputString.charAt(0))
                + (inputString.length() == 1 ? "" : inputString.substring(1));
    }

    public static final String lowerCaseFirstLetter(String inputString) {
        if (inputString == null || inputString.isEmpty())
            return inputString;
        return Character.toLowerCase(inputString.charAt(0))
                + (inputString.length() == 1 ? "" : inputString.substring(1));
    }

    public static String implodeWithGlue(@NonNull String glue, @NonNull Collection<?> elements) {
        if (elements.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        var it = elements.iterator();
        sb.append(it.next());
        while (it.hasNext()) {
            sb.append(glue).append(it.next());
        }
        return sb.toString();
    }

    public static String implodeWithGlue(@NonNull String glue, @NonNull Object[] array, int start, int end) {
        if (start >= end) {
            throw new IllegalArgumentException("Start must be less than end, got " + start + " >= " + end);
        }
        if (array.length < start) {
            throw new IllegalArgumentException("Array's length must >= " + start + ", got " + array.length);
        }
        if (array.length < end) {
            throw new IllegalArgumentException("Array's length must >= " + end + ", got " + array.length);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(array[start]);
        for (int i = start + 1; i < end; i++) {
            sb.append(glue).append(array[i]);
        }
        return sb.toString();
    }

    private static final String[] REGEX_SPECIAL_CHARS = new String[] { "\\", ".", "*", "+", "-", "[", "]", "(", ")",
            "$", "^", "|", "{", "}", "?" };

    public static final String normalizeForRegex(String key) {
        String result = key;
        for (String c : REGEX_SPECIAL_CHARS) {
            result = result.replaceAll("\\" + c, "\\\\\\" + c);
        }
        return result;
    }

    public static void tabs(int num, Appendable sb) {
        try {
            for (int i = 0; i < num; i++) {
                sb.append("\t");
            }
        } catch (IOException e) {
            throw new RuntimeIOException("Error while append tab(s)", e);
        }
    }
}
