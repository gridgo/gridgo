package io.gridgo.utils.format;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.StringUtils;
import io.gridgo.utils.pojo.PojoUtils;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringFormatter {

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

    public static String format(String pattern, Object args, StringFormatOption option) {
        var matches = StringUtils.getAllMatches(pattern, "\\{\\{[a-zA-Z0-9_]+\\}\\}");
        var keys = new HashSet<String>();
        for (var matche : matches) {
            keys.add(matche.substring(2, matche.length() - 2));
        }
        var result = pattern;
        for (var key : keys) {
            var value = PojoUtils.getValueByPath(args, key);
            if (value instanceof Number && option != null && option.isAutoFormatNumber()) {
                value = option.getDecimalFormat().format(value);
            }
            try {
                var valueString = PrimitiveUtils.getStringValueFrom(value);
                result = result.replaceAll("\\{\\{" + key + "\\}\\}", valueString);
            } catch (Exception ex) {
                log.error("Error while inject value for key: `{}`, value={}", key, value);
                throw ex;
            }
        }
        return result;
    }

    /**
     * Transform using GlobalFormatTransformerRegistry singleton instance
     *
     * @param source
     * @param args
     * @return
     */
    public static String transform(String source, Object args) {
        return transform(source, args, GlobalFormatTransformerRegistry.getInstance());
    }

    public static String transform(String source, Object args, FormatTransformerRegistry transformerRegistry) {
        if (source == null)
            return null;
        if (transformerRegistry == null)
            return format(source, args, null);

        var pattern = Pattern.compile("\\{\\{([^\\{\\}]+)\\}\\}");
        var matcher = pattern.matcher(source);
        var matchedGroups = new LinkedList<String[]>();
        while (matcher.find()) {
            matchedGroups.add(new String[] { matcher.group(0), matcher.group(1) });
        }

        if (matchedGroups.isEmpty())
            return source;

        var result = source;
        for (var matchedGroup : matchedGroups) {
            var key = matchedGroup[0];
            var argName = matchedGroup[1];

            var arr = argName.trim().split("\\s*>\\s*");

            var value = PojoUtils.getValueByPath(args, arr[0]);
            if (value != null) {
                if (arr.length > 1) {
                    var transformerNames = Arrays.copyOfRange(arr, 1, arr.length);
                    var chain = transformerRegistry.getChain(transformerNames);
                    for (var transformer : chain) {
                        value = transformer.transform(value instanceof Supplier ? ((Supplier<?>) value).get() : value);
                    }
                }
                result = result.replaceAll(StringUtils.normalizeForRegex(key),
                        PrimitiveUtils.getStringValueFrom(value));
            }
        }

        return result;
    }
}
