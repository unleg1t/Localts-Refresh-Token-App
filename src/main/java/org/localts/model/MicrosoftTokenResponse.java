package org.localts.model;

import com.google.gson.JsonObject;

import org.localts.exception.AuthenticationException;

public class MicrosoftTokenResponse {
   private String accessToken;
   private String refreshToken;
   private Long expiresIn;
   private String error;
   private String description;

   public static MicrosoftTokenResponse fromJson(JsonObject json) {
      MicrosoftTokenResponse response = new MicrosoftTokenResponse();
      if (json.has("error")) {
         response.setError(json.get("error").getAsString());
         if (json.has("description")) {
            response.setDescription(json.get("description").getAsString());
         }
      } else {
         if (!json.has("access_token") || !json.has("refresh_token") || !json.has("expires_in")) {
            throw new AuthenticationException("Received invalid JSON object while trying to refresh oauth tokens");
         }

         response.setAccessToken(json.get("access_token").getAsString());
         response.setRefreshToken(json.get("refresh_token").getAsString());
         response.setExpiresIn(json.get("expires_in").getAsLong());
      }

      return response;
   }

   public String getError() {
      return this.description != null ? "%s (%s)".formatted(this.error, this.description) : this.error;
   }

   public boolean isSuccessful() {
      return this.error == null;
   }

   
   public String getAccessToken() {
      return this.accessToken;
   }

   
   public String getRefreshToken() {
      return this.refreshToken;
   }

   
   public Long getExpiresIn() {
      return this.expiresIn;
   }

   
   public String getDescription() {
      return this.description;
   }

   
   private void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
   }

   
   private void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
   }

   
   private void setExpiresIn(Long expiresIn) {
      this.expiresIn = expiresIn;
   }

   
   private void setError(String error) {
      this.error = error;
   }

   
   private void setDescription(String description) {
      this.description = description;
   }

   
   private MicrosoftTokenResponse() {
   }
}
