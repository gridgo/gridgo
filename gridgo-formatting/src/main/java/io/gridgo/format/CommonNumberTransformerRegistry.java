package io.gridgo.format;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;

import com.udojava.evalex.Expression;

public class CommonNumberTransformerRegistry extends DefaultFormatTransformerRegistry {

	public static final FormatTransformer newDecimaFormatTransformer(final String pattern) {
		return source -> {
			if (source == null) {
				return null;
			}
			DecimalFormat df = new DecimalFormat(pattern);
			return df.format(Double.valueOf(source.toString()));
		};
	}

	public static final FormatTransformer newEvalExpTransformer(final String expression,
			final String defaultSingleVariable) {
		return source -> {
			if (source == null) {
				return null;
			}

			final Expression exp = new Expression(expression);
			if (source instanceof Map) {
				for (Entry<?, ?> entry : ((Map<?, ?>) source).entrySet()) {
					exp.with(entry.getKey().toString(),
							entry.getValue() instanceof Number
									? new BigDecimal(((Number) entry.getValue()).doubleValue())
									: new BigDecimal(entry.getValue().toString()));
				}
			} else {
				exp.with(defaultSingleVariable,
						source instanceof Number ? new BigDecimal(((Number) source).doubleValue())
								: new BigDecimal(source.toString()));
			}
			return exp.eval();
		};
	}

	public static final FormatTransformer newXEvalExpTransformer(final String expression) {
		return newEvalExpTransformer(expression, "x");
	}

	public static final FormatTransformer ROUND_TO_INTEGER = (source) -> source == null ? null
			: Long.valueOf(new BigDecimal(source.toString()).longValue()).toString();
	public static final FormatTransformer FORMAT_PERCENTAGE = newDecimaFormatTransformer("#.##%");
	public static final FormatTransformer FORMAT_WITH_THOUSAND_SEPARATOR = newDecimaFormatTransformer("###,###.####");

	public static final CommonNumberTransformerRegistry newInstance() {
		return new CommonNumberTransformerRegistry();
	}

	private CommonNumberTransformerRegistry() {
		this.addTransformer("roundToInteger", ROUND_TO_INTEGER);
		this.addTransformer("percentage", FORMAT_PERCENTAGE);
		this.addTransformer("thousandSeparate", FORMAT_WITH_THOUSAND_SEPARATOR);
	}

}
