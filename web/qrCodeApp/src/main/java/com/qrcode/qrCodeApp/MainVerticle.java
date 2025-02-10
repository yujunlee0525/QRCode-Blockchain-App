package com.qrcode.qrCodeApp;

import com.qrcode.qrCodeApp.Services.QRCode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import org.json.JSONObject;
import io.vertx.ext.web.Router;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;

public class MainVerticle extends AbstractVerticle {
  private static final String TEAM_NAME = System.getenv("TEAM_NAME");
  private static final String TEAM_AWS_ACCOUNT_ID = System.getenv("AWS_ACCOUNT_ID");
  private static final String AUTH_SERVICE_HOST = "auth-service.default.svc.cluster.local";
  private static final int AUTH_SERVICE_PORT = 9000;

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    router.get("/qrcode").handler(this::handleQRCodeRequest);
    router.get("/").handler(this::handleRootRequest);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8888)
      .onSuccess(server -> {
        System.out.println("HTTP server started on port " + server.actualPort());
        startPromise.complete();
      })
      .onFailure(startPromise::fail);
  }

  private void handleQRCodeRequest(RoutingContext context) {
    String type = context.request().getParam("type");
    String data = context.request().getParam("data");
    String timestamp = context.request().getParam("timestamp");
    
    if (type == null || data == null) {
      sendResponse(context, 400, "Missing required parameters");
      return;
    }

    try {
      String decodedData = URLDecoder.decode(data, StandardCharsets.UTF_8);
      String response;
      // TODO: What should we do if request is invalid?
      if ("encode".equals(type)) {
        response = QRCode.handleEncodeRequest(decodedData);
        sendResponse(context, 200, response);
      } else if ("decode".equals(type)) {
        if (timestamp == null) {
          sendResponse(context, 400, "Missing timestamp parameter for decode request");
          return;
        }
        Long timestampLong = Long.parseLong(timestamp);
        response = QRCode.handleDecodeRequest(decodedData, timestamp);
        WebClient client = WebClient.create(vertx);
        
        client.post(AUTH_SERVICE_PORT, AUTH_SERVICE_HOST, "/rest_auth")
          .putHeader("Content-Type", "application/json")
          .sendJsonObject(new JsonObject()
            .put("team_name", TEAM_NAME)
            .put("timestamp", timestampLong)
            .put("decoded_qrcode", response))
          .onSuccess(res->{
            JsonObject responseBody = res.bodyAsJsonObject();;
            String message = responseBody.getString("message");
            String token = responseBody.getString("token");
            sendResponse(context, 200, TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\n" + token);
          })
          .onComplete(ar -> {
            client.close();
          });
      } else {
        sendResponse(context, 400, "Invalid type parameter");
        return;
      }
    } catch (IllegalArgumentException e) {
      sendResponse(context, 400, "Invalid QR code data: " + e.getMessage());
    } catch (IOException | NoSuchAlgorithmException e) {
      sendResponse(context, 500, "Error processing request: " + e.getMessage());
    } catch (Exception e) {
      System.out.println(type);
      System.out.println(data);
      sendResponse(context, 500, "Unexpected error: " + e.getMessage());
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
