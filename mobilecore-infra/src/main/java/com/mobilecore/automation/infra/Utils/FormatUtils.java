package com.mobilecore.automation.infra.Utils;

import org.apache.commons.lang3.StringUtils;

public class FormatUtils {

	public static String stringArrayToString(String[] array, String separator) {
		return StringUtils.join(array, separator);
	}
}
