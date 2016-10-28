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
import java.util.TimeZone;


public class Q2 extends Verticle {
    HTableManager hTableManager = HTableManager.getInstance();
    HTableInterface tweets = hTableManager.getHTable("final");
    String final_String = new String("elder,934442434066\n");

    private String query(String user_id, String hashtag) throws IOException{
        Get g = new Get(Bytes.toBytes(user_id + "_" + hashtag));
        long time1 = System.currentTimeMillis();
        Result r = tweets.get(g);
        long time2 = System.currentTimeMillis();
        if (r == null)
            return "\n";
        byte[] value = r.getValue(Bytes.toBytes("data"), Bytes.toBytes("info"));
        if (value == null)
            return "\n";
        String a = Bytes.toString(value);
            a = a.replace("\\n", "\n").
                    replace("\\t", "\t").
                    replace("\\\"", "\"").
                    replace("\\r", "\r").
                    replace("!%^?dawn!%^?", ";") + ";;";
        if (time2 - time1 > 100)
            System.out.println(a);
        return a;
    }
    @Override
    public void start() {
        final HttpServer server = vertx.createHttpServer();
        server.setAcceptBacklog(32767);
        server.setUsePooledBuffers(true);
        server.setReceiveBufferSize(4096);
        server.requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest req) {
                if (req.statusCode() != 200) {
                    try {
                        req.response().end(final_String + "\n"));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MultiMap map = req.params();
                    final String userid = map.get("userid");
                    final String hashtag = map.get("hashtag");

                    try {
                        if (userid == null || hashtag == null) {
                            req.response().end(final_String + "\n");
                            return;
                        }
                        req.response().end(final_String + query(userid, hashtag));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    
            }
        });
        server.listen(80);
    }

}