package qqwry;

public class IPZone {
   private final String ip;
   private String mainInfo = "";
   private String subInfo = "";

   public IPZone(String ip) {
      this.ip = ip;
   }

   public String getIp() {
      return this.ip;
   }

   public String getMainInfo() {
      return this.mainInfo;
   }

   public String getSubInfo() {
      return this.subInfo;
   }

   public void setMainInfo(String info) {
      this.mainInfo = info;
   }

   public void setSubInfo(String info) {
      this.subInfo = info;
   }

   public String toString() {
      return this.mainInfo + this.subInfo;
   }
}
