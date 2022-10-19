package com.scai.entities.utilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtilites {

	public static boolean isDoubleNumeric(String string) {

		if (string == null || string.equals("")) {
			return false;
		}
		try {
			if (Double.parseDouble(string) > 0) {
				return true;
			} else {
				return false;
			}

		} catch (NumberFormatException e) {
			log.error("Entities_Service : : : : > Exception in isDoubleNumeric : " + e.getMessage());
		}
		return false;
	}

}
