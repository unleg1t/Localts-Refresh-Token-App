package org.localts.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class LocaltsApi {
   private static final String HOST = "https://localts.store";
   private static final String BASE = "https://localts.store/v1";
   private static final RequestBody EMPTY = RequestBody.create((byte[])(new byte[0]), (MediaType)null);
   private final OkHttpClient client = newClient();
   private final OkHttpClient directClient;
   private volatile String apiKey;

   public LocaltsApi(String apiKey) {
      this.directClient = this.client.newBuilder().proxy(Proxy.NO_PROXY).build();
      this.apiKey = apiKey;
   }

   public void setApiKey(String apiKey) {
      this.apiKey = apiKey;
   }

   public Me getMe() throws IOException {
      return parseMe(this.get("/me", true));
   }

   public List<Product> getProducts() throws IOException {
      return parseProducts(this.get("/products", false));
   }

   public String purchase(String productId, int amount) throws IOException {
      Request.Builder var10001 = new Request.Builder();
      String var10002 = enc(productId);
      JsonObject o = this.exec(var10001.url("https://localts.store/v1/products/" + var10002 + "/purchase?amount=" + amount).post(EMPTY), true);
      return strOr(o, "orderId", (String)null);
   }

   public OrdersPage getOrders(int page, int size) throws IOException {
      return parseOrders(this.get("/orders?page=" + page + "&size=" + size, true));
   }

   public Order getOrder(String id) throws IOException {
      return parseOrder(this.get("/orders/get-order?id=" + enc(id), true), id);
   }

   public byte[] getProductLogo(String logoUrl) {
      if (logoUrl != null && !logoUrl.isBlank()) {
         String url = logoUrl.startsWith("http") ? logoUrl : "https://localts.store" + (logoUrl.startsWith("/") ? "" : "/") + logoUrl;
         Request.Builder b = (new Request.Builder()).url(url).get();
         if (this.apiKey != null && !this.apiKey.isBlank()) {
            b.header("X-API-Key", this.apiKey);
         }

         try {
            Response r = this.call(b.build());

            byte[] var5;
            try {
               var5 = r.isSuccessful() && r.body() != null ? r.body().bytes() : null;
            } catch (Throwable var8) {
               if (r != null) {
                  try {
                     r.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (r != null) {
               r.close();
            }

            return var5;
         } catch (Exception var9) {
            return null;
         }
      } else {
         return null;
      }
   }

   static Me parseMe(JsonObject o) {
      return new Me(strOr(o, "username", ""), lng(o, "balance"));
   }

   static List<Product> parseProducts(JsonObject o) {
      List<Product> products = new ArrayList();

      for(JsonElement e : o.getAsJsonArray("products")) {
         JsonObject p = e.getAsJsonObject();
         Map<Integer, Integer> discounts = new LinkedHashMap();
         if (p.has("quantityDiscounts") && p.get("quantityDiscounts").isJsonObject()) {
            for(Map.Entry<String, JsonElement> d : p.getAsJsonObject("quantityDiscounts").entrySet()) {
               discounts.put(Integer.parseInt((String)d.getKey()), ((JsonElement)d.getValue()).getAsInt());
            }
         }

         List<String> tags = new ArrayList();
         if (p.has("tags") && p.get("tags").isJsonArray()) {
            for(JsonElement t : p.getAsJsonArray("tags")) {
               tags.add(t.getAsString());
            }
         }

         products.add(new Product(strOr(p, "id", ""), strOr(p, "name", ""), strOr(p, "description", ""), strOr(p, "category", ""), lng(p, "priceInCredits"), lng(p, "stock"), strOr(p, "type", ""), strOr(p, "logoUrl", ""), tags, discounts));
      }

      return products;
   }

   static OrdersPage parseOrders(JsonObject o) {
      List<OrderSummary> orders = new ArrayList();
      if (o.has("orders") && o.get("orders").isJsonArray()) {
         for(JsonElement e : o.getAsJsonArray("orders")) {
            JsonObject s = e.getAsJsonObject();
            orders.add(new OrderSummary(strOr(s, "id", ""), strOr(s, "productId", ""), strOr(s, "productType", ""), lng(s, "timestamp")));
         }
      }

      return new OrdersPage(orders, intt(o, "page"), intt(o, "size"), intt(o, "totalPages"), lng(o, "totalElements"));
   }

   static Order parseOrder(JsonObject o, String fallbackId) {
      List<OrderItem> items = new ArrayList();
      if (o.has("items") && o.get("items").isJsonArray()) {
         for(JsonElement e : o.getAsJsonArray("items")) {
            JsonObject it = e.getAsJsonObject();
            items.add(new OrderItem(strOr(it, "id", ""), strOr(it, "content", "")));
         }
      }

      return new Order(strOr(o, "order-id", fallbackId), strOr(o, "status", ""), o.has("product-name") && !o.get("product-name").isJsonNull() ? o.get("product-name").getAsString() : null, items);
   }

   private JsonObject get(String path, boolean auth) throws IOException {
      return this.exec((new Request.Builder()).url("https://localts.store/v1" + path).get(), auth);
   }

   private static OkHttpClient newClient() {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();

      try {
         X509TrustManager systemRoots = systemRootTrustManager();
         if (systemRoots != null) {
            X509TrustManager trust = new DualTrustManager(trustManagerFor((KeyStore)null), systemRoots);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init((KeyManager[])null, new TrustManager[]{trust}, (SecureRandom)null);
            builder.sslSocketFactory(context.getSocketFactory(), trust);
         }
      } catch (IOException | GeneralSecurityException var4) {
      }

      return builder.build();
   }

   private static X509TrustManager systemRootTrustManager() throws GeneralSecurityException, IOException {
      String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      if (os.contains("win")) {
         KeyStore windowsRoots = KeyStore.getInstance("Windows-ROOT");
         windowsRoots.load((InputStream)null, (char[])null);
         return trustManagerFor(windowsRoots);
      }

      String[] bundles = new String[]{"/etc/ssl/certs/ca-certificates.crt", "/etc/pki/tls/certs/ca-bundle.crt", "/etc/ssl/ca-bundle.pem", "/etc/pki/ca-trust/extracted/pem/tls-ca-bundle.pem", "/etc/ssl/cert.pem"};
      for (String path : bundles) {
         Path file = Path.of(path);
         if (Files.isReadable(file)) {
            return trustManagerFor(loadPemCertificates(file));
         }
      }

      return null;
   }

   private static KeyStore loadPemCertificates(Path file) throws GeneralSecurityException, IOException {
      KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
      store.load((InputStream)null, (char[])null);
      CertificateFactory factory = CertificateFactory.getInstance("X.509");

      try (InputStream in = Files.newInputStream(file)) {
         for(Certificate cert : factory.generateCertificates(in)) {
            store.setCertificateEntry(cert.getType() + " " + Integer.toHexString(cert.hashCode()), cert);
         }
      }

      return store;
   }

   private static X509TrustManager trustManagerFor(KeyStore store) throws GeneralSecurityException {
      TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      factory.init(store);

      for(TrustManager tm : factory.getTrustManagers()) {
         if (tm instanceof X509TrustManager x509) {
            return x509;
         }
      }

      String var10002 = store == null ? "JVM defaults" : store.getType();
      throw new NoSuchAlgorithmException("No X509TrustManager for " + var10002);
   }

   private Response call(Request request) throws IOException {
      try {
         return this.client.newCall(request).execute();
      } catch (SSLException viaProxy) {
         boolean requestNeverSent = viaProxy instanceof SSLHandshakeException;
         if (!requestNeverSent && !"GET".equals(request.method())) {
            throw secureConnectionError(viaProxy);
         } else {
            try {
               return this.directClient.newCall(request).execute();
            } catch (SSLException direct) {
               throw secureConnectionError(direct);
            }
         }
      }
   }

   private static ApiException secureConnectionError(SSLException cause) {
      return new ApiException("Couldn't establish a secure connection to Localts. A VPN, proxy, or antivirus that scans HTTPS traffic is the usual cause — turn off HTTPS/SSL scanning (or your proxy/VPN) and reconnect. [" + String.valueOf(cause) + "]", cause);
   }

   private JsonObject exec(Request.Builder builder, boolean auth) throws IOException {
      if (auth) {
         if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new ApiException("No API key set");
         }

         builder.header("X-API-Key", this.apiKey);
      }

      Response response = this.call(builder.build());

      JsonObject var6;
      try {
         String body = response.body() != null ? response.body().string() : "";
         if (response.code() == 401) {
            throw new ApiException("Invalid or missing API key", (Throwable)null, 401);
         }

         JsonObject json = body.isBlank() ? new JsonObject() : JsonParser.parseString(body).getAsJsonObject();
         if (json.has("success") && !json.get("success").getAsBoolean()) {
            throw new ApiException(strOr(json, "error", "Request failed"), (Throwable)null, response.code());
         }

         if (response.code() >= 400) {
            throw new ApiException("HTTP " + response.code() + (body.isBlank() ? "" : ": " + body), (Throwable)null, response.code());
         }

         var6 = json;
      } catch (Throwable var8) {
         if (response != null) {
            try {
               response.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (response != null) {
         response.close();
      }

      return var6;
   }

   private static String enc(String s) {
      return URLEncoder.encode(s, StandardCharsets.UTF_8);
   }

   private static String strOr(JsonObject o, String key, String def) {
      return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : def;
   }

   private static long lng(JsonObject o, String key) {
      return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsLong() : 0L;
   }

   private static int intt(JsonObject o, String key) {
      return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsInt() : 0;
   }

   public static record Me(String username, long balance) {
   }

   public static record Product(String id, String name, String description, String category, long priceInCredits, long stock, String type, String logoUrl, List<String> tags, Map<Integer, Integer> quantityDiscounts) {
      public boolean hasTag(String tag) {
         for(String t : this.tags) {
            if (t.equalsIgnoreCase(tag)) {
               return true;
            }
         }

         return false;
      }
   }

   public static record OrderSummary(String id, String productId, String productType, long timestamp) {
   }

   public static record OrdersPage(List<OrderSummary> orders, int page, int size, int totalPages, long totalElements) {
   }

   public static record OrderItem(String id, String content) {
   }

   public static record Order(String orderId, String status, String productName, List<OrderItem> items) {
      public boolean isPackaged() {
         return "PACKAGED".equalsIgnoreCase(this.status);
      }
   }

   public static final class ApiException extends RuntimeException {
      private final int status;

      public ApiException(String message) {
         this(message, (Throwable)null, 0);
      }

      public ApiException(String message, Throwable cause) {
         this(message, cause, 0);
      }

      public ApiException(String message, Throwable cause, int status) {
         super(message, cause);
         this.status = status;
      }

      public int status() {
         return this.status;
      }
   }

   private static record DualTrustManager(X509TrustManager jdk, X509TrustManager windows) implements X509TrustManager {
      public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
         try {
            this.jdk.checkServerTrusted(chain, authType);
         } catch (CertificateException rejectedByJdk) {
            try {
               this.windows.checkServerTrusted(chain, authType);
            } catch (CertificateException rejectedByWindows) {
               rejectedByJdk.addSuppressed(rejectedByWindows);
               throw rejectedByJdk;
            }
         }

      }

      public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
         this.jdk.checkClientTrusted(chain, authType);
      }

      public X509Certificate[] getAcceptedIssuers() {
         X509Certificate[] a = this.jdk.getAcceptedIssuers();
         X509Certificate[] b = this.windows.getAcceptedIssuers();
         X509Certificate[] all = (X509Certificate[])Arrays.copyOf(a, a.length + b.length);
         System.arraycopy(b, 0, all, a.length, b.length);
         return all;
      }
   }
}
