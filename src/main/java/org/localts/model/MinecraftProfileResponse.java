package org.localts.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class MinecraftProfileResponse {
   private String username;
   private UUID uuid;
   private String skinUrl;
   private Set<String> capes;

   public static MinecraftProfileResponse fromJson(JsonObject json) {
      MinecraftProfileResponse profile = new MinecraftProfileResponse();
      profile.setUsername(json.get("name").getAsString());
      profile.setUuid(UUID.fromString(dashedUUID(json.get("id").getAsString())));
      if (json.has("skins") && json.get("skins").isJsonArray()) {
         json.get("skins").getAsJsonArray().asList().stream().map(JsonElement::getAsJsonObject).filter((s) -> s.has("url")).reduce((first, second) -> "ACTIVE".equalsIgnoreCase(second.has("state") ? second.get("state").getAsString() : "") ? second : first).ifPresent((s) -> profile.setSkinUrl(s.get("url").getAsString()));
      }

      if (json.has("capes") && json.get("capes").isJsonArray()) {
         profile.setCapes((Set)json.get("capes").getAsJsonArray().asList().stream().map((jsonElement) -> jsonElement.getAsJsonObject().get("alias").getAsString()).collect(Collectors.toSet()));
      }

      return profile;
   }

   private static String dashedUUID(String input) {
      return input.replaceAll("(.{8})(.{4})(.{4})(.{4})(.{12})", "$1-$2-$3-$4-$5");
   }

   
   public String getUsername() {
      return this.username;
   }

   
   public UUID getUuid() {
      return this.uuid;
   }

   
   public String getSkinUrl() {
      return this.skinUrl;
   }

   
   public Set<String> getCapes() {
      return this.capes;
   }

   
   private void setUsername(String username) {
      this.username = username;
   }

   
   private void setUuid(UUID uuid) {
      this.uuid = uuid;
   }

   
   private void setSkinUrl(String skinUrl) {
      this.skinUrl = skinUrl;
   }

   
   private void setCapes(Set<String> capes) {
      this.capes = capes;
   }

   
   private MinecraftProfileResponse() {
   }
}
