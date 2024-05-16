package aggressor.browsers;

import aggressor.Aggressor;
import aggressor.AggressorClient;
import aggressor.MultiFrame;
import aggressor.Prefs;
import aggressor.dialogs.ConnectDialog;
import common.Callback;
import common.CommonUtils;
import common.TeamQueue;
import common.TeamSocket;
import dialog.DialogListener;
import dialog.DialogManager;
import dialog.DialogUtils;
import dialog.SafeDialogCallback;
import dialog.SafeDialogs;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ssl.ArmitageTrustListener;
import ssl.SecureSocket;

public class Connect implements DialogListener, Callback, ArmitageTrustListener {
   protected MultiFrame window;
   protected TeamQueue tqueue = null;
   protected String alias = "";
   protected Map m_options = null;
   private boolean Ď = false;
   private String č = null;
   private String ď = null;

   public Connect(MultiFrame var1) {
      this.window = var1;
   }

   public Connect(MultiFrame var1, boolean var2, String var3) {
      this.window = var1;
      this.Ď = var2;
      this.č = var3;
   }

   public boolean trust(String var1) {
      HashSet var2 = new HashSet(Prefs.getPreferences().getList("trusted.servers"));
      if (var2.contains(var1)) {
         return true;
      } else {
         int var3 = JOptionPane.showConfirmDialog((Component)null, "The team server's fingerprint is:\n\n<html><body><b>" + var1 + "</b></body></html>\n\nDoes this match the fingerprint shown when the team server started?", "VerifyFingerprint", 0);
         if (var3 == 0) {
            Prefs.getPreferences().appendList("trusted.servers", var1);
            Prefs.getPreferences().save();
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean A(Map var1) {
      String var2 = var1.get("user") + "";
      String var3 = var1.get("host") + "";
      String var4 = var1.get("port") + "";
      String var5 = var1.get("alias") + "";
      StringBuilder var6 = new StringBuilder();
      if (CommonUtils.isNullOrEmpty(var5)) {
         var6.append((var6.length() > 0 ? "\n" : "") + "Alias name can not be empty.");
      } else {
         if ('*' == var5.charAt(0)) {
            var6.append((var6.length() > 0 ? "\n" : "") + "Alias name can not start with *.");
         }

         if (this.window.checkCollision(var5)) {
            var6.append((var6.length() > 0 ? "\n" : "") + "Alias name already in use.");
         }
      }

      if (CommonUtils.isNullOrEmpty(var3)) {
         var6.append((var6.length() > 0 ? "\n" : "") + "Host name can not be empty.");
      }

      if (CommonUtils.isNullOrEmpty(var4)) {
         var6.append((var6.length() > 0 ? "\n" : "") + "Port can not be empty.");
      } else if (!CommonUtils.isNumber(var4)) {
         var6.append((var6.length() > 0 ? "\n" : "") + "Port needs to be a number.");
      }

      if (CommonUtils.isNullOrEmpty(var2)) {
         var6.append((var6.length() > 0 ? "\n" : "") + "User name can not be empty.");
      }

      if (var6.length() > 0) {
         Prefs.getPreferences().set("connection.last", this.ď != null ? this.ď : "New Profile");
         (new ConnectDialog(this.window)).show();
         DialogUtils.showError(var6.toString());
         return false;
      } else {
         return true;
      }
   }
   public  static String aggressorRemoteIp;
   public void dialogAction(ActionEvent var1, Map var2) {
      if (this.A(var2)) {
         this.m_options = var2;
         String var3 = var2.get("user") + "";
         String var4 = var2.get("host") + "";
         String var5 = var2.get("port") + "";
         String var6 = var2.get("pass") + "";
         this.alias = var2.get("alias") + "";
         Prefs.getPreferences().set("connection.last", var4);
         Prefs.getPreferences().appendList("connection.profiles", var4);
         Prefs.getPreferences().set("connection.profiles." + var4 + ".user", var3);
         Prefs.getPreferences().set("connection.profiles." + var4 + ".port", var5);
         Prefs.getPreferences().set("connection.profiles." + var4 + ".password", var6);
         Prefs.getPreferences().set("connection.profiles." + var4 + ".alias", this.alias);
         Prefs.getPreferences().save();
         aggressorRemoteIp = var4;
         try {
            SecureSocket var7 = new SecureSocket(var4, Integer.parseInt(var5), this);
            var7.authenticate(var6);
            this.tqueue = new TeamQueue(new TeamSocket(var7.getSocket()));
            this.tqueue.call("aggressor.authenticate", CommonUtils.args(var3, var6, Aggressor.VERSION), this);
         } catch (Exception var9) {
            String var8 = this.Ď ? "again?" : "another connection?";
            SafeDialogs.askYesNoBoth(var9.getMessage() + "\n\nA Cobalt Strike team server is not available on\n" + "the specified host and port. You must start a\n" + "Cobalt Strike team server first.\n\n" + "Would you like to try " + var8, "Connection Error", new SafeDialogCallback() {
               public void dialogResult(String var1) {
                  if ("no".equals(var1)) {
                     CommonUtils.runSafe(new Runnable() {
                        public void run() {
                           Connect.this.window.quit(Connect.this.m_options);
                        }
                     });
                  } else {
                     if (Connect.this.Ď) {
                        (new Connect(Connect.this.window, true, Connect.this.alias)).dialogAction((ActionEvent)null, Connect.this.m_options);
                     } else {
                        (new ConnectDialog(Connect.this.window)).show();
                     }

                  }
               }
            });
         }

      }
   }

   public void result(String var1, Object var2) {
      if ("aggressor.authenticate".equals(var1)) {
         String var3 = var2 + "";
         if (var3.equals("SUCCESS")) {
            this.tqueue.call("aggressor.metadata", CommonUtils.args(System.currentTimeMillis()), this);
         } else {
            DialogUtils.showError(var3);
            this.tqueue.close();
         }
      } else if ("aggressor.metadata".equals(var1)) {
         final AggressorClient var4 = new AggressorClient(this.window, this.tqueue, (Map)var2, this.m_options);
         CommonUtils.runSafe(new Runnable() {
            public void run() {
               Connect.this.window.addButton(Connect.this.alias, var4, Connect.this.č == null ? Connect.this.alias : Connect.this.č);
               var4.showTime();
            }
         });
      }

   }

   public JComponent getContent(JFrame var1, String var2, String var3, String var4, String var5, String var6) {
      JPanel var7 = new JPanel();
      var7.setLayout(new BorderLayout());
      this.ď = var4;
      DialogManager var8 = new DialogManager(var1);
      var8.addDialogListener(this);
      var8.set("user", var2);
      var8.set("pass", var3);
      var8.set("host", var4);
      var8.set("port", var5);
      var8.set("alias", var6);
      var8.text("alias", "Alias:", 20);
      var8.text("host", "Host:", 20);
      var8.text("port", "Port:", 10);
      var8.text("user", "User:", 20);
      var8.password("pass", "Password:", 20);
      JButton var9 = var8.action("Connect");
      JButton var10 = var8.help("https://www.cobaltstrike.com/help-setup-collaboration");
      var7.add(var8.layout(), "Center");
      var7.add(DialogUtils.center(var9, var10), "South");
      return var7;
   }
}
