package com.blockchain.blockChainApp;

import com.blockchain.blockChainApp.Services.BlockChain;
import com.blockchain.blockChainApp.Services.Validation;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.json.JSONObject;

public class MainVerticle extends AbstractVerticle {
    private static final String TEAM_NAME = System.getenv("TEAM_NAME");
    private static final String TEAM_AWS_ACCOUNT_ID = System.getenv("AWS_ACCOUNT_ID");

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        router.get("/blockchain").handler(this::handleBlockchainRequest);
        router.get("/").handler(this::handleRootRequest);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8888)
                .onSuccess(server -> {
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

    private void handleBlockchainRequest(RoutingContext context) {
        String encodedRequest = context.request().getParam("cc");
        if (encodedRequest == null || encodedRequest.isEmpty()) {
            String invalidResponse = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\nINVALID";
            sendResponse(context, 200, invalidResponse);
            return;
        }

        try {
//            JSONObject requestObject = BlockChain.getJsonObjectFromRequest(encodedRequest);
//            if (requestObject == null) {
//                String invalidResponse = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\nINVALID";
//                sendResponse(context, 400, invalidResponse);
//                return;
//            }
//            Validation validation = new Validation();
//
//            if (!validation.validateRequest(requestObject)) {
//                String invalidResponse = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\nINVALID";
//                sendResponse(context, 400, invalidResponse);
//                return;
//            }
            BlockChain blockChain = new BlockChain();
            // log
            String response = blockChain.getResponseFromRequest(encodedRequest);
            if (response == null) {
                String invalidResponse = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\nINVALID";
                sendResponse(context, 200, invalidResponse);
                return;
            }
            response = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\n" + response;
            sendResponse(context, 200, response);
        } catch (Exception e) {
          String invalidResponse = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID +
            "\nINVALID";
          sendResponse(context, 200, invalidResponse);
        }
    }

    private void handleRootRequest(RoutingContext context) {
        sendResponse(context, 200, TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID);
    }

    private void sendResponse(RoutingContext context, int statusCode, String response) {
        context.response()
                .setStatusCode(statusCode)
                .putHeader("Content-Type", "text/plain")
                .putHeader("Content-Length", String.valueOf(response.length()))
                .end(response);
    }
}
