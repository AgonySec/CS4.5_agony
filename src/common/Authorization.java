package common;

import java.io.File;
import java.util.Calendar;

public class Authorization {
   protected int watermark = 0;
   protected String validto = "";
   protected String error = null;
   protected boolean valid = false;
   protected String watermarkHash = "";

   public Authorization() {
//      String var1 = CommonUtils.canonicalize("cobaltstrike.auth");
//      if (!(new File(var1)).exists()) {
//         try {
//            File var2 = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
//            if (var2.getName().toLowerCase().endsWith(".jar")) {
//               var2 = var2.getParentFile();
//            }
//
//            var1 = (new File(var2, "cobaltstrike.auth")).getAbsolutePath();
//         } catch (Exception var22) {
//            MudgeSanity.logException("trouble locating auth file", var22, false);
//         }
//      }
//
//      byte[] var23 = CommonUtils.readFile(var1);
//      if (var23.length == 0) {
//         this.error = "Could not read " + var1;
//      } else {
//         AuthCrypto var3 = new AuthCrypto();
//         byte[] var4 = var3.decrypt(var23);

         byte[] var4 = {1, -55, -61, 127, 0, 1, -122, -96, 45, 16, 27, -27, -66, 82, -58, 37, 92, 51, 85, -114, -118, 28, -74, 103, -53, 6, 16, -128, -29, 42, 116, 32, 96, -72, -124, 65, -101, -96, -63, 113, -55, -86, 118, 16, -78, 13, 72, 122, -35, -44, 113, 52, 24, -14, -43, -93, -82, 2, -89, -96, 16, 58, 68, 37, 73, 15, 56, -102, -18, -61, 18, -67, -41, 88, -83, 43, -103, 16, 94, -104, 25, 74, 1, -58, -76, -113, -91, -126, -90, -87, -4, -69, -110, -42, 16, -13, -114, -77, -47, -93, 53, -78, 82, -75, -117, -62, -84, -34, -127, -75, 66, 0, 0, 0, 24, 66, 101, 117, 100, 116, 75, 103, 113, 110, 108, 109, 48, 82, 117, 118, 102, 43, 86, 89, 120, 117, 119, 61, 61};
         if (var4.length == 0) {
//            this.error = var3.error();
         } else {
            try {
               DataParser var5 = new DataParser(var4);
               var5.big();
               int var6 = var5.readInt();
               this.watermark = var5.readInt();
               byte var7 = var5.readByte();
               if (var7 < 45) {
                  this.error = "Authorization file is not for Cobalt Strike 4.5+";
                  return;
               }

               byte var8 = var5.readByte();
               var5.readBytes(var8);
               byte var10 = var5.readByte();
               var5.readBytes(var10);
               byte var12 = var5.readByte();
               var5.readBytes(var12);
               byte var14 = var5.readByte();
               var5.readBytes(var14);
               byte var16 = var5.readByte();
               var5.readBytes(var16);
               byte var18 = var5.readByte();
               byte[] var19 = var5.readBytes(var18);
               if (var7 < 45) {
                  CommonUtils.print_error("Authorization version " + var7 + " does not support watermark hash.");
               } else if (!var5.more()) {
                  CommonUtils.print_error("Authorization data is incomplete. Watermark hash is not available.");
               } else {
                  int var20 = var5.readInt();
                  if (var20 > 0) {
                     this.watermarkHash = var5.readString(var20);
                  }
               }

               if (29999999 == var6) {
                  this.validto = "forever";
                  MudgeSanity.systemDetail("valid to", "perpetual");
               } else {
                  if (!this.A(var6)) {
                     this.error = "Valid to date (" + var6 + ") is invalid";
                     return;
                  }

                  this.validto = "20" + var6;
                  MudgeSanity.systemDetail("valid to", CommonUtils.formatDateAny("MMMMM d, YYYY", this.getExpirationDate()));
               }

               this.valid = true;
               MudgeSanity.systemDetail("id", this.watermark + "");
               SleevedResource.Setup(var19);
            } catch (Exception var21) {
               MudgeSanity.logException("auth file parsing", var21, false);
            }

         }
   }

   private final boolean A(int var1) {
      if (var1 > 999999) {
         return false;
      } else {
         int var2 = 2000 + var1 / 10000;
         int var3 = var1 % 10000 / 100;
         int var4 = var1 % 100;
         byte var5 = 10;
         int var6 = Calendar.getInstance().get(1) + var5;
         if (var2 > var6) {
            return false;
         } else {
            Calendar var7 = Calendar.getInstance();
            var7.clear();
            var7.setLenient(false);

            try {
               var7.set(var2, var3 - 1, var4);
               var7.getTime();
               return true;
            } catch (Throwable var9) {
               return false;
            }
         }
      }
   }

   public boolean isPerpetual() {
      return "forever".equals(this.validto);
   }

   public boolean isValid() {
      return this.valid;
   }

   public String getError() {
      return this.error;
   }

   public String getWatermark() {
      return this.watermark + "";
   }

   public String getWatermarkHash() {
      return this.watermarkHash + "";
   }

   public long getExpirationDate() {
      return CommonUtils.parseDate(this.validto, "yyyyMMdd");
   }

   public boolean isExpired() {
      return System.currentTimeMillis() > this.getExpirationDate() + B(1);
   }

   public String whenExpires() {
      long var1 = (this.getExpirationDate() + B(1) - System.currentTimeMillis()) / B(1);
      if (var1 == 1L) {
         return "1 day (" + CommonUtils.formatDateAny("MMMMM d, YYYY", this.getExpirationDate()) + ")";
      } else {
         return var1 <= 0L ? "TODAY (" + CommonUtils.formatDateAny("MMMMM d, YYYY", this.getExpirationDate()) + ")" : var1 + " days (" + CommonUtils.formatDateAny("MMMMM d, YYYY", this.getExpirationDate()) + ")";
      }
   }

   public boolean isAlmostExpired() {
      long var1 = System.currentTimeMillis() + B(30);
      return var1 > this.getExpirationDate();
   }

   private static final long B(int var0) {
      return 86400000L * (long)var0;
   }
}
