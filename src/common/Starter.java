package common;

import aggressor.TeamServerProps;
import aggressor.dialogs.PayloadGeneratorDialog;
import beacon.BeaconC2;
import beacon.BeaconData;
import dns.SleeveSecurity;

public abstract class Starter {
   public static final boolean S1MODE = true;

   protected final void initializeStarter(Class var1) {
      if (!this.A(var1)) {
         System.exit(0);
      }

   }

   public final boolean isStartable(Class var1) {
      return this.A(var1);
   }

   private final boolean A(Class var1) {
      boolean var2 = true;
//      Class var3 = null;
//      var3 = BeaconC2.class;
//      String var4 = "beacon/BeaconC2.class";
//      long var5 = 3137186602L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      ++var5;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = BeaconData.class;
//      var4 = "beacon/BeaconData.class";
//      var5 = 199064708L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      --var5;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = AuthCrypto.class;
//      var4 = "common/AuthCrypto.class";
//      var5 = 895661977L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      --var5;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var4 = "resources/authkey.pub";
//      var5 = 1661186542L;
//      if (!Initializer.isFileOK(var1, var4, var5, true)) {
//         var2 = false;
//      }
//
//      --var5;
//      if (Initializer.isFileOK(var1, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = License.class;
//      var4 = "common/License.class";
//      var5 = 1993230717L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      --var5;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = Authorization.class;
//      var4 = "common/Authorization.class";
//      var5 = 2257287691L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      --var5;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = SleevedResource.class;
//      var4 = "common/SleevedResource.class";
//      var5 = 3881376138L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      --var5;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = SleeveSecurity.class;
//      var4 = "dns/SleeveSecurity.class";
//      var5 = 3962922538L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var5 -= 159L;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = BaseArtifactUtils.class;
//      var4 = "common/BaseArtifactUtils.class";
//      var5 = 3809676837L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      ++var5;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = BaseResourceUtils.class;
//      var4 = "common/BaseResourceUtils.class";
//      var5 = 510216517L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var5 /= 2L;
//      if (Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = TeamServerProps.class;
//      var4 = "aggressor/TeamServerProps.class";
//      var5 = 2434092077L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }
//
//      var3 = PayloadGeneratorDialog.class;
//      var4 = "aggressor/dialogs/PayloadGeneratorDialog.class";
//      var5 = 3501849500L;
//      if (!Initializer.isOK(var1, var3, var4, var5, true)) {
//         var2 = false;
//      }

      return var2;
   }
}
