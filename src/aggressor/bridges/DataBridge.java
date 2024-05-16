package aggressor.bridges;

import aggressor.AggressorClient;
import aggressor.DataUtils;
import common.Callback;
import common.CommonUtils;
import common.DownloadFileSimple;
import common.DownloadNotify;
import common.LoggedEvent;
import common.ScriptUtils;
import common.TeamQueue;
import cortana.Cortana;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import sleep.bridges.BridgeUtilities;
import sleep.engine.ObjectUtilities;
import sleep.interfaces.Function;
import sleep.interfaces.Loadable;
import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;
import sleep.runtime.SleepUtils;

import static aggressor.browsers.Connect.aggressorRemoteIp;

public class DataBridge implements Function, Loadable {
   protected Cortana engine;
   protected TeamQueue conn;
   protected AggressorClient client;

   public DataBridge(AggressorClient var1, Cortana var2, TeamQueue var3) {
      this.client = var1;
      this.engine = var2;
      this.conn = var3;
   }

   public static Map getKeys() {
      HashMap var0 = new HashMap();
      var0.put("&applications", "applications");
      var0.put("&archives", "archives");
      var0.put("&credentials", "credentials");
      var0.put("&downloads", "downloads");
      var0.put("&keystrokes", "keystrokes");
      var0.put("&pivots", "socks");
      var0.put("&screenshots", "screenshots");
      var0.put("&services", "services");
      var0.put("&sites", "sites");
      var0.put("&targets", "targets");
      return var0;
   }

   public void scriptLoaded(ScriptInstance var1) {
      Cortana.put(var1, "&mynick", this);
      Cortana.put(var1, "&tstamp", this);
      Cortana.put(var1, "&dstamp", this);
      Cortana.put(var1, "&tokenToEmail", this);
      Cortana.put(var1, "&localip", this);
      Cortana.put(var1, "&sync_download", this);
      Cortana.put(var1, "&hosts", this);
      Cortana.put(var1, "&host_update", this);
      Cortana.put(var1, "&host_delete", this);
      Cortana.put(var1, "&host_info", this);
      Cortana.put(var1, "&credential_add", this);
      Cortana.put(var1,"&serverip",this);
      Iterator var2 = getKeys().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Cortana.put(var1, (String)var3.getKey(), this);
      }

      Cortana.put(var1, "&data_query", this);
      Cortana.put(var1, "&data_keys", this);
      Cortana.put(var1, "&resetData", this);
   }

   public static String getStringOrNull(Stack var0) {
      if (var0.isEmpty()) {
         return null;
      } else {
         Scalar var1 = (Scalar)var0.pop();
         return SleepUtils.isEmptyScalar(var1) ? null : var1.toString();
      }
   }

   public void scriptUnloaded(ScriptInstance var1) {
   }

   public Scalar evaluate(String var1, ScriptInstance var2, Stack var3) {
      if (var1.equals("&mynick")) {
         return SleepUtils.getScalar(DataUtils.getNick(this.client.getData()));
      } else if (var1.equals("&localip")) {
         return SleepUtils.getScalar(DataUtils.getLocalIP(this.client.getData()));
      } else if(var1.equals("&serverip")){
         return SleepUtils.getScalar(aggressorRemoteIp);
      }
      else {
         long var18;
         if (var1.equals("&dstamp")) {
            var18 = BridgeUtilities.getLong(var3);
            return SleepUtils.getScalar(CommonUtils.formatDate(var18));
         } else if (var1.equals("&tstamp")) {
            var18 = BridgeUtilities.getLong(var3);
            return SleepUtils.getScalar(CommonUtils.formatTime(var18));
         } else {
            String var13;
            if (var1.equals("&tokenToEmail")) {
               var13 = BridgeUtilities.getString(var3, "");
               return SleepUtils.getScalar(DataUtils.TokenToEmail(var13));
            } else {
               String[] var4;
               String var7;
               if (var1.equals("&host_delete")) {
                  var4 = ScriptUtils.ArrayOrString(var3);

                  for(int var5 = 0; var5 < var4.length; ++var5) {
                     HashMap var6 = new HashMap();
                     var6.put("address", var4[var5]);
                     var7 = CommonUtils.TargetKey(var6);
                     this.client.getConnection().call("targets.remove", CommonUtils.args(var7));
                  }

                  this.client.getConnection().call("targets.push");
               } else {
                  String var15;
                  String var17;
                  if (var1.equals("&host_update")) {
                     var4 = ScriptUtils.ArrayOrString(var3);
                     var15 = getStringOrNull(var3);
                     var17 = getStringOrNull(var3);
                     double var21 = BridgeUtilities.getDouble(var3, 0.0);
                     String var9 = getStringOrNull(var3);

                     for(int var10 = 0; var10 < var4.length; ++var10) {
                        HashMap var11 = new HashMap();
                        var11.put("address", CommonUtils.trim(var4[var10]));
                        if (var15 != null) {
                           var11.put("name", var15);
                        }

                        if (var9 != null) {
                           var11.put("note", var9);
                        }

                        if (var17 != null) {
                           var11.put("os", var17);
                        }

                        if (var21 != 0.0) {
                           var11.put("version", var21 + "");
                        }

                        String var12 = CommonUtils.TargetKey(var11);
                        this.client.getConnection().call("targets.update", CommonUtils.args(var12, var11));
                     }

                     this.client.getConnection().call("targets.push");
                  } else {
                     if ("&host_info".equals(var1)) {
                        var13 = BridgeUtilities.getString(var3, "");
                        Map var22 = this.client.getData().getModelDirect("targets", var13);
                        return ScriptUtils.IndexOrMap(var22, var3);
                     }

                     if ("&hosts".equals(var1)) {
                        List var16 = (List)this.client.getData().getDataSafe("targets");
                        LinkedList var19 = new LinkedList();
                        Iterator var23 = var16.iterator();

                        while(var23.hasNext()) {
                           Map var25 = (Map)var23.next();
                           var19.add(var25.get("address"));
                        }

                        return ScriptUtils.convertAll(var19);
                     }

                     if ("&data_keys".equals(var1)) {
                        return ScriptUtils.convertAll(this.client.getData().getDataKeys());
                     }

                     if ("&data_query".equals(var1)) {
                        var13 = BridgeUtilities.getString(var3, "");
                        return ScriptUtils.convertAll(this.client.getData().getDataSafe(var13));
                     }

                     if ("&resetData".equals(var1)) {
                        this.client.getConnection().call("aggressor.event", CommonUtils.args(LoggedEvent.Notify(DataUtils.getNick(this.client.getData()) + " reset the data model.")), (Callback)null);
                        this.client.getConnection().call("aggressor.reset_data");
                     } else if ("&credential_add".equals(var1)) {
                        var13 = BridgeUtilities.getString(var3, "");
                        var15 = BridgeUtilities.getString(var3, "");
                        var17 = BridgeUtilities.getString(var3, "");
                        var7 = BridgeUtilities.getString(var3, "manual");
                        String var8 = BridgeUtilities.getString(var3, "");
                        HashMap var26 = new HashMap();
                        var26.put("user", var13);
                        var26.put("password", var15);
                        var26.put("realm", var17);
                        var26.put("source", var7);
                        var26.put("host", var8);
                        this.client.getConnection().call("credentials.add", CommonUtils.args(CommonUtils.CredKey(var26), var26));
                        this.client.getConnection().call("credentials.push");
                     } else if ("&sync_download".equals(var1)) {
                        var13 = BridgeUtilities.getString(var3, "");
                        var15 = BridgeUtilities.getString(var3, "");
                        DownloadNotify var20 = null;
                        if (!var3.isEmpty()) {
                           Scalar var24 = (Scalar)var3.pop();
                           var20 = (DownloadNotify)ObjectUtilities.buildArgument(DownloadNotify.class, var24, var2);
                        }

                        (new DownloadFileSimple(this.client.getConnection(), var13, new File(var15), var20)).start();
                     } else {
                        Map var14 = getKeys();
                        if (var14.containsKey(var1)) {
                           var15 = (String)var14.get(var1);
                           return ScriptUtils.convertAll(this.client.getData().getDataSafe(var15));
                        }
                     }
                  }
               }

               return SleepUtils.getEmptyScalar();
            }
         }
      }
   }
}
