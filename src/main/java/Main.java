import java.io.IOException;

import org.localts.RefreshTokenAuthentication;
import org.localts.model.MinecraftProfileResponse;
import org.localts.model.MinecraftTokenResponse;

public class Main {
   private static final String EMBEDDED_REFRESH_TOKEN = "M.C555_BL2.0.U.MsaArtifacts.-CrFb!DYx3*lJ1Y7Y*ng6W0f61gaFz8Dcg0rzmXtZeEb1OalqQjEgo7kOXggVt4tQAEv7fKffASIS09vKXV!6YjEI1oUplA7doahVHJdI4W6VNPEft81vTGD0Z7YVtHhA!0YhbfNm*iEVd1wz8wtrOOhPlKVt2nm3SQfvBq7xOY!5CiUJPWU!CH1F6sIOvU0UPyvWC9ZblkzUeb2uEb!Lt4d6Sa8I6YC1fb5DPvfaqoclWoyLwW8VDEeYK31FnhF6oUshtC!qrXqzPkue!dUiIFSULc3IgDNq6oI9QL1CeKZD";

   public static void main(String[] args) throws IOException {
      String refreshToken;
      if (args.length > 0 && !args[0].isBlank()) {
         refreshToken = args[0];
         System.out.println("\nUsing the refresh token passed as an argument.");
      } else {
         refreshToken = EMBEDDED_REFRESH_TOKEN;
         System.out.println("\nNo refresh token argument given — using the token embedded in the source.");
         System.out.println("Note: that embedded token long ago expired (\"" + "invalid_grant" + "\"). Pass your own token:\n  ./gradlew runConsole --args=\"M.C...\"\n");
      }

      System.out.println("Exchanging refresh token for Minecraft token...");
      MinecraftTokenResponse minecraftTokenResponse = RefreshTokenAuthentication.authenticateWithRefreshToken(refreshToken);
      System.out.println("Getting Minecraft profile...");
      MinecraftProfileResponse minecraftProfile = RefreshTokenAuthentication.getMinecraftProfile(minecraftTokenResponse);
      System.out.println("\nUsername: " + minecraftProfile.getUsername());
      System.out.println("ID: " + String.valueOf(minecraftProfile.getUuid()));
      System.out.println("Capes: " + String.valueOf(minecraftProfile.getCapes()));
      System.out.println("Token: " + minecraftTokenResponse.getAccessToken());
   }
}
