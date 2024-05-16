package common;

import aggressor.Aggressor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import server.ServerUtils;
import server.TeamServer;

public abstract class Starter2 {
   public static final boolean S2MODE = true;

   protected final void initialize(Class var1) {
      if (!this.A(var1)) {
         System.exit(0);
      }

   }

   public final boolean isReady(Class var1) {
      return this.A(var1);
   }

   private final long[] A(Object var1) {
      long[] var2;
      if (var1 == Starter.class) {
         var2 = new long[]{294837039L};
         return var2;
      } else if (var1 == TeamServer.class) {
         var2 = new long[]{2640655532L};
         return var2;
      } else if (var1 == Aggressor.class) {
         var2 = new long[]{408700496L};
         return var2;
      } else if (var1 == ServerUtils.class) {
         var2 = new long[]{1304112678L};
         return var2;
      } else {
         System.exit(1);
         var2 = new long[]{1L};
         return var2;
      }
   }

   private final boolean A(Class var1) {
      boolean var2 = true;
//      Class var3 = null;
//      var3 = Starter.class;
//      String var4 = "common/Starter.class";
//      long[] var5 = this.A((Object)var3);
//      if (!B(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = TeamServer.class;
//      var4 = "server/TeamServer.class";
//      var5 = this.A((Object)var3);
//      if (!B(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = Aggressor.class;
//      var4 = "aggressor/Aggressor.class";
//      var5 = this.A((Object)var3);
//      if (!B(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = ServerUtils.class;
//      var4 = "server/ServerUtils.class";
//      var5 = this.A((Object)var3);
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
