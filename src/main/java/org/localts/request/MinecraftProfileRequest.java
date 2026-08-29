package org.localts.request;

import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import org.localts.RefreshTokenAuthentication;
import org.localts.exception.AuthenticationException;
import org.localts.model.MinecraftProfileResponse;

public final class MinecraftProfileRequest {
   private static final String API_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";

   public static MinecraftProfileResponse getMinecraftProfile(String accessToken) throws IOException, AuthenticationException {
      Request request = (new Request.Builder()).url("https://api.minecraftservices.com/minecraft/profile").header("Authorization", String.format("Bearer %s", accessToken)).build();
      Response response = RefreshTokenAuthentication.CLIENT.newCall(request).execute();

      MinecraftProfileResponse var3;
      try {
         if (response.code() == 404 || response.code() == 400) {
            throw new AuthenticationException("Profile not found! (The username is most likely unset)");
         }

         if (response.code() >= 500) {
            throw new AuthenticationException("Minecraft services are unavailable", true);
         }

         if (response.code() != 200) {
            throw new AuthenticationException("Invalid response from Minecraft services (code: " + response.code() + ")");
         }

         var3 = MinecraftProfileResponse.fromJson((JsonObject)RefreshTokenAuthentication.GSON.fromJson(response.body().string(), JsonObject.class));
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
