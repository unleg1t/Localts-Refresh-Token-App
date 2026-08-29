package org.localts.model;

import com.google.gson.JsonObject;

import org.localts.exception.AuthenticationException;

public class MinecraftTokenResponse {
   private String accessToken;
   private Long expiresIn;
   private String error;

   public static MinecraftTokenResponse fromJson(JsonObject json) {
      MinecraftTokenResponse response = new MinecraftTokenResponse();
      if (json.has("path")) {
         if (json.has("error")) {
            response.setError(json.get("error").getAsString());
         } else if (json.has("details") && json.get("details").getAsJsonObject().has("reason")) {
            response.setError(json.get("details").getAsJsonObject().get("reason").getAsString());
         } else {
            response.setError("You're being rate limited, try again in a moment!");
         }
      } else {
         if (!json.has("access_token") || !json.has("expires_in")) {
            throw new AuthenticationException("No Minecraft access token found!");
         }

         response.setAccessToken(json.get("access_token").getAsString());
         response.setExpiresIn(json.get("expires_in").getAsLong());
      }

      return response;
   }

   public boolean isSuccessful() {
      return this.error == null;
   }

   
   public String getAccessToken() {
      return this.accessToken;
   }

   
   public Long getExpiresIn() {
      return this.expiresIn;
   }

   
   public String getError() {
      return this.error;
   }

   
   private void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
   }

   
   private void setExpiresIn(Long expiresIn) {
      this.expiresIn = expiresIn;
   }

   
   private void setError(String error) {
      this.error = error;
   }

   
   private MinecraftTokenResponse() {
   }
}
