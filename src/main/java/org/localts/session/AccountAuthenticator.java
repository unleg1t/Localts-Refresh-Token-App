package org.localts.session;

import java.util.UUID;
import java.util.function.Consumer;
import net.raphimc.minecraftauth.step.java.StepMCProfile;
import net.raphimc.minecraftauth.step.java.StepMCToken;
import net.raphimc.minecraftauth.step.java.StepPlayerCertificates;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import net.raphimc.minecraftauth.step.xbl.StepXblXstsToken;
import org.localts.RefreshTokenAuthentication;

public final class AccountAuthenticator {
   private AccountAuthenticator() {
   }

   public static AuthResult authenticate(String refreshToken) throws Exception {
      return authenticate(refreshToken, (msg) -> {
      });
   }

   public static AuthResult authenticate(String refreshToken, Consumer<String> log) throws Exception {
      RefreshTokenAuthentication.Session s = RefreshTokenAuthentication.login(refreshToken, log);
      long now = System.currentTimeMillis();
      long mcExpireMs = s.minecraft().getExpiresIn() != null ? now + s.minecraft().getExpiresIn() * 1000L : now;
      StepFullJavaSession.FullJavaSession session = minimalSession(s.profile().getUuid(), s.profile().getUsername(), s.profile().getSkinUrl(), s.minecraft().getAccessToken(), mcExpireMs);
      return new AuthResult(session, s.microsoft().getRefreshToken(), 0L);
   }

   public static StepFullJavaSession.FullJavaSession minimalSession(UUID id, String name, String skinUrl, String mcAccessToken, long mcExpireMs) {
      StepMCToken.MCToken mcToken = new StepMCToken.MCToken(mcAccessToken, "Bearer", mcExpireMs, (StepXblXstsToken.XblXsts)null);
      StepMCProfile.MCProfile profile = new StepMCProfile.MCProfile(id, name, skinUrl, mcToken);
      return new StepFullJavaSession.FullJavaSession(profile, (StepPlayerCertificates.PlayerCertificates)null);
   }

   public static record AuthResult(StepFullJavaSession.FullJavaSession session, String refreshToken, long refreshExpireMs) {
   }
}
