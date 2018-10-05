package io.gridgo.format;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CommonDateTransformerRegistry extends DefaultFormatTransformerRegistry {

	public static final FormatTransformer newDateTransformer(final String pattern, final String timeZoneId) {
		if (pattern != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			if (timeZoneId != null) {
				sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
			}
			return (source) -> {
				if (source != null) {
					if (source instanceof Date) {
						return sdf.format(source);
					}
					return source;
				}
				return null;
			};
		}
		return null;
	}

	public static final FormatTransformer LOCAL_ONLY_DATE = newDateTransformer("yyyy/MM/dd XXX", null);
	public static final FormatTransformer LOCAL_ONLY_TIME_24 = newDateTransformer("HH:mm:ss.SSS", null);
	public static final FormatTransformer LOCAL_ONLY_TIME_12 = newDateTransformer("hh:mm:ss.SSS a", null);
	public static final FormatTransformer LOCAL_FULL_TIME_24 = newDateTransformer("yyyy/MM/dd HH:mm:ss.SSS XXX", null);
	public static final FormatTransformer LOCAL_FULL_TIME_12 = newDateTransformer("yyyy/MM/dd hh:mm:ss.SSS a XXX",
			null);

	public static final FormatTransformer GMT_ONLY_DATE = newDateTransformer("yyyy/MM/dd XXX", "GMT");
	public static final FormatTransformer GMT_ONLY_TIME_24 = newDateTransformer("HH:mm:ss.SSS", "GMT");
	public static final FormatTransformer GMT_ONLY_TIME_12 = newDateTransformer("hh:mm:ss.SSS a", "GMT");
	public static final FormatTransformer GMT_FULL_TIME_24 = newDateTransformer("yyyy/MM/dd HH:mm:ss.SSS XXX", "GMT");
	public static final FormatTransformer GMT_FULL_TIME_12 = newDateTransformer("yyyy/MM/dd hh:mm:ss.SSS a XXX", "GMT");

	public static final FormatTransformerRegistry newInstance() {
		return new CommonDateTransformerRegistry();
	}

	private CommonDateTransformerRegistry() {
		this.addTransformer("localOnlyDate", LOCAL_ONLY_DATE);
		this.addTransformer("localOnlyTime24", LOCAL_ONLY_TIME_24);
		this.addTransformer("localOnlyTime12", LOCAL_ONLY_TIME_12);
		this.addTransformer("localFullTime24", LOCAL_FULL_TIME_24);
		this.addTransformer("localFullTime12", LOCAL_FULL_TIME_12);

		this.addTransformer("gmtOnlyDate", GMT_ONLY_DATE);
		this.addTransformer("gmtOnlyTime24", GMT_ONLY_TIME_24);
		this.addTransformer("gmtOnlyTime12", GMT_ONLY_TIME_12);
		this.addTransformer("gmtFullTime24", GMT_FULL_TIME_24);
		this.addTransformer("gmtFullTime12", GMT_FULL_TIME_12);
	}

}
