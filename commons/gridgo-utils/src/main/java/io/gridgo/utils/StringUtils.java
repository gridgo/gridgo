package io.gridgo.utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.gridgo.utils.exception.RuntimeIOException;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

public final class StringUtils {

    @Data
    @Builder
    public static class StringFormatOption {

        private boolean autoFormatNumber;
        private DecimalFormat decimalFormat;

        public DecimalFormat getDecimalFormat() {
            if (this.decimalFormat == null) {
                this.decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
                this.decimalFormat.getDecimalFormatSymbols().setGroupingSeparator(',');
            }
            return this.decimalFormat;
        }
    }

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

    /**
     * Replace consecutive whitespace characters with a single space.
     *
     * @param value input String
     * @return collapsed String
     */
    public static String collapseWhitespace(final String value) {
        if (value == null) {
            throw new RuntimeException("Value must not be null.");
        }
        return value.trim().replaceAll("\\s\\s+", " ");
    }

    /**
     * Transform to StudlyCaps.
     *
     * @param value The input String
     * @return String in StudlyCaps.
     */
    public static String toStudlyCase(final String value) {
        if (value == null) {
            throw new RuntimeException("Value must not be null.");
        }

        String[] words = collapseWhitespace(value.trim()).split("\\s*(_|-|\\s)\\s*");
        StringBuilder buf = new StringBuilder(value.length());

        if (words != null && words.length > 0) {
            for (int i = 0; i < words.length; i++) {
                buf.append(upperCaseFirstLetter(words[i]));
            }
        }
        return buf.toString();
    }

    /**
     * Transform to camelCase
     *
     * @param value The input String
     * @return String in camelCase.
     */
    public static String toCamelCase(final String value) {
        if (value == null) {
            throw new RuntimeException("Value must not be null.");
        }
        String str = toStudlyCase(value);
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static final String upperCaseFirstLetter(String inputString) {
        if (inputString == null || inputString.isEmpty())
            return inputString;
        return Character.toUpperCase(inputString.charAt(0)) + inputString.substring(1);
    }

    public static final String lowerCaseFirstLetter(String inputString) {
        if (inputString == null || inputString.isEmpty())
            return inputString;
        return Character.toLowerCase(inputString.charAt(0)) + inputString.substring(1);
    }

    public static final boolean isPrinable(String str) {
        return !match(str, "\\p{C}");
    }

    public static final boolean match(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(string);
        return matcher.find();
    }

    public static String implode(Object... elements) {
        if (elements != null) {
            StringBuilder sb = new StringBuilder();
            for (Object ele : elements) {
                sb.append(ele);
            }
            return sb.toString();
        }
        return null;
    }

    public static String implodeWithGlue(@NonNull String glue, Object... elements) {
        if (elements == null || glue == null)
            return null;
        return implodeWithGlue(glue, Arrays.asList(elements));
    }

    public static String implodeWithGlue(@NonNull String glue, @NonNull Object[] array, int start, int end) {
        if (start >= end) {
            throw new IllegalArgumentException("Start must be less than end, got " + start + " >= " + end);
        }

        if (array.length < start)
            throw new IllegalArgumentException("Array's length must >= " + start + ", got " + array.length);

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

    public static boolean isRepresentNumber(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        boolean foundDot = false;
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c == '.') {
                if (foundDot) {
                    return false;
                }
                foundDot = true;
            } else if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
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

    public static void softTabs(int num, Appendable sb) {
        try {
            for (int i = 0; i < num; i++) {
                sb.append("    ");
            }
        } catch (IOException e) {
            throw new RuntimeIOException("Error while append tab(s)", e);
        }
    }

    public static String tabs(int num) {
        StringBuilder sb = new StringBuilder();
        tabs(num, sb);
        return sb.toString();
    }
}
