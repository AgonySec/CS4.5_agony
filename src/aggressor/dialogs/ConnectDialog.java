package aggressor.dialogs;

import aggressor.MultiFrame;
import aggressor.Prefs;
import aggressor.browsers.Connect;
import common.Starter2;
import dialog.DialogUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import ui.Navigator;

public class ConnectDialog extends Starter2 {
   protected MultiFrame window;
   protected Navigator options = null;
   protected boolean useAliasName;
   protected JButton viewAliasName = null;
   protected JButton viewHostName = null;

   public ConnectDialog(MultiFrame var1) {
      this.window = var1;
      this.useAliasName = Prefs.getPreferences().isSet("connection.view.alias.boolean", false);
      super.initialize(this.getClass());
   }

   public void show() {
      A();
      boolean var1 = false;
      String var2 = Prefs.getPreferences().getString("connection.last", "New Profile");
      JFrame var3 = DialogUtils.dialog("Connect", 640, 480);
      var3.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            ConnectDialog.this.window.closeConnect();
         }
      });
      this.options = new Navigator();
      this.options.addPage("New Profile", (Icon)null, "Cobalt Strike 4.5 By agony.", (new Connect(this.window)).getContent(var3, "neo", "password", "127.0.0.1", "29391", "neo@127.0.0.1"));
      List var4 = Prefs.getPreferences().getList("connection.profiles");
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         String var7 = Prefs.getPreferences().getString("connection.profiles." + var6 + ".user", "neo");
         String var8 = Prefs.getPreferences().getString("connection.profiles." + var6 + ".password", "password");
         String var9 = Prefs.getPreferences().getString("connection.profiles." + var6 + ".port", "50050");
         String var10 = Prefs.getPreferences().getString("connection.profiles." + var6 + ".alias", var7 + "@" + var6);
         String var11 = this.window.isConnected(var6) ? "*" : "";
         String var12 = var11 + var6 + "!!" + var11 + var10;
         this.options.addPage(var12, (Icon)null, "Cobalt Strike 4.5 By agony.welcome to use it.", (new Connect(this.window)).getContent(var3, var7, var8, var6, var9, var10));
         if (var2.equals(var6)) {
            var1 = true;
            var2 = var12;
         }
      }

      this.options.set(var1 ? var2 : "New Profile");
      this.options.useAlternateValue(this.useAliasName);
      this.viewAliasName = new JButton("Alias Names");
      this.viewAliasName.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            ConnectDialog.this.options.useAlternateValue(true);
            ConnectDialog.this.viewAliasName.setFont(ConnectDialog.this.viewAliasName.getFont().deriveFont(1));
            ConnectDialog.this.viewHostName.setFont(ConnectDialog.this.viewHostName.getFont().deriveFont(0));
         }
      });
      this.viewHostName = new JButton("Host Names");
      this.viewHostName.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            ConnectDialog.this.options.useAlternateValue(false);
            ConnectDialog.this.viewHostName.setFont(ConnectDialog.this.viewHostName.getFont().deriveFont(1));
            ConnectDialog.this.viewAliasName.setFont(ConnectDialog.this.viewAliasName.getFont().deriveFont(0));
         }
      });
      var3.add(DialogUtils.center(this.viewAliasName, this.viewHostName), "North");
      var3.add(this.options, "Center");
      var3.pack();
      if (this.useAliasName) {
         this.viewAliasName.setFont(this.viewAliasName.getFont().deriveFont(1));
         this.viewAliasName.requestFocusInWindow();
      } else {
         this.viewHostName.setFont(this.viewHostName.getFont().deriveFont(1));
         this.viewHostName.requestFocusInWindow();
      }

      var3.setVisible(true);
   }

   private static final void A() {
      RuntimeMXBean var0 = ManagementFactory.getRuntimeMXBean();
      List var1 = var0.getInputArguments();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var3 != null && var3.toLowerCase().contains("-javaagent:")) {
            System.exit(0);
         }
      }

   }
}
