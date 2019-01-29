package org.raidar.app.rest;

import org.raidar.app.data.CarNumber;

import java.util.HashMap;
import java.util.Random;

public class CarNumberManager {

	private static final CarNumberHandler handler = new CarNumberHandler();

	// MAYBE: Use StringBuilder for numbers with only one fixed format.
	private HashMap<String, CarNumber> carNumbers = new HashMap<>();

	private CarNumber lastNumber = CarNumberHandler.NULL_NUMBER;

	public CarNumberManager () {
		// WARN: Static class or Simple class?
	}

	private CarNumber gainNumber (CarNumber carNumber) {
		lastNumber = carNumber; // WARN: Use copy constructor?
		carNumbers.put(carNumber.getContent(), carNumber);

		return carNumber;
	}

	private boolean existsNumber (CarNumber carNumber) {
		return carNumbers.containsKey(carNumber.getContent()) &&
			   carNumbers.get(carNumber.getContent()).equals(carNumber);
	}

	private String randomSeries (Random random) {
		StringBuilder series = new StringBuilder();
		for (int i = 0; i < CarNumberHandler.SERIES_LENGTH; i++) {
			series.append(handler.randomLetter(random));
		}

		return series.toString();
	}

	public CarNumber randomNumber () {
		CarNumber newNumber;

		int i = 0;
		Random random = new Random();
		do {
			i++;

			String series = randomSeries(random);
			String digits = handler.randomDigits(random);
			//newNumber = new CarNumber("ABC", "123", REGION);
			newNumber = new CarNumber(series, digits, CarNumberHandler.REGION);

		} while ((i < CarNumberHandler.RANDOM_LIMIT) && existsNumber(newNumber));

		if (i >= CarNumberHandler.RANDOM_LIMIT) {
			throw new RuntimeException("Превышен лимит генерации номерных знаков");
		}

		return gainNumber(newNumber);
	}

	public CarNumber nextNumber () {
		String newSeries;
		String newDigits = handler.nextDigits(lastNumber.getDigits());
		if (newDigits == null) {
			newDigits = handler.formatStartDigits();

			newSeries = handler.nextSeries(lastNumber.getSeries());
			if (newSeries == null) {
				throw new RuntimeException("Превышен лимит номерных знаков внутри региона");
			}

		} else {
			newSeries = lastNumber.getSeries();
		}

		CarNumber newNumber = new CarNumber(newSeries, newDigits, lastNumber.getRegion());
		return gainNumber(newNumber);
	}

	public CarNumber nextNumber (String carNumber) {
		if ((carNumber != null) && !carNumber.isEmpty()) {
			lastNumber = handler.parse(carNumber);
		}

		return nextNumber();
	}

	public CarNumber parseNumber (String carNumber) {
		handler.checkContent(carNumber);
		return handler.parse(carNumber);
	}
}
