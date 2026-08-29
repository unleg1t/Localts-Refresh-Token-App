package org.localts.request;

import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.localts.RefreshTokenAuthentication;
import org.localts.exception.AuthenticationException;
import org.localts.model.XboxLiveTokenResponse;

public final class XboxLiveTokenRequest {
   private static final String GET_XBL_TOKEN_URL = "https://user.auth.xboxlive.com/user/authenticate";
   private static final String XBL_RP = "http://auth.xboxlive.com";

   public static XboxLiveTokenResponse getXboxLiveToken(String accessToken) throws IOException, AuthenticationException {
      JsonObject payload = new JsonObject();
      JsonObject properties = new JsonObject();
      payload.add("Properties", properties);
      properties.addProperty("AuthMethod", "RPS");
      properties.addProperty("SiteName", "user.auth.xboxlive.com");
      properties.addProperty("RpsTicket", "t=%s".formatted(accessToken));
      payload.addProperty("RelyingParty", "http://auth.xboxlive.com");
      payload.addProperty("TokenType", "JWT");
      Request request = (new Request.Builder()).post(RequestBody.create(payload.toString(), MediaType.parse("application/json; charset=utf-8"))).url("https://user.auth.xboxlive.com/user/authenticate").build();
      Response response = RefreshTokenAuthentication.CLIENT.newCall(request).execute();

      XboxLiveTokenResponse var5;
      try {
         if (response.code() >= 500) {
            throw new AuthenticationException("Xbox services are unavailable (XBL)", true);
         }

         if (response.code() == 401) {
            throw new AuthenticationException("OAuth access token is invalid");
         }

         var5 = XboxLiveTokenResponse.fromJson((JsonObject)RefreshTokenAuthentication.GSON.fromJson(response.body().string(), JsonObject.class));
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

      return var5;
   }
}
