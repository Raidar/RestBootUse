package org.raidar.app.data;

public class WelcomeResponse {

	private final String message;

	public WelcomeResponse (String message) {
		super();

		this.message = message;
	}

	public String getMessage () {
		return message;
	}

}
