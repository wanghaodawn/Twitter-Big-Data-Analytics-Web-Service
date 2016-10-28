import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;


public class Q2 extends Verticle {
    HTableManager hTableManager = HTableManager.getInstance();
    HTableInterface tweets = hTableManager.getHTable("new_file");
    public  static int max_sizze = 1000000;
    public static LinkedHashMap<String, String> cache = new LinkedHashMap<String, String>(500000, 1.0f){
        @Override
        public boolean removeEldestEntry(Map.Entry eldest)
        {
            //when to remove the eldest entry
            return size() > max_sizze ;   //size exceeded the max allowed
        }
    };
    public static String parseResult(String s, String hashtag, String id_tag) throws IOException {

        String[] ss = s.split("!%\\^\\?!!!dawn!!!%\\^\\?");

        String a = "";
        for (int i = 1; i < ss.length; i += 2) {
            if (ss[i].equals(hashtag)) {
                a = ss[i+1];
                break;
            }
        }
        if (a == null) {
            return "\n";
        }
        a = a.replace("\\n", "\n").
                replace("\\t", "\t").
                replace("\\\"", "\"").
                replace("\\r", "\r").
                replace("!%^?dawn!%^?", ";") + ";";
        cache.put(id_tag, a);
        return a;
    }
    private String query(String user_id) throws IOException{
        Get g = new Get(Bytes.toBytes(user_id));
        g.setCacheBlocks(false);
        Result r = tweets.get(g);
        if (r == null)
            return "\n";
        byte[] value = r.getValue(Bytes.toBytes("data"), Bytes.toBytes("info"));
        if (value == null)
            return "\n";
        String a = Bytes.toString(value);
        cache.put(user_id, a);
        return a;
    }
    @Override
    public void start(){
        final HttpServer server = vertx.createHttpServer();
        server.setAcceptBacklog(32767);
        server.setUsePooledBuffers(true);
        server.setReceiveBufferSize(4*1024);
        server.requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest req) {
                MultiMap map = req.params();
                String final_String = new String("elder,934442434066\n");
                final String userid = map.get("userid");
                final String hashtag = map.get("hashtag");
                final String id_tag = userid + hashtag;
                if (userid == null || hashtag == null) {
                    req.response().end(final_String + "\n");
                } else {
                    try {
                        if (cache.containsKey(id_tag)) {
                            System.out.println("hit");
                            req.response().end(final_String + cache.get(id_tag));
                        }
                        else
                        {
                        String data = query(userid);
                        if (data.equals("\n"))
                            final_String = final_String + data;
                        else
                            final_String = final_String + parseResult(data, hashtag, id_tag);
                        req.response().end(final_String);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        server.listen(80);
    }

}
