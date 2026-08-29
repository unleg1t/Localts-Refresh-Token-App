package org.localts.request;

import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.localts.RefreshTokenAuthentication;
import org.localts.exception.AuthenticationException;
import org.localts.model.MicrosoftTokenResponse;

public final class RefreshTokenRequest {
   private static final String REFRESH_TOKENS_URL = "https://login.live.com/oauth20_token.srf";
   private static final String CLIENT_ID = "00000000402b5328";
   private static final String REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf";
   private static final String SCOPE = "service::user.auth.xboxlive.com::MBI_SSL";

   public static MicrosoftTokenResponse refreshToken(String refreshToken) throws IOException, AuthenticationException {
      FormBody payload = (new FormBody.Builder()).add("client_id", "00000000402b5328").add("grant_type", "refresh_token").add("redirect_uri", "https://login.live.com/oauth20_desktop.srf").add("refresh_token", refreshToken).add("scope", "service::user.auth.xboxlive.com::MBI_SSL").build();
      Request request = (new Request.Builder()).post(payload).url("https://login.live.com/oauth20_token.srf").build();
      Response response = RefreshTokenAuthentication.CLIENT.newCall(request).execute();

      MicrosoftTokenResponse var6;
      try {
         if (response.code() >= 500) {
            throw new AuthenticationException("Microsoft services are unavailable", true);
         }

         JsonObject json = (JsonObject)RefreshTokenAuthentication.GSON.fromJson(response.body().string(), JsonObject.class);
         if (json == null) {
            throw new AuthenticationException("Received no response when trying to refresh oauth tokens (code %s)".formatted(response.code()));
         }

         MicrosoftTokenResponse microsoftResponse = MicrosoftTokenResponse.fromJson(json);
         if (!microsoftResponse.isSuccessful()) {
            throw new AuthenticationException("Received an error while refreshing oauth tokens: " + microsoftResponse.getError());
         }

         var6 = microsoftResponse;
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
