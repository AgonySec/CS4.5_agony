package aggressor;

import aggressor.dialogs.ConnectDialog;
import aggressor.ui.UseSynthetica;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import common.Authorization;
import common.CommonUtils;
import common.License;
import common.Requirements;
import common.Starter;
import java.awt.Component;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

import sleep.parser.ParserConfig;

public class Aggressor extends Starter {
   public static final String VERSION = "4.5 (20211214) " + (License.isTrial() ? "Trial" : "Licensed");
   public static final String VERSION_SHORT = "4.5";
   private static MultiFrame B = null;

   public static MultiFrame getFrame() {
      return B;
   }

   public static void main(String[] var0) {
      Aggressor var1 = new Aggressor();
      var1.A(var0);
   }

   private final void A(String[] var1) {
      RuntimeMXBean var2 = ManagementFactory.getRuntimeMXBean();
      List var3 = var2.getInputArguments();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         if (var5 != null && var5.toLowerCase().contains("-javaagent:")) {
            String var6 = "The Cobalt Strike Client process cannot be started with a javaagent.";
            JOptionPane.showMessageDialog((Component)null, var6, (String)null, 0);
            CommonUtils.print_error(var6);
            System.exit(0);
         }
      }

//      JOptionPane.showMessageDialog(null, "欢迎使用agony版本CS!");
      ParserConfig.installEscapeConstant('c', "\u0003");
      ParserConfig.installEscapeConstant('U', "\u001f");
      ParserConfig.installEscapeConstant('o', "\u000f");
      (new UseSynthetica()).setup();
      //使用美化主题
      FlatLightLaf.setup();
      // repair file explorer
      Object DirIcon = UIManager.get("FileView.directoryIcon");
      Object DirIcon2 = UIManager.get("FileView.fileIcon");
      Object DirIcon3 = UIManager.get("FileView.computerIcon");
      Object DirIcon4 = UIManager.get("FileView.hardDriveIcon");
      Object DirIcon5 = UIManager.get("FileView.floppyDriveIcon");

      Requirements.checkGUI();
      try {
         FlatIntelliJLaf.setup();
         UIManager.put("FileView.directoryIcon", DirIcon);
         UIManager.put("FileView.fileIcon", DirIcon2);
         UIManager.put("FileView.computerIcon", DirIcon3);
         UIManager.put("FileView.hardDriveIcon", DirIcon4);
         UIManager.put("FileView.floppyDriveIcon", DirIcon5);
      } catch (Exception var11) {
         System.err.println("Failed to initialize LaF");
      }
      Requirements.checkGUI();

      License.checkLicenseGUI(new Authorization());
      B = new MultiFrame();

      super.initializeStarter(this.getClass());
      (new ConnectDialog(B)).show();
   }
}
