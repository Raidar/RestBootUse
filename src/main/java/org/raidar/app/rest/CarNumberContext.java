package org.raidar.app.rest;

import org.raidar.app.data.CarNumber;

import java.util.HashMap;
import java.util.Random;

public class CarNumberContext {

	public static final int SERIES_LENGTH = 3;
	public static final int DIGITS_LENGTH = 3;
	public static final int REGION_LENGTH_MIN = 4; // one digit + space + two letters
	public static final int NUMBER_LENGTH_MIN = SERIES_LENGTH + DIGITS_LENGTH + REGION_LENGTH_MIN;

	public static final String REGION = "116 RUS";

	private static final String LETTERS = "АВЕКМНОРСТУХ";
	private static final String SERIES_ZERO = new String(new char[SERIES_LENGTH]).replace("\0",
																						  String.valueOf(LETTERS.charAt(0)));
	private static final String DIGITS = "0123456789";
	private static final String DIGITS_ZERO = String.format("%0" + DIGITS_LENGTH + "d", 0);

	//private static final int SERIES_MAX = (int)Math.pow(LETTERS.length(), SERIES_LENGTH);
	private static final int DIGITS_MIN = 1;
	private static final int DIGITS_MAX = Integer.parseInt("1" + DIGITS_ZERO, 10) - 1; // 999

	// Constant to exclude long generation of random number
	public static final int RANDOM_LIMIT = 1000000;
	//public static final int RANDOM_LIMIT = (SERIES_MAX * DIGITS_MAX);

	public static final CarNumber NULL_NUMBER = new CarNumber(SERIES_ZERO, DIGITS_ZERO, REGION);

	public CarNumberContext () {
		// WARN: Static class or Simple class?
	}

	public String formatDigits (int digits) {
		return String.format("%0" + DIGITS_LENGTH + "d", digits);
	}

	public String formatStartDigits () {
		return formatDigits(DIGITS_MIN);
	}

	public void checkNumber (CarNumber value) {

		String series = value.getSeries();
		if (series.length() != SERIES_LENGTH) {
			throw new RuntimeException(String.format("Ошибка в серии '%s' номерного знака", series));
		}

		String digits = value.getDigits();
		if (digits.length() != DIGITS_LENGTH) {
			throw new RuntimeException(String.format("Ошибка в номере '%s' номерного знака", digits));
		}

		String region = value.getRegion();
		if (region.length() < REGION_LENGTH_MIN) {
			throw new RuntimeException(String.format("Ошибка в регионе '%s' номерного знака", region));
		}
	}

	public void checkContent (String value) {
		if (value == null) {
			throw new RuntimeException("Не найден номерной знак");
		}
		if (value.length() < NUMBER_LENGTH_MIN) {
			throw new RuntimeException(String.format("Ошибка в номерном знаке '%s'", value));
		}
	}

	public String format (CarNumber value) {
		checkNumber(value);

		StringBuilder result = new StringBuilder();

		String series = value.getSeries();
		// First letter:
		result.append(series.substring(0, 1));
		// All digits:
		result.append(value.getDigits());
		// Other letters:
		result.append(series.substring(1, series.length()));
		// Region:
		result.append(' ');
		result.append(value.getRegion());

		return result.toString();
	}

	public CarNumber parse (String content) {
		checkContent(content);

		char[] chars = content.toCharArray();

		int pos = 0;

		StringBuilder seriesBuilder = new StringBuilder();

		// First letter:
		char charFirst = chars[pos];
		if (LETTERS.indexOf(charFirst) < 0) {
			throw new RuntimeException(String.format("Неверная первая буква '%c' в номерном знаке '%s'", charFirst, content));
		}
		seriesBuilder.append(charFirst);

		StringBuilder digitsBuilder = new StringBuilder();

		// All digits:
		for (int i = 1; i < chars.length; i++) {
			char charDigit = chars[i];
			if ((i > DIGITS_LENGTH) || (DIGITS.indexOf(charDigit) < 0)) {
				pos = i;

				break;
			}
			digitsBuilder.append(charDigit);
		}

		if (pos >= chars.length) { // CHECK: always false
			throw new RuntimeException(String.format("Неверная серия в номерном знаке '%s'", content));
		}

		// Other letters:
		for (int i = pos; i < chars.length; i++) {
			char charOther = chars[i];
			if ((i - pos > SERIES_LENGTH - 1) || (LETTERS.indexOf(charOther) < 0)) {
				pos = i;

				break;
			}
			seriesBuilder.append(charOther);
		}

		if (pos >= chars.length) { // CHECK: always false
			throw new RuntimeException(String.format("Неверный регион в номерном знаке '%s'", content));
		}

		// Region:
		char space = chars[pos];
		if (space != ' ') {
			throw new RuntimeException(String.format("Нет разделителя региона в номерном знаке '%s'", content));
		}

		pos++;
		if (pos >= chars.length) {
			throw new RuntimeException(String.format("Отсутствует регион в номерном знаке '%s'", content));
		}

		String region = new String(chars, pos, chars.length - pos);

		CarNumber newNumber = new CarNumber(seriesBuilder.toString(), digitsBuilder.toString(), region);
		checkNumber(newNumber);

		return newNumber;
	}

	private int randomLetterInt (Random random) {
		return random.nextInt(LETTERS.length());
	}

	public char randomLetter (Random random) {
		return LETTERS.charAt(randomLetterInt(random));
	}

	public String randomSeries (Random random) {
		StringBuilder seriesBuilder = new StringBuilder();
		for (int i = 0; i < SERIES_LENGTH; i++) {
			seriesBuilder.append(randomLetter(random));
		}

		return seriesBuilder.toString();
	}

	private int randomDigitsInt (Random random) {
		return DIGITS_MIN + random.nextInt(DIGITS_MAX - DIGITS_MIN + 1);
	}

	public String randomDigits (Random random) {
		return formatDigits(randomDigitsInt(random));
	}

	public CarNumber randomNumber (Random random) {
		String series = handler.randomSeries(random);
		String digits = handler.randomDigits(random);

		return new CarNumber(series, digits, REGION);
	}

	public String nextSeries (String oldSeries) {
		char[] chars = oldSeries.toCharArray();

		boolean found = false;
		StringBuilder seriesBuilder = new StringBuilder();
		for (int i = chars.length - 1; i >= 0; i--) {
			int index = LETTERS.indexOf(chars[i]);
			if (index < 0) {
				throw new RuntimeException(String.format("Неверная буква '%c' в серии '%s' номерного знака", chars[i], oldSeries));
			}

			if (!found) {
				if (index == (LETTERS.length() - 1)) {
					if (i == 0) {
						return null;
					}

					index = 0;

				} else {
					index++;
					found = true;
				}
			}

			seriesBuilder.append(LETTERS.charAt(index));
		}

		String newSeries = seriesBuilder.reverse().toString();
		return (newSeries.length() > SERIES_LENGTH) ? null : newSeries;
	}

	public String nextDigits (String oldDigits) {
		int newDigits = Integer.parseInt(oldDigits, 10) + 1;
		return (newDigits > DIGITS_MAX) ? null : formatDigits(newDigits);
	}

	public CarNumber nextNumber (CarNumber current) {

		String oldSeries = current.getSeries();
		String newSeries;
		String newDigits = nextDigits(current.getDigits());
		if (newDigits == null) {
			newDigits = formatStartDigits();

			newSeries = nextSeries(oldSeries);
			if (newSeries == null) {
				throw new RuntimeException("Превышен лимит номерных знаков внутри региона");
			}
		} else {
            newSeries = oldSeries;
		}

		return new CarNumber(newSeries, newDigits, current.getRegion());
	}
}
