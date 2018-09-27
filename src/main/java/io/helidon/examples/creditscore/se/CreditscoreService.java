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

import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import io.helidon.common.http.Http;
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
     * A service registers itself by updating the routine rules.
     * @param rules the routing rules.
     */
    @Override
    public final void update(final Routing.Rules rules) {
        rules
            .post("/", this::getCreditscore)
            .get("/testing", this::getTestMessage);
    }

    /**
     * Return a test message.
     * @param request the server request
     * @param response the server response
     */
    private void getTestMessage(final ServerRequest request,
                                   final ServerResponse response) {
        String msg = String.format("Available");

        JsonObject returnObject = Json.createObjectBuilder()
                .add("message", msg)
                .build();
        response.send(returnObject);
    }

    /**
     * Return a creditscore using the data that was provided.
     * @param request the server request
     * @param response the server response
     */
    private void getCreditscore(final ServerRequest request,
                            final ServerResponse response) {
    	
    	Consumer<JsonObject> json = null;
        request.content()
        .as(JsonObject.class)
        .thenAccept(json)
        .exceptionally(throwable -> {
            response.status(Http.Status.INTERNAL_SERVER_ERROR_500);
            response.send(throwable.getClass().getName() + ": " + throwable.getMessage());
            return null;
        });
        
        logger.info("Request: " + json);

        JsonObject returnObject = Json.createObjectBuilder()
                .add("message", "test")
                .build();
        response.send(returnObject);
    }


}
