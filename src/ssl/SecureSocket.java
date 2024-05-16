package ssl;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import sleep.bridges.io.IOObject;

public class SecureSocket {
   protected SSLSocket socket = null;
   private static byte[] A = null;

   public static SSLSocketFactory getMyFactory(ArmitageTrustListener var0) throws Exception {
      SSLContext var1 = SSLContext.getInstance("SSL");
      var1.init((KeyManager[])null, new TrustManager[]{new ArmitageTrustManager(var0)}, new SecureRandom());
      SSLSocketFactory var2 = var1.getSocketFactory();
      return var2;
   }

   public SecureSocket(String var1, int var2, ArmitageTrustListener var3) throws Exception {
      SSLSocketFactory var4 = getMyFactory(var3);
      this.socket = (SSLSocket)var4.createSocket(var1, var2);
      this.socket.setSoTimeout(4048);
      this.socket.startHandshake();
   }

   public SecureSocket(Socket var1) throws Exception {
      SSLContext var2 = SSLContext.getInstance("SSL");
      var2.init((KeyManager[])null, new TrustManager[]{new ITrustYouDude()}, new SecureRandom());
      SSLSocketFactory var3 = var2.getSocketFactory();
      this.socket = (SSLSocket)var3.createSocket(var1, var1.getInetAddress().getHostName(), var1.getPort(), true);
      this.socket.setUseClientMode(true);
      this.socket.setSoTimeout(4048);
      this.socket.startHandshake();
   }

   public static byte[] readbytes(InputStream var0) throws IOException {
      Class var1 = SecureSocket.class;
      synchronized(SecureSocket.class) {
         if (A == null) {
            A = new byte[1048576];
         }

         int var2 = var0.read(A);
         return var2 > 0 ? Arrays.copyOf(A, var2) : new byte[0];
      }
   }

   public void authenticate(String var1) {
      try {
         this.socket.setSoTimeout(0);
         DataInputStream var2 = new DataInputStream(this.socket.getInputStream());
         DataOutputStream var3 = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
         var3.writeInt(13217);
         var3.writeByte(var1.length());

         int var4;
         for(var4 = 0; var4 < var1.length(); ++var4) {
            var3.writeByte((byte)var1.charAt(var4));
         }

         for(var4 = var1.length(); var4 < 256; ++var4) {
            var3.writeByte(65);
         }

         var3.flush();
         var4 = var2.readInt();
         if (var4 != 51966) {
            throw new RuntimeException("authentication failure!");
         }
      } catch (RuntimeException var5) {
         throw var5;
      } catch (Exception var6) {
         var6.printStackTrace();
         throw new RuntimeException(var6);
      }
   }

   public IOObject client() {
      try {
         IOObject var1 = new IOObject();
         var1.openRead(this.socket.getInputStream());
         var1.openWrite(new BufferedOutputStream(this.socket.getOutputStream(), 65536));
         this.socket.setSoTimeout(0);
         return var1;
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   public Socket getSocket() {
      return this.socket;
   }
}
