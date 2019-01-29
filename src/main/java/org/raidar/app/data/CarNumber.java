package org.raidar.app.data;

import org.raidar.app.rest.CarNumberHandler;

public class CarNumber {

	private static final CarNumberHandler handler = new CarNumberHandler();

	private String series;
	private String digits;
	private String region;
	private String content;

	public CarNumber (String series, String digits, String region) {
		if (series == null) {
			throw new RuntimeException("Не найдена серия номерного знака");
		}

		if (digits == null) {
			throw new RuntimeException("Не найден номер номерного знака");
		}

		if (region == null) {
			throw new RuntimeException("Не найден регион номерного знака");
		}

		this.series = series;
		this.digits = digits;
		this.region = region;
	}

	public String getSeries () {
		return series;
	}

	public String getDigits () {
		return digits;
	}

	public String getRegion () {
		return region;
	}

	public String getContent () {
		if ((content == null) || content.isEmpty()) {
			makeContent();
		}

		return content;
	}

	protected void makeContent () {
		content = handler.format(this);
	}

	public String toString () {
		return getContent();
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) {
			return true;
		}

		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}

		CarNumber carNumber = (CarNumber)o;
		return series.equals(carNumber.series) &&
			   digits.equals(carNumber.digits) &&
			   region.equals(carNumber.region);
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = series.hashCode();
		result = prime * result + digits.hashCode();
		result = prime * result + region.hashCode();
		return result;
	}
}
