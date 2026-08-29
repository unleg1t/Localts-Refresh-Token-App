package org.localts.model;

import com.google.gson.JsonObject;

import org.localts.exception.AuthenticationException;

public class XboxLiveTokenResponse {
   private String token;
   private String userHash;

   public static XboxLiveTokenResponse fromJson(JsonObject json) {
      XboxLiveTokenResponse xboxLiveResponse = new XboxLiveTokenResponse();
      if (json.has("Token") && json.has("DisplayClaims")) {
         xboxLiveResponse.setToken(json.get("Token").getAsString());
         xboxLiveResponse.setUserHash(json.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString());
         return xboxLiveResponse;
      } else {
         throw new AuthenticationException("Missing Token or DisplayClaims when trying to get Xbox live token");
      }
   }

   
   public String getToken() {
      return this.token;
   }

   
   public String getUserHash() {
      return this.userHash;
   }

   
   private void setToken(String token) {
      this.token = token;
   }

   
   private void setUserHash(String userHash) {
      this.userHash = userHash;
   }

   
   private XboxLiveTokenResponse() {
   }
}
