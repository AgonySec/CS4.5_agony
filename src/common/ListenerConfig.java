package common;

import aggressor.DataUtils;
import beacon.BeaconPayload;
import c2profile.Profile;

public class ListenerConfig {
   protected boolean haspublic;
   protected String pname;
   protected String subhost;
   protected int txtlen;
   protected int garbage_bytes = 0;
   protected String uri_x86;
   protected String uri_x64;
   protected String qstring;
   protected String headers;
   protected long stage_offset;
   protected String useragent;
   protected boolean usescookie;
   protected int watermark;

   public ListenerConfig(Profile var1, ScListener var2) {
      this.pname = var1.getString(".pipename_stager");
      this.subhost = var1.getString(".dns-beacon.dns_stager_subhost");
      this.haspublic = var1.option(".host_stage");
      this.useragent = BeaconPayload.randua(var1);
      this.uri_x86 = var1.getString(".http-stager.uri_x86");
      this.uri_x64 = var1.getString(".http-stager.uri_x64");
      this.qstring = var1.getQueryString(".http-stager.client");
      this.headers = var1.getHeaders(".http-stager.client", var2.getHostHeader());
      this.stage_offset = var1.getHTTPContentOffset(".http-stager.server");
      this.garbage_bytes = var1.getString(".bind_tcp_garbage").length();
      this.txtlen = var1.getString(".dns-beacon.dns_stager_prepend").length();
      this.watermark = var1.getInt(".watermark");
      this.usescookie = var1.usesCookie(".http-stager.client");
      this.pname = DataUtils.getPipeName(this.pname, var2.getPublicKey());
   }

   public boolean usesCookie() {
      return this.usescookie;
   }

   public String pad(String var1, int var2) {
      StringBuffer var3 = new StringBuffer();
      var3.append(var1);

      while(var3.length() < var2) {
         if (this.watermark == 0) {
//            var3.append("5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*\u0000");
            var3.append((char)CommonUtils.rand(255));
         } else {
            var3.append((char)CommonUtils.rand(255));
         }
      }

      return var3.toString().substring(0, var2);
   }

   public String getWatermark() {
      Packer var1 = new Packer();
      var1.addInt(this.watermark);
      return CommonUtils.bString(var1.getBytes());
   }

   public int getDNSOffset() {
      return this.txtlen;
   }

   public int getBindGarbageLength() {
      return this.garbage_bytes;
   }

   public long getHTTPStageOffset() {
      return this.stage_offset;
   }

   public String getHTTPHeaders() {
      return this.headers;
   }

   public String getQueryString() {
      return this.qstring;
   }

   public String getURI() {
      return !"".equals(this.uri_x86) ? this.uri_x86 : CommonUtils.MSFURI();
   }

   public String getURI_X64() {
      return !"".equals(this.uri_x64) ? this.uri_x64 : CommonUtils.MSFURI_X64();
   }

   public String getUserAgent() {
      return this.useragent;
   }

   public String getStagerPipe() {
      return this.pname;
   }

   public String getDNSSubhost() {
      return this.subhost;
   }

   public boolean hasPublicStage() {
      return this.haspublic;
   }
}
