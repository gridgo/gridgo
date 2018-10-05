package io.gridgo.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Builder;
import lombok.Data;

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

	public static final String upperCaseFirstLetter(String inputString) {
		if (inputString == null) {
			return null;
		}
		return Character.toUpperCase(inputString.charAt(0)) + inputString.substring(1);
	}

	public static final String lowerCaseFirstLetter(String inputString) {
		if (inputString == null) {
			return null;
		}
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

	public static String implodeWithGlue(String glue, Object... elements) {
		if (elements != null && glue != null) {
			StringBuilder sb = new StringBuilder();
			boolean isFirst = true;
			for (Object ele : elements) {
				if (!isFirst) {
					sb.append(glue);
				} else {
					isFirst = false;
				}
				sb.append(ele);
			}
			return sb.toString();
		}
		return null;
	}

	public static String implodeWithGlue(String glue, List<?> elements) {
		if (elements != null && glue != null) {
			StringBuilder sb = new StringBuilder();
			boolean isFirst = true;
			for (Object ele : elements) {
				if (!isFirst) {
					sb.append(glue);
				} else {
					isFirst = false;
				}
				sb.append(ele);
			}
			return sb.toString();
		}
		return null;
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

	public static void tabs(int num, StringBuilder sb) {
		for (int i = 0; i < num; i++) {
			sb.append("\t");
		}
	}

	public static String tabs(int num) {
		StringBuilder sb = new StringBuilder();
		tabs(num, sb);
		return sb.toString();
	}
}
