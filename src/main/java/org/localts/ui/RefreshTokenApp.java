package org.localts.ui;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Desktop.Action;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.raphimc.minecraftauth.step.java.StepMCProfile;
import net.raphimc.minecraftauth.step.java.StepMCToken;
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession;
import org.localts.api.LocaltsApi;
import org.localts.exception.AuthenticationException;
import org.localts.proxy.MinecraftProxy;
import org.localts.session.AccountAuthenticator;

public class RefreshTokenApp {
   static final Color BG_TOP = new Color(18, 22, 29);
   static final Color BG_BOTTOM = new Color(11, 14, 19);
   static final Color CARD = new Color(22, 27, 34);
   static final Color CARD_T = new Color(22, 27, 34, 144);
   static final Color CARD_HI = new Color(29, 35, 45);
   static final Color BORDER = new Color(37, 44, 55);
   static final Color BORDER_T = new Color(37, 44, 55, 102);
   static final Color BORDER_HI = new Color(51, 60, 74);
   static final Color TEXT = new Color(230, 232, 235);
   static final Color SUBTLE = new Color(138, 146, 157);
   static final Color ACCENT = new Color(62, 123, 214);
   static final Color ACCENT_DIM = new Color(51, 74, 107);
   static final Color OK = new Color(91, 169, 130);
   static final Color ERROR = new Color(229, 83, 75);
   static final Color CYAN = new Color(87, 182, 201);
   static final Color SOFT_RED = new Color(210, 110, 104);
   private static final int W = 440;
   private static final int RADIUS = 18;
   private static final int INNER = 396;
   private static final int SIDEBAR = 48;
   private static final int WIN_W = 488;
   private static final int WIN_H = 550;
   private static final Preferences PREFS = Preferences.userNodeForPackage(RefreshTokenApp.class);
   private static final Path ACCOUNTS_FILE = Paths.get(System.getProperty("user.home"), ".localts", "accounts.json");
   private static final String PREF_KEY = "localts_api_key";
   private static final String PREF_TARGET = "target_server";
   private static final String PREF_LAUNCHED = "launched_before";
   private static final String PREF_SORT = "account_sort";
   private static final int STORE_LIST_H = 388;
   private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm").withZone(ZoneId.systemDefault());
   private static final DateTimeFormatter LOG_FMT = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
   private static final int LOG_MAX = 400;
   private final JFrame frame = new JFrame("Localts Account Switcher");
   private final StatusBar status = new StatusBar();
   private final MinecraftProxy proxy = new MinecraftProxy();
   private final LocaltsApi api;
   private final JPanel accountsPanel;
   private final List<AccountRow> rows;
   private JComponent accountsSection;
   private JComponent accountsDeleteBtn;
   private JScrollPane accountsScroll;
   private SortButton sortButton;
   private SortMode accountSort;
   private String currentTarget;
   private String accountsQuery;
   private JTextField accountsSearchField;
   private TargetIcon hypixelTarget;
   private TargetIcon donutTarget;
   private TargetIcon wrenchTarget;
   private static final String HYPIXEL = "mc.hypixel.net";
   private static final String DONUT = "donutsmp.net";
   private static final int ACCOUNT_ROW_H = 44;
   private static final int ACCOUNT_ROW_GAP = 8;
   private static final int ACCOUNTS_MAX_VISIBLE = 7;
   private final JPanel contentHolder;
   private final Map<String, JComponent> tabs;
   private final Map<String, SidebarButton> tabButtons;
   private String currentTab;
   private ApiKeyField apiKeyField;
   private final List<JLabel> balanceLabels;
   private final List<JComponent> ordersLoadingUi;
   private final List<JComponent> productsLoadingUi;
   private String balanceText;
   private final JPanel productsPanel;
   private final JPanel ordersPanel;
   private final JTextPane logPane;
   private final List<String> logLines;
   private final Map<String, LocaltsApi.Product> productsById;
   private final Map<String, LocaltsApi.Order> orderCache;
   private final Set<String> invalidTokens;
   private String lastConnectedKey;
   private String productsSig;
   private String ordersSig;
   private static final long RATE_LIMIT_COOLDOWN_MS = 60000L;
   private volatile long pollCooldownUntil;
   private static final long STORE_STALE_MS = 60000L;
   private long productsFetchedAt;
   private long ordersFetchedAt;
   private boolean ordersAfterProducts;
   private List<LocaltsApi.Product> allProducts;
   private List<OrderListing> allOrders;
   private String productsQuery;
   private String ordersQuery;
   private static final Version[] VERSIONS = new Version[]{new Version("Auto-detect (match client)", -1), new Version("1.21.5", 770), new Version("1.21.4", 769), new Version("1.21.3", 768), new Version("1.21 / 1.21.1", 767), new Version("1.20.6", 766), new Version("1.20.4", 765), new Version("1.20.2", 764), new Version("1.20.1", 763), new Version("1.19.4", 762), new Version("1.19.2", 760), new Version("1.18.2", 758), new Version("1.17.1", 756), new Version("1.16.5", 754), new Version("1.12.2", 340), new Version("1.8.9", 47)};
   private static BufferedImage cartIcon;
   private static BufferedImage dotsIcon;
   private static final String REFRESH_TOKEN_TAG = "Refresh token";
   private static final int LOGO_SIZE = 54;
   private static final int ROW_ARC = 12;
   private static final int AVATAR = 32;
   private static final Path LOGO_CACHE_DIR = Paths.get(System.getProperty("user.home"), ".localts", "logo-cache");
   private static final long LOGO_TTL_MS = 21600000L;
   private static final Map<String, CachedLogo> logoMemCache = new ConcurrentHashMap();

   public static void main(String[] args) {
      String os = System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT);
      if (os.contains("mac")) {
         System.setProperty("apple.awt.application.name", "Localts Refresher");
         System.setProperty("apple.laf.useScreenMenuBar", "true");
      } else {
         System.setProperty("awt.useSystemAAFontSettings", "lcd");
         System.setProperty("swing.aatext", "true");
      }

      SwingUtilities.invokeLater(RefreshTokenApp::new);
   }

   RefreshTokenApp() {
      this.api = new LocaltsApi(PREFS.get("localts_api_key", ""));
      this.accountsPanel = new JPanel();
      this.rows = new ArrayList();
      this.accountSort = RefreshTokenApp.SortMode.IMPORT;
      this.currentTarget = "";
      this.accountsQuery = "";
      this.contentHolder = new JPanel(new BorderLayout());
      this.tabs = new HashMap();
      this.tabButtons = new HashMap();
      this.currentTab = "accounts";
      this.balanceLabels = new ArrayList();
      this.ordersLoadingUi = new ArrayList();
      this.productsLoadingUi = new ArrayList();
      this.balanceText = " ";
      this.productsPanel = new JPanel();
      this.ordersPanel = new JPanel();
      this.logPane = new JTextPane();
      this.logLines = new ArrayList();
      this.productsById = new ConcurrentHashMap();
      this.orderCache = new ConcurrentHashMap();
      this.invalidTokens = new HashSet();
      this.lastConnectedKey = null;
      this.allProducts = new ArrayList();
      this.allOrders = new ArrayList();
      this.productsQuery = "";
      this.ordersQuery = "";
      this.frame.setUndecorated(true);
      styleTooltips();
      RootPanel root = new RootPanel();
      root.setLayout(new BorderLayout());
      this.frame.setContentPane(root);
      root.add(this.buildSidebar(), "West");
      root.add(this.buildContent(), "Center");
      this.installUnfocusBehavior(root);
      this.proxy.setLogger((msg) -> this.log("Proxy: " + msg));

      try {
         this.proxy.start();
         this.status.set("Proxy on 127.0.0.1:25565 — add an account & set a server", SUBTLE);
      } catch (Exception e) {
         this.status.set("Couldn't start proxy on port 25565: " + e.getMessage(), ERROR);
      }

      this.loadSavedAccounts();
      (new Timer(30000, (ex) -> this.accountsPanel.repaint())).start();
      this.fitWindow();
      this.frame.setLocationRelativeTo((Component)null);
      this.frame.setVisible(true);
      if (!PREFS.get("localts_api_key", "").isBlank()) {
         this.apiKeyField.lock();
      }

   }

   private void installUnfocusBehavior(JComponent root) {
      root.setFocusable(true);
      Toolkit.getDefaultToolkit().addAWTEventListener((event) -> {
         KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
         if (event instanceof MouseEvent me) {
            if (me.getID() == 501) {
               Component patt0$temp = kfm.getFocusOwner();
               if (patt0$temp instanceof JTextComponent) {
                  JTextComponent owner = (JTextComponent)patt0$temp;
                  patt0$temp = me.getComponent();
                  Container group = owner.getParent();
                  if (patt0$temp != owner && (group == null || !SwingUtilities.isDescendingFrom(patt0$temp, group))) {
                     root.requestFocusInWindow();
                     return;
                  }
               }

               return;
            }
         }

         if (event instanceof KeyEvent ke) {
            if (ke.getID() == 401 && ke.getKeyCode() == 10 && kfm.getFocusOwner() instanceof JTextComponent) {
               Objects.requireNonNull(root);
               SwingUtilities.invokeLater(root::requestFocusInWindow);
            }
         }

      }, 24L);
   }

   private void fitWindow() {
      this.frame.setSize(488, 550);
      this.frame.setShape(new RoundRectangle2D.Double((double)0.0F, (double)0.0F, (double)488.0F, (double)550.0F, (double)18.0F, (double)18.0F));
   }

   private void exitApp() {
      try {
         PREFS.flush();
      } catch (Exception var2) {
      }

      this.frame.dispose();
      System.exit(0);
   }

   private static void styleTooltips() {
      UIManager.put("ToolTip.background", CARD_HI);
      UIManager.put("ToolTip.foreground", TEXT);
      UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_HI, 1), new EmptyBorder(4, 9, 4, 9)));
      UIManager.put("ToolTip.font", font(1, 11.0F));
      ToolTipManager.sharedInstance().setInitialDelay(350);
   }

   private JComponent buildTitleBar() {
      JPanel bar = new JPanel(new BorderLayout());
      bar.setOpaque(false);
      bar.setBorder(new EmptyBorder(12, 18, 4, 14));
      JLabel title = new JLabel("Localts Account Switcher");
      title.setForeground(TEXT);
      title.setFont(font(1, 14.0F));
      JPanel controls = new JPanel();
      controls.setOpaque(false);
      controls.setLayout(new BoxLayout(controls, 0));
      controls.add(new MinimizeButton(() -> this.frame.setExtendedState(1)));
      controls.add(Box.createRigidArea(new Dimension(2, 0)));
      controls.add(new CloseButton(this::exitApp));
      bar.add(title, "West");
      bar.add(controls, "East");
      final Point[] origin = new Point[]{null};
      MouseAdapter drag = new MouseAdapter() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         public void mousePressed(MouseEvent e) {
            origin[0] = e.getPoint();
         }

         public void mouseReleased(MouseEvent e) {
            origin[0] = null;
         }

         public void mouseDragged(MouseEvent e) {
            if (origin[0] != null) {
               Point p = RefreshTokenApp.this.frame.getLocation();
               RefreshTokenApp.this.frame.setLocation(p.x + e.getX() - origin[0].x, p.y + e.getY() - origin[0].y);
            }
         }
      };
      bar.addMouseListener(drag);
      bar.addMouseMotionListener(drag);
      title.addMouseListener(drag);
      title.addMouseMotionListener(drag);
      return bar;
   }

   private JComponent buildSidebar() {
      JPanel rail = new JPanel() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         protected void paintComponent(Graphics g) {
            g.setColor(RefreshTokenApp.BORDER);
            g.fillRect(this.getWidth() - 1, 10, 1, this.getHeight() - 20);
         }
      };
      rail.setOpaque(false);
      rail.setLayout(new BoxLayout(rail, 1));
      rail.setBorder(new EmptyBorder(9, 0, 12, 0));
      AnimatedWebp logo = new AnimatedWebp("/icons/logo.webp", 30);
      logo.setAlignmentX(0.5F);
      rail.add(logo);
      rail.add(Box.createRigidArea(new Dimension(0, 12)));
      rail.add(this.sidebarButton("accounts", "/icons/accounts.svg", "Accounts"));
      rail.add(Box.createRigidArea(new Dimension(0, 4)));
      rail.add(this.sidebarButton("orders", "/icons/orders.svg", "Orders"));
      rail.add(Box.createRigidArea(new Dimension(0, 4)));
      rail.add(this.sidebarButton("products", "/icons/products.svg", "Products"));
      rail.add(Box.createVerticalGlue());
      rail.add(new SidebarButton("/icons/discord.svg", "Join our Discord", () -> this.openUrl("https://localts.store/discord")));
      rail.add(Box.createRigidArea(new Dimension(0, 4)));
      rail.add(this.sidebarButton("help", "/icons/question.svg", "How to use"));
      rail.add(Box.createRigidArea(new Dimension(0, 4)));
      rail.add(this.sidebarButton("logging", "/icons/history.svg", "Logging"));
      rail.add(Box.createRigidArea(new Dimension(0, 4)));
      rail.add(this.sidebarButton("settings", "/icons/settings.svg", "Settings"));
      return rail;
   }

   private SidebarButton sidebarButton(String tab, String svgPath, String tooltip) {
      SidebarButton b = new SidebarButton(svgPath, tooltip, () -> this.showTab(tab));
      this.tabButtons.put(tab, b);
      return b;
   }

   private void openUrl(String url) {
      try {
         if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
            Desktop.getDesktop().browse(URI.create(url));
            this.log("Opened " + url);
         } else {
            this.status.set("Couldn't open a browser — visit " + url, SUBTLE);
         }
      } catch (Exception e) {
         this.status.set("Couldn't open " + url, ERROR);
         this.log("Failed to open " + url + ": " + String.valueOf(e), ERROR);
      }

   }

   private JComponent buildContent() {
      JPanel right = new JPanel(new BorderLayout());
      right.setOpaque(false);
      right.add(this.buildTitleBar(), "North");
      this.tabs.put("accounts", this.buildAccountsTab());
      this.tabs.put("orders", this.buildOrdersTab());
      this.tabs.put("products", this.buildProductsTab());
      this.tabs.put("help", this.buildHelpTab());
      this.tabs.put("logging", this.buildLoggingTab());
      this.tabs.put("settings", this.buildSettingsTab());
      this.contentHolder.setOpaque(false);
      right.add(this.contentHolder, "Center");
      right.add(this.status, "South");
      this.status.setLogger(this::appendLog);
      boolean firstLaunch = !PREFS.getBoolean("launched_before", false);
      PREFS.putBoolean("launched_before", true);
      this.showTab(firstLaunch ? "help" : "accounts");
      return right;
   }

   private void showTab(String name) {
      this.currentTab = name;
      this.contentHolder.removeAll();
      this.contentHolder.add((Component)this.tabs.get(name), "Center");
      this.contentHolder.revalidate();
      this.contentHolder.repaint();
      this.tabButtons.forEach((k, b) -> b.setActive(k.equals(name)));
      this.loadStoreTab(name);
      this.fitWindow();
   }

   private JComponent buildAccountsTab() {
      JPanel s = this.tabPanel();
      s.add(this.buildTargetBar());
      s.add(Box.createRigidArea(new Dimension(0, 10)));
      s.add(this.buildAccountSearchBar());
      s.add(Box.createRigidArea(new Dimension(0, 14)));
      this.accountsPanel.setOpaque(false);
      this.accountsPanel.setLayout(new BoxLayout(this.accountsPanel, 1));
      this.accountsPanel.setAlignmentX(0.5F);
      JPanel section = new JPanel();
      section.setOpaque(false);
      section.setLayout(new BoxLayout(section, 1));
      section.setAlignmentX(0.5F);
      JPanel header = new JPanel(new BorderLayout());
      header.setOpaque(false);
      header.setMaximumSize(new Dimension(396, 22));
      header.setAlignmentX(0.5F);
      JLabel hl = new JLabel("ACCOUNTS");
      hl.setForeground(SUBTLE);
      hl.setFont(font(1, 10.0F));
      hl.setBorder(new EmptyBorder(3, 0, 0, 0));
      JLabel sep = new JLabel("•");
      sep.setForeground(SUBTLE);
      sep.setFont(font(1, 10.0F));
      sep.setBorder(new EmptyBorder(3, 0, 0, 0));
      JLabel addClip = linkLabel("Add from clipboard", "Authenticate the refresh token on your clipboard and add the account", this::addAccountFromClipboard);
      addClip.setBorder(new EmptyBorder(3, 0, 0, 0));
      JPanel west = new JPanel();
      west.setOpaque(false);
      west.setLayout(new BoxLayout(west, 0));
      west.add(hl);
      west.add(Box.createRigidArea(new Dimension(8, 0)));
      west.add(sep);
      west.add(Box.createRigidArea(new Dimension(8, 0)));
      west.add(addClip);
      header.add(west, "West");

      try {
         this.accountSort = RefreshTokenApp.SortMode.valueOf(PREFS.get("account_sort", RefreshTokenApp.SortMode.IMPORT.name()));
      } catch (IllegalArgumentException var9) {
      }

      this.accountsDeleteBtn = new SvgButton("/icons/trash-bin.svg", 15, 24, 20, SOFT_RED, ERROR, "Delete selected accounts", this::deleteSelectedAccounts);
      this.accountsDeleteBtn.setVisible(false);
      String var10003 = this.accountSort.label;
      this.sortButton = new SortButton("Sort: " + var10003, this::cycleSortMode);
      this.sortButton.setToolTipText("Click to change sort order");
      JPanel east = new JPanel();
      east.setOpaque(false);
      east.setLayout(new BoxLayout(east, 0));
      east.add(this.accountsDeleteBtn);
      east.add(Box.createRigidArea(new Dimension(10, 0)));
      east.add(this.sortButton);
      header.add(east, "East");
      section.add(header);
      section.add(Box.createRigidArea(new Dimension(0, 6)));
      this.accountsScroll = this.scrollOf(this.accountsPanel, 44);
      this.accountsScroll.setVisible(!this.rows.isEmpty());
      section.add(this.accountsScroll);
      this.accountsSection = section;
      s.add(section);
      return s;
   }

   private JComponent buildOrdersTab() {
      JPanel s = this.tabPanel();
      s.add(this.balanceLine("Loading orders", this.ordersLoadingUi, this::refreshOrders));
      s.add(Box.createRigidArea(new Dimension(0, 10)));
      s.add(this.searchBar((q) -> {
         this.ordersQuery = q;
         this.renderOrders();
      }));
      s.add(Box.createRigidArea(new Dimension(0, 16)));
      this.ordersPanel.setOpaque(false);
      this.ordersPanel.setLayout(new BoxLayout(this.ordersPanel, 1));
      s.add(this.scrollOf(this.ordersPanel, 388));
      return s;
   }

   private JComponent buildProductsTab() {
      JPanel s = this.tabPanel();
      s.add(this.balanceLine("Loading products", this.productsLoadingUi));
      s.add(Box.createRigidArea(new Dimension(0, 10)));
      s.add(this.searchBar((q) -> {
         this.productsQuery = q;
         this.renderProducts();
      }));
      s.add(Box.createRigidArea(new Dimension(0, 16)));
      this.productsPanel.setOpaque(false);
      this.productsPanel.setLayout(new BoxLayout(this.productsPanel, 1));
      s.add(this.scrollOf(this.productsPanel, 388));
      return s;
   }

   private JComponent buildSettingsTab() {
      JPanel o = this.tabPanel();
      o.add(this.label("LOCALTS API KEY"));
      o.add(Box.createRigidArea(new Dimension(0, 6)));
      this.apiKeyField = new ApiKeyField(this::onConnect);
      this.apiKeyField.setKey(PREFS.get("localts_api_key", ""));
      o.add(this.apiKeyField);
      o.add(Box.createRigidArea(new Dimension(0, 12)));
      o.add(this.label("VERSION"));
      o.add(Box.createRigidArea(new Dimension(0, 6)));
      o.add(this.makeVersionSelector());
      return o;
   }

   private JComponent buildHelpTab() {
      JPanel s = this.tabPanel();
      s.add(this.label("HOW TO USE"));
      s.add(Box.createRigidArea(new Dimension(0, 8)));
      String step = "color:#E6E8EB;font-weight:bold";
      String accent = "#57B6C9";
      String html = "<html><body style='color:#C2C7CE'><div style='" + step + "'>1 &nbsp;Connect</div>Open " + tabRef("settings", "Settings") + " (bottom-left) and paste your Localts <b>API key</b> — it connects automatically.<br><br><div style='" + step + "'>2 &nbsp;Buy</div>On " + tabRef("products", "Products") + ", click <span style='color:" + accent + "'>Purchase</span> on a product and pick a quantity (bulk discounts apply).<br><br><div style='" + step + "'>3 &nbsp;Import</div>On " + tabRef("orders", "Orders") + ", click your order and hit <span style='color:" + accent + "'>Import</span> next to each account.<br><br><div style='" + step + "'>4 &nbsp;Use</div>On " + tabRef("accounts", "Accounts") + ", pick a <b>target server</b> you want to play on, this will be the server you will be connected to later. Then, select an account by clicking on it, go to Minecraft and connect to <b>127.0.0.1</b> (or simply \"localhost\"). To switch accounts - select another one and reconnect, no restart needed.<br><br><div style='color:#8A929D'>Everything is saved between restarts. The <b>⋮</b> menu on an account copies its tokens, refreshes or deletes it; the Logging tab (history icon) shows all activity.</div></body></html>";
      JEditorPane pane = new JEditorPane() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         public boolean getScrollableTracksViewportWidth() {
            return true;
         }
      };
      pane.setContentType("text/html");
      pane.putClientProperty("JEditorPane.honorDisplayProperties", Boolean.TRUE);
      pane.setFont(font(0, 12.0F));
      pane.setForeground(new Color(194, 199, 206));
      pane.setText(html);
      pane.setCaretPosition(0);
      pane.setEditable(false);
      pane.setOpaque(false);
      pane.setBorder(new EmptyBorder(2, 2, 2, 6));
      Dictionary<URL, Image> icons = new Hashtable();

      for(String name : new String[]{"settings", "products", "orders", "accounts"}) {
         BufferedImage icon = renderSvg("/icons/" + name + ".svg", 24);
         if (icon != null) {
            try {
               icons.put(URI.create("file:tab-" + name).toURL(), tintImage(icon, new Color(194, 199, 206)));
            } catch (Exception var13) {
            }
         }
      }

      pane.getDocument().putProperty("imageCache", icons);
      JScrollPane sp = new JScrollPane(pane, 20, 31);
      sp.setBorder((Border)null);
      sp.setOpaque(false);
      sp.getViewport().setOpaque(false);
      sp.setAlignmentX(0.5F);
      sp.setPreferredSize(new Dimension(396, 508));
      sp.setMaximumSize(new Dimension(396, 508));
      JScrollBar vbar = sp.getVerticalScrollBar();
      vbar.setUI(new SlimScrollBarUI());
      vbar.setOpaque(false);
      vbar.setUnitIncrement(14);
      vbar.setPreferredSize(new Dimension(9, 0));
      s.add(sp);
      return s;
   }

   private static String tabRef(String icon, String label) {
      return "<img src='file:tab-" + icon + "' width='12' height='12'>&nbsp;<b>" + label + "</b>";
   }

   private JComponent buildLoggingTab() {
      JPanel s = this.tabPanel();
      JPanel header = new JPanel(new BorderLayout());
      header.setOpaque(false);
      header.setMaximumSize(new Dimension(396, 24));
      header.setAlignmentX(0.5F);
      JLabel hl = new JLabel("LOG");
      hl.setForeground(SUBTLE);
      hl.setFont(font(1, 10.0F));
      hl.setBorder(new EmptyBorder(3, 0, 0, 0));
      header.add(hl, "West");
      IconButton[] copyRef = new IconButton[1];
      copyRef[0] = new IconButton(0, "Copy", () -> this.copyLogs(copyRef[0]));
      copyRef[0].setToolTipText("Copy the latest 250 log lines");
      header.add(eastWrap(copyRef[0]), "East");
      s.add(header);
      s.add(Box.createRigidArea(new Dimension(0, 6)));
      this.logPane.setEditable(false);
      this.logPane.setOpaque(false);
      this.logPane.setBackground(new Color(0, 0, 0, 0));
      this.logPane.setBorder(new EmptyBorder(0, 2, 0, 2));
      this.logPane.setFont(font(0, 12.0F));
      this.logPane.setCaretColor(new Color(0, 0, 0, 0));
      JScrollPane sp = new JScrollPane(this.logPane, 20, 31);
      sp.setBorder((Border)null);
      sp.setOpaque(false);
      sp.getViewport().setOpaque(false);
      sp.setAlignmentX(0.5F);
      sp.setPreferredSize(new Dimension(396, 508));
      sp.setMaximumSize(new Dimension(396, 508));
      JScrollBar vbar = sp.getVerticalScrollBar();
      vbar.setUI(new SlimScrollBarUI());
      vbar.setOpaque(false);
      vbar.setUnitIncrement(14);
      vbar.setPreferredSize(new Dimension(9, 0));
      s.add(sp);
      return s;
   }

   private void copyLogs(IconButton btn) {
      if (this.logLines.isEmpty()) {
         this.status.set("No log lines to copy yet", SUBTLE);
      } else {
         int n = Math.min(250, this.logLines.size());
         String text = String.join(System.lineSeparator(), this.logLines.subList(this.logLines.size() - n, this.logLines.size()));
         writeClipboard(text);
         this.status.set("Copied " + n + " log line(s) to clipboard", OK);
         btn.setLabel("Copied!");
         Timer t = new Timer(900, (e) -> btn.setLabel("Copy"));
         t.setRepeats(false);
         t.start();
      }
   }

   private void log(String text) {
      this.log(text, SUBTLE);
   }

   private void log(String text, Color color) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.appendLog(text, color);
      } else {
         SwingUtilities.invokeLater(() -> this.appendLog(text, color));
      }

   }

   private void appendLog(String text, Color color) {
      if (text != null && !text.isBlank()) {
         String time = LOG_FMT.format(Instant.now());
         this.logLines.add(time + "  " + text);

         while(this.logLines.size() > 400) {
            this.logLines.remove(0);
         }

         StyledDocument doc = this.logPane.getStyledDocument();
         SimpleAttributeSet timeStyle = new SimpleAttributeSet();
         StyleConstants.setForeground(timeStyle, SUBTLE);
         SimpleAttributeSet msgStyle = new SimpleAttributeSet();
         StyleConstants.setForeground(msgStyle, color);

         try {
            doc.insertString(0, "\n", (AttributeSet)null);
            doc.insertString(0, text, msgStyle);
            doc.insertString(0, time + "  ", timeStyle);

            while(countLines(doc) > 400) {
               int lastNl = doc.getText(0, doc.getLength()).lastIndexOf(10, doc.getLength() - 2);
               doc.remove(lastNl + 1, doc.getLength() - (lastNl + 1));
            }
         } catch (BadLocationException var8) {
         }

         this.logPane.setCaretPosition(0);
      }
   }

   private static int countLines(StyledDocument doc) {
      return doc.getDefaultRootElement().getElementCount();
   }

   private JPanel tabPanel() {
      JPanel s = new JPanel();
      s.setOpaque(false);
      s.setLayout(new BoxLayout(s, 1));
      s.setBorder(new EmptyBorder(8, 22, 14, 22));
      return s;
   }

   private JComponent balanceLine(String loadingTitle, List<JComponent> loadingUi) {
      return this.balanceLine(loadingTitle, loadingUi, (Runnable)null);
   }

   private JComponent balanceLine(String loadingTitle, List<JComponent> loadingUi, Runnable onRefresh) {
      JLabel l = new JLabel(this.balanceText);
      l.setForeground(this.balanceText.isBlank() ? SUBTLE : TEXT);
      l.setFont(font(1, 12.0F));
      this.balanceLabels.add(l);
      JPanel group = new JPanel();
      group.setOpaque(false);
      group.setLayout(new BoxLayout(group, 0));
      JLabel dot = new JLabel("•");
      dot.setForeground(TEXT);
      dot.setFont(font(1, 12.0F));
      group.add(Box.createRigidArea(new Dimension(13, 0)));
      group.add(dot);
      group.add(Box.createRigidArea(new Dimension(13, 0)));
      group.add(new Spinner(14));
      group.add(Box.createRigidArea(new Dimension(6, 0)));
      JLabel title = new JLabel(loadingTitle);
      title.setForeground(SUBTLE);
      title.setFont(font(1, 12.0F));
      group.add(title);
      group.setVisible(false);
      loadingUi.add(group);
      JPanel left = new JPanel();
      left.setOpaque(false);
      left.setLayout(new BoxLayout(left, 0));
      left.add(l);
      left.add(group);
      JPanel wrap = new JPanel(new BorderLayout());
      wrap.setOpaque(false);
      wrap.add(left, "West");
      if (onRefresh != null) {
         wrap.add(linkLabel("Refresh", "Reload from Localts", onRefresh), "East");
      }

      wrap.setMaximumSize(new Dimension(396, 18));
      wrap.setAlignmentX(0.5F);
      return wrap;
   }

   private static void setLoadingUi(List<JComponent> ui, boolean on) {
      for(JComponent c : ui) {
         c.setVisible(on);
      }

   }

   private static JLabel linkLabel(String text, String tooltip, final Runnable onClick) {
      final JLabel l = new JLabel(text);
      l.setForeground(ACCENT);
      l.setFont(font(1, 10.0F));
      l.setCursor(Cursor.getPredefinedCursor(12));
      l.setToolTipText(tooltip);
      l.addMouseListener(new MouseAdapter() {
         public void mouseEntered(MouseEvent e) {
            l.setForeground(RefreshTokenApp.TEXT);
         }

         public void mouseExited(MouseEvent e) {
            l.setForeground(RefreshTokenApp.ACCENT);
         }

         public void mousePressed(MouseEvent e) {
            onClick.run();
         }
      });
      return l;
   }

   private JComponent searchBar(Consumer<String> onChange) {
      return this.searchBar(onChange, (f) -> {
      });
   }

   private JComponent searchBar(Consumer<String> onChange, Consumer<JTextField> fieldOut) {
      JPanel bar = new JPanel(new BorderLayout(10, 0));
      bar.setOpaque(false);
      bar.setMaximumSize(new Dimension(396, 24));
      bar.setAlignmentX(0.5F);
      JLabel lbl = new JLabel("SEARCH");
      lbl.setForeground(SUBTLE);
      lbl.setFont(font(1, 10.0F));
      bar.add(lbl, "West");
      JTextField f = new JTextField() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(RefreshTokenApp.BORDER_HI);
            g.fillRect(0, this.getHeight() - 1, this.getWidth(), 1);
         }
      };
      f.setOpaque(false);
      f.setBorder(new EmptyBorder(0, 0, 2, 0));
      f.setForeground(TEXT);
      f.setCaretColor(TEXT);
      f.setFont(font(0, 12.0F));
      Dimension fd = new Dimension(136, 22);
      f.setPreferredSize(fd);
      f.setMaximumSize(fd);
      f.setMinimumSize(fd);
      f.getDocument().addDocumentListener((SimpleDoc)() -> onChange.accept(f.getText().trim()));
      bar.add(f, "East");
      fieldOut.accept(f);
      return bar;
   }

   private JComponent label(String text) {
      JLabel l = new JLabel(text);
      l.setForeground(SUBTLE);
      l.setFont(font(1, 10.0F));
      JPanel wrap = new JPanel(new BorderLayout());
      wrap.setOpaque(false);
      wrap.add(l, "West");
      wrap.setMaximumSize(new Dimension(396, 16));
      wrap.setAlignmentX(0.5F);
      return wrap;
   }

   private JComponent makeVersionSelector() {
      JComboBox<Version> combo = new JComboBox(VERSIONS);
      combo.setUI(new BasicComboBoxUI() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(RefreshTokenApp.CARD);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
         }

         protected JButton createArrowButton() {
            JButton b = new JButton() {
               protected void paintComponent(Graphics g) {
                  Graphics2D g2 = (Graphics2D)g.create();
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  int cx = this.getWidth() / 2;
                  int cy = this.getHeight() / 2;
                  g2.setColor(RefreshTokenApp.SUBTLE);
                  g2.setStroke(new BasicStroke(1.4F, 1, 1));
                  g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                  g2.drawLine(cx + 4, cy - 2, cx, cy + 2);
                  g2.dispose();
               }
            };
            b.setBorder((Border)null);
            b.setContentAreaFilled(false);
            b.setFocusable(false);
            return b;
         }
      });
      combo.setMaximumRowCount(10);
      combo.setBackground(CARD);
      combo.setForeground(TEXT);
      combo.setFont(font(1, 13.0F));
      combo.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
      combo.setMaximumSize(new Dimension(396, 38));
      combo.setPreferredSize(new Dimension(396, 38));
      combo.setAlignmentX(0.5F);
      combo.setFocusable(false);
      combo.setRenderer(new DefaultListCellRenderer() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            super.getListCellRendererComponent(list, value, index, selected, focus);
            this.setForeground(RefreshTokenApp.TEXT);
            this.setBackground(selected ? RefreshTokenApp.CARD_HI : RefreshTokenApp.CARD);
            this.setBorder(new EmptyBorder(4, 10, 4, 10));
            return this;
         }
      });
      combo.addActionListener((e) -> {
         Version v = (Version)combo.getSelectedItem();
         if (v != null) {
            this.proxy.setVersion(v.protocol());
            String var10001 = v.label();
            this.log("Proxy protocol version set to " + var10001 + (v.protocol() == -1 ? " (follow client)" : " (" + v.protocol() + ")"));
         }

      });
      return combo;
   }

   private JComponent buildTargetBar() {
      JPanel bar = new JPanel(new BorderLayout());
      bar.setOpaque(false);
      bar.setMaximumSize(new Dimension(396, 30));
      bar.setAlignmentX(0.5F);
      JLabel lbl = new JLabel("TARGET SERVER");
      lbl.setForeground(SUBTLE);
      lbl.setFont(font(1, 10.0F));
      bar.add(lbl, "West");
      JPanel icons = new JPanel();
      icons.setOpaque(false);
      icons.setLayout(new BoxLayout(icons, 0));
      this.hypixelTarget = new TargetIcon(loadResourceIcon("/icons/hypixel_logo.png", 20), false, "Hypixel", () -> this.selectTarget("mc.hypixel.net"));
      this.donutTarget = new TargetIcon(loadResourceIcon("/icons/donut_logo.webp", 20), false, "DonutSMP", () -> this.selectTarget("donutsmp.net"));
      this.wrenchTarget = new TargetIcon(renderSvg("/icons/wrench.svg", 18), true, "Custom server", this::openCustomTarget);
      icons.add(this.hypixelTarget);
      icons.add(Box.createRigidArea(new Dimension(4, 0)));
      icons.add(this.donutTarget);
      icons.add(Box.createRigidArea(new Dimension(4, 0)));
      icons.add(this.wrenchTarget);
      bar.add(icons, "East");
      this.currentTarget = PREFS.get("target_server", "");
      if (!this.currentTarget.isBlank()) {
         this.applyTargetString(this.currentTarget);
      }

      this.updateTargetSelection();
      return bar;
   }

   private void selectTarget(String server) {
      this.currentTarget = server == null ? "" : server.trim();
      PREFS.put("target_server", this.currentTarget);
      this.applyTargetString(this.currentTarget);
      this.updateTargetSelection();
      if (this.currentTarget.isBlank()) {
         this.status.set("No target server selected", SUBTLE);
      } else {
         this.status.set("Target server: " + this.currentTarget, OK);
      }

   }

   private void updateTargetSelection() {
      boolean h = "mc.hypixel.net".equalsIgnoreCase(this.currentTarget);
      boolean d = "donutsmp.net".equalsIgnoreCase(this.currentTarget);
      this.hypixelTarget.setSelected(h);
      this.donutTarget.setSelected(d);
      this.wrenchTarget.setSelected(!h && !d && !this.currentTarget.isBlank());
   }

   private void openCustomTarget() {
      String server = askCustomServer(this.frame, this.currentTarget);
      if (server != null) {
         this.selectTarget(server);
      }

   }

   private JComponent buildAccountSearchBar() {
      return this.searchBar((q) -> {
         this.accountsQuery = q;
         this.rebuildAccountsList();
      }, (f) -> this.accountsSearchField = f);
   }

   private static BufferedImage loadResourceIcon(String path, int size) {
      try {
         InputStream in = RefreshTokenApp.class.getResourceAsStream(path);

         BufferedImage img;
         label52: {
            BufferedImage var4;
            try {
               if (in == null) {
                  img = null;
                  break label52;
               }

               img = ImageIO.read(in);
               var4 = img == null ? null : iconFit(img, size);
            } catch (Throwable var6) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (in != null) {
               in.close();
            }

            return var4;
         }

         if (in != null) {
            in.close();
         }

         return img;
      } catch (Exception var7) {
         return null;
      }
   }

   private static BufferedImage renderSvg(String path, int size) {
      try (InputStream in = RefreshTokenApp.class.getResourceAsStream(path)) {
         if (in == null) {
            return null;
         }

         SVGDocument doc = (new SVGLoader()).load(in);
         if (doc == null) {
            return null;
         }

         BufferedImage img = new BufferedImage(size, size, 2);
         Graphics2D g = img.createGraphics();
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         doc.render((JComponent)null, g, new ViewBox(0.0F, 0.0F, (float)size, (float)size));
         g.dispose();
         return img;
      } catch (Exception var9) {
         return null;
      }
   }

   private static BufferedImage cartIcon() {
      if (cartIcon == null) {
         cartIcon = renderSvg("/icons/products.svg", 17);
      }

      return cartIcon;
   }

   private static BufferedImage dotsIcon() {
      if (dotsIcon == null) {
         dotsIcon = renderSvg("/icons/three-dots.svg", 18);
      }

      return dotsIcon;
   }

   private static BufferedImage tintImage(BufferedImage src, Color color) {
      BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), 2);
      Graphics2D g = out.createGraphics();
      g.drawImage(src, 0, 0, (ImageObserver)null);
      g.setComposite(AlphaComposite.SrcIn);
      g.setColor(color);
      g.fillRect(0, 0, out.getWidth(), out.getHeight());
      g.dispose();
      return out;
   }

   private static int le(byte[] b, int off, int n) {
      int v = 0;

      for(int i = 0; i < n; ++i) {
         v |= (b[off + i] & 255) << 8 * i;
      }

      return v;
   }

   private static Animation loadAnimatedWebp(String path, int size) throws Exception {
      try (InputStream in = RefreshTokenApp.class.getResourceAsStream(path)) {
         if (in == null) {
            return null;
         }
         byte[] bytes = in.readAllBytes();
         int canvasW = 0;
         int canvasH = 0;
         List<int[]> meta = new ArrayList();
         int chunkSize;
         int payload;
         for(int pos = 12; pos + 8 <= bytes.length; pos = payload + chunkSize + (chunkSize & 1)) {
            String fourcc = new String(bytes, pos, 4, StandardCharsets.ISO_8859_1);
            chunkSize = le(bytes, pos + 4, 4);
            payload = pos + 8;
            if (fourcc.equals("VP8X")) {
               canvasW = le(bytes, payload + 4, 3) + 1;
               canvasH = le(bytes, payload + 7, 3) + 1;
            } else if (fourcc.equals("ANMF")) {
               int x = le(bytes, payload, 3) * 2;
               int y = le(bytes, payload + 3, 3) * 2;
               int dur = le(bytes, payload + 12, 3);
               int flags = bytes[payload + 15] & 255;
               meta.add(new int[]{x, y, dur, flags & 1, flags >> 1 & 1});
            }
         }

         if (canvasW <= 0 || canvasH <= 0) {
            return null;
         }

         try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
            if (!it.hasNext()) {
               return null;
            }

            ImageReader reader = (ImageReader)it.next();
            reader.setInput(iis);
            int n = Math.min(reader.getNumImages(true), meta.size());
            BufferedImage canvas = new BufferedImage(canvasW, canvasH, 2);
            Graphics2D cg = canvas.createGraphics();
            List<BufferedImage> frames = new ArrayList(n);
            int[] delays = new int[n];

            for(int i = 0; i < n; ++i) {
               BufferedImage sub = reader.read(i);
               int[] m = (int[])meta.get(i);
               int x = m[0];
               int y = m[1];
               if (m[4] == 1) {
                  cg.setComposite(AlphaComposite.Clear);
                  cg.fillRect(x, y, sub.getWidth(), sub.getHeight());
               }

               cg.setComposite(AlphaComposite.SrcOver);
               cg.drawImage(sub, x, y, (ImageObserver)null);
               frames.add(scaleTo(smoothDownscale(canvas, size), size));
               delays[i] = Math.max(m[2], 20);
               if (m[3] == 1) {
                  cg.setComposite(AlphaComposite.Clear);
                  cg.fillRect(x, y, sub.getWidth(), sub.getHeight());
               }
            }

            cg.dispose();
            return new Animation(frames, delays);
         }
      }
   }

   private static BufferedImage scaleTo(BufferedImage src, int size) {
      BufferedImage out = new BufferedImage(size, size, 2);
      Graphics2D g = out.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.drawImage(src, 0, 0, size, size, (ImageObserver)null);
      g.dispose();
      return out;
   }

   private static BufferedImage iconFit(BufferedImage src, int size) {
      double scale = Math.min((double)size / (double)src.getWidth(), (double)size / (double)src.getHeight());
      int w = Math.max(1, (int)Math.round((double)src.getWidth() * scale));
      int h = Math.max(1, (int)Math.round((double)src.getHeight() * scale));
      BufferedImage small = smoothDownscale(src, Math.max(w, h));
      BufferedImage out = new BufferedImage(size, size, 2);
      Graphics2D g = out.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.drawImage(small, (size - w) / 2, (size - h) / 2, w, h, (ImageObserver)null);
      g.dispose();
      return out;
   }

   private static BufferedImage smoothDownscale(BufferedImage src, int target) {
      int w = src.getWidth();
      int h = src.getHeight();

      BufferedImage cur;
      BufferedImage tmp = null;
      for(cur = src; w > target * 2 || h > target * 2; cur = tmp) {
         w = Math.max(target, w / 2);
         h = Math.max(target, h / 2);
         tmp = new BufferedImage(w, h, 2);
         Graphics2D g = tmp.createGraphics();
         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         g.drawImage(cur, 0, 0, w, h, (ImageObserver)null);
         g.dispose();
      }

      return cur;
   }

   private void applyTargetString(String t) {
      if (t != null && !t.isBlank()) {
         t = t.trim();
         String host = t;
         int port = 25565;
         int colon = t.indexOf(58);
         if (colon > 0 && colon == t.lastIndexOf(58)) {
            try {
               port = Integer.parseInt(t.substring(colon + 1).trim());
               host = t.substring(0, colon).trim();
            } catch (NumberFormatException var6) {
            }
         }

         this.proxy.setTarget(host, port);
      } else {
         this.proxy.setTarget((String)null, 25565);
      }
   }

   private void addAccountFromClipboard() {
      final String token = readClipboard();
      if (token.isEmpty()) {
         this.status.set("Clipboard is empty — copy a refresh token first", SUBTLE);
      } else {
         this.status.set("Adding account from clipboard…", SUBTLE);
         this.log("Adding account from clipboard — authenticating…");
         (new SwingWorker<AccountAuthenticator.AuthResult, Void>() {
            {
               Objects.requireNonNull(RefreshTokenApp.this);
            }

            protected AccountAuthenticator.AuthResult doInBackground() throws Exception {
               return AccountAuthenticator.authenticate(token, (step) -> RefreshTokenApp.this.log("  clipboard: " + step));
            }

            protected void done() {
               try {
                  AccountAuthenticator.AuthResult auth = (AccountAuthenticator.AuthResult)this.get();
                  RefreshTokenApp.this.addAccount(auth, "Clipboard");
                  String name = auth.session().getMcProfile().getName();
                  RefreshTokenApp.this.status.set("Added " + name, RefreshTokenApp.OK);
                  RefreshTokenApp.this.log("Added " + name + " from clipboard", RefreshTokenApp.OK);
               } catch (Exception var4) {
                  Throwable cause = (Throwable)(var4 instanceof ExecutionException && var4.getCause() != null ? var4.getCause() : var4);
                  String reason = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
                  RefreshTokenApp.this.log("Clipboard import failed: " + reason, RefreshTokenApp.ERROR);
                  if (RefreshTokenApp.isAuthFailure(cause)) {
                     RefreshTokenApp.this.invalidTokens.add(token);
                     RefreshTokenApp.this.status.set("Clipboard token is invalid or expired", RefreshTokenApp.ERROR);
                  } else {
                     RefreshTokenApp.this.storeError(var4);
                  }
               }

            }
         }).execute();
      }
   }

   private void addAccount(AccountAuthenticator.AuthResult auth, String source) {
      StepFullJavaSession.FullJavaSession session = auth.session();
      String refreshToken = auth.refreshToken();
      long refreshExpire = auth.refreshExpireMs();
      UUID uuid = session.getMcProfile().getId();
      String name = session.getMcProfile().getName();

      for(AccountRow existing : this.rows) {
         if (existing.uuid.equals(uuid)) {
            existing.session = session;
            existing.refreshToken = refreshToken;
            existing.refreshExpire = refreshExpire;
            existing.invalid = false;
            if (source != null && !source.isBlank()) {
               existing.source = source;
            }

            this.persistAccounts();
            this.activate(existing);
            this.status.set("Refreshed " + name, OK);
            return;
         }
      }

      AccountRow row = this.addRow(session, refreshToken, refreshExpire, source, System.currentTimeMillis());
      this.persistAccounts();
      this.activate(row);
      this.fitWindow();
   }

   private AccountRow addRow(StepFullJavaSession.FullJavaSession session, String refreshToken, long refreshExpire, String source, long importedAt) {
      AccountRow row = new AccountRow(session.getMcProfile().getName(), session.getMcProfile().getId(), session);
      row.refreshToken = refreshToken;
      row.refreshExpire = refreshExpire;
      row.importedAt = importedAt;
      row.source = source;
      row.onActivate = () -> this.activate(row);
      row.onRemove = () -> this.removeAccount(row);
      row.onRefresh = () -> this.refreshTokens(row);
      row.onSelect = this::updateAccountSelection;
      this.rows.add(row);
      loadPlayerHead(session.getMcProfile().getSkinUrl(), row);
      this.rebuildAccountsList();
      return row;
   }

   private void rebuildAccountsList() {
      this.accountsPanel.removeAll();
      String q = this.accountsQuery.toLowerCase();
      List<AccountRow> shown = new ArrayList();

      for(AccountRow r : this.rows) {
         if (q.isEmpty() || r.name.toLowerCase().contains(q)) {
            shown.add(r);
         }
      }

      this.sortRows(shown);

      for(int i = 0; i < shown.size(); ++i) {
         this.accountsPanel.add((Component)shown.get(i));
         if (i < shown.size() - 1) {
            this.accountsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
         }
      }

      if (shown.isEmpty() && !this.rows.isEmpty()) {
         this.accountsPanel.add(this.emptyNote("No matching accounts"));
      }

      int visible = Math.max(1, Math.min(shown.size(), 7));
      int h = visible * 44 + (visible - 1) * 8 + 4;
      this.accountsScroll.setPreferredSize(new Dimension(396, h));
      this.accountsScroll.setMaximumSize(new Dimension(396, Integer.MAX_VALUE));
      this.accountsScroll.setVisible(!this.rows.isEmpty());
      this.accountsScroll.revalidate();
      this.accountsPanel.revalidate();
      this.accountsPanel.repaint();
   }

   private void sortRows(List<AccountRow> shown) {
      Comparator<AccountRow> var10000;
      switch (this.accountSort.ordinal()) {
         case 0 -> var10000 = Comparator.comparingLong((AccountRow r) -> r.session.getMcProfile().getMcToken().getExpireTimeMs()).reversed();
         case 1 -> var10000 = Comparator.comparingLong((AccountRow r) -> r.importedAt).reversed();
         case 2 -> var10000 = Comparator.comparing((AccountRow r) -> {
   String s = r.source == null ? "" : r.source.trim();
   return s.isEmpty() ? "\uffff" : s.toLowerCase();
});
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      Comparator<AccountRow> c = var10000;
      shown.sort(c);
   }

   private void setSortMode(SortMode mode) {
      this.accountSort = mode;
      PREFS.put("account_sort", mode.name());
      if (this.sortButton != null) {
         this.sortButton.setText("Sort: " + mode.label);
      }

      this.rebuildAccountsList();
   }

   private void cycleSortMode() {
      SortMode[] modes = RefreshTokenApp.SortMode.values();
      this.setSortMode(modes[(this.accountSort.ordinal() + 1) % modes.length]);
   }

   private void revealAccount(String name) {
      AccountRow target = null;

      for(AccountRow r : this.rows) {
         if (r.name.equalsIgnoreCase(name)) {
            target = r;
            break;
         }
      }

      if (target != null) {
         AccountRow finalTarget = target;
         if (!this.accountsQuery.isEmpty() && !name.toLowerCase().contains(this.accountsQuery.toLowerCase())) {
            if (this.accountsSearchField != null) {
               this.accountsSearchField.setText("");
            } else {
               this.accountsQuery = "";
               this.rebuildAccountsList();
            }
         }

         this.showTab("accounts");
         SwingUtilities.invokeLater(() -> {
            finalTarget.scrollRectToVisible(new Rectangle(0, 0, finalTarget.getWidth(), finalTarget.getHeight()));
            finalTarget.pulse();
         });
      }
   }

   private void removeAccount(AccountRow row) {
      boolean wasActive = row.active;
      this.rows.remove(row);
      this.persistAccounts();
      this.rebuildAccountsList();
      if (wasActive) {
         if (this.rows.isEmpty()) {
            this.proxy.setActiveAccount((StepFullJavaSession.FullJavaSession)null, (Supplier)null);
         } else {
            this.activate((AccountRow)this.rows.get(0));
         }
      }

      this.status.set("Removed " + row.name, SUBTLE);
      this.fitWindow();
   }

   private void persistAccounts() {
      JsonArray arr = new JsonArray();

      for(AccountRow r : this.rows) {
         StepMCProfile.MCProfile profile = r.session.getMcProfile();
         StepMCToken.MCToken mc = profile.getMcToken();
         JsonObject o = new JsonObject();
         o.addProperty("name", profile.getName());
         o.addProperty("id", profile.getId().toString());
         o.addProperty("skinUrl", profile.getSkinUrl() == null ? "" : profile.getSkinUrl());
         o.addProperty("mcToken", mc.getAccessToken());
         o.addProperty("mcExpire", (Number)mc.getExpireTimeMs());
         o.addProperty("refreshToken", r.refreshToken == null ? "" : r.refreshToken);
         o.addProperty("refreshExpire", (Number)r.refreshExpire);
         o.addProperty("importedAt", (Number)r.importedAt);
         o.addProperty("source", r.source == null ? "" : r.source);
         arr.add((JsonElement)o);
      }

      try {
         Files.createDirectories(ACCOUNTS_FILE.getParent());
         Files.writeString(ACCOUNTS_FILE, arr.toString(), StandardCharsets.UTF_8);
         this.log("Saved " + this.rows.size() + " account(s) to disk");
      } catch (Exception ex) {
         this.status.set("Couldn't save accounts for next time", SUBTLE);
         this.log("Account save failed: " + String.valueOf(ex), ERROR);
      }

   }

   private void loadSavedAccounts() {
      if (Files.exists(ACCOUNTS_FILE, new LinkOption[0])) {
         try {
            JsonArray arr = JsonParser.parseString(Files.readString(ACCOUNTS_FILE, StandardCharsets.UTF_8)).getAsJsonArray();
            int index = 0;

            for(JsonElement el : arr) {
               try {
                  JsonObject o = el.getAsJsonObject();
                  StepFullJavaSession.FullJavaSession session = AccountAuthenticator.minimalSession(UUID.fromString(o.get("id").getAsString()), str(o, "name"), str(o, "skinUrl"), str(o, "mcToken"), o.has("mcExpire") ? o.get("mcExpire").getAsLong() : 0L);
                  long importedAt = o.has("importedAt") ? o.get("importedAt").getAsLong() : (long)index;
                  this.addRow(session, str(o, "refreshToken"), o.has("refreshExpire") ? o.get("refreshExpire").getAsLong() : 0L, str(o, "source"), importedAt);
               } catch (Exception var9) {
               }

               ++index;
            }
         } catch (Exception var10) {
         }

         int var10001 = this.rows.size();
         this.log("Restored " + var10001 + " saved account(s) from " + String.valueOf(ACCOUNTS_FILE.getFileName()));
         if (!this.rows.isEmpty()) {
            this.activate((AccountRow)this.rows.get(0));
         }

      }
   }

   private static String str(JsonObject o, String key) {
      return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsString() : null;
   }

   private boolean isAccountImported(String name) {
      for(AccountRow r : this.rows) {
         if (r.name.equalsIgnoreCase(name)) {
            return true;
         }
      }

      return false;
   }

   private void activate(final AccountRow row) {
      this.proxy.setActiveAccount(row.session, () -> this.refreshAccount(row));

      for(AccountRow r : this.rows) {
         r.setActive(r == row);
         r.selected = false;
      }

      this.updateAccountSelection();
      if (isExpired(row) && row.refreshToken != null && !row.refreshToken.isBlank()) {
         this.status.set("Refreshing " + row.name + "…", SUBTLE);
         (new SwingWorker<Boolean, Void>() {
            {
               Objects.requireNonNull(RefreshTokenApp.this);
            }

            protected Boolean doInBackground() {
               return RefreshTokenApp.this.refreshAccount(row) != null;
            }

            protected void done() {
               row.repaint();

               boolean ok;
               try {
                  ok = (Boolean)this.get();
               } catch (Exception var3) {
                  ok = false;
               }

               if (ok) {
                  if (row.active) {
                     RefreshTokenApp.this.proxy.setActiveAccount(row.session, () -> RefreshTokenApp.this.refreshAccount(row));
                  }

                  RefreshTokenApp.this.announceActive(row);
               } else if (row.invalid) {
                  RefreshTokenApp.this.status.set(row.name + " — token is invalid, re-import the account", RefreshTokenApp.ERROR);
               } else {
                  RefreshTokenApp.this.status.set("Couldn't refresh " + row.name + " — try again in a moment", RefreshTokenApp.ERROR);
               }

            }
         }).execute();
      } else {
         this.announceActive(row);
      }

   }

   private void announceActive(AccountRow row) {
      if (this.currentTarget.isBlank()) {
         this.status.set("Active: " + row.name + " — now pick a target server above", SUBTLE);
      } else {
         this.status.set("Active: " + row.name + " → " + this.currentTarget + "  —  connect MC to 127.0.0.1", OK);
      }

   }

   private static boolean isExpired(AccountRow row) {
      return row.session.getMcProfile().getMcToken().getExpireTimeMs() <= System.currentTimeMillis();
   }

   private void updateAccountSelection() {
      int n = 0;

      for(AccountRow r : this.rows) {
         if (r.selected) {
            ++n;
         }
      }

      if (this.accountsDeleteBtn != null) {
         this.accountsDeleteBtn.setVisible(n >= 2);
      }

   }

   private void deleteSelectedAccounts() {
      List<AccountRow> remove = new ArrayList();
      boolean removedActive = false;

      for(AccountRow r : this.rows) {
         if (r.selected) {
            remove.add(r);
            removedActive |= r.active;
         }
      }

      if (remove.size() >= 2) {
         this.rows.removeAll(remove);
         this.persistAccounts();
         this.rebuildAccountsList();
         this.updateAccountSelection();
         if (removedActive) {
            if (this.rows.isEmpty()) {
               this.proxy.setActiveAccount((StepFullJavaSession.FullJavaSession)null, (Supplier)null);
            } else {
               this.activate((AccountRow)this.rows.get(0));
            }
         }

         this.status.set("Removed " + remove.size() + " accounts", SUBTLE);
         this.fitWindow();
      }
   }

   private StepFullJavaSession.FullJavaSession refreshAccount(AccountRow row) {
      if (row.refreshToken != null && !row.refreshToken.isBlank()) {
         try {
            AccountAuthenticator.AuthResult auth = AccountAuthenticator.authenticate(row.refreshToken, (step) -> this.log("  " + row.name + " (refresh): " + step));
            if (auth.refreshToken() != null) {
               row.refreshToken = auth.refreshToken();
               row.refreshExpire = auth.refreshExpireMs();
            }

            row.session = auth.session();
            row.invalid = false;
            SwingUtilities.invokeLater(this::persistAccounts);
            return auth.session();
         } catch (Exception var4) {
            String reason = var4.getMessage() != null ? var4.getMessage() : var4.getClass().getSimpleName();
            if (isAuthFailure(var4)) {
               row.invalid = true;
               if (row.refreshToken != null) {
                  this.invalidTokens.add(row.refreshToken);
               }
            }

            this.log("Failed: " + reason + " (" + row.name + " refresh)", ERROR);
            return null;
         }
      } else {
         return null;
      }
   }

   private void refreshTokens(final AccountRow row) {
      this.status.set("Refreshing tokens for " + row.name + "…", SUBTLE);
      (new SwingWorker<StepFullJavaSession.FullJavaSession, Void>() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         protected StepFullJavaSession.FullJavaSession doInBackground() {
            return RefreshTokenApp.this.refreshAccount(row);
         }

         protected void done() {
            try {
               if (this.get() == null) {
                  row.repaint();
                  RefreshTokenApp.this.status.set(row.invalid ? row.name + " — token is invalid, re-import the account" : "Couldn't refresh " + row.name, RefreshTokenApp.ERROR);
                  return;
               }

               if (row.active) {
                  RefreshTokenApp.this.proxy.setActiveAccount(row.session, () -> RefreshTokenApp.this.refreshAccount(row));
               }

               row.repaint();
               RefreshTokenApp.this.status.set("Refreshed tokens for " + row.name, RefreshTokenApp.OK);
            } catch (Exception ex) {
               RefreshTokenApp.this.storeError(ex);
            }

         }
      }).execute();
   }

   private JScrollPane scrollOf(JComponent view, int height) {
      JPanel holder = new JPanel(new BorderLayout());
      holder.setOpaque(false);
      holder.setBorder(new EmptyBorder(2, 0, 2, 4));
      holder.add(view, "North");
      JScrollPane sp = new JScrollPane(holder, 20, 31);
      sp.setBorder((Border)null);
      sp.setOpaque(false);
      sp.getViewport().setOpaque(false);
      sp.setAlignmentX(0.5F);
      sp.setPreferredSize(new Dimension(396, height));
      sp.setMaximumSize(new Dimension(396, height));
      JScrollBar vbar = sp.getVerticalScrollBar();
      vbar.setUI(new SlimScrollBarUI());
      vbar.setOpaque(false);
      vbar.setUnitIncrement(14);
      vbar.setPreferredSize(new Dimension(9, 0));
      return sp;
   }

   private void onConnect() {
      final String key = this.apiKeyField.getKey();
      if (!key.isEmpty()) {
         if (!key.equals(this.lastConnectedKey)) {
            PREFS.put("localts_api_key", key);
            this.api.setApiKey(key);
            String var10001 = key.substring(Math.max(0, key.length() - 4));
            this.log("Connecting to Localts (key …" + var10001 + ")");
            this.status.set("Connecting to Localts…", SUBTLE);
            (new SwingWorker<LocaltsApi.Me, Void>() {
               {
                  Objects.requireNonNull(RefreshTokenApp.this);
               }

               protected LocaltsApi.Me doInBackground() throws Exception {
                  return RefreshTokenApp.this.api.getMe();
               }

               protected void done() {
                  try {
                     LocaltsApi.Me me = (LocaltsApi.Me)this.get();
                     RefreshTokenApp.this.lastConnectedKey = key;
                     RefreshTokenApp.this.productsSig = null;
                     RefreshTokenApp.this.ordersSig = null;
                     RefreshTokenApp.this.productsFetchedAt = 0L;
                     RefreshTokenApp.this.ordersFetchedAt = 0L;
                     RefreshTokenApp.this.orderCache.clear();
                     RefreshTokenApp.this.showBalance(me);
                     RefreshTokenApp.this.status.set("Connected as " + me.username(), RefreshTokenApp.OK);
                     RefreshTokenApp.this.loadStoreTab(RefreshTokenApp.this.currentTab);
                  } catch (Exception ex) {
                     RefreshTokenApp.this.lastConnectedKey = null;
                     RefreshTokenApp.this.storeError(ex);
                  }

               }
            }).execute();
         }
      }
   }

   private void showBalance(LocaltsApi.Me me) {
      String var10001 = me.username();
      this.balanceText = var10001 + "    •    " + me.balance() + " credits";
      long var4 = me.balance();
      this.log("Balance: " + var4 + " credits (" + me.username() + ")");

      for(JLabel l : this.balanceLabels) {
         l.setText(this.balanceText);
         l.setForeground(TEXT);
      }

   }

   private void refreshBalance() {
      (new SwingWorker<LocaltsApi.Me, Void>() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         protected LocaltsApi.Me doInBackground() throws Exception {
            return RefreshTokenApp.this.api.getMe();
         }

         protected void done() {
            try {
               RefreshTokenApp.this.showBalance((LocaltsApi.Me)this.get());
            } catch (Exception var2) {
            }

         }
      }).execute();
   }

   private void loadProducts() {
      if (this.productsSig == null) {
         setLoadingUi(this.productsLoadingUi, true);
      }

      (new SwingWorker<List<LocaltsApi.Product>, Void>() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         protected List<LocaltsApi.Product> doInBackground() throws Exception {
            return RefreshTokenApp.this.api.getProducts();
         }

         protected void done() {
            RefreshTokenApp.setLoadingUi(RefreshTokenApp.this.productsLoadingUi, false);

            try {
               List<LocaltsApi.Product> all = (List)this.get();
               RefreshTokenApp.this.productsFetchedAt = System.currentTimeMillis();
               RefreshTokenApp.this.productsById.clear();

               for(LocaltsApi.Product p : all) {
                  RefreshTokenApp.this.productsById.put(p.id(), p);
               }

               List<LocaltsApi.Product> shown = new ArrayList();

               for(LocaltsApi.Product p : all) {
                  if (p.hasTag("Refresh token")) {
                     shown.add(p);
                  }
               }

               String sig = RefreshTokenApp.productsSignature(shown);
               if (!sig.equals(RefreshTokenApp.this.productsSig)) {
                  RefreshTokenApp.this.productsSig = sig;
                  RefreshTokenApp.this.allProducts = shown;
                  RefreshTokenApp var10000 = RefreshTokenApp.this;
                  int var10001 = shown.size();
                  var10000.log("Products updated — " + var10001 + " refresh-token product(s) (" + all.size() + " total in store)");
                  RefreshTokenApp.this.renderProducts();
               }

               if (RefreshTokenApp.this.ordersAfterProducts) {
                  RefreshTokenApp.this.ordersAfterProducts = false;
                  RefreshTokenApp.this.loadOrders();
               }
            } catch (Exception ex) {
               RefreshTokenApp.this.ordersAfterProducts = false;
               RefreshTokenApp.this.storeError(ex);
            }

         }
      }).execute();
   }

   private static String productsSignature(List<LocaltsApi.Product> products) {
      StringBuilder sb = new StringBuilder();

      for(LocaltsApi.Product p : products) {
         sb.append(p.id()).append(':').append(p.stock()).append(':').append(p.priceInCredits()).append('|');
      }

      return sb.toString();
   }

   private void loadOrders() {
      if (this.ordersSig == null) {
         setLoadingUi(this.ordersLoadingUi, true);
      }

      (new SwingWorker<List<OrderListing>, Void>() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         protected List<OrderListing> doInBackground() throws Exception {
            List<OrderListing> listings = new ArrayList();
            LocaltsApi.OrdersPage page = RefreshTokenApp.this.api.getOrders(0, 25);

            for(LocaltsApi.OrderSummary o : page.orders()) {
               LocaltsApi.Product product = (LocaltsApi.Product)RefreshTokenApp.this.productsById.get(o.productId());
               if (product != null && product.hasTag("Refresh token")) {
                  listings.add(new OrderListing(o, product));
               }
            }

            return listings;
         }

         protected void done() {
            try {
               List<OrderListing> listings = (List)this.get();
               RefreshTokenApp.this.ordersFetchedAt = System.currentTimeMillis();
               String sig = RefreshTokenApp.ordersSignature(listings);
               if (!sig.equals(RefreshTokenApp.this.ordersSig)) {
                  RefreshTokenApp.this.ordersSig = sig;
                  RefreshTokenApp.this.allOrders = listings;
                  RefreshTokenApp.this.log("Orders updated — " + listings.size() + " order(s) listed");
                  RefreshTokenApp.this.renderOrders();
               }
            } catch (Exception ex) {
               RefreshTokenApp.this.storeError(ex);
            } finally {
               RefreshTokenApp.setLoadingUi(RefreshTokenApp.this.ordersLoadingUi, false);
            }

         }
      }).execute();
   }

   private static String ordersSignature(List<OrderListing> listings) {
      StringBuilder sb = new StringBuilder();

      for(OrderListing l : listings) {
         sb.append(l.summary().id()).append('|');
      }

      return sb.toString();
   }

   private void renderOrders() {
      this.ordersPanel.removeAll();
      String q = this.ordersQuery.toLowerCase();
      int shown = 0;

      for(OrderListing l : this.allOrders) {
         if (!q.isEmpty()) {
            String id = l.summary().id().toLowerCase();
            String nm = l.product() != null ? l.product().name().toLowerCase() : "";
            if (!id.contains(q) && !nm.contains(q)) {
               continue;
            }
         }

         this.ordersPanel.add(new OrderRow(l.summary(), l.product(), this.api, this::loadOrderDetails, this::importItem, this::isAccountImported, this::isTokenInvalid, this::revealAccount));
         this.ordersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
         ++shown;
      }

      if (shown == 0) {
         this.ordersPanel.add(this.emptyNote(this.allOrders.isEmpty() ? "No orders yet" : "No matching orders"));
      }

      this.ordersPanel.revalidate();
      this.ordersPanel.repaint();
   }

   private void renderProducts() {
      this.productsPanel.removeAll();
      String q = this.productsQuery.toLowerCase();
      int shown = 0;

      for(LocaltsApi.Product p : this.allProducts) {
         if (q.isEmpty() || p.name().toLowerCase().contains(q) || p.id().toLowerCase().contains(q)) {
            this.productsPanel.add(new ProductRow(p, this.api, this::buyProduct));
            this.productsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            ++shown;
         }
      }

      if (shown == 0) {
         this.productsPanel.add(this.emptyNote(this.allProducts.isEmpty() ? "No \"Refresh token\" products available" : "No matching products"));
      }

      this.productsPanel.revalidate();
      this.productsPanel.repaint();
   }

   private void refreshOrders() {
      if (this.lastConnectedKey == null) {
         this.status.set("Connect your API key first (Settings tab)", SUBTLE);
      } else {
         this.ordersFetchedAt = 0L;
         this.loadStoreTab("orders");
      }
   }

   private void loadStoreTab(String tab) {
      if (this.lastConnectedKey != null) {
         if (System.currentTimeMillis() >= this.pollCooldownUntil) {
            long now = System.currentTimeMillis();
            if ("products".equals(tab)) {
               if (now - this.productsFetchedAt > 60000L) {
                  this.loadProducts();
               }
            } else if ("orders".equals(tab) && now - this.ordersFetchedAt > 60000L) {
               if (this.productsById.isEmpty()) {
                  this.ordersAfterProducts = true;
                  this.loadProducts();
               } else {
                  this.loadOrders();
               }
            }

         }
      }
   }

   private JComponent emptyNote(String text) {
      JLabel l = new JLabel(text);
      l.setForeground(SUBTLE);
      l.setFont(font(1, 12.0F));
      l.setBorder(new EmptyBorder(10, 4, 10, 4));
      l.setAlignmentX(0.0F);
      return l;
   }

   private static int discountPercentFor(int amount, LocaltsApi.Product p) {
      int pct = 0;
      int bestThreshold = 0;

      for(Map.Entry<Integer, Integer> e : p.quantityDiscounts().entrySet()) {
         if (amount >= (Integer)e.getKey() && (Integer)e.getKey() > bestThreshold) {
            bestThreshold = (Integer)e.getKey();
            pct = (Integer)e.getValue();
         }
      }

      return pct;
   }

   private static int askBuyAmount(Window owner, LocaltsApi.Product p) {
      long unit = p.priceInCredits();
      int max = (int)Math.max(1L, Math.min(p.stock(), 999L));
      int[] amount = new int[]{1};
      int[] result = new int[]{0};
      JDialog dialog = new JDialog(owner, ModalityType.APPLICATION_MODAL);
      dialog.setUndecorated(true);
      JPanel content = new JPanel() {
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0.0F, 0.0F, RefreshTokenApp.BG_TOP, 0.0F, (float)this.getHeight(), RefreshTokenApp.BG_BOTTOM));
            g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 16, 16);
            g2.setColor(RefreshTokenApp.BORDER);
            g2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 16, 16);
            g2.dispose();
         }
      };
      content.setOpaque(false);
      content.setLayout(new BoxLayout(content, 1));
      content.setBorder(new EmptyBorder(16, 20, 14, 20));
      JLabel t = new JLabel("Confirm purchase");
      t.setForeground(TEXT);
      t.setFont(font(1, 14.0F));
      t.setAlignmentX(0.0F);
      content.add(t);
      content.add(Box.createRigidArea(new Dimension(0, 10)));
      JLabel m = new JLabel("<html><body style='width:300px'>Buy “" + escapeHtml(p.name()) + "”</body></html>");
      m.setForeground(new Color(194, 199, 206));
      m.setFont(font(1, 13.0F));
      m.setAlignmentX(0.0F);
      content.add(m);
      content.add(Box.createRigidArea(new Dimension(0, 14)));
      JLabel qLabel = new JLabel("QUANTITY  (" + p.stock() + " in stock)");
      qLabel.setForeground(SUBTLE);
      qLabel.setFont(font(1, 10.0F));
      qLabel.setAlignmentX(0.0F);
      content.add(qLabel);
      content.add(Box.createRigidArea(new Dimension(0, 6)));
      JLabel value = new JLabel("1", 0);
      value.setForeground(TEXT);
      value.setFont(font(1, 14.0F));
      value.setPreferredSize(new Dimension(52, 32));
      value.setMaximumSize(new Dimension(52, 32));
      JLabel total = new JLabel();
      total.setForeground(TEXT);
      total.setFont(font(1, 13.0F));
      total.setAlignmentX(0.0F);
      Runnable update = () -> {
         value.setText(String.valueOf(amount[0]));
         long base = unit * (long)amount[0];
         int pct = discountPercentFor(amount[0], p);
         long tot = base * (long)(100 - pct) / 100L;
         if (pct > 0) {
            total.setText("<html><span style='color:#8A929D'><s>" + base + "</s></span>  <span style='color:#E6E8EB'>" + tot + " credits</span>  <span style='color:#8A929D'>-" + pct + "%</span></html>");
         } else {
            total.setText(tot + " credits");
         }

      };
      FlatButton minus = new FlatButton("−", 36, 32, () -> {
         if (amount[0] > 1) {
            int var10002 = amount[0]--;
            update.run();
         }

      });
      FlatButton plus = new FlatButton("+", 36, 32, () -> {
         if (amount[0] < max) {
            int var10002 = amount[0]++;
            update.run();
         }

      });
      JPanel stepper = new JPanel();
      stepper.setOpaque(false);
      stepper.setLayout(new BoxLayout(stepper, 0));
      stepper.setAlignmentX(0.0F);
      stepper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
      stepper.add(minus);
      stepper.add(Box.createRigidArea(new Dimension(6, 0)));
      stepper.add(value);
      stepper.add(Box.createRigidArea(new Dimension(6, 0)));
      stepper.add(plus);
      stepper.add(Box.createHorizontalGlue());
      content.add(stepper);
      content.add(Box.createRigidArea(new Dimension(0, 10)));
      content.add(total);
      update.run();
      content.add(Box.createRigidArea(new Dimension(0, 16)));
      JPanel btns = new JPanel();
      btns.setOpaque(false);
      btns.setLayout(new BoxLayout(btns, 0));
      btns.setAlignmentX(0.0F);
      btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
      FlatButton cancel = new FlatButton("Cancel", 88, 32, () -> {
         result[0] = 0;
         dialog.dispose();
      });
      FlatButton buy = (new FlatButton("Buy", 88, 32, () -> {
         result[0] = amount[0];
         dialog.dispose();
      })).primary();
      btns.add(Box.createHorizontalGlue());
      btns.add(cancel);
      btns.add(Box.createRigidArea(new Dimension(8, 0)));
      btns.add(buy);
      content.add(btns);
      dialog.setContentPane(content);
      dialog.pack();
      int dw = 340;
      int dh = dialog.getHeight();
      dialog.setSize(dw, dh);
      dialog.setShape(new RoundRectangle2D.Double((double)0.0F, (double)0.0F, (double)dw, (double)dh, (double)16.0F, (double)16.0F));
      dialog.setLocationRelativeTo(owner);
      JRootPane rp = dialog.getRootPane();
      rp.registerKeyboardAction((e) -> {
         result[0] = 0;
         dialog.dispose();
      }, KeyStroke.getKeyStroke("ESCAPE"), 2);
      rp.registerKeyboardAction((e) -> {
         result[0] = amount[0];
         dialog.dispose();
      }, KeyStroke.getKeyStroke("ENTER"), 2);
      dialog.setVisible(true);
      return result[0];
   }

   private static String escapeHtml(String s) {
      return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
   }

   private static String askCustomServer(Window owner, String current) {
      JDialog dialog = new JDialog(owner, ModalityType.APPLICATION_MODAL);
      dialog.setUndecorated(true);
      String[] result = new String[]{null};
      JPanel content = new JPanel() {
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0.0F, 0.0F, RefreshTokenApp.BG_TOP, 0.0F, (float)this.getHeight(), RefreshTokenApp.BG_BOTTOM));
            g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 16, 16);
            g2.setColor(RefreshTokenApp.BORDER);
            g2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 16, 16);
            g2.dispose();
         }
      };
      content.setOpaque(false);
      content.setLayout(new BoxLayout(content, 1));
      content.setBorder(new EmptyBorder(16, 20, 14, 20));
      JLabel t = new JLabel("Custom target server");
      t.setForeground(TEXT);
      t.setFont(font(1, 14.0F));
      t.setAlignmentX(0.0F);
      content.add(t);
      content.add(Box.createRigidArea(new Dimension(0, 10)));
      JPanel fieldWrap = new JPanel(new BorderLayout()) {
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(RefreshTokenApp.CARD);
            g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
            g2.setColor(RefreshTokenApp.BORDER_HI);
            g2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
         }
      };
      fieldWrap.setOpaque(false);
      fieldWrap.setBorder(new EmptyBorder(0, 12, 0, 12));
      Dimension fd = new Dimension(300, 38);
      fieldWrap.setPreferredSize(fd);
      fieldWrap.setMaximumSize(fd);
      fieldWrap.setAlignmentX(0.0F);
      JTextField field = new JTextField(current == null ? "" : current);
      field.setOpaque(false);
      field.setBorder((Border)null);
      field.setForeground(TEXT);
      field.setCaretColor(TEXT);
      field.setFont(font(1, 13.0F));
      field.putClientProperty("JTextField.placeholderText", "play.example.net  (or host:port)");
      fieldWrap.add(field, "Center");
      content.add(fieldWrap);
      content.add(Box.createRigidArea(new Dimension(0, 16)));
      JPanel btns = new JPanel();
      btns.setOpaque(false);
      btns.setLayout(new BoxLayout(btns, 0));
      btns.setAlignmentX(0.0F);
      btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
      FlatButton cancel = new FlatButton("Cancel", 88, 32, () -> {
         result[0] = null;
         dialog.dispose();
      });
      FlatButton save = (new FlatButton("Save", 88, 32, () -> {
         result[0] = field.getText().trim();
         dialog.dispose();
      })).primary();
      btns.add(Box.createHorizontalGlue());
      btns.add(cancel);
      btns.add(Box.createRigidArea(new Dimension(8, 0)));
      btns.add(save);
      content.add(btns);
      dialog.setContentPane(content);
      dialog.pack();
      int dw = 340;
      int dh = dialog.getHeight();
      dialog.setSize(dw, dh);
      dialog.setShape(new RoundRectangle2D.Double((double)0.0F, (double)0.0F, (double)dw, (double)dh, (double)16.0F, (double)16.0F));
      dialog.setLocationRelativeTo(owner);
      JRootPane rp = dialog.getRootPane();
      rp.registerKeyboardAction((e) -> {
         result[0] = null;
         dialog.dispose();
      }, KeyStroke.getKeyStroke("ESCAPE"), 2);
      rp.registerKeyboardAction((e) -> {
         result[0] = field.getText().trim();
         dialog.dispose();
      }, KeyStroke.getKeyStroke("ENTER"), 2);
      dialog.setVisible(true);
      return result[0];
   }

   private void buyProduct(final LocaltsApi.Product p) {
      final int amount = askBuyAmount(this.frame, p);
      if (amount > 0) {
         this.log("Purchase request: " + amount + "× " + p.name() + " (product " + p.id() + ", " + discountedTotal(amount, p) + " credits)");
         this.status.set("Purchasing " + amount + "× " + p.name() + "…", SUBTLE);
         (new SwingWorker<String, Void>() {
            {
               Objects.requireNonNull(RefreshTokenApp.this);
            }

            protected String doInBackground() throws Exception {
               return RefreshTokenApp.this.api.purchase(p.id(), amount);
            }

            protected void done() {
               try {
                  String orderId = (String)this.get();
                  RefreshTokenApp.this.log("Purchase accepted — order " + orderId, RefreshTokenApp.OK);
                  RefreshTokenApp.this.status.set("Purchased " + amount + "× " + p.name() + " — checking accounts…", RefreshTokenApp.OK);
                  RefreshTokenApp.this.refreshBalance();
                  RefreshTokenApp.this.watchOrder(orderId, amount, p);
               } catch (Exception ex) {
                  RefreshTokenApp.this.storeError(ex);
               }

            }
         }).execute();
      }
   }

   private void watchOrder(final String orderId, final int amount, final LocaltsApi.Product p) {
      (new SwingWorker<LocaltsApi.Order, Void>() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         protected LocaltsApi.Order doInBackground() throws Exception {
            for(int i = 0; i < 9; ++i) {
               try {
                  LocaltsApi.Order o = RefreshTokenApp.this.api.getOrder(orderId);
                  if (o.isPackaged()) {
                     return o;
                  }
               } catch (Exception e) {
                  if (RefreshTokenApp.isRateLimited(e)) {
                     RefreshTokenApp.this.pollCooldownUntil = System.currentTimeMillis() + 60000L;
                     return null;
                  }
               }

               Thread.sleep(10000L);
            }

            return null;
         }

         protected void done() {
            try {
               LocaltsApi.Order o = (LocaltsApi.Order)this.get();
               if (o == null) {
                  if (System.currentTimeMillis() >= RefreshTokenApp.this.pollCooldownUntil) {
                     RefreshTokenApp.this.loadOrders();
                  }

                  return;
               }

               int valid = o.items().size();
               int invalid = amount - valid;
               RefreshTokenApp.this.log("Order " + RefreshTokenApp.shortId(orderId) + " packaged — " + valid + " of " + amount + " account(s) delivered");
               if (invalid > 0) {
                  long refund = RefreshTokenApp.discountedTotal(amount, p) * (long)invalid / (long)amount;
                  String id = RefreshTokenApp.shortId(orderId);
                  if (valid == 0) {
                     RefreshTokenApp.this.status.set("Order #" + id + " — no valid accounts, refunded " + refund + " credits", RefreshTokenApp.ERROR);
                  } else {
                     RefreshTokenApp.this.status.set("Order #" + id + " — " + invalid + " invalid, refunded " + refund + " credits", RefreshTokenApp.ERROR);
                  }
               } else {
                  RefreshTokenApp.this.status.set("Order #" + RefreshTokenApp.shortId(orderId) + " ready — " + valid + " account(s), import below", RefreshTokenApp.OK);
               }

               RefreshTokenApp.this.refreshBalance();
               RefreshTokenApp.this.loadOrders();
            } catch (Exception ex) {
               RefreshTokenApp.this.storeError(ex);
            }

         }
      }).execute();
   }

   private static long discountedTotal(int amount, LocaltsApi.Product p) {
      long base = p.priceInCredits() * (long)amount;
      return base * (long)(100 - discountPercentFor(amount, p)) / 100L;
   }

   private void loadOrderDetails(final String id, final Consumer<LocaltsApi.Order> onLoaded) {
      LocaltsApi.Order cached = (LocaltsApi.Order)this.orderCache.get(id);
      if (cached != null) {
         onLoaded.accept(cached);
      } else {
         this.status.set("Loading order " + shortId(id) + "…", SUBTLE);
         (new SwingWorker<LocaltsApi.Order, Void>() {
            {
               Objects.requireNonNull(RefreshTokenApp.this);
            }

            protected LocaltsApi.Order doInBackground() throws Exception {
               LocaltsApi.Order order = RefreshTokenApp.this.api.getOrder(id);
               if (!order.isPackaged()) {
                  throw new IllegalStateException("Order is " + order.status() + " — not ready yet");
               } else {
                  return order;
               }
            }

            protected void done() {
               try {
                  LocaltsApi.Order order = (LocaltsApi.Order)this.get();
                  RefreshTokenApp.this.orderCache.put(id, order);
                  RefreshTokenApp var10000 = RefreshTokenApp.this;
                  String var10001 = RefreshTokenApp.shortId(id);
                  var10000.log("Order " + var10001 + " fetched — " + order.items().size() + " account(s)");
                  RefreshTokenApp.this.status.set("Order " + RefreshTokenApp.shortId(id) + " — pick an account to import", RefreshTokenApp.SUBTLE);
                  onLoaded.accept(order);
               } catch (Exception ex) {
                  RefreshTokenApp.this.storeError(ex);
                  onLoaded.accept(null);
               }

            }
         }).execute();
      }
   }

   private void importItem(final String name, final String refreshToken, final String productName, final Runnable onImported, final Runnable onInvalid, final Runnable onDone) {
      this.status.set("Importing " + name + "…", SUBTLE);
      this.log("Importing " + name + " — authenticating…");
      (new SwingWorker<AccountAuthenticator.AuthResult, Void>() {
         {
            Objects.requireNonNull(RefreshTokenApp.this);
         }

         protected AccountAuthenticator.AuthResult doInBackground() throws Exception {
            return AccountAuthenticator.authenticate(refreshToken, (step) -> RefreshTokenApp.this.log("  " + name + ": " + step));
         }

         protected void done() {
            try {
               RefreshTokenApp.this.addAccount((AccountAuthenticator.AuthResult)this.get(), productName);
               RefreshTokenApp.this.log("Imported " + name + " (" + productName + ")", RefreshTokenApp.OK);
               onImported.run();
            } catch (Exception var7) {
               Throwable cause = (Throwable)(var7 instanceof ExecutionException && var7.getCause() != null ? var7.getCause() : var7);
               String reason = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
               RefreshTokenApp.this.log("Failed: " + reason + " (" + name + ")", RefreshTokenApp.ERROR);
               if (RefreshTokenApp.isAuthFailure(cause)) {
                  RefreshTokenApp.this.invalidTokens.add(refreshToken);
                  RefreshTokenApp.this.status.set(name + " — invalid or expired token", RefreshTokenApp.ERROR);
                  onInvalid.run();
               } else {
                  RefreshTokenApp.this.storeError(var7);
               }
            } finally {
               onDone.run();
            }

         }
      }).execute();
   }

   private boolean isTokenInvalid(String token) {
      return this.invalidTokens.contains(token);
   }

   private static boolean isAuthFailure(Throwable t) {
      for(Throwable c = t; c != null && c != c.getCause(); c = c.getCause()) {
         if (c instanceof AuthenticationException ae) {
            return !ae.isRetryable();
         }
      }

      return false;
   }

   private static boolean isRateLimited(Throwable t) {
      for(Throwable c = t; c != null && c != c.getCause(); c = c.getCause()) {
         if (c instanceof LocaltsApi.ApiException ae) {
            if (ae.status() == 429) {
               return true;
            }
         }
      }

      return false;
   }

   private void storeError(Exception ex) {
      Throwable cause = (Throwable)(ex instanceof ExecutionException && ex.getCause() != null ? ex.getCause() : ex);
      if (isRateLimited(cause)) {
         this.pollCooldownUntil = System.currentTimeMillis() + 60000L;
         this.log("Rate limited by the store — pausing store requests for 60s", SUBTLE);
      }

      this.status.set(cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName(), ERROR);
      StringBuilder chain = (new StringBuilder("Error detail: ")).append(cause);

      for(Throwable c = cause.getCause(); c != null && c != c.getCause(); c = c.getCause()) {
         chain.append("  ⇐  ").append(c);
      }

      this.log(chain.toString(), ERROR);
   }

   private static String readClipboard() {
      try {
         Object data = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
         return data == null ? "" : data.toString().trim();
      } catch (Exception var1) {
         return "";
      }
   }

   private static void writeClipboard(String s) {
      try {
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s == null ? "" : s), (ClipboardOwner)null);
      } catch (Exception var2) {
      }

   }

   private static String tokenRemaining(long expireMs) {
      long mins = (expireMs - System.currentTimeMillis()) / 60000L;
      if (mins <= 0L) {
         return "expired";
      } else {
         long h = mins / 60L;
         long m = mins % 60L;
         return h > 0L ? h + "h" + m + "m" : m + "m";
      }
   }

   private static Color lifetimeColor(long expireMs) {
      double t = Math.max((double)0.0F, Math.min((double)1.0F, (double)(expireMs - System.currentTimeMillis()) / (double)8.64E7F));
      Color red = new Color(238, 128, 122);
      Color green = new Color(62, 168, 85);
      int r = (int)Math.round((double)red.getRed() + (double)(green.getRed() - red.getRed()) * t);
      int g = (int)Math.round((double)red.getGreen() + (double)(green.getGreen() - red.getGreen()) * t);
      int b = (int)Math.round((double)red.getBlue() + (double)(green.getBlue() - red.getBlue()) * t);
      return new Color(r, g, b);
   }

   private static final String FONT_FAMILY = bestSansFamily();

   private static String bestSansFamily() {
      String[] candidates = new String[]{"Arial", "Helvetica Neue", "Noto Sans", "DejaVu Sans", "Liberation Sans"};
      for(String family : candidates) {
         if (new Font(family, 0, 12).getFamily().equalsIgnoreCase(family)) {
            return family;
         }
      }

      return Font.SANS_SERIF;
   }

   private static Font font(int style, float size) {
      return new Font(FONT_FAMILY, style, (int)size);
   }

   static void accountMenu(JComponent invoker, int x, int y, AccountRow row) {
      JPopupMenu menu = new JPopupMenu();
      menu.setBackground(CARD);
      menu.setBorder(BorderFactory.createEmptyBorder());
      JPanel content = new JPanel() {
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(RefreshTokenApp.CARD);
            g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
            g2.setColor(RefreshTokenApp.BORDER_HI);
            g2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
         }
      };
      content.setOpaque(false);
      content.setLayout(new BoxLayout(content, 1));
      content.setBorder(new EmptyBorder(6, 6, 6, 6));
      Color base = new Color(194, 199, 206);
      content.add(new MenuRow("Copy username", base, TEXT, menu, () -> writeClipboard(row.name), true));
      content.add(new MenuRow("Copy UUID", base, TEXT, menu, () -> writeClipboard(row.uuid.toString()), true));
      content.add(new MenuRow("Copy access token", base, TEXT, menu, () -> writeClipboard(row.session.getMcProfile().getMcToken().getAccessToken()), true));
      content.add(new MenuRow("Copy OAuth refresh token", base, TEXT, menu, () -> writeClipboard(row.refreshToken), true));
      content.add(menuSeparator());
      content.add(new MenuRow("Refresh tokens", base, TEXT, menu, () -> {
         if (row.onRefresh != null) {
            row.onRefresh.run();
         }

      }, false));
      content.add(menuSeparator());
      content.add(new MenuRow("Delete", SOFT_RED, ERROR, menu, () -> {
         if (row.onRemove != null) {
            row.onRemove.run();
         }

      }, false));
      menu.add(content);
      menu.show(invoker, x, y);
   }

   private static JComponent menuSeparator() {
      JComponent sep = new JComponent() {
         protected void paintComponent(Graphics g) {
            g.setColor(RefreshTokenApp.BORDER);
            g.fillRect(8, this.getHeight() / 2, this.getWidth() - 16, 1);
         }
      };
      sep.setPreferredSize(new Dimension(190, 7));
      sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 7));
      sep.setAlignmentX(0.0F);
      return sep;
   }

   private static JComponent infoBlock(String title, String... subs) {
      JPanel p = new JPanel();
      p.setOpaque(false);
      p.setLayout(new BoxLayout(p, 1));
      JLabel t = new JLabel(title);
      t.setForeground(TEXT);
      t.setFont(font(1, 12.0F));
      t.setAlignmentX(0.0F);
      p.add(Box.createVerticalGlue());
      p.add(t);

      for(String sub : subs) {
         if (sub != null && !sub.isBlank()) {
            JLabel s = new JLabel(sub);
            s.setForeground(SUBTLE);
            s.setFont(font(1, 11.0F));
            s.setAlignmentX(0.0F);
            p.add(Box.createRigidArea(new Dimension(0, 6)));
            p.add(s);
         }
      }

      p.add(Box.createVerticalGlue());
      return p;
   }

   private static String productLogoPath(String productId) {
      return "/api/product/" + productId + "/gallery/logo";
   }

   private static void loadPlayerHead(final String skinUrl, final AccountRow target) {
      if (skinUrl != null && !skinUrl.isBlank()) {
         (new SwingWorker<Image, Void>() {
            protected Image doInBackground() throws Exception {
               BufferedImage skin = ImageIO.read(URI.create(skinUrl).toURL());
               return skin == null ? null : RefreshTokenApp.headFromSkin(skin, 32);
            }

            protected void done() {
               try {
                  Image head = (Image)this.get();
                  if (head != null) {
                     target.setHead(head);
                  }
               } catch (Exception var2) {
               }

            }
         }).execute();
      }
   }

   private static BufferedImage headFromSkin(BufferedImage skin, int size) {
      BufferedImage out = new BufferedImage(size, size, 2);
      Graphics2D g = out.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      g.drawImage(skin, 0, 0, size, size, 8, 8, 16, 16, (ImageObserver)null);
      g.drawImage(skin, 0, 0, size, size, 40, 8, 48, 16, (ImageObserver)null);
      g.dispose();
      return out;
   }

   private static JLabel logoLabel() {
      JLabel l = new JLabel();
      l.setHorizontalAlignment(0);
      l.setPreferredSize(new Dimension(54, 54));
      return l;
   }

   private static JComponent logoHolder(JComponent logo) {
      JPanel p = new JPanel(new GridBagLayout());
      p.setOpaque(false);
      p.setPreferredSize(new Dimension(54, 58));
      p.add(logo);
      return p;
   }

   private static String shortId(String id) {
      if (id == null) {
         return "";
      } else {
         return id.length() > 10 ? id.substring(0, 4) + "…" + id.substring(id.length() - 4) : id;
      }
   }

   private static Path logoCacheFile(String logoUrl) {
      return LOGO_CACHE_DIR.resolve(String.valueOf(UUID.nameUUIDFromBytes(logoUrl.getBytes(StandardCharsets.UTF_8))) + ".img");
   }

   private static byte[] cachedLogoBytes(String logoUrl) {
      try {
         Path f = logoCacheFile(logoUrl);
         return System.currentTimeMillis() - Files.getLastModifiedTime(f).toMillis() > 21600000L ? null : Files.readAllBytes(f);
      } catch (Exception var2) {
         return null;
      }
   }

   private static void storeLogoBytes(String logoUrl, byte[] bytes) {
      try {
         Files.createDirectories(LOGO_CACHE_DIR);
         Files.write(logoCacheFile(logoUrl), bytes, new OpenOption[0]);
      } catch (Exception var3) {
      }

   }

   private static void loadLogo(final LocaltsApi api, final String logoUrl, final JLabel target) {
      if (logoUrl != null && !logoUrl.isBlank()) {
         CachedLogo mem = (CachedLogo)logoMemCache.get(logoUrl);
         if (mem != null && System.currentTimeMillis() - mem.at() < 21600000L) {
            target.setIcon(mem.icon());
         } else {
            (new SwingWorker<ImageIcon, Void>() {
               protected ImageIcon doInBackground() throws Exception {
                  byte[] bytes = RefreshTokenApp.cachedLogoBytes(logoUrl);
                  if (bytes == null) {
                     bytes = api.getProductLogo(logoUrl);
                     if (bytes == null || bytes.length == 0) {
                        return null;
                     }

                     RefreshTokenApp.storeLogoBytes(logoUrl, bytes);
                  }

                  BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
                  if (img == null) {
                     return null;
                  } else {
                     return new ImageIcon(RefreshTokenApp.roundedLogo(img));
                  }
               }

               protected void done() {
                  try {
                     ImageIcon icon = (ImageIcon)this.get();
                     if (icon != null) {
                        RefreshTokenApp.logoMemCache.put(logoUrl, new CachedLogo(icon, System.currentTimeMillis()));
                        target.setIcon(icon);
                     }
                  } catch (Exception var2) {
                  }

               }
            }).execute();
         }
      }
   }

   private static BufferedImage roundedLogo(BufferedImage src) {
      return scaledRounded(src, 54, 12);
   }

   private static BufferedImage scaledRounded(BufferedImage src, int size, int arc) {
      BufferedImage out = new BufferedImage(size, size, 2);
      Graphics2D g2 = out.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.setColor(Color.WHITE);
      g2.fill(new RoundRectangle2D.Float(0.0F, 0.0F, (float)size, (float)size, (float)arc, (float)arc));
      g2.setComposite(AlphaComposite.SrcIn);
      g2.drawImage(src, 0, 0, size, size, (ImageObserver)null);
      g2.dispose();
      return out;
   }

   private static JComponent eastWrap(JComponent c) {
      JPanel wrap = new JPanel(new GridBagLayout());
      wrap.setOpaque(false);
      wrap.add(c);
      return wrap;
   }

   private static void paintRowBackground(Graphics g, JComponent c) {
      Graphics2D g2 = (Graphics2D)g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(CARD_T);
      g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
      g2.setColor(BORDER_T);
      g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
      g2.dispose();
   }

   private static void drawGlyph(Graphics2D g2, int glyph, int cx, int cy, Color color) {
      g2.setColor(color);
      g2.setStroke(new BasicStroke(1.3F, 1, 1));
      switch (glyph) {
         case 0:
            g2.drawRoundRect(cx - 5, cy - 6, 10, 13, 3, 3);
            g2.fillRoundRect(cx - 2, cy - 8, 4, 3, 1, 1);
            break;
         case 1:
            g2.drawOval(cx - 6, cy - 4, 12, 8);
            g2.fillOval(cx - 2, cy - 2, 4, 4);
            break;
         case 2:
            g2.drawOval(cx - 6, cy - 4, 12, 8);
            g2.fillOval(cx - 2, cy - 2, 4, 4);
            g2.drawLine(cx - 6, cy + 5, cx + 6, cy - 5);
            break;
         case 3:
            g2.drawLine(cx, cy - 6, cx, cy + 2);
            g2.drawLine(cx - 3, cy - 1, cx, cy + 2);
            g2.drawLine(cx + 3, cy - 1, cx, cy + 2);
            g2.drawLine(cx - 5, cy + 5, cx + 5, cy + 5);
            break;
         case 4:
            for(int i = 0; i < 8; ++i) {
               double a = (Math.PI / 4D) * (double)i;
               g2.drawLine(cx + (int)Math.round(Math.cos(a) * (double)4.0F), cy + (int)Math.round(Math.sin(a) * (double)4.0F), cx + (int)Math.round(Math.cos(a) * (double)7.0F), cy + (int)Math.round(Math.sin(a) * (double)7.0F));
            }

            g2.drawOval(cx - 4, cy - 4, 8, 8);
            g2.drawOval(cx - 1, cy - 1, 2, 2);
            break;
         case 5:
            g2.drawLine(cx - 6, cy - 4, cx + 6, cy - 4);
            g2.drawLine(cx - 2, cy - 6, cx + 2, cy - 6);
            g2.drawLine(cx - 5, cy - 4, cx - 4, cy + 6);
            g2.drawLine(cx + 5, cy - 4, cx + 4, cy + 6);
            g2.drawLine(cx - 4, cy + 6, cx + 4, cy + 6);
            g2.drawLine(cx - 1, cy - 1, cx - 1, cy + 4);
            g2.drawLine(cx + 1, cy - 1, cx + 1, cy + 4);
            break;
         case 6:
            g2.drawOval(cx - 7, cy - 3, 6, 6);
            g2.drawLine(cx - 1, cy, cx + 7, cy);
            g2.drawLine(cx + 7, cy, cx + 7, cy + 3);
            g2.drawLine(cx + 4, cy, cx + 4, cy + 3);
            break;
         case 7:
            g2.drawRoundRect(cx - 5, cy - 3, 10, 9, 3, 3);
            g2.drawArc(cx - 3, cy - 7, 6, 7, 0, 180);
      }

   }

   private static enum SortMode {
      EXPIRY("Expiry"),
      IMPORT("Import date"),
      PRODUCT("Product");

      final String label;

      private SortMode(String label) {
         this.label = label;
      }

      // $FF: synthetic method
      private static SortMode[] $values() {
         return new SortMode[]{EXPIRY, IMPORT, PRODUCT};
      }
   }

   private static record Version(String label, int protocol) {
      public String toString() {
         return this.label;
      }
   }

   private static record Animation(List<BufferedImage> frames, int[] delays) {
   }

   private static record OrderListing(LocaltsApi.OrderSummary summary, LocaltsApi.Product product) {
   }

   private interface SimpleDoc extends DocumentListener {
      void changed();

      default void insertUpdate(DocumentEvent e) {
         this.changed();
      }

      default void removeUpdate(DocumentEvent e) {
         this.changed();
      }

      default void changedUpdate(DocumentEvent e) {
         this.changed();
      }
   }

   static class RootPanel extends JPanel {
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setPaint(new GradientPaint(0.0F, 0.0F, RefreshTokenApp.BG_TOP, 0.0F, (float)this.getHeight(), RefreshTokenApp.BG_BOTTOM));
         g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 18, 18);
         g2.setColor(RefreshTokenApp.BORDER);
         g2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 18, 18);
         g2.dispose();
      }
   }

   static class AccountRow extends JComponent {
      final UUID uuid;
      final String name;
      StepFullJavaSession.FullJavaSession session;
      String refreshToken;
      long refreshExpire;
      long importedAt;
      String source;
      Runnable onActivate;
      Runnable onRemove;
      Runnable onRefresh;
      Runnable onSelect;
      boolean selected;
      volatile boolean invalid;
      private Image head;
      private boolean active;
      private boolean hover;
      private boolean menuHover;
      private long pulseUntil;
      private Timer pulseTimer;

      AccountRow(String name, UUID uuid, StepFullJavaSession.FullJavaSession session) {
         this.name = name;
         this.uuid = uuid;
         this.session = session;
         this.setPreferredSize(new Dimension(356, 44));
         this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
         this.setMinimumSize(new Dimension(120, 44));
         this.setAlignmentX(0.5F);
         this.setCursor(Cursor.getPredefinedCursor(12));
         MouseAdapter mouse = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               AccountRow.this.hover = true;
               AccountRow.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               AccountRow.this.hover = false;
               AccountRow.this.menuHover = false;
               AccountRow.this.repaint();
            }

            public void mouseMoved(MouseEvent e) {
               boolean m = AccountRow.this.menuBounds().contains(e.getPoint());
               if (m != AccountRow.this.menuHover) {
                  AccountRow.this.menuHover = m;
                  AccountRow.this.repaint();
               }

            }

            public void mouseReleased(MouseEvent e) {
               if (AccountRow.this.contains(e.getPoint())) {
                  if (AccountRow.this.menuBounds().contains(e.getPoint())) {
                     AccountRow.this.showMenu();
                  } else if (e.isControlDown()) {
                     AccountRow.this.selected = !AccountRow.this.selected;
                     AccountRow.this.repaint();
                     if (AccountRow.this.onSelect != null) {
                        AccountRow.this.onSelect.run();
                     }
                  } else if (AccountRow.this.onActivate != null) {
                     AccountRow.this.onActivate.run();
                  }

               }
            }
         };
         this.addMouseListener(mouse);
         this.addMouseMotionListener(mouse);
      }

      private void showMenu() {
         Rectangle mb = this.menuBounds();
         int mw = 200;
         RefreshTokenApp.accountMenu(this, Math.max(8, mb.x + mb.width - mw), mb.y + mb.height + 2, this);
      }

      void setActive(boolean active) {
         this.active = active;
         this.repaint();
      }

      void setHead(Image head) {
         this.head = head;
         this.repaint();
      }

      void pulse() {
         this.pulseUntil = System.currentTimeMillis() + 3000L;
         if (this.pulseTimer == null) {
            this.pulseTimer = new Timer(33, (e) -> {
               if (System.currentTimeMillis() >= this.pulseUntil) {
                  ((Timer)e.getSource()).stop();
               }

               this.repaint();
            });
         }

         this.pulseTimer.restart();
      }

      private Rectangle menuBounds() {
         int sz = 24;
         return new Rectangle(this.getWidth() - sz - 8, (this.getHeight() - sz) / 2, sz, sz);
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
         int w = this.getWidth();
         int h = this.getHeight();
         int cy = h / 2;
         boolean expired = this.session.getMcProfile().getMcToken().getExpireTimeMs() <= System.currentTimeMillis();
         if (expired && !this.hover && !this.selected) {
            g2.setComposite(AlphaComposite.getInstance(3, 0.5F));
         }

         g2.setColor(this.selected ? new Color(32, 44, 64) : (this.hover ? RefreshTokenApp.CARD_HI : RefreshTokenApp.CARD_T));
         g2.fillRoundRect(0, 0, w, h, 12, 12);
         g2.setColor(!this.active && !this.selected ? RefreshTokenApp.BORDER_T : RefreshTokenApp.ACCENT_DIM);
         g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
         long pulseLeft = this.pulseUntil - System.currentTimeMillis();
         if (pulseLeft > 0L) {
            float wave = (float)((double)0.5F + (double)0.5F * Math.sin((double)pulseLeft / (double)200.0F));
            int alpha = Math.round((40.0F + 215.0F * wave) * Math.min(1.0F, (float)pulseLeft / 600.0F));
            g2.setColor(new Color(RefreshTokenApp.ACCENT.getRed(), RefreshTokenApp.ACCENT.getGreen(), RefreshTokenApp.ACCENT.getBlue(), alpha));
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2.0F));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 12, 12);
            g2.setStroke(oldStroke);
         }

         int headX = this.active ? 20 : 12;
         if (this.active) {
            g2.setColor(RefreshTokenApp.ACCENT);
            g2.fillOval(7, cy - 3, 6, 6);
         }

         if (this.head != null) {
            g2.drawImage(this.head, headX, (h - 32) / 2, 32, 32, (ImageObserver)null);
         } else {
            g2.setColor(this.active ? RefreshTokenApp.ACCENT : RefreshTokenApp.BORDER_HI);
            g2.fillOval(headX + 16 - 5, cy - 5, 10, 10);
         }

         int nameX = headX + 32 + 10;
         g2.setFont(RefreshTokenApp.font(1, 13.0F));
         g2.setColor(this.active ? RefreshTokenApp.TEXT : new Color(194, 199, 206));
         FontMetrics fm = g2.getFontMetrics();
         int baseline = cy + fm.getAscent() / 2 - 1;
         g2.drawString(this.name, nameX, baseline);
         if (this.source != null && !this.source.isBlank()) {
            g2.setFont(RefreshTokenApp.font(0, 9.0F));
            g2.setColor(RefreshTokenApp.SUBTLE);
            g2.drawString("(" + this.source + ")", nameX + fm.stringWidth(this.name) + 6, baseline);
         }

         Rectangle mb = this.menuBounds();
         if (this.menuHover) {
            g2.setColor(RefreshTokenApp.CARD_HI);
            g2.fillRoundRect(mb.x, mb.y, mb.width, mb.height, 7, 7);
         }

         Color ic = this.menuHover ? RefreshTokenApp.TEXT : RefreshTokenApp.SUBTLE;
         BufferedImage dots = RefreshTokenApp.dotsIcon();
         if (dots != null) {
            g2.drawImage(RefreshTokenApp.tintImage(dots, ic), mb.x + (mb.width - 18) / 2, cy - 9, 18, 18, (ImageObserver)null);
         } else {
            g2.setColor(ic);
            int dx = mb.x + mb.width / 2 - 1;
            g2.fillOval(dx, cy - 7, 3, 3);
            g2.fillOval(dx, cy - 1, 3, 3);
            g2.fillOval(dx, cy + 5, 3, 3);
         }

         long expireMs = this.session.getMcProfile().getMcToken().getExpireTimeMs();
         String time = this.invalid ? "invalid" : RefreshTokenApp.tokenRemaining(expireMs);
         g2.setFont(RefreshTokenApp.font(1, 10.0F));
         FontMetrics tfm = g2.getFontMetrics();
         int timeX = mb.x - 10 - tfm.stringWidth(time);
         g2.setColor(this.invalid ? RefreshTokenApp.SOFT_RED : RefreshTokenApp.lifetimeColor(expireMs));
         g2.drawString(time, timeX, cy + 4);
         g2.dispose();
      }
   }

   static class MenuRow extends JComponent {
      private final String label;
      private final Color base;
      private final Color hoverColor;
      private final JPopupMenu menu;
      private final Runnable action;
      private final boolean copyFeedback;
      private boolean hover;
      private boolean done;

      MenuRow(String label, Color base, Color hoverColor, JPopupMenu menu, Runnable action, boolean copyFeedback) {
         this.label = label;
         this.base = base;
         this.hoverColor = hoverColor;
         this.menu = menu;
         this.action = action;
         this.copyFeedback = copyFeedback;
         this.setAlignmentX(0.0F);
         this.setPreferredSize(new Dimension(190, 30));
         this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
         this.setMinimumSize(new Dimension(150, 30));
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               MenuRow.this.hover = true;
               MenuRow.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               MenuRow.this.hover = false;
               MenuRow.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (MenuRow.this.contains(e.getPoint())) {
                  action.run();
                  if (copyFeedback) {
                     MenuRow.this.done = true;
                     MenuRow.this.repaint();
                     Timer t = new Timer(600, (ev) -> menu.setVisible(false));
                     t.setRepeats(false);
                     t.start();
                  } else {
                     menu.setVisible(false);
                  }

               }
            }
         });
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
         int w = this.getWidth();
         int h = this.getHeight();
         int cy = h / 2;
         if (this.hover) {
            g2.setColor(RefreshTokenApp.CARD_HI);
            g2.fillRoundRect(2, 2, w - 4, h - 4, 7, 7);
         }

         g2.setFont(RefreshTokenApp.font(1, 12.0F));
         g2.setColor(this.done ? RefreshTokenApp.OK : (this.hover ? this.hoverColor : this.base));
         FontMetrics fm = g2.getFontMetrics();
         g2.drawString(this.done ? "Copied!" : this.label, 12, cy + fm.getAscent() / 2 - 1);
         g2.dispose();
      }
   }

   static class FlatButton extends JComponent {
      private final String text;
      private final int glyph;
      private final BufferedImage icon;
      private final Runnable onClick;
      private boolean hover;
      private boolean pressed;
      private boolean primary;

      FlatButton(String text, int w, int h, Runnable onClick) {
         this(text, -1, (BufferedImage)null, w, h, onClick);
      }

      FlatButton(int glyph, int w, int h, Runnable onClick) {
         this((String)null, glyph, (BufferedImage)null, w, h, onClick);
      }

      FlatButton(BufferedImage icon, int w, int h, Runnable onClick) {
         this((String)null, -1, icon, w, h, onClick);
      }

      FlatButton primary() {
         this.primary = true;
         return this;
      }

      private FlatButton(String text, int glyph, BufferedImage icon, int w, int h, Runnable onClick) {
         this.text = text;
         this.glyph = glyph;
         this.icon = icon;
         this.onClick = onClick;
         Dimension d = new Dimension(w, h);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               FlatButton.this.hover = true;
               FlatButton.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               FlatButton.this.hover = false;
               FlatButton.this.pressed = false;
               FlatButton.this.repaint();
            }

            public void mousePressed(MouseEvent e) {
               FlatButton.this.pressed = true;
               FlatButton.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               boolean fire = FlatButton.this.pressed && FlatButton.this.contains(e.getPoint());
               FlatButton.this.pressed = false;
               FlatButton.this.repaint();
               if (fire) {
                  onClick.run();
               }

            }
         });
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
         int w = this.getWidth();
         int h = this.getHeight();
         int y = this.pressed ? 1 : 0;
         Color fg;
         if (this.primary) {
            g2.setColor(this.hover ? RefreshTokenApp.ACCENT.brighter() : RefreshTokenApp.ACCENT);
            g2.fillRoundRect(0, y, w, h - 1, 9, 9);
            g2.setColor(RefreshTokenApp.ACCENT);
            g2.drawRoundRect(0, y, w - 1, h - 2, 9, 9);
            fg = Color.WHITE;
         } else {
            g2.setColor(this.hover ? RefreshTokenApp.CARD_HI : new Color(26, 32, 41));
            g2.fillRoundRect(0, y, w, h - 1, 9, 9);
            g2.setColor(this.hover ? RefreshTokenApp.ACCENT : RefreshTokenApp.BORDER_HI);
            g2.drawRoundRect(0, y, w - 1, h - 2, 9, 9);
            fg = this.hover ? RefreshTokenApp.TEXT : new Color(194, 199, 206);
         }

         if (this.icon != null) {
            BufferedImage tinted = RefreshTokenApp.tintImage(this.icon, fg);
            g2.drawImage(tinted, (w - this.icon.getWidth()) / 2, y + (h - this.icon.getHeight()) / 2, (ImageObserver)null);
         } else if (this.glyph >= 0) {
            RefreshTokenApp.drawGlyph(g2, this.glyph, w / 2, y + h / 2, fg);
         } else {
            g2.setFont(RefreshTokenApp.font(1, 11.0F));
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(this.text, (w - fm.stringWidth(this.text)) / 2, y + (h + fm.getAscent()) / 2 - 3);
         }

         g2.dispose();
      }
   }

   static class ChevronToggle extends JComponent {
      private final Runnable onClick;
      private final Timer timer;
      private boolean hover;
      private float anim;
      private float target;

      ChevronToggle(Runnable onClick) {
         this.onClick = onClick;
         Dimension d = new Dimension(34, 30);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.timer = new Timer(15, (e) -> {
            this.anim += (this.target - this.anim) * 0.22F;
            if (Math.abs(this.target - this.anim) < 0.005F) {
               this.anim = this.target;
               ((Timer)e.getSource()).stop();
            }

            this.repaint();
         });
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               ChevronToggle.this.hover = true;
               ChevronToggle.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               ChevronToggle.this.hover = false;
               ChevronToggle.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (ChevronToggle.this.contains(e.getPoint())) {
                  onClick.run();
               }

            }
         });
      }

      void setExpanded(boolean expanded) {
         this.target = expanded ? 1.0F : 0.0F;
         if (!this.timer.isRunning()) {
            this.timer.start();
         }

      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int w = this.getWidth();
         int h = this.getHeight();
         if (this.hover) {
            g2.setColor(RefreshTokenApp.CARD_HI);
            g2.fillRoundRect((w - 26) / 2, (h - 26) / 2, 26, 26, 8, 8);
         }

         g2.translate((double)w / (double)2.0F, (double)h / (double)2.0F);
         g2.rotate(Math.PI * (double)this.anim);
         g2.setColor(this.hover ? RefreshTokenApp.TEXT : RefreshTokenApp.SUBTLE);
         g2.setStroke(new BasicStroke(1.4F, 1, 1));
         g2.drawLine(-4, -2, 0, 2);
         g2.drawLine(4, -2, 0, 2);
         g2.dispose();
      }
   }

   static class ProductRow extends JPanel {
      ProductRow(LocaltsApi.Product p, LocaltsApi api, Consumer<LocaltsApi.Product> onBuy) {
         this.setOpaque(false);
         this.setLayout(new BorderLayout(8, 0));
         this.setBorder(new EmptyBorder(0, 2, 0, 8));
         this.setPreferredSize(new Dimension(356, 58));
         this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
         this.setAlignmentX(0.0F);
         JLabel logo = RefreshTokenApp.logoLabel();
         this.add(RefreshTokenApp.logoHolder(logo), "West");
         long var10000 = p.priceInCredits();
         String priceStock = var10000 + " credits  •  " + (p.stock() > 0L ? p.stock() + " in stock" : "out of stock");
         this.add(RefreshTokenApp.infoBlock(p.name(), p.category(), priceStock), "Center");
         if (p.stock() > 0L) {
            BufferedImage cart = RefreshTokenApp.cartIcon();
            IconButton buy = cart != null ? new IconButton(cart, "Purchase", () -> onBuy.accept(p)) : new IconButton(7, "Purchase", () -> onBuy.accept(p));
            buy.setToolTipText("Purchase this product");
            this.add(RefreshTokenApp.eastWrap(buy), "East");
         } else {
            JLabel sold = new JLabel("sold out");
            sold.setForeground(RefreshTokenApp.SUBTLE);
            sold.setFont(RefreshTokenApp.font(1, 11.0F));
            this.add(RefreshTokenApp.eastWrap(sold), "East");
         }

         RefreshTokenApp.loadLogo(api, p.logoUrl().isBlank() ? RefreshTokenApp.productLogoPath(p.id()) : p.logoUrl(), logo);
      }

      protected void paintComponent(Graphics g) {
         RefreshTokenApp.paintRowBackground(g, this);
         super.paintComponent(g);
      }
   }

   static class OrderRow extends JPanel {
      private static final int EXPAND_MS = 240;
      private final String orderId;
      private float anim;
      private float animTarget;
      private float animFrom;
      private long animT0;
      private Timer animTimer;
      private final JPanel detailContent = new JPanel();
      private final JPanel detail = new JPanel() {
         private int fullHeight() {
            Insets in = this.getInsets();
            return OrderRow.this.detailContent.getPreferredSize().height + in.top + in.bottom;
         }

         public void doLayout() {
            Insets in = this.getInsets();
            OrderRow.this.detailContent.setBounds(in.left, in.top, this.getWidth() - in.left - in.right, OrderRow.this.detailContent.getPreferredSize().height);
         }

         public Dimension getPreferredSize() {
            Insets in = this.getInsets();
            return new Dimension(OrderRow.this.detailContent.getPreferredSize().width + in.left + in.right, Math.round((float)this.fullHeight() * OrderRow.this.anim));
         }

         public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Math.round((float)this.fullHeight() * OrderRow.this.anim));
         }

         public Dimension getMinimumSize() {
            return new Dimension(0, Math.round((float)this.fullHeight() * OrderRow.this.anim));
         }
      };
      private final ChevronToggle toggle;
      private final BiConsumer<String, Consumer<LocaltsApi.Order>> loader;
      private final ImportAction onImportItem;
      private final Predicate<String> isImported;
      private final Predicate<String> isInvalid;
      private final Consumer<String> onShowAccount;
      private final String productName;
      private LocaltsApi.Order cachedOrder;
      private boolean expanded;
      private boolean loading;

      OrderRow(LocaltsApi.OrderSummary o, LocaltsApi.Product product, LocaltsApi api, BiConsumer<String, Consumer<LocaltsApi.Order>> loader, ImportAction onImportItem, Predicate<String> isImported, Predicate<String> isInvalid, Consumer<String> onShowAccount) {
         this.orderId = o.id();
         this.loader = loader;
         this.onImportItem = onImportItem;
         this.isImported = isImported;
         this.isInvalid = isInvalid;
         this.onShowAccount = onShowAccount;
         this.setOpaque(false);
         this.setLayout(new BoxLayout(this, 1));
         this.setAlignmentX(0.0F);
         this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
         JPanel header = new JPanel(new BorderLayout(8, 0));
         header.setOpaque(false);
         header.setBorder(new EmptyBorder(0, 2, 0, 8));
         header.setPreferredSize(new Dimension(356, 58));
         header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
         header.setAlignmentX(0.0F);
         JLabel logo = RefreshTokenApp.logoLabel();
         header.add(RefreshTokenApp.logoHolder(logo), "West");
         String when = RefreshTokenApp.TIME_FMT.format(Instant.ofEpochMilli(o.timestamp()));
         String title = product != null ? product.name() : (o.productType().isBlank() ? "Order" : o.productType());
         this.productName = title;
         String[] var10002 = new String[1];
         String var10005 = RefreshTokenApp.shortId(o.id());
         var10002[0] = "#" + var10005 + "   ·   " + when;
         header.add(RefreshTokenApp.infoBlock(title, var10002), "Center");
         this.toggle = new ChevronToggle(this::toggleExpand);
         this.toggle.setToolTipText("Show accounts");
         header.add(RefreshTokenApp.eastWrap(this.toggle), "East");
         header.setCursor(Cursor.getPredefinedCursor(12));
         header.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
               if (header.contains(e.getPoint())) {
                  OrderRow.this.toggleExpand();
               }

            }
         });
         this.add(header);
         this.detailContent.setOpaque(false);
         this.detailContent.setLayout(new BoxLayout(this.detailContent, 1));
         this.detail.setOpaque(false);
         this.detail.setAlignmentX(0.0F);
         this.detail.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, RefreshTokenApp.BORDER), new EmptyBorder(8, 12, 8, 8)));
         this.detail.add(this.detailContent);
         this.detail.setVisible(false);
         this.add(this.detail);
         if (!o.productId().isBlank()) {
            RefreshTokenApp.loadLogo(api, RefreshTokenApp.productLogoPath(o.productId()), logo);
         }

      }

      private void toggleExpand() {
         if (!this.loading) {
            if (this.expanded) {
               this.setExpanded(false);
            } else if (this.cachedOrder != null) {
               this.populate(this.cachedOrder);
            } else {
               this.loading = true;
               this.loader.accept(this.orderId, (order) -> {
                  this.loading = false;
                  if (order != null) {
                     this.cachedOrder = order;
                     this.populate(order);
                  }

               });
            }
         }
      }

      private void populate(LocaltsApi.Order order) {
         this.detailContent.removeAll();
         if (order.items().isEmpty()) {
            this.detailContent.add(note("No accounts in this order"));
         } else {
            int n = 1;

            for(LocaltsApi.OrderItem item : order.items()) {
               if (n > 1) {
                  this.detailContent.add(Box.createRigidArea(new Dimension(0, 6)));
               }

               String content = item.content();
               int colon = content.indexOf(58);
               String name = colon > 0 ? content.substring(0, colon) : "Account";
               String token = colon >= 0 ? content.substring(colon + 1) : content;
               this.detailContent.add(this.accountRow(n++, name, token));
            }
         }

         this.setExpanded(true);
      }

      private void setExpanded(boolean exp) {
         this.expanded = exp;
         this.toggle.setExpanded(exp);
         this.toggle.setToolTipText(exp ? "Hide accounts" : "Show accounts");
         if (exp) {
            this.detail.setVisible(true);
         }

         this.animTarget = exp ? 1.0F : 0.0F;
         this.animFrom = this.anim;
         this.animT0 = System.nanoTime();
         if (this.animTimer == null) {
            this.animTimer = new Timer(3, (e) -> {
               float t = Math.min(1.0F, (float)(System.nanoTime() - this.animT0) / 1000000.0F / 240.0F);
               float eased = 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
               this.anim = this.animFrom + (this.animTarget - this.animFrom) * eased;
               if (t >= 1.0F) {
                  ((Timer)e.getSource()).stop();
                  if (this.anim == 0.0F) {
                     this.detail.setVisible(false);
                  }
               }

               this.relayout();
            });
         }

         this.animTimer.restart();
      }

      private void relayout() {
         this.detail.revalidate();
         this.revalidate();
         this.repaint();
         Container p = this.getParent();
         if (p != null) {
            p.revalidate();
            p.repaint();
         }

      }

      private JComponent accountRow(int index, String name, String token) {
         JPanel row = new JPanel(new BorderLayout(8, 0));
         row.setOpaque(false);
         row.setAlignmentX(0.0F);
         row.setPreferredSize(new Dimension(336, 34));
         row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
         JLabel num = new JLabel("#" + index);
         num.setForeground(RefreshTokenApp.SUBTLE);
         num.setFont(RefreshTokenApp.font(1, 12.0F));
         row.add(num, "West");
         JPanel mid = new JPanel();
         mid.setOpaque(false);
         mid.setLayout(new BoxLayout(mid, 0));
         JLabel n = new JLabel(name);
         n.setForeground(RefreshTokenApp.TEXT);
         n.setFont(RefreshTokenApp.font(1, 12.0F));
         mid.add(n);
         JLabel tag = new JLabel();
         tag.setFont(RefreshTokenApp.font(0, 11.0F));
         mid.add(Box.createRigidArea(new Dimension(6, 0)));
         mid.add(tag);
         if (this.isInvalid.test(token)) {
            tag.setText("(Invalid)");
            tag.setForeground(RefreshTokenApp.SOFT_RED);
         } else if (this.isImported.test(name)) {
            tag.setText("(Imported)");
            tag.setForeground(RefreshTokenApp.CYAN);
         }

         tag.addMouseListener(new MouseAdapter() {
            private boolean linkable() {
               return "(Imported)".equals(tag.getText());
            }

            public void mouseEntered(MouseEvent e) {
               if (this.linkable()) {
                  tag.setCursor(Cursor.getPredefinedCursor(12));
                  Map<TextAttribute, Object> a = new HashMap(RefreshTokenApp.font(2, 11.0F).getAttributes());
                  a.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                  tag.setFont(RefreshTokenApp.font(2, 11.0F).deriveFont(a));
               }
            }

            public void mouseExited(MouseEvent e) {
               tag.setCursor(Cursor.getDefaultCursor());
               tag.setFont(RefreshTokenApp.font(0, 11.0F));
            }

            public void mouseReleased(MouseEvent e) {
               if (this.linkable() && tag.contains(e.getPoint())) {
                  OrderRow.this.onShowAccount.accept(name);
               }

            }
         });
         row.add(mid, "Center");
         JPanel importing = new JPanel();
         importing.setOpaque(false);
         importing.setLayout(new BoxLayout(importing, 0));
         importing.add(new Spinner(14));
         importing.add(Box.createRigidArea(new Dimension(6, 0)));
         JLabel importingLbl = new JLabel("Importing");
         importingLbl.setForeground(RefreshTokenApp.SUBTLE);
         importingLbl.setFont(RefreshTokenApp.font(1, 11.0F));
         importing.add(importingLbl);
         importing.setVisible(false);
         IconButton[] impRef = new IconButton[1];
         impRef[0] = new IconButton(3, this.isImported.test(name) ? "Re-import" : "Import", () -> {
            impRef[0].setVisible(false);
            importing.setVisible(true);
            this.onImportItem.start(name, token, this.productName, () -> {
               setTag(tag, "(Imported)", RefreshTokenApp.CYAN);
               impRef[0].setLabel("Re-import");
            }, () -> setTag(tag, "(Invalid)", RefreshTokenApp.SOFT_RED), () -> {
               importing.setVisible(false);
               impRef[0].setVisible(true);
            });
         });
         IconButton imp = impRef[0];
         imp.setToolTipText("Import this account");
         JPanel east = new JPanel(new GridBagLayout());
         east.setOpaque(false);
         east.add(imp);
         east.add(importing);
         row.add(east, "East");
         return row;
      }

      private static void setTag(JLabel tag, String text, Color color) {
         tag.setText(text);
         tag.setForeground(color);
         tag.revalidate();
         tag.repaint();
      }

      private static JComponent note(String text) {
         JLabel l = new JLabel(text);
         l.setForeground(RefreshTokenApp.SUBTLE);
         l.setFont(RefreshTokenApp.font(1, 11.0F));
         l.setAlignmentX(0.0F);
         return l;
      }

      protected void paintComponent(Graphics g) {
         RefreshTokenApp.paintRowBackground(g, this);
         super.paintComponent(g);
      }
   }

   private static record CachedLogo(ImageIcon icon, long at) {
   }

   static class ApiKeyField extends JPanel {
      private final JPasswordField field = new JPasswordField();
      private final Runnable onLock;
      private final IconButton eye;
      private boolean hidden = true;

      ApiKeyField(Runnable onLock) {
         this.onLock = onLock;
         this.setOpaque(false);
         this.setLayout(new BorderLayout(6, 0));
         this.setBorder(new EmptyBorder(0, 12, 0, 6));
         Dimension d = new Dimension(396, 40);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.setAlignmentX(0.5F);
         this.field.setOpaque(false);
         this.field.setBorder((Border)null);
         this.field.setForeground(RefreshTokenApp.TEXT);
         this.field.setCaretColor(RefreshTokenApp.TEXT);
         this.field.setFont(RefreshTokenApp.font(0, 13.0F));
         this.field.setEchoChar('•');
         this.field.putClientProperty("JTextField.placeholderText", "your-api-key");
         this.field.addActionListener((e) -> this.lock());
         this.field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
               if (ApiKeyField.this.field.isEditable()) {
                  ApiKeyField.this.lock();
               }

            }
         });
         this.field.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
               if (!ApiKeyField.this.field.isEditable()) {
                  ApiKeyField.this.unlock();
               }

            }
         });
         JPanel icons = new JPanel();
         icons.setOpaque(false);
         icons.setLayout(new BoxLayout(icons, 0));
         IconButton paste = new IconButton(0, () -> {
            this.unlock();
            String clip = RefreshTokenApp.readClipboard();
            if (!clip.isEmpty()) {
               this.field.setText(clip);
            }

         });
         this.eye = new IconButton(1, this::toggleHide);
         paste.setFocusable(false);
         this.eye.setFocusable(false);
         icons.add(paste);
         icons.add(Box.createRigidArea(new Dimension(2, 0)));
         icons.add(this.eye);
         this.add(this.field, "Center");
         this.add(icons, "East");
      }

      void lock() {
         if (this.field.isEditable()) {
            this.field.setEditable(false);
            this.field.setForeground(RefreshTokenApp.SUBTLE);
            this.repaint();
            this.onLock.run();
         }
      }

      void unlock() {
         this.field.setEditable(true);
         this.field.setForeground(RefreshTokenApp.TEXT);
         this.repaint();
         this.field.requestFocusInWindow();
      }

      private void toggleHide() {
         this.hidden = !this.hidden;
         this.field.setEchoChar((char)(this.hidden ? '•' : '\u0000'));
         this.eye.setGlyph(this.hidden ? 1 : 2);
      }

      String getKey() {
         return (new String(this.field.getPassword())).trim();
      }

      void setKey(String key) {
         this.field.setText(key);
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setColor(RefreshTokenApp.CARD);
         g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
         g2.setColor(this.field.isEditable() ? RefreshTokenApp.BORDER_HI : RefreshTokenApp.BORDER);
         g2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 10, 10);
         g2.dispose();
         super.paintComponent(g);
      }
   }

   static class SortButton extends JComponent {
      private static BufferedImage icon;
      private String text;
      private boolean hover;
      private final Runnable onClick;

      SortButton(String text, Runnable onClick) {
         this.text = text;
         this.onClick = onClick;
         this.setFont(RefreshTokenApp.font(1, 11.0F));
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.resize();
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               SortButton.this.hover = true;
               SortButton.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               SortButton.this.hover = false;
               SortButton.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (SortButton.this.contains(e.getPoint())) {
                  onClick.run();
               }

            }
         });
      }

      void setText(String text) {
         this.text = text;
         this.resize();
         this.repaint();
      }

      private void resize() {
         int w = 20 + this.getFontMetrics(this.getFont()).stringWidth(this.text);
         Dimension d = new Dimension(w, 20);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.revalidate();
      }

      protected void paintComponent(Graphics g) {
         if (icon == null) {
            BufferedImage raw = RefreshTokenApp.renderSvg("/icons/sort.svg", 28);
            if (raw != null) {
               icon = raw;
            }
         }

         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         Color c = this.hover ? RefreshTokenApp.TEXT : RefreshTokenApp.SUBTLE;
         int cy = this.getHeight() / 2;
         if (icon != null) {
            g2.drawImage(RefreshTokenApp.tintImage(icon, c), 0, cy - 7, 14, 14, (ImageObserver)null);
         }

         g2.setColor(c);
         g2.setFont(this.getFont());
         FontMetrics fm = g2.getFontMetrics();
         g2.drawString(this.text, 20, (this.getHeight() + fm.getAscent() - fm.getDescent()) / 2);
         g2.dispose();
      }
   }

   static class IconButton extends JComponent {
      static final int PASTE = 0;
      static final int EYE = 1;
      static final int EYE_OFF = 2;
      static final int IMPORT = 3;
      static final int GEAR = 4;
      static final int TRASH = 5;
      static final int KEY = 6;
      static final int BAG = 7;
      private int glyph;
      private final BufferedImage image;
      private boolean hover;
      private final Runnable onClick;
      private String label;

      IconButton(int glyph, Runnable onClick) {
         this(glyph, (BufferedImage)null, (String)null, onClick);
      }

      IconButton(int glyph, String label, Runnable onClick) {
         this(glyph, (BufferedImage)null, label, onClick);
      }

      IconButton(BufferedImage image, String label, Runnable onClick) {
         this(-1, image, label, onClick);
      }

      private IconButton(int glyph, BufferedImage image, String label, Runnable onClick) {
         this.glyph = glyph;
         this.image = image;
         this.onClick = onClick;
         this.setFont(RefreshTokenApp.font(1, 11.0F));
         this.setLabel(label);
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               IconButton.this.hover = true;
               IconButton.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               IconButton.this.hover = false;
               IconButton.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (IconButton.this.contains(e.getPoint())) {
                  onClick.run();
               }

            }
         });
      }

      void setGlyph(int glyph) {
         this.glyph = glyph;
         this.repaint();
      }

      void setLabel(String label) {
         this.label = label;
         int w = 24;
         if (label != null) {
            w += this.getFontMetrics(this.getFont()).stringWidth(label) + 10;
         }

         Dimension d = new Dimension(w, 24);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.revalidate();
         this.repaint();
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int w = this.getWidth();
         int h = this.getHeight();
         if (this.hover) {
            g2.setColor(RefreshTokenApp.CARD_HI);
            g2.fillRoundRect(2, 2, w - 4, h - 4, 7, 7);
         }

         Color c = this.hover ? RefreshTokenApp.TEXT : RefreshTokenApp.SUBTLE;
         int iconCx = this.label == null ? w / 2 : 12;
         if (this.image != null) {
            g2.drawImage(RefreshTokenApp.tintImage(this.image, c), iconCx - this.image.getWidth() / 2, (h - this.image.getHeight()) / 2, (ImageObserver)null);
         } else {
            RefreshTokenApp.drawGlyph(g2, this.glyph, iconCx, h / 2, c);
         }

         if (this.label != null) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setColor(c);
            g2.setFont(this.getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(this.label, 26, (h + fm.getAscent() - fm.getDescent()) / 2);
         }

         g2.dispose();
      }
   }

   static class Spinner extends JComponent {
      private static BufferedImage img;
      private float angle;
      private final Timer timer = new Timer(33, (e) -> {
         this.angle += 0.18F;
         this.repaint();
      });

      Spinner(int size) {
         Dimension d = new Dimension(size, size);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.addHierarchyListener((e) -> {
            if ((e.getChangeFlags() & 4L) != 0L) {
               if (this.isShowing()) {
                  this.timer.start();
               } else {
                  this.timer.stop();
               }
            }

         });
      }

      protected void paintComponent(Graphics g) {
         if (img == null) {
            BufferedImage raw = RefreshTokenApp.renderSvg("/icons/loading.svg", 28);
            if (raw == null) {
               return;
            }

            img = RefreshTokenApp.tintImage(raw, RefreshTokenApp.SUBTLE);
         }

         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         int s = Math.min(this.getWidth(), this.getHeight());
         g2.rotate((double)this.angle, (double)this.getWidth() / (double)2.0F, (double)this.getHeight() / (double)2.0F);
         g2.drawImage(img, (this.getWidth() - s) / 2, (this.getHeight() - s) / 2, s, s, (ImageObserver)null);
         g2.dispose();
      }
   }

   static class AnimatedWebp extends JComponent {
      private List<BufferedImage> frames;
      private int[] delays;
      private int idx;

      AnimatedWebp(String path, int size) {
         Dimension d = new Dimension(size, size);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         (new SwingWorker<Animation, Void>() {
            protected RefreshTokenApp.Animation doInBackground() throws Exception {
               return RefreshTokenApp.loadAnimatedWebp(path, size);
            }

            protected void done() {
               try {
                  RefreshTokenApp.Animation a = (RefreshTokenApp.Animation)this.get();
                  if (a == null || a.frames().isEmpty()) {
                     return;
                  }

                  AnimatedWebp.this.frames = a.frames();
                  AnimatedWebp.this.delays = a.delays();
                  Timer timer = new Timer(AnimatedWebp.this.delays[0], (e) -> {
                     AnimatedWebp.this.idx = (AnimatedWebp.this.idx + 1) % AnimatedWebp.this.frames.size();
                     ((Timer)e.getSource()).setDelay(AnimatedWebp.this.delays[AnimatedWebp.this.idx]);
                     AnimatedWebp.this.repaint();
                  });
                  timer.start();
                  AnimatedWebp.this.repaint();
               } catch (Exception var3) {
               }

            }
         }).execute();
      }

      protected void paintComponent(Graphics g) {
         if (this.frames != null && !this.frames.isEmpty()) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage((Image)this.frames.get(this.idx), 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
            g2.dispose();
         }
      }
   }

   static class SidebarButton extends JComponent {
      private final BufferedImage icon;
      private final Runnable onClick;
      private boolean active;
      private boolean hover;

      SidebarButton(String svgPath, String tooltip, Runnable onClick) {
         this.icon = svgPath == null ? null : RefreshTokenApp.renderSvg(svgPath, 22);
         this.onClick = onClick;
         this.setToolTipText(tooltip);
         Dimension d = new Dimension(48, 46);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.setAlignmentX(0.5F);
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               SidebarButton.this.hover = true;
               SidebarButton.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               SidebarButton.this.hover = false;
               SidebarButton.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (SidebarButton.this.contains(e.getPoint())) {
                  onClick.run();
               }

            }
         });
      }

      void setActive(boolean active) {
         this.active = active;
         this.repaint();
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int w = this.getWidth();
         int h = this.getHeight();
         if (this.active || this.hover) {
            g2.setColor(this.active ? RefreshTokenApp.CARD_HI : RefreshTokenApp.CARD);
            g2.fillRoundRect(6, 5, w - 11, h - 10, 9, 9);
         }

         if (this.active) {
            g2.setColor(RefreshTokenApp.ACCENT);
            g2.fillRoundRect(0, h / 2 - 9, 3, 18, 2, 2);
         }

         Color c = this.active ? RefreshTokenApp.ACCENT : (this.hover ? RefreshTokenApp.TEXT : RefreshTokenApp.SUBTLE);
         if (this.icon != null) {
            g2.drawImage(RefreshTokenApp.tintImage(this.icon, c), (w - 22) / 2, (h - 22) / 2, 22, 22, (ImageObserver)null);
         } else {
            RefreshTokenApp.drawGlyph(g2, 4, w / 2, h / 2, c);
         }

         g2.dispose();
      }
   }

   static class MinimizeButton extends JComponent {
      private boolean hover;

      MinimizeButton(Runnable onClick) {
         Dimension d = new Dimension(26, 26);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               MinimizeButton.this.hover = true;
               MinimizeButton.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               MinimizeButton.this.hover = false;
               MinimizeButton.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (MinimizeButton.this.contains(e.getPoint())) {
                  onClick.run();
               }

            }
         });
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int w = this.getWidth();
         int h = this.getHeight();
         if (this.hover) {
            g2.setColor(RefreshTokenApp.CARD_HI);
            g2.fillRoundRect(0, 0, w, h, 8, 8);
         }

         g2.setColor(this.hover ? RefreshTokenApp.TEXT : RefreshTokenApp.SUBTLE);
         g2.setStroke(new BasicStroke(1.6F, 1, 1));
         g2.drawLine(w / 2 - 5, h / 2, w / 2 + 5, h / 2);
         g2.dispose();
      }
   }

   static class CloseButton extends JComponent {
      private boolean hover;

      CloseButton(Runnable onClick) {
         Dimension d = new Dimension(26, 26);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               CloseButton.this.hover = true;
               CloseButton.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               CloseButton.this.hover = false;
               CloseButton.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (CloseButton.this.contains(e.getPoint())) {
                  onClick.run();
               }

            }
         });
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int w = this.getWidth();
         int h = this.getHeight();
         Color red = RefreshTokenApp.ERROR;
         if (this.hover) {
            g2.setColor(new Color(red.getRed(), red.getGreen(), red.getBlue(), 36));
            g2.fillRoundRect(0, 0, w, h, 8, 8);
         }

         int r = 5;
         int cx = w / 2;
         int cy = h / 2;
         g2.setColor(this.hover ? red : RefreshTokenApp.SUBTLE);
         g2.setStroke(new BasicStroke(1.6F, 1, 1));
         g2.drawLine(cx - r, cy - r, cx + r, cy + r);
         g2.drawLine(cx - r, cy + r, cx + r, cy - r);
         g2.dispose();
      }
   }

   static class SvgButton extends JComponent {
      private final BufferedImage icon;
      private final int iconSize;
      private final Color base;
      private final Color hoverColor;
      private final Runnable onClick;
      private boolean hover;

      SvgButton(String svgPath, int iconSize, int w, int h, Color base, Color hoverColor, String tooltip, Runnable onClick) {
         this.icon = RefreshTokenApp.renderSvg(svgPath, iconSize);
         this.iconSize = iconSize;
         this.base = base;
         this.hoverColor = hoverColor;
         this.onClick = onClick;
         this.setToolTipText(tooltip);
         Dimension d = new Dimension(w, h);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               SvgButton.this.hover = true;
               SvgButton.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               SvgButton.this.hover = false;
               SvgButton.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (SvgButton.this.contains(e.getPoint())) {
                  onClick.run();
               }

            }
         });
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int w = this.getWidth();
         int h = this.getHeight();
         if (this.hover) {
            g2.setColor(RefreshTokenApp.CARD_HI);
            g2.fillRoundRect(0, 0, w, h, 7, 7);
         }

         if (this.icon != null) {
            g2.drawImage(RefreshTokenApp.tintImage(this.icon, this.hover ? this.hoverColor : this.base), (w - this.iconSize) / 2, (h - this.iconSize) / 2, this.iconSize, this.iconSize, (ImageObserver)null);
         }

         g2.dispose();
      }
   }

   static class TargetIcon extends JComponent {
      private final BufferedImage icon;
      private final boolean tint;
      private final Runnable onClick;
      private boolean selected;
      private boolean hover;

      TargetIcon(BufferedImage icon, boolean tint, String tooltip, Runnable onClick) {
         this.icon = icon;
         this.tint = tint;
         this.onClick = onClick;
         this.setToolTipText(tooltip);
         Dimension d = new Dimension(30, 28);
         this.setPreferredSize(d);
         this.setMaximumSize(d);
         this.setMinimumSize(d);
         this.setCursor(Cursor.getPredefinedCursor(12));
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               TargetIcon.this.hover = true;
               TargetIcon.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               TargetIcon.this.hover = false;
               TargetIcon.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
               if (TargetIcon.this.contains(e.getPoint())) {
                  onClick.run();
               }

            }
         });
      }

      void setSelected(boolean selected) {
         this.selected = selected;
         this.repaint();
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int w = this.getWidth();
         int h = this.getHeight();
         if (this.selected || this.hover) {
            g2.setColor(this.selected ? RefreshTokenApp.CARD_HI : RefreshTokenApp.CARD);
            g2.fillRoundRect(0, 0, w, h, 8, 8);
         }

         if (this.selected) {
            g2.setColor(RefreshTokenApp.ACCENT_DIM);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
         }

         if (this.icon != null) {
            int s = 20;
            BufferedImage img = this.tint ? RefreshTokenApp.tintImage(this.icon, this.selected ? RefreshTokenApp.TEXT : (this.hover ? RefreshTokenApp.TEXT : RefreshTokenApp.SUBTLE)) : this.icon;
            g2.drawImage(img, (w - s) / 2, (h - s) / 2, s, s, (ImageObserver)null);
         }

         g2.dispose();
      }
   }

   static class SlimScrollBarUI extends BasicScrollBarUI {
      protected void configureScrollBarColors() {
         this.thumbColor = RefreshTokenApp.BORDER_HI;
         this.trackColor = new Color(0, 0, 0, 0);
      }

      protected JButton createDecreaseButton(int orientation) {
         return zeroButton();
      }

      protected JButton createIncreaseButton(int orientation) {
         return zeroButton();
      }

      private static JButton zeroButton() {
         JButton b = new JButton();
         Dimension zero = new Dimension(0, 0);
         b.setPreferredSize(zero);
         b.setMinimumSize(zero);
         b.setMaximumSize(zero);
         return b;
      }

      protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
      }

      protected void paintThumb(Graphics g, JComponent c, Rectangle b) {
         if (!b.isEmpty() && this.scrollbar.isEnabled()) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.isThumbRollover() ? RefreshTokenApp.BORDER_HI : RefreshTokenApp.BORDER);
            int w = 5;
            int x = b.x + (b.width - w) / 2;
            g2.fillRoundRect(x, b.y + 2, w, b.height - 4, w, w);
            g2.dispose();
         }
      }
   }

   static class StatusBar extends JPanel {
      private String text = "";
      private Color color;
      private BiConsumer<String, Color> onSet;

      StatusBar() {
         this.color = RefreshTokenApp.SUBTLE;
         this.setOpaque(false);
         this.setPreferredSize(new Dimension(440, 38));
      }

      void setLogger(BiConsumer<String, Color> onSet) {
         this.onSet = onSet;
      }

      void set(String text, Color color) {
         this.text = text;
         this.color = color;
         this.repaint();
         if (this.onSet != null) {
            this.onSet.accept(text, color);
         }

      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
         g2.setColor(RefreshTokenApp.BORDER);
         g2.drawLine(18, 0, this.getWidth() - 18, 0);
         int cy = this.getHeight() / 2;
         g2.setColor(this.color);
         g2.fillOval(18, cy - 4, 8, 8);
         g2.setFont(RefreshTokenApp.font(0, 12.0F));
         g2.setColor(this.color == RefreshTokenApp.SUBTLE ? RefreshTokenApp.SUBTLE : RefreshTokenApp.TEXT);
         FontMetrics fm = g2.getFontMetrics();
         g2.drawString(ellipsize(this.text, fm, this.getWidth() - 18 - 34), 34, cy + fm.getAscent() / 2 - 1);
         g2.dispose();
      }

      private static String ellipsize(String s, FontMetrics fm, int max) {
         if (s == null) {
            return "";
         } else if (fm.stringWidth(s) <= max) {
            return s;
         } else {
            while(s.length() > 1 && fm.stringWidth(s + "…") > max) {
               s = s.substring(0, s.length() - 1);
            }

            return s + "…";
         }
      }
   }

   static class AnimatedButton extends JComponent {
      private final Dimension size;
      private final float fontSize;
      private final Runnable onClick;
      private final String label;
      private boolean hover;
      private boolean pressed;
      private boolean loading;
      private float phase;

      AnimatedButton(String label, Dimension size, float fontSize, Runnable onClick) {
         this.label = label;
         this.size = size;
         this.fontSize = fontSize;
         this.onClick = onClick;
         this.setPreferredSize(size);
         this.setMaximumSize(size);
         this.setMinimumSize(size);
         this.setCursor(Cursor.getPredefinedCursor(12));
         (new Timer(16, (e) -> {
            this.phase += 0.05F;
            this.repaint();
         })).start();
         this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               AnimatedButton.this.hover = true;
               AnimatedButton.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
               AnimatedButton.this.hover = false;
               AnimatedButton.this.pressed = false;
               AnimatedButton.this.repaint();
            }

            public void mousePressed(MouseEvent e) {
               if (!AnimatedButton.this.loading) {
                  AnimatedButton.this.pressed = true;
                  AnimatedButton.this.repaint();
               }

            }

            public void mouseReleased(MouseEvent e) {
               boolean fire = AnimatedButton.this.pressed && !AnimatedButton.this.loading && AnimatedButton.this.contains(e.getPoint());
               AnimatedButton.this.pressed = false;
               AnimatedButton.this.repaint();
               if (fire && onClick != null) {
                  onClick.run();
               }

            }
         });
      }

      void setLoading(boolean loading) {
         this.loading = loading;
         this.setCursor(Cursor.getPredefinedCursor(loading ? 3 : 12));
         this.repaint();
      }

      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
         int w = this.getWidth();
         int h = this.getHeight();
         int sink = this.pressed ? 1 : 0;
         int arc = 12;
         Color top = this.hover ? RefreshTokenApp.CARD_HI : RefreshTokenApp.CARD;
         Color bottom = this.hover ? RefreshTokenApp.CARD : new Color(17, 21, 28);
         g2.setPaint(new GradientPaint(0.0F, (float)sink, top, 0.0F, (float)h, bottom));
         g2.fillRoundRect(0, sink, w, h - sink - 1, arc, arc);
         float glow = (float)((Math.sin((double)this.phase) + (double)1.0F) / (double)2.0F);
         int a = Math.round((float)(this.loading ? 70 : 36) + glow * (float)(this.loading ? 60 : 24));
         Color edge = !this.hover && !this.loading ? RefreshTokenApp.BORDER_HI : RefreshTokenApp.ACCENT;
         g2.setColor(new Color(edge.getRed(), edge.getGreen(), edge.getBlue(), a));
         g2.setStroke(new BasicStroke(1.2F));
         g2.drawRoundRect(0, sink, w - 1, h - sink - 2, arc, arc);
         int cx = w / 2;
         int cy = (h + sink) / 2;
         if (this.loading) {
            int r = h / 2 - 12;
            float start = (float)Math.toDegrees((double)(-this.phase) * 2.2);
            g2.setStroke(new BasicStroke(2.6F, 1, 1));
            g2.setColor(new Color(255, 255, 255, 40));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(RefreshTokenApp.ACCENT);
            g2.draw(new Arc2D.Float((float)(cx - r), (float)(cy - r), (float)(r * 2), (float)(r * 2), start, 100.0F, 0));
         } else {
            g2.setFont(RefreshTokenApp.font(1, this.fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(this.label);
            g2.setColor(this.hover ? RefreshTokenApp.TEXT : new Color(206, 211, 218));
            g2.drawString(this.label, cx - tw / 2, cy + fm.getAscent() / 2 - 2);
         }

         g2.dispose();
      }
   }

   @FunctionalInterface
   interface ImportAction {
      void start(String var1, String var2, String var3, Runnable var4, Runnable var5, Runnable var6);
   }
}
