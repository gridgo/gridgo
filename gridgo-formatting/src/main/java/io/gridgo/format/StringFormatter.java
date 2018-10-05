package io.gridgo.format;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.gridgo.utils.ObjectUtils;
import io.gridgo.utils.PrimitiveUtils;
import io.gridgo.utils.StringUtils;
import io.gridgo.utils.StringUtils.StringFormatOption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringFormatter {

	public static String format(String pattern, Object args, StringFormatOption option) {
		List<String> matches = StringUtils.getAllMatches(pattern, "\\{\\{[a-zA-Z0-9_]+\\}\\}");
		Set<String> keys = new HashSet<>();
		for (String matche : matches) {
			keys.add(matche.substring(2, matche.length() - 2));
		}
		String result = new String(pattern);
		for (String key : keys) {
			Object value = ObjectUtils.getValueByPath(args, key);
			if (value instanceof Number && option != null && option.isAutoFormatNumber()) {
				value = option.getDecimalFormat().format(value);
			}
			try {
				String valueString = PrimitiveUtils.getStringValueFrom(value);
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
		if (source != null) {
			if (transformerRegistry == null) {
				return format(source, args, null);
			}

			Pattern pattern = Pattern.compile("\\{\\{([^\\{\\}]+)\\}\\}");
			Matcher matcher = pattern.matcher(source);
			List<String[]> matchedGroups = new LinkedList<>();
			while (matcher.find()) {
				matchedGroups.add(new String[] { matcher.group(0), matcher.group(1) });
			}

			if (matchedGroups.isEmpty()) {
				return source;
			}

			String result = source;
			for (String[] matchedGroup : matchedGroups) {
				final String key = matchedGroup[0];
				String argName = matchedGroup[1];

				String[] arr = argName.trim().split("\\s*>\\s*");

				Object value = ObjectUtils.getValueByPath(args, arr[0]);
				if (value != null) {
					if (arr.length > 1) {
						String[] transformerNames = Arrays.copyOfRange(arr, 1, arr.length);
						List<FormatTransformer> chain = transformerRegistry.getChain(transformerNames);
						for (FormatTransformer transformer : chain) {
							value = transformer.transform(value);
						}
					}
					result = result.replaceAll(StringUtils.normalizeForRegex(key),
							PrimitiveUtils.getStringValueFrom(value));
				}
			}

			return result;
		}
		return null;
	}
}
