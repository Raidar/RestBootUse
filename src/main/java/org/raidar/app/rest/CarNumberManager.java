package org.raidar.app.rest;

import org.raidar.app.data.CarNumber;

import java.util.HashMap;
import java.util.Random;

public class CarNumberManager {

	private static final CarNumberContext handler = new CarNumberContext();

	// MAYBE: Use StringBuilder for numbers with only one fixed format.
	private HashMap<String, CarNumber> carNumbers = new HashMap<>();

	private CarNumber lastNumber = CarNumberContext.NULL_NUMBER;

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

	public synchronized CarNumber randomNumber () {
		CarNumber newNumber;

		int i = 0;
		Random random = new Random();
		do {
			i++;
			newNumber = handler.randomNumber();

		} while ((i < CarNumberContext.RANDOM_LIMIT) && existsNumber(newNumber));

		if (i >= CarNumberContext.RANDOM_LIMIT) {
			throw new RuntimeException("Превышен лимит генерации номерных знаков");
		}

		return gainNumber(newNumber);
	}

	public synchronized CarNumber nextNumber () {
		return gainNumber(handler.nextNumber(lastNumber));
	}

	public synchronized CarNumber nextNumber (String carNumber) {
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
