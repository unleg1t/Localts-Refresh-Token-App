package org.localts;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.function.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.localts.exception.AuthenticationException;
import org.localts.model.EntitlementsResponse;
import org.localts.model.MicrosoftTokenResponse;
import org.localts.model.MinecraftProfileResponse;
import org.localts.model.MinecraftTokenResponse;
import org.localts.model.XboxLiveTokenResponse;
import org.localts.request.EntitlementsRequest;
import org.localts.request.MinecraftProfileRequest;
import org.localts.request.MinecraftTokenRequest;
import org.localts.request.RefreshTokenRequest;
import org.localts.request.XboxLiveTokenRequest;
import org.localts.request.XstsTokenRequest;

public class RefreshTokenAuthentication {
   public static final Gson GSON = new Gson();
   public static final OkHttpClient CLIENT = (new OkHttpClient()).newBuilder().followSslRedirects(false).followRedirects(false).addInterceptor((chain) -> {
      Response response = chain.proceed(chain.request());
      if (response.code() == 429) {
         response.close();
         throw new AuthenticationException("You are rate limited, try again in a moment!", true);
      } else {
         return response;
      }
   }).build();

   public static Session login(String refreshToken) throws IOException, AuthenticationException {
      return login(refreshToken, (msg) -> {
      });
   }

   public static Session login(String refreshToken, Consumer<String> log) throws IOException, AuthenticationException {
      MicrosoftTokenResponse microsoft = RefreshTokenRequest.refreshToken(refreshToken);
      log.accept("Refreshed token");
      XboxLiveTokenResponse xbox = XboxLiveTokenRequest.getXboxLiveToken(microsoft.getAccessToken());
      log.accept("Got XBL token");
      String xsts = XstsTokenRequest.getXstsToken(xbox.getToken()).getToken();
      log.accept("Got XSTS token");
      MinecraftTokenResponse minecraft = MinecraftTokenRequest.getMinecraftAccessToken(xsts, xbox.getUserHash());
      log.accept("Got MC token");
      EntitlementsResponse entitlements = EntitlementsRequest.getEntitlements(minecraft.getAccessToken());
      if (!entitlements.checkOwnership()) {
         throw new AuthenticationException("Account doesn't own Minecraft!");
      } else {
         log.accept("Checked ownership");
         MinecraftProfileResponse profile = MinecraftProfileRequest.getMinecraftProfile(minecraft.getAccessToken());
         log.accept("Got profile (" + profile.getUsername() + ")");
         return new Session(microsoft, minecraft, profile);
      }
   }

   public static MinecraftTokenResponse authenticateWithRefreshToken(String refreshToken) throws IOException, AuthenticationException {
      XboxLiveTokenResponse xboxLiveResponse = XboxLiveTokenRequest.getXboxLiveToken(RefreshTokenRequest.refreshToken(refreshToken).getAccessToken());
      return MinecraftTokenRequest.getMinecraftAccessToken(XstsTokenRequest.getXstsToken(xboxLiveResponse.getToken()).getToken(), xboxLiveResponse.getUserHash());
   }

   public static MinecraftProfileResponse getMinecraftProfile(MinecraftTokenResponse minecraftTokenResponse) throws IOException, AuthenticationException {
      EntitlementsResponse entitlements = EntitlementsRequest.getEntitlements(minecraftTokenResponse.getAccessToken());
      if (!entitlements.checkOwnership()) {
         throw new AuthenticationException("Account doesn't own Minecraft!");
      } else {
         return MinecraftProfileRequest.getMinecraftProfile(minecraftTokenResponse.getAccessToken());
      }
   }

   public static record Session(MicrosoftTokenResponse microsoft, MinecraftTokenResponse minecraft, MinecraftProfileResponse profile) {
   }
}
