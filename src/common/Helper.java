package common;

import aggressor.dialogs.ConnectDialog;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import server.Resources;

public class Helper {
   public static final boolean HELPED = true;

   public final long[] getHelpID(Object var1) {
      long[] var2;
      if (var1 == Starter2.class) {
         var2 = new long[]{2350918770L};
         return var2;
      } else if (var1 == ConnectDialog.class) {
         var2 = new long[]{2712085043L};
         return var2;
      } else if (var1 == Resources.class) {
         var2 = new long[]{3182866266L};
         return var2;
      } else {
         System.exit(2);
         var2 = new long[]{1L};
         return var2;
      }
   }

   public final boolean startHelper(Class var1) {
      boolean var2 = true;
//      Class var3 = null;
//      var3 = Starter2.class;
//      String var4 = "common/Starter2.class";
//      long[] var5 = this.getHelpID(var3);
//      if (!B(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = ConnectDialog.class;
//      var4 = "aggressor/dialogs/ConnectDialog.class";
//      var5 = this.getHelpID(var3);
//      if (!B(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = Resources.class;
//      var4 = "server/Resources.class";
//      var5 = this.getHelpID(var3);
//      if (!B(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }

      return var2;
   }

   private static final boolean B(Class var0, Class var1, String var2, long[] var3, boolean var4) {
      return A(var0, var1, var2, var3, var4);
   }

   private static final boolean A(Class var0, Class var1, String var2, long[] var3, boolean var4) {
      ZipFile var5 = null;

      boolean var6;
      try {
         var5 = A(var0, var1);
         if (var5 != null) {
            var6 = A(var5, var2, var3);
            return var6;
         }

         var6 = !var4;
      } finally {
         try {
            if (var5 != null) {
               var5.close();
            }
         } catch (Throwable var14) {
         }

      }

      return var6;
   }

   private static final ZipFile A(Class var0, Class var1) {
      try {
         String var2 = var0.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
         String var3 = var1.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
         return var2.equals(var3) ? new ZipFile(var2) : null;
      } catch (Throwable var4) {
         return null;
      }
   }

   private static final boolean A(ZipFile var0, String var1, long[] var2) {
      try {
         ZipEntry var3 = var0.getEntry(var1);
         if (var3 == null) {
            return false;
         } else {
            return A(var3.getCrc(), var2);
         }
      } catch (Throwable var4) {
         return true;
      }
   }

   private static final boolean A(long var0, long[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var0 == var2[var3]) {
            return true;
         }
      }

      return false;
   }
}
