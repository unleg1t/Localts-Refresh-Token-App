package org.localts.request;

import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import org.localts.RefreshTokenAuthentication;
import org.localts.exception.AuthenticationException;
import org.localts.model.EntitlementsResponse;

public final class EntitlementsRequest {
   private static final String MINECRAFTSERVICES_PRODUCTS_URL = "https://api.minecraftservices.com/entitlements/license?requestId=auth";

   public static EntitlementsResponse getEntitlements(String accessToken) throws IOException, AuthenticationException {
      Request request = (new Request.Builder()).url("https://api.minecraftservices.com/entitlements/license?requestId=auth").header("Authorization", String.format("Bearer %s", accessToken)).build();
      Response response = RefreshTokenAuthentication.CLIENT.newCall(request).execute();

      EntitlementsResponse var3;
      try {
         if (response.code() >= 500) {
            throw new AuthenticationException("Minecraft services are unavailable", true);
         }

         if (response.code() != 200) {
            throw new AuthenticationException("Received code " + response.code() + " when trying to check game ownership");
         }

         var3 = EntitlementsResponse.fromJson((JsonObject)RefreshTokenAuthentication.GSON.fromJson(response.body().string(), JsonObject.class));
      } catch (Throwable var6) {
         if (response != null) {
            try {
               response.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (response != null) {
         response.close();
      }

      return var3;
   }
}
