package org.localts.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.raphimc.minecraftauth.step.java.StepMCProfile;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;

public final class MinecraftProxy {
   public static final int PORT = 25565;
   public static final int AUTO_VERSION = -1;
   private static final int CB_DISCONNECT = 0;
   private static final int CB_ENCRYPTION_REQUEST = 1;
   private static final int CB_LOGIN_SUCCESS = 2;
   private static final int CB_SET_COMPRESSION = 3;
   private static final int CB_LOGIN_PLUGIN_REQUEST = 4;
   private final SecureRandom random = new SecureRandom();
   private final Object authLock = new Object();
   private volatile StepFullJavaSession.FullJavaSession active;
   private volatile Supplier<StepFullJavaSession.FullJavaSession> refresher;
   private volatile String targetHost;
   private volatile int targetPort = 25565;
   private volatile int forcedProtocol = -1;
   private volatile ServerSocket serverSocket;
   private volatile Consumer<String> logger;

   public void setLogger(Consumer<String> logger) {
      this.logger = logger;
   }

   private void log(String msg) {
      Consumer<String> l = this.logger;
      if (l != null) {
         l.accept(msg);
      }

   }

   public void setActiveAccount(StepFullJavaSession.FullJavaSession session, Supplier<StepFullJavaSession.FullJavaSession> refresher) {
      this.active = session;
      this.refresher = refresher;
   }

   public void setTarget(String host, int port) {
      this.targetHost = host;
      this.targetPort = port;
   }

   public void setVersion(int protocol) {
      this.forcedProtocol = protocol;
   }

   public boolean isRunning() {
      return this.serverSocket != null;
   }

   public synchronized void start() throws IOException {
      if (this.serverSocket == null) {
         ServerSocket ss = new ServerSocket();
         ss.bind(new InetSocketAddress("127.0.0.1", 25565));
         this.serverSocket = ss;
         Thread t = new Thread(() -> this.acceptLoop(ss), "mc-proxy-accept");
         t.setDaemon(true);
         t.start();
      }
   }

   private void acceptLoop(ServerSocket ss) {
      while(!ss.isClosed()) {
         Socket socket;
         try {
            socket = ss.accept();
         } catch (IOException var4) {
            return;
         }

         Thread t = new Thread(() -> this.handle(socket), "mc-proxy-conn");
         t.setDaemon(true);
         t.start();
      }

   }

   private void handle(Socket clientSocket) {
      Connection client = null;
      Connection server = null;

      try {
         clientSocket.setTcpNoDelay(true);
         client = new Connection(clientSocket);
         ProtocolIO.Reader hs = new ProtocolIO.Reader(client.readPacket());
         hs.readVarInt();
         int protocol = hs.readVarInt();
         hs.readString();
         hs.readUnsignedShort();
         int next = hs.readVarInt();
         if (next != 1) {
            if (next != 2) {
               return;
            }

            ProtocolIO.Reader ls = new ProtocolIO.Reader(client.readPacket());
            ls.readVarInt();
            String clientName = ls.readString();
            StepFullJavaSession.FullJavaSession session = this.active;
            String host = this.targetHost;
            int port = this.targetPort;
            if (session != null && host != null && !host.isBlank()) {
               Creds creds = this.freshCreds(session);
               int proto = this.forcedProtocol == -1 ? protocol : this.forcedProtocol;
               this.log("Login: client \"" + clientName + "\" (protocol " + protocol + ") → " + host + ":" + port + " as " + creds.name() + (proto != protocol ? " (forced protocol " + proto + ")" : ""));
               server = new Connection(new Socket(host, port));
               server.socket.setTcpNoDelay(true);
               server.writePacket((new ProtocolIO.Writer()).varInt(0).varInt(proto).string(host).unsignedShort(port).varInt(2).toBytes());
               server.writePacket(loginStart(proto, creds));
               this.backendLogin(server, client, creds, proto);
               this.log("Logged in as " + creds.name() + " on " + host + ":" + port + " — relaying");
               this.pump(client, server);
               this.log("Session ended — " + creds.name() + " on " + host + ":" + port);
               return;
            }

            this.log("Login rejected — no account or target server selected (client \"" + clientName + "\")");
            disconnectClientDuringLogin(client, "No account or target server selected in the app.");
            return;
         }

         this.handleStatus(client, protocol);
      } catch (Exception var19) {
         Exception e = var19;
         String var10001 = var19.getMessage() != null ? var19.getMessage() : var19.getClass().getSimpleName();
         this.log("Connection error: " + var10001);
         if (client != null && server == null) {
            try {
               disconnectClientDuringLogin(client, "Proxy error: " + e.getMessage());
            } catch (Exception var18) {
            }

            return;
         }

         return;
      } finally {
         closeQuietly(server);
         closeQuietly(client);
      }

   }

   private void backendLogin(Connection server, Connection client, Creds creds, int proto) throws Exception {
      while(true) {
         byte[] packet = server.readPacket();
         ProtocolIO.Reader r = new ProtocolIO.Reader(packet);
         int id = r.readVarInt();
         switch (id) {
            case 0:
               String reason = r.readString();
               this.log("Server refused login: " + reason);
               disconnectClientDuringLogin(client, "Server: " + reason);
               throw new IOException("backend disconnect during login");
            case 1:
               String serverIdField = r.readString();
               byte[] publicKey = r.readPrefixedBytes();
               byte[] verifyToken = r.readPrefixedBytes();
               byte[] secret = new byte[16];
               this.random.nextBytes(secret);
               PublicKey key = rsaPublicKey(publicKey);
               String serverHash = MojangAuth.serverIdHash(serverIdField, secret, publicKey);
               MojangAuth.joinServer(creds.token, creds.uuidDashless, serverHash);
               server.writePacket(encryptionResponse(proto, rsaEncrypt(key, secret), rsaEncrypt(key, verifyToken)));
               server.enableEncryption(secret);
               this.log("Mojang joinServer OK — connection encrypted");
               break;
            case 2:
               client.writePacket(packet);
               return;
            case 3:
               server.threshold = r.readVarInt();
               break;
            case 4:
               int messageId = r.readVarInt();
               server.writePacket((new ProtocolIO.Writer()).varInt(2).varInt(messageId).bool(false).toBytes());
               break;
            default:
               throw new IOException("unexpected login packet id 0x" + Integer.toHexString(id));
         }
      }
   }

   private void pump(Connection client, Connection server) {
      Thread up = new Thread(() -> this.relay(client, server), "mc-proxy-c2s");
      up.setDaemon(true);
      up.start();
      this.relay(server, client);

      try {
         up.join();
      } catch (InterruptedException var5) {
      }

   }

   private void relay(Connection from, Connection to) {
      try {
         while(true) {
            to.writePacket(from.readPacket());
         }
      } catch (IOException var4) {
         closeQuietly(from);
         closeQuietly(to);
      }
   }

   private void handleStatus(Connection client, int protocol) throws IOException {
      client.readPacket();
      String json = "{\"version\":{\"name\":\"Localts\",\"protocol\":" + protocol + "},\"players\":{\"max\":1,\"online\":0},\"description\":{\"text\":\"Localts proxy — pick an account in the app, then connect\"}}";
      client.writePacket((new ProtocolIO.Writer()).varInt(0).string(json).toBytes());
      byte[] ping = client.readPacket();
      client.writePacket(ping);
   }

   private Creds freshCreds(StepFullJavaSession.FullJavaSession session) {
      StepFullJavaSession.FullJavaSession fresh = this.ensureFresh(session);
      StepMCProfile.MCProfile profile = fresh.getMcProfile();
      return new Creds(profile.getName(), profile.getId(), profile.getId().toString().replace("-", ""), profile.getMcToken().getAccessToken());
   }

   private StepFullJavaSession.FullJavaSession ensureFresh(StepFullJavaSession.FullJavaSession session) {
      long expiry = session.getMcProfile().getMcToken().getExpireTimeMs();
      if (System.currentTimeMillis() < expiry - 120000L) {
         return session;
      } else {
         this.log("Access token for " + session.getMcProfile().getName() + " is stale — refreshing before login");
         synchronized(this.authLock) {
            StepFullJavaSession.FullJavaSession var10000;
            try {
               Supplier<StepFullJavaSession.FullJavaSession> r = this.refresher;
               StepFullJavaSession.FullJavaSession refreshed = r != null ? (StepFullJavaSession.FullJavaSession)r.get() : null;
               if (refreshed == null) {
                  this.log("Token refresh returned nothing — continuing with the stale session");
                  var10000 = session;
                  return var10000;
               }

               if (this.active == session) {
                  this.active = refreshed;
               }

               var10000 = refreshed;
            } catch (Exception e) {
               this.log("Token refresh failed (" + e.getMessage() + ") — continuing with the stale session");
               return session;
            }

            return var10000;
         }
      }
   }

   static byte[] loginStart(int proto, Creds creds) {
      ProtocolIO.Writer w = (new ProtocolIO.Writer()).varInt(0).string(creds.name());
      if (proto >= 764) {
         w.uuid(creds.uuid);
      } else if (proto >= 761) {
         w.bool(true).uuid(creds.uuid);
      } else if (proto == 760) {
         w.bool(false).bool(true).uuid(creds.uuid);
      } else if (proto == 759) {
         w.bool(false);
      }

      return w.toBytes();
   }

   static byte[] encryptionResponse(int proto, byte[] encSecret, byte[] encToken) {
      ProtocolIO.Writer w = (new ProtocolIO.Writer()).varInt(1).prefixedBytes(encSecret);
      if (proto == 759 || proto == 760) {
         w.bool(true);
      }

      return w.prefixedBytes(encToken).toBytes();
   }

   private static PublicKey rsaPublicKey(byte[] encoded) throws Exception {
      return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
   }

   private static byte[] rsaEncrypt(PublicKey key, byte[] data) throws Exception {
      Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      c.init(1, key);
      return c.doFinal(data);
   }

   private static void disconnectClientDuringLogin(Connection client, String message) throws IOException {
      String var10000 = message.replace("\\", "\\\\");
      String json = "{\"text\":\"" + var10000.replace("\"", "\\\"") + "\"}";
      client.writePacket((new ProtocolIO.Writer()).varInt(0).string(json).toBytes());
   }

   private static void closeQuietly(Connection c) {
      if (c != null) {
         try {
            c.socket.close();
         } catch (IOException var2) {
         }
      }

   }

   static record Creds(String name, UUID uuid, String uuidDashless, String token) {
   }

   static final class Connection {
      final Socket socket;
      private InputStream in;
      private OutputStream out;
      int threshold = -1;

      Connection(Socket socket) throws IOException {
         this.socket = socket;
         this.in = socket.getInputStream();
         this.out = socket.getOutputStream();
      }

      void enableEncryption(byte[] secret) throws Exception {
         this.in = new CfbInputStream(this.in, cipher(2, secret));
         this.out = new CfbOutputStream(this.out, cipher(1, secret));
      }

      private static Cipher cipher(int mode, byte[] secret) throws Exception {
         Cipher c = Cipher.getInstance("AES/CFB8/NoPadding");
         c.init(mode, new SecretKeySpec(secret, "AES"), new IvParameterSpec(secret));
         return c;
      }

      byte[] readPacket() throws IOException {
         int frameLen = ProtocolIO.readVarInt(this.in);
         byte[] frame = this.in.readNBytes(frameLen);
         if (frame.length != frameLen) {
            throw new IOException("short read");
         } else if (this.threshold < 0) {
            return frame;
         } else {
            ProtocolIO.Reader r = new ProtocolIO.Reader(frame);
            int dataLen = r.readVarInt();
            byte[] rest = r.readRemaining();
            return dataLen == 0 ? rest : ProtocolIO.inflate(rest, dataLen);
         }
      }

      void writePacket(byte[] packet) throws IOException {
         ProtocolIO.Writer body = new ProtocolIO.Writer();
         if (this.threshold < 0) {
            body.raw(packet);
         } else if (packet.length >= this.threshold) {
            body.varInt(packet.length).raw(ProtocolIO.deflate(packet));
         } else {
            body.varInt(0).raw(packet);
         }

         byte[] framed = body.toBytes();
         ProtocolIO.Writer head = (new ProtocolIO.Writer()).varInt(framed.length);
         this.out.write(head.toBytes());
         this.out.write(framed);
         this.out.flush();
      }
   }

   private static final class CfbInputStream extends InputStream {
      private final InputStream src;
      private final Cipher cipher;

      CfbInputStream(InputStream src, Cipher cipher) {
         this.src = src;
         this.cipher = cipher;
      }

      public int read() throws IOException {
         int b = this.src.read();
         return b == -1 ? -1 : this.cipher.update(new byte[]{(byte)b})[0] & 255;
      }

      public int read(byte[] b, int off, int len) throws IOException {
         int n = this.src.read(b, off, len);
         if (n <= 0) {
            return n;
         } else {
            byte[] dec = this.cipher.update(b, off, n);
            System.arraycopy(dec, 0, b, off, n);
            return n;
         }
      }
   }

   private static final class CfbOutputStream extends OutputStream {
      private final OutputStream dst;
      private final Cipher cipher;

      CfbOutputStream(OutputStream dst, Cipher cipher) {
         this.dst = dst;
         this.cipher = cipher;
      }

      public void write(int b) throws IOException {
         this.dst.write(this.cipher.update(new byte[]{(byte)b}));
      }

      public void write(byte[] b, int off, int len) throws IOException {
         if (len != 0) {
            this.dst.write(this.cipher.update(b, off, len));
         }
      }

      public void flush() throws IOException {
         this.dst.flush();
      }
   }
}
