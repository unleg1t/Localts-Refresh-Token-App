package org.localts.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.localts.RefreshTokenAuthentication;
import org.localts.exception.AuthenticationException;
import org.localts.model.XstsTokenResponse;

public final class XstsTokenRequest {
   private static final String GET_XSTS_TOKEN_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";

   public static XstsTokenResponse getXstsToken(String accessToken) throws IOException, AuthenticationException {
      JsonObject payload = new JsonObject();
      JsonObject properties = new JsonObject();
      JsonArray userTokens = new JsonArray();
      properties.addProperty("SandboxId", "RETAIL");
      userTokens.add(accessToken);
      properties.add("UserTokens", userTokens);
      payload.add("Properties", properties);
      payload.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
      payload.addProperty("TokenType", "JWT");
      Request request = (new Request.Builder()).post(RequestBody.create(payload.toString(), MediaType.parse("application/json; charset=utf-8"))).url("https://xsts.auth.xboxlive.com/xsts/authorize").build();
      Response response = RefreshTokenAuthentication.CLIENT.newCall(request).execute();

      XstsTokenResponse var7;
      try {
         if (response.code() >= 500) {
            throw new AuthenticationException("Xbox services are unavailable (XSTS)", true);
         }

         XstsTokenResponse xstsResponse = XstsTokenResponse.fromJson((JsonObject)RefreshTokenAuthentication.GSON.fromJson(response.body().string(), JsonObject.class));
         if (!xstsResponse.isSuccessful()) {
            throw new AuthenticationException("Received an error while getting XSTS Token: " + xstsResponse.getError());
         }

         var7 = xstsResponse;
      } catch (Throwable var9) {
         if (response != null) {
            try {
               response.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (response != null) {
         response.close();
      }

      return var7;
   }
}
