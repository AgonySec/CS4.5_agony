package qqwry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QQWry {
   private static final int INDEX_RECORD_LENGTH = 7;
   private static final byte REDIRECT_MODE_1 = 1;
   private static final byte REDIRECT_MODE_2 = 2;
   private static final byte STRING_END = 0;
   private final byte[] data;
   private final long indexHead;
   private final long indexTail;
   private final String databaseVersion;

   public QQWry() throws IOException {
      InputStream in = QQWry.class.getClassLoader().getResourceAsStream("qqwry.dat");
      ByteArrayOutputStream out = new ByteArrayOutputStream(10485760);
      byte[] buffer = new byte[8192];

      while(true) {
         int r = in.read(buffer);
         if (r == -1) {
            this.data = out.toByteArray();
            this.indexHead = this.readLong32(0);
            this.indexTail = this.readLong32(4);
            this.databaseVersion = this.parseDatabaseVersion();
            return;
         }

         out.write(buffer, 0, r);
      }
   }

   public QQWry(byte[] data) {
      this.data = data;
      this.indexHead = this.readLong32(0);
      this.indexTail = this.readLong32(4);
      this.databaseVersion = this.parseDatabaseVersion();
   }

   public QQWry(Path file) throws IOException {
      this(Files.readAllBytes(file));
   }

   public IPZone findIP(String ip) {
      long ipNum = this.toNumericIP(ip);
      QIndex idx = this.searchIndex(ipNum);
      return idx == null ? new IPZone(ip) : this.readIP(ip, idx);
   }

   private long getMiddleOffset(long begin, long end) {
      long records = (end - begin) / 7L;
      records >>= 1;
      if (records == 0L) {
         records = 1L;
      }

      return begin + records * 7L;
   }

   private QIndex readIndex(int offset) {
      long min = this.readLong32(offset);
      int record = this.readInt24(offset + 4);
      long max = this.readLong32(record);
      return new QIndex(min, max, record);
   }

   private int readInt24(int offset) {
      int v = this.data[offset] & 255;
      v |= this.data[offset + 1] << 8 & '\uff00';
      v |= this.data[offset + 2] << 16 & 16711680;
      return v;
   }

   private IPZone readIP(String ip, QIndex idx) {
      int pos = idx.recordOffset + 4;
      byte mode = this.data[pos];
      IPZone z = new IPZone(ip);
      if (mode == 1) {
         int offset = this.readInt24(pos + 1);
         if (this.data[offset] == 2) {
            this.readMode2(z, offset);
         } else {
            QString mainInfo = this.readString(offset);
            String subInfo = this.readSubInfo(offset + mainInfo.length);
            z.setMainInfo(mainInfo.string);
            z.setSubInfo(subInfo);
         }
      } else if (mode == 2) {
         this.readMode2(z, pos);
      } else {
         QString mainInfo = this.readString(pos);
         String subInfo = this.readSubInfo(pos + mainInfo.length);
         z.setMainInfo(mainInfo.string);
         z.setSubInfo(subInfo);
      }

      return z;
   }

   private long readLong32(int offset) {
      long v = (long)this.data[offset] & 255L;
      v |= (long)(this.data[offset + 1] << 8) & 65280L;
      v |= (long)(this.data[offset + 2] << 16) & 16711680L;
      v |= (long)(this.data[offset + 3] << 24) & 4278190080L;
      return v;
   }

   private void readMode2(IPZone z, int offset) {
      int mainInfoOffset = this.readInt24(offset + 1);
      String main = this.readString(mainInfoOffset).string;
      String sub = this.readSubInfo(offset + 4);
      z.setMainInfo(main);
      z.setSubInfo(sub);
   }

   private QString readString(int offset) {
      int i = 0;

      while(true) {
         byte b = this.data[offset + i];
         if (0 == b) {
            try {
               return new QString(new String(this.data, offset, i, "GB18030"), i + 1);
            } catch (UnsupportedEncodingException var4) {
               return new QString("", 0);
            }
         }

         ++i;
      }
   }

   private String readSubInfo(int offset) {
      byte b = this.data[offset];
      if (b != 1 && b != 2) {
         return this.readString(offset).string;
      } else {
         int areaOffset = this.readInt24(offset + 1);
         return areaOffset == 0 ? "" : this.readString(areaOffset).string;
      }
   }

   private QIndex searchIndex(long ip) {
      long head = this.indexHead;
      long tail = this.indexTail;

      while(tail > head) {
         long cur = this.getMiddleOffset(head, tail);
         QIndex idx = this.readIndex((int)cur);
         if (ip >= idx.minIP && ip <= idx.maxIP) {
            return idx;
         }

         if (cur == head || cur == tail) {
            return idx;
         }

         if (ip < idx.minIP) {
            tail = cur;
         } else {
            if (ip <= idx.maxIP) {
               return idx;
            }

            head = cur;
         }
      }

      return null;
   }

   private long toNumericIP(String s) {
      String[] parts = s.split("\\.");
      if (parts.length != 4) {
         throw new IllegalArgumentException("ip=" + s);
      } else {
         long n = Long.parseLong(parts[0]) << 24;
         n += Long.parseLong(parts[1]) << 16;
         n += Long.parseLong(parts[2]) << 8;
         n += Long.parseLong(parts[3]);
         return n;
      }
   }

   public String getDatabaseVersion() {
      return this.databaseVersion;
   }

   String parseDatabaseVersion() {
      Pattern dbVerPattern = Pattern.compile("(\\d+)年(\\d+)月(\\d+)日.*");
      IPZone ipz = this.findIP("255.255.255.255");
      Matcher m = dbVerPattern.matcher(ipz.getSubInfo());
      return m.matches() && m.groupCount() == 3 ? String.format("%s.%s.%s", m.group(1), m.group(2), m.group(3)) : "0.0.0";
   }

   private static class QString {
      public final String string;
      public final int length;

      public QString(String string, int length) {
         this.string = string;
         this.length = length;
      }
   }

   private static class QIndex {
      public final long minIP;
      public final long maxIP;
      public final int recordOffset;

      public QIndex(long minIP, long maxIP, int recordOffset) {
         this.minIP = minIP;
         this.maxIP = maxIP;
         this.recordOffset = recordOffset;
      }
   }
}
