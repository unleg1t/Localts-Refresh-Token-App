package org.localts.request;

import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.localts.RefreshTokenAuthentication;
import org.localts.exception.AuthenticationException;
import org.localts.model.MinecraftTokenResponse;

public final class MinecraftTokenRequest {
   private static final String LOGIN_WITH_XBOX_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";

   public static MinecraftTokenResponse getMinecraftAccessToken(String xstsToken, String userHash) throws IOException, AuthenticationException {
      JsonObject payload = new JsonObject();
      payload.addProperty("identityToken", "XBL3.0 x=%s;%s".formatted(userHash, xstsToken));
      Request request = (new Request.Builder()).post(RequestBody.create(payload.toString(), MediaType.parse("application/json; charset=utf-8"))).url("https://api.minecraftservices.com/authentication/login_with_xbox").build();
      Response response = RefreshTokenAuthentication.CLIENT.newCall(request).execute();

      MinecraftTokenResponse var6;
      try {
         if (response.code() >= 500) {
            throw new AuthenticationException("Xbox services are unavailable (login_with_xbox)", true);
         }

         MinecraftTokenResponse minecraftResponse = MinecraftTokenResponse.fromJson((JsonObject)RefreshTokenAuthentication.GSON.fromJson(response.body().string(), JsonObject.class));
         if (!minecraftResponse.isSuccessful()) {
            throw new AuthenticationException("Received an error while trying to get Minecraft access token: " + minecraftResponse.getError());
         }

         var6 = minecraftResponse;
      } catch (Throwable var8) {
         if (response != null) {
            try {
               response.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (response != null) {
         response.close();
      }

      return var6;
   }
}
