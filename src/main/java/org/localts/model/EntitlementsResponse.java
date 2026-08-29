package org.localts.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.localts.exception.AuthenticationException;

public class EntitlementsResponse {
   private static final Set<String> SOURCES = Set.of("GAMEPASS", "PURCHASE", "MC_PURCHASE");
   private Map<String, String> entitlements;

   public static EntitlementsResponse fromJson(JsonObject json) {
      EntitlementsResponse entitlementsResponse = new EntitlementsResponse();
      if (!json.has("items")) {
         throw new AuthenticationException("Couldn't receive entitlements");
      } else {
         entitlementsResponse.setEntitlements((Map)json.getAsJsonArray("items").asList().stream().map(JsonElement::getAsJsonObject).collect(Collectors.toMap((obj) -> obj.get("name").getAsString(), (obj) -> obj.get("source").getAsString(), (existing, replacement) -> replacement)));
         return entitlementsResponse;
      }
   }

   public boolean checkOwnership() {
      return this.entitlements.entrySet().stream().anyMatch((entry) -> ((String)entry.getKey()).contains("minecraft") && SOURCES.contains(entry.getValue()));
   }

   
   public Map<String, String> getEntitlements() {
      return this.entitlements;
   }

   
   private void setEntitlements(Map<String, String> entitlements) {
      this.entitlements = entitlements;
   }

   
   private EntitlementsResponse() {
   }
}
