package org.localts.proxy;

import com.google.gson.JsonObject;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

final class MojangAuth {
   private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
   private static final HttpClient WEB = HttpClient.newHttpClient();

   private MojangAuth() {
   }

   static String serverIdHash(String serverId, byte[] sharedSecret, byte[] publicKey) throws Exception {
      MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
      sha1.update(serverId.getBytes(StandardCharsets.ISO_8859_1));
      sha1.update(sharedSecret);
      sha1.update(publicKey);
      return (new BigInteger(sha1.digest())).toString(16);
   }

   static void joinServer(String accessToken, String dashlessUuid, String serverHash) throws Exception {
      JsonObject body = new JsonObject();
      body.addProperty("accessToken", accessToken);
      body.addProperty("selectedProfile", dashlessUuid);
      body.addProperty("serverId", serverHash);
      HttpResponse<String> resp = WEB.send(HttpRequest.newBuilder(URI.create("https://sessionserver.mojang.com/session/minecraft/join")).header("Content-Type", "application/json").POST(BodyPublishers.ofString(body.toString())).build(), BodyHandlers.ofString());
      if (resp.statusCode() != 204) {
         String detail = resp.body() != null && !((String)resp.body()).isBlank() ? " — " + (String)resp.body() : "";
         int var10002 = resp.statusCode();
         throw new IllegalStateException("Mojang join failed (HTTP " + var10002 + ")" + detail);
      }
   }
}
