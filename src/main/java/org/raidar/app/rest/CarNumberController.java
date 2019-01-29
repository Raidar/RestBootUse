package org.raidar.app.rest;

import org.springframework.web.bind.annotation.*;

import org.raidar.app.data.*;

@RestController
@RequestMapping("/number")
public class CarNumberController {

	private static final String WELCOME = "Welcome!";

	private static final CarNumberManager numberManager = new CarNumberManager();

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcome () {
        return WELCOME;
    }

	@RequestMapping(value = "/welcome/json", method = RequestMethod.GET)
	public WelcomeResponse welcomeAsJson () {
		return new WelcomeResponse(WELCOME);
	}

    @RequestMapping(value = "/random", method = RequestMethod.GET)
    public String randomNumber () {
        return numberManager.randomNumber().toString();
    }

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	public String nextNumber () {
		return numberManager.nextNumber().toString();
	}

	@RequestMapping(value = "/next/{value}", method = RequestMethod.GET)
	public String nextNumberFor (@PathVariable("value") String carNumber) {
		return numberManager.nextNumber(carNumber).toString();
	}

	@RequestMapping(value = "/parse/{value}", method = RequestMethod.GET)
	public String parseNumber (@PathVariable("value") String carNumber) {
		return numberManager.parseNumber(carNumber).toString();
	}
}
