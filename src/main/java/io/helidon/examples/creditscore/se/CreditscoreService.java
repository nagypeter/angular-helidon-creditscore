/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.examples.creditscore.se;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;

import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

/**
 *
 */

public class CreditscoreService implements Service {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
    /**
     * This gets config from application.yaml on classpath
     * and uses "app" section.
     */
    private static final Config CONFIG = Config.create().get("app");

    /**
     * The config value for the key {@code greeting}.
     */
    private static String test = CONFIG.get("test").asString("Ciao");
	
	private static final int SCORE_MAX = 800;
	private static final int SCORE_MIN = 550;

	/**
	 * A service registers itself by updating the routine rules.
	 * 
	 * @param rules the routing rules.
	 */
	@Override
	public final void update(final Routing.Rules rules) {
		rules
			.get("/healthcheck", this::getTestMessage)
			.post("/", this::getCreditscore);
	}

    /**
     * Return a test greeting message.
     * @param request the server request
     * @param response the server response
     */
    private void getTestMessage(final ServerRequest request,
                                   final ServerResponse response) {
        String msg = String.format("%s %s", "Service", test);

        JsonObject returnObject = Json.createObjectBuilder()
                .add("message", msg)
                .build();
        response.send(returnObject);
    }	

	/**
     * Return a customer data including creditscore value, using the data that was provided.
     * @param request the server request
     * @param response the server response
     */
	private void getCreditscore(final ServerRequest request,
            final ServerResponse response) {

		request.content()
		.as(JsonObject.class)
		.thenAccept(json -> {
		    logger.log(Level.INFO, "Request: {0}", json);
		    response.send(
		            Json.createObjectBuilder(json)
		                    .add("score", getCreditscore(json))
		                    .build()
		    );
		});
	}
	
	/**
	 * calculate creditscore based on customer's properties
	 * @param customer
	 * @return
	 */
	private int getCreditscore(JsonObject customer) {
		
		int score = Math.abs(customer.getString("firstname").hashCode() + customer.getString("lastname").hashCode()
				+ customer.getString("dateofbirth").hashCode() + customer.getString("ssn").hashCode());

		logger.info(customer.toString() + " hashcode: " + score);

		score = score % SCORE_MAX;

		while (score < SCORE_MIN) {
			score = score + 100;
		}

		return score;
	}

}
