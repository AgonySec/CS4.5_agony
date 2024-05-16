package cloudstrike;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServer extends NanoHTTPD {
   protected Map hooks = new HashMap();
   protected Map hooksSecondary = new HashMap();
   protected Map always = new HashMap();
   protected Map hosts = new HashMap();
   protected String blockedUAList = "";
   protected String[] blockedUAArray = new String[0];
   protected String allowedUAList = "";
   protected String[] allowedUAArray = new String[0];
   protected List weblisteners = new LinkedList();

   public void associate(String uri, String host) {
      this.hosts.put(uri, host);
   }

   public void addWebListener(WebListener l) {
      this.weblisteners.add(l);
   }

   public List sites() {
      List temp = new LinkedList();

      HashMap result;
      for(Iterator i = this.hooks.entrySet().iterator(); i.hasNext(); temp.add(result)) {
         Map.Entry entry = (Map.Entry)i.next();
         String uri = entry.getKey() + "";
         String desc = entry.getValue() + "";
         String type = ((WebService)entry.getValue()).getType();
         String host = (String)this.hosts.get(uri);
         result = new HashMap();
         result.put("URI", uri);
         result.put("Description", desc);
         result.put("Type", type);
         result.put("Host", host);
         if (this.isSSL()) {
            result.put("Proto", "https://");
         } else {
            result.put("Proto", "http://");
         }
      }

      return temp;
   }

   public boolean isRegistered(String uri) {
      return this.hooks.containsKey(uri);
   }

   public void register(String uri, WebService service) {
      if (this.hooks.containsKey(uri)) {
         throw new RuntimeException("URI " + uri + " hosts: " + this.get(uri).getType());
      } else {
         this.always.remove(uri);
         this.hooks.put(uri, service);
      }
   }

   public void registerSecondary(String uri, WebService service) {
      this.always.remove(uri);
      this.hooksSecondary.put(uri, service);
   }

   public void setSpecialPostURI(String uri) {
      this.always.put(uri, Boolean.TRUE);
   }

   public void blockUseragents(String blockedList) {
      if (blockedList != null && blockedList.trim().length() != 0) {
         this.blockedUAList = blockedList.toLowerCase().trim();
      } else {
         this.blockedUAList = "";
      }

      this.blockedUAArray = this.blockedUAList.split(",");
   }

   public void allowUseragents(String allowedList) {
      if (allowedList != null && allowedList.trim().length() != 0) {
         this.allowedUAList = allowedList.toLowerCase().trim();
         this.allowedUAArray = this.allowedUAList.split(",");
      } else {
         this.allowedUAList = "";
         this.allowedUAArray = new String[0];
      }

   }

   public boolean alwaysRaw(String uri) {
      if (this.always.containsKey(uri)) {
         return true;
      } else {
         Iterator i = this.always.keySet().iterator();

         String hook;
         do {
            if (!i.hasNext()) {
               return false;
            }

            hook = i.next() + "";
         } while(!uri.startsWith(hook));

         return true;
      }
   }

   public WebService get(String uri) {
      return (WebService)this.hooks.get(uri);
   }

   public boolean deregister(String uri) {
      this.hooks.remove(uri);
      this.always.remove(uri);
      this.hosts.remove(uri);
      if (this.hooks.size() == 0) {
         this.stop();
         return true;
      } else {
         return false;
      }
   }

   protected void fireWebListener(String uri, String method, Properties header, Properties param, String desc, boolean primary, String response, long size) {
      Iterator i = this.weblisteners.iterator();

      while(i.hasNext()) {
         ((WebListener)i.next()).receivedClient(uri, method, header, param, desc, this.getPort(), primary, response, size);
      }

   }

   protected Response processResponse(String uri, String method, Properties header, Properties param, boolean primary, WebService service, Response r) {
      String desc;
      if (service == null) {
         desc = null;
      } else {
         desc = service.getType() + " " + service.toString();
      }

      String resp = r.status;
      long size = r.size;
      if (service == null || !service.suppressEvent(uri)) {
         this.fireWebListener(uri, method, header, param, desc, primary, resp, size);
      }

      return r;
   }

   public static long checksum8(String text) {
      if (text.length() < 4) {
         return 0L;
      } else {
         text = text.replace("/", "");
         long sum = 0L;

         for(int x = 0; x < text.length(); ++x) {
            sum += (long)text.charAt(x);
         }

//         return sum % 256L;
         return sum;
      }
   }

   public static boolean isStager(String uri) {
      return checksum8(uri) == 2247L;//A60BED6CD0B5A4E80523DB2D91682DC9.jpg
   }

   public static boolean isStagerX64(String uri) {
      return checksum8(uri) == 2288L; //ECFB2CA9428299F31F0BBB9B5EA28DC3.jpg
   }

   public static boolean isStagerStrict(String uri) {
      return isStager(uri) && uri.length() == 5;
   }

   public static boolean isStagerX64Strict(String uri) {
      return isStagerX64(uri) && uri.length() == 5;
   }

   public Response handleRanges(String method, Properties header, Response original) {
      if (header.containsKey("Range") && "GET".equals(method) && original.size > 0L && original.data != null && "200 OK".equals(original.status)) {
         Pattern p = Pattern.compile("bytes=(\\d+)-(\\d+)");
         Matcher m = p.matcher((String)header.get("Range"));
         if (m.matches()) {
            int start = Integer.parseInt(m.group(1));
            int end = Integer.parseInt(m.group(2)) + 1;

            try {
               if (start < end && (long)end <= original.size) {
                  byte[] rdata = new byte[end - start];
                  original.data.skip((long)start);
                  int r = original.data.read(rdata, 0, rdata.length);
                  if (r != rdata.length) {
                     throw new RuntimeException("Read " + r + " bytes instead of expected " + rdata.length);
                  }

                  original.addHeader("Content-Range", "bytes " + start + "-" + m.group(2) + "/" + original.size);
                  original.addHeader("Content-Length", rdata.length + "");
                  original.size = (long)rdata.length;
                  original.data = new ByteArrayInputStream(rdata);
                  original.status = "206 Partial Content";
                  return original;
               }
            } catch (Exception var10) {
               logException("Range handling failed: " + header.get("Range") + "; " + start + ", " + end + "; size = " + original.size, var10, false);
            }

            Response bust = new Response("416 Range Not Satisfiable", "text/plain", "Range Not Satisfiable");
            bust.addHeader("Content-Range", "bytes */" + original.size);
            return bust;
         }
      }

      return original;
   }

   public Response serve(String uri, String method, Properties header, Properties param) {
      return this.handleRanges(method, header, this._serve(uri, method, header, param));
   }

   public Response _serve(String uri, String method, Properties header, Properties param) {
      String useragent = (header.getProperty("User-Agent") + "").toLowerCase();
      boolean blockedByUA = false;
      String[] arr$ = this.blockedUAArray;
      int len$ = arr$.length;

//      int len$;
      for(len$ = 0; len$ < len$; ++len$) {
         String blockedUA = arr$[len$];
         if (blockedUA.trim().length() > 0 && CSUtils.matchesSimpleGeneric(useragent, blockedUA.trim())) {
            blockedByUA = true;
         }
      }

      boolean allowedByUA = true;
      String hook;
      if (this.allowedUAArray.length > 0) {
         allowedByUA = false;
        arr$ = this.allowedUAArray;
         len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            hook = arr$[i$];
            if (hook.trim().length() > 0 && CSUtils.matchesSimpleGeneric(useragent, hook.trim())) {
               allowedByUA = true;
            }
         }
      }

      if (allowedByUA && !blockedByUA) {
         if (method.equals("OPTIONS")) {
            Response r = this.processResponse(uri, method, header, param, false, (WebService)null, new Response("200 OK", "text/html", ""));
            r.addHeader("Allow", "OPTIONS,GET,HEAD,POST");
            return r;
         } else {
            WebService service;
            if (this.hooks.containsKey(uri)) {
               service = (WebService)this.hooks.get(uri);
               return this.processResponse(uri, method, header, param, true, service, service.serve(uri, method, header, param));
            }else if (!uri.startsWith("/")) {
               return this.processResponse(uri, method, header, param, false, null, new Response("404 Not Found.","text/html",""));
            } else if (this.hooksSecondary.containsKey(uri)) {
               service = (WebService)this.hooksSecondary.get(uri);
               return this.processResponse(uri, method, header, param, false, service, service.serve(uri, method, header, param));
            } else if (this.hooks.containsKey(uri + "/")) {
               service = (WebService)this.hooks.get(uri + "/");
               return this.processResponse(uri + "/", method, header, param, true, service, service.serve(uri, method, header, param));
            } else if (uri.startsWith("http://")) {
               service = (WebService)this.hooks.get("proxy");
               return service != null ? this.processResponse(uri, method, header, param, true, service, service.serve(uri, method, header, param)) : this.processResponse(uri, method, header, param, false, (WebService)null, new Response("404 Not Found", "text/plain", ""));
            } else if (isStagerX64Strict(uri) && this.hooks.containsKey("stager64")) {
               service = (WebService)this.hooks.get("stager64");
               return this.processResponse(uri + "/", method, header, param, true, service, service.serve(uri, method, header, param));
            } else if (isStagerStrict(uri) && this.hooks.containsKey("stager")) {
               service = (WebService)this.hooks.get("stager");
               return this.processResponse(uri + "/", method, header, param, true, service, service.serve(uri, method, header, param));
            } else {
               Iterator i = this.hooksSecondary.entrySet().iterator();

               WebService svc;
               do {
                  if (!i.hasNext()) {
//                     WebService service;
                     if (isStagerX64(uri) && this.hooks.containsKey("stager64")) {
                        service = (WebService)this.hooks.get("stager64");
                        return this.processResponse(uri + "/", method, header, param, true, service, service.serve(uri, method, header, param));
                     }

                     if (isStagerX64(uri)) {
                        print_warn("URI Matches staging (x64) URL, but there is no stager bound...: " + uri);
                        return this.processResponse(uri, method, header, param, false, (WebService)null, new Response("404 Not Found", "text/plain", ""));
                     }

                     if (isStager(uri) && this.hooks.containsKey("stager")) {
                        service = (WebService)this.hooks.get("stager");
                        return this.processResponse(uri + "/", method, header, param, true, service, service.serve(uri, method, header, param));
                     }

                     if (isStager(uri)) {
                        print_warn("URI Matches staging (x86) URL, but there is no stager bound...: " + uri);
                        return this.processResponse(uri, method, header, param, false, (WebService)null, new Response("404 Not Found", "text/plain", ""));
                     }

                     return this.processResponse(uri, method, header, param, false, (WebService)null, new Response("404 Not Found", "text/plain", ""));
                  }

                  Map.Entry e = (Map.Entry)i.next();
                  svc = (WebService)e.getValue();
                  hook = e.getKey() + "";
               } while(!uri.startsWith(hook) || !svc.isFuzzy());

               return this.processResponse(uri, method, header, param, false, svc, svc.serve(uri.substring(hook.length()), method, header, param));
            }
         }
      } else {
         String remoteAddress = header.getProperty("REMOTE_ADDRESS");
         if (!allowedByUA) {
            print_warn("Request not allowed for useragent '" + useragent + "'. URI=" + uri + " Method=" + method + " Remote Address=" + remoteAddress);
         }

         if (blockedByUA) {
            print_warn("Request blocked for useragent '" + useragent + "'. URI=" + uri + " Method=" + method + " Remote Address=" + remoteAddress);
         }

         return this.processResponse(uri, method, header, param, false, (WebService)null, new Response("404 Not Found", "text/plain", ""));
      }
   }

   public WebServer(int port) throws IOException {
      super(port);
   }

   public WebServer(int port, boolean ssl, InputStream keystore, String password) throws IOException {
      super(port, ssl, keystore, password);
   }

   public static void main(String[] args) {
      try {
         new WebServer(80);
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public interface WebListener {
      void receivedClient(String var1, String var2, Properties var3, Properties var4, String var5, int var6, boolean var7, String var8, long var9);
   }
}
