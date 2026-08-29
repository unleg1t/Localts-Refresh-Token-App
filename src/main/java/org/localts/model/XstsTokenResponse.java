package org.localts.model;

import com.google.gson.JsonObject;
import java.util.Map;

import org.localts.exception.AuthenticationException;

public class XstsTokenResponse {
   private static final Map<Long, String> ERRORS = Map.of(2148916227L, "The account is banned from Xbox", 2148916233L, "The account doesn't have an Xbox account (never signed in)", 2148916235L, "The account is from a country where Xbox Live is not available/banned", 2148916236L, "The account needs adult verification on Xbox page. (South Korea)", 2148916237L, "The account needs adult verification on Xbox page. (South Korea)", 2148916238L, "The account is a child (under 18) and cannot proceed unless the account is added to a Family by an adult", 2148916262L, "Unknown error");
   private String token;
   private Long errorCode;
   private String error;

   public static XstsTokenResponse fromJson(JsonObject json) {
      XstsTokenResponse response = new XstsTokenResponse();
      if (json.has("XErr")) {
         response.setErrorCode(json.get("XErr").getAsLong());
         response.setError((String)ERRORS.getOrDefault(response.getErrorCode(), "Unknown error"));
      } else {
         if (!json.has("Token")) {
            throw new AuthenticationException("XSTS token not found");
         }

         response.setToken(json.get("Token").getAsString());
      }

      return response;
   }

   public boolean isSuccessful() {
      return this.error == null;
   }

   
   public String getToken() {
      return this.token;
   }

   
   public Long getErrorCode() {
      return this.errorCode;
   }

   
   public String getError() {
      return this.error;
   }

   
   private void setToken(String token) {
      this.token = token;
   }

   
   private void setErrorCode(Long errorCode) {
      this.errorCode = errorCode;
   }

   
   private void setError(String error) {
      this.error = error;
   }

   
   private XstsTokenResponse() {
   }
}
