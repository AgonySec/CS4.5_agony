package aggressor;

import aggressor.bridges.AggressorBridge;
import aggressor.bridges.AliasManager;
import aggressor.bridges.ArtifactBridge;
import aggressor.bridges.AttackBridge;
import aggressor.bridges.BeaconBridge;
import aggressor.bridges.BeaconTaskBridge;
import aggressor.bridges.CovertVPNBridge;
import aggressor.bridges.DataBridge;
import aggressor.bridges.DialogBridge;
import aggressor.bridges.ElevateBridge;
import aggressor.bridges.ElevatorBridge;
import aggressor.bridges.EventLogBridge;
import aggressor.bridges.GraphBridge;
import aggressor.bridges.LateralBridge;
import aggressor.bridges.ListenerBridge;
import aggressor.bridges.PreferencesBridge;
import aggressor.bridges.RemoteExecBridge;
import aggressor.bridges.ReportingBridge;
import aggressor.bridges.SafeDialogBridge;
import aggressor.bridges.SecureShellAliasManager;
import aggressor.bridges.SiteBridge;
import aggressor.bridges.TabBridge;
import aggressor.bridges.TeamServerBridge;
import aggressor.bridges.ToolBarBridge;
import aggressor.bridges.UtilityBridge;
import aggressor.browsers.Connect;
import aggressor.browsers.TestConnection;
import beacon.BeaconCommands;
import beacon.BeaconElevators;
import beacon.BeaconExploits;
import beacon.BeaconRemoteExecMethods;
import beacon.BeaconRemoteExploits;
import beacon.SecureShellCommands;
import common.Callback;
import common.CommonUtils;
import common.DisconnectListener;
import common.License;
import common.MudgeSanity;
import common.PlaybackStatus;
import common.TeamQueue;
import common.TeamSocket;
import console.Activity;
import cortana.Cortana;
import cortana.gui.ScriptableApplication;
import dialog.DialogUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import report.ReportEngine;
import tunnel.TunnelManager;
import ui.KeyBindings;
import ui.KeyHandler;

public class AggressorClient extends JComponent implements ScriptableApplication, DisconnectListener, Callback {
   protected KeyBindings keys = new KeyBindings();
   protected JMenuBar menu = new JMenuBar();
   protected TabManager tabs = null;
   protected Cortana engine = null;
   protected JSplitPane split = null;
   protected String title = "Cobalt Strike 4.5 By Agony";
   protected MultiFrame window = null;
   protected JSplitPane split2 = null;
   protected JToolBar tool = new JToolBar();
   protected TeamQueue conn = null;
   protected DataManager data = null;
   protected ReportEngine reports = null;
   protected AliasManager aliases = null;
   protected TunnelManager tunnels = null;
   protected boolean connected = true;
   protected boolean userInitiatedClose = false;
   protected TestConnection testConnection = null;
   protected SecureShellAliasManager ssh_aliases = null;
   protected JPanel viz = new JPanel();
   protected CardLayout viz_c = new CardLayout();

   public String getTitle() {
      return License.isTrial() ? this.title + " (Trial)" : this.title;
   }

   public String getTeamServerAlias() {
      return this.data.getMapSafe("options").get("alias") + "";
   }

   public void setTeamServerAlias(String var1) {
      synchronized(this.data) {
         Map var3 = (Map)this.data.get("options", Collections.emptyMap());
         var3.put("alias", var1);
      }

      String var2 = DataUtils.getTeamServerIP(this.data);
      Prefs.getPreferences().set("connection.profiles." + var2 + ".alias", var1);
      Prefs.getPreferences().save();
   }

   public MultiFrame getWindow() {
      return this.window;
   }

   public TunnelManager getTunnelManager() {
      return this.tunnels;
   }

   public ReportEngine getReportEngine() {
      return this.reports;
   }

   public void showViz(String var1) {
      this.viz_c.show(this.viz, var1);
      this.viz.validate();
   }

   public void addViz(String var1, JComponent var2) {
      this.viz.add(var2, var1);
   }

   public Cortana getScriptEngine() {
      return this.engine;
   }

   public TabManager getTabManager() {
      return this.tabs;
   }

   public TeamQueue getConnection() {
      return this.conn;
   }

   public DataManager getData() {
      return this.data;
   }

   public KeyBindings getBindings() {
      return this.keys;
   }

   public SecureShellAliasManager getSSHAliases() {
      return this.ssh_aliases;
   }

   public AliasManager getAliases() {
      return this.aliases;
   }

   public void bindKey(String var1, KeyHandler var2) {
      this.keys.bind(var1, var2);
   }

   public boolean isConnected() {
      return this.connected;
   }

   public JMenuBar getJMenuBar() {
      return this.menu;
   }

   public void touch() {
      Component var1 = this.tabs.getTabbedPane().getSelectedComponent();
      if (var1 != null) {
         if (var1 instanceof Activity) {
            ((Activity)var1).resetNotification();
         }

         var1.requestFocusInWindow();
      }
   }

   public void kill() {
      this.userInitiatedClose = true;
      CommonUtils.print_info("shutting down client");
      this.tabs.stop();
      this.engine.getEventManager().stop();
      this.engine.stop();
      this.conn.close();
   }

   protected void stopTestConnection() {
      if (this.testConnection != null) {
         this.testConnection.stop();
         this.testConnection = null;
      }

   }

   public void disconnected(TeamSocket var1) {
      this.disconnected();
   }

   public void disconnected() {
      JButton var2 = new JButton("Close");
      var2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            AggressorClient.this.stopTestConnection();
            AggressorClient.this.window.quit(AggressorClient.this, false);
         }
      });
      JButton var3 = new JButton("Reconnect");
      var3.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            AggressorClient.this.stopTestConnection();
            Map var2 = AggressorClient.this.data.getMapSafe("options");
            String var3 = AggressorClient.this.window.getActiveAlias();
            AggressorClient.this.window.quit(AggressorClient.this, true);
            (new Connect(AggressorClient.this.window, true, var3)).dialogAction((ActionEvent)null, var2);
         }
      });
      JPanel var4 = new JPanel();
      var4.setLayout(new BorderLayout());
      var4.setBackground(Color.RED);
      JLabel var5 = new JLabel("Disconnected from server (" + this.getTeamServerAlias() + ")");
      var5.setFont(var5.getFont().deriveFont(1));
      var4.add(var5, "Center");
      JLabel var6 = new JLabel("");
      JComponent var7;
      if (this.userInitiatedClose) {
         var7 = DialogUtils.center(var6, var3, var2);
      } else {
         var7 = DialogUtils.center(var6, var2);
      }

      var7.setBackground(Color.RED);
      var4.add(var7, "East");
      var4.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.add(var4, "South");
      this.revalidate();
      this.getData().dead();
      this.connected = false;
      if (!this.userInitiatedClose) {
         this.testConnection = new TestConnection(this.data.getMapSafe("options"), var6, var3);
         (new Thread(this.testConnection)).start();
      }

   }

   public void dock(JComponent var1, Dimension var2) {
      this.split2.setBottomComponent(var1);
      this.split2.setDividerSize(10);
      this.split2.setResizeWeight(1.0);
      var1.setPreferredSize(var2);
      var1.setSize(var2);
      this.validate();
   }

   public void noDock() {
      this.split2.setBottomComponent((Component)null);
      this.split2.setDividerSize(0);
      this.split2.setResizeWeight(1.0);
      this.validate();
   }

   public void result(String var1, Object var2) {
      if ("server_error".equals(var1)) {
         CommonUtils.print_error("Server error: " + var2);
      }

   }

   public void loadScripts() {
      try {
         this.engine.loadScript("scripts/default.cna", CommonUtils.resource("scripts/default.cna"));
      } catch (Exception var5) {
         MudgeSanity.logException("Loading scripts/default.cna", var5, false);
      }

      Iterator var1 = Prefs.getPreferences().getList("cortana.scripts").iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();

         try {
            this.engine.loadScript(var2);
         } catch (Exception var4) {
            MudgeSanity.logException("Loading " + var2, var4, true);
         }
      }

   }

   public AggressorClient(MultiFrame var1, TeamQueue var2, Map var3, Map var4) {
      this.setup(var1, var2, var3, var4);
   }

   public boolean isHeadless() {
      return false;
   }

   public AggressorClient() {
   }

   public void setup(MultiFrame var1, TeamQueue var2, Map var3, Map var4) {
      this.window = var1;
      this.conn = var2;
      var2.addDisconnectListener(this);
      this.tabs = new TabManager(this);
      this.engine = new Cortana(this);
      this.reports = new ReportEngine(this);
      this.aliases = new AliasManager();
      this.ssh_aliases = new SecureShellAliasManager();
      this.engine.register(new TabBridge(this.engine, this.tabs));
      this.engine.register(new GraphBridge(this.engine, this.tabs));
      this.engine.register(new AggressorBridge(this, this.engine, this.tabs, var1, var2));
      this.engine.register(new ToolBarBridge(this.tool));
      this.engine.register(new TeamServerBridge(this.engine, var2));
      this.engine.register(new DataBridge(this, this.engine, var2));
      this.engine.register(new BeaconBridge(this, this.engine, var2));
      this.engine.register(new BeaconTaskBridge(this));
      this.engine.register(new ElevateBridge(this));
      this.engine.register(new ElevatorBridge(this));
      this.engine.register(new LateralBridge(this));
      this.engine.register(new RemoteExecBridge(this));
      this.engine.register(new UtilityBridge(this));
      this.engine.register(new ReportingBridge(this));
      this.engine.register(new EventLogBridge(this));
      this.engine.register(new SafeDialogBridge(this));
      this.engine.register(new PreferencesBridge(this));
      this.engine.register(new ListenerBridge(this));
      this.engine.register(new CovertVPNBridge(this));
      this.engine.register(new ArtifactBridge(this));
      this.engine.register(new DialogBridge(this));
      this.engine.register(new SiteBridge(this));
      this.engine.register(new AttackBridge());
      this.engine.register(this.aliases.getBridge());
      this.engine.register(this.ssh_aliases.getBridge());
      this.reports.registerInternal("scripts/default.rpt");
      this.data = new DataManager(this.engine);
      this.data.put("metadata", var3);
      this.data.put("options", var4);
      this.data.put("beacon_commands", new BeaconCommands());
      this.data.put("beacon_exploits", new BeaconExploits());
      this.data.put("beacon_elevators", new BeaconElevators());
      this.data.put("beacon_remote_exploits", new BeaconRemoteExploits());
      this.data.put("beacon_remote_exec_methods", new BeaconRemoteExecMethods());
      DataUtils.getBeaconExploits(this.data).registerDefaults(this);
      DataUtils.getBeaconElevators(this.data).registerDefaults(this);
      DataUtils.getBeaconRemoteExploits(this.data).registerDefaults(this);
      DataUtils.getBeaconRemoteExecMethods(this.data).registerDefaults(this);
      this.data.put("ssh_commands", new SecureShellCommands());
      this.tunnels = new TunnelManager(this);
      this.tunnels.init();
      var2.setSubscriber(this.data);
      this.data.subscribe("server_error", this);
      this.tabs.start();
      this.loadScripts();
      this.viz.setLayout(this.viz_c);
      this.split2 = new JSplitPane(0, this.tabs.getTabbedPane(), (Component)null);
      this.split2.setDividerSize(0);
      this.split2.setOneTouchExpandable(true);
      this.split = new JSplitPane(0, this.viz, this.split2);
      this.split.setOneTouchExpandable(true);
      this.tool.setFloatable(false);
      this.tool.add(Box.createHorizontalGlue());
      this.add(this.split, "Center");
      if (Prefs.getPreferences().isSet("client.toolbar.boolean", true)) {
         JPanel var5 = new JPanel();
         var5.setLayout(new BorderLayout());
         var5.add(this.menu, "North");
         var5.add(this.tool, "Center");
         this.setLayout(new BorderLayout());
         this.add(var5, "North");
         this.add(this.split, "Center");
      } else {
         this.setLayout(new BorderLayout());
         this.add(this.menu, "North");
         this.add(this.split, "Center");
      }

      if (!this.isHeadless()) {
         new SyncMonitor(this);
      }

      this.getData().subscribe("playback.status", new Callback() {
         public void result(String var1, Object var2) {
            PlaybackStatus var3 = (PlaybackStatus)var2;
            if (var3.isDone()) {
               if (AggressorClient.this.isHeadless()) {
                  GlobalDataManager.getGlobalDataManager().wait(AggressorClient.this.getData());
               }

               AggressorClient.this.engine.getEventManager().fireEvent("ready", new Stack());
               AggressorClient.this.engine.go();
            }

         }
      });
      var2.call("aggressor.ready");
   }

   public void showTime() {
      CommonUtils.Guard();
      this.split.setDividerLocation(0.5);
   }
}
