import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.LinkedHashMap;
import java.util.Map;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;

public class HelloWorldServer {
    private static String team = "elder,934442434066\n";
    private static QuerySqlV4 querysql = new QuerySqlV4();
    private final static int CACHE_MAX_SIZE = 1200000;

    // cache key=userid, value=query result from db
    private static LinkedHashMap<String, String> cache = new LinkedHashMap<String, String>(CACHE_MAX_SIZE, 0.75f, false) { 
        protected boolean removeEldestEntry(
            Map.Entry<String, String> eldest) {
            // Remove the eldest entry if the size of the cache exceeds the maximum size
            return size() > CACHE_MAX_SIZE;
        }
    };

    final BigInteger X = new BigInteger("64266330917908644872330635228106713310880186591609208114244758680898150367880703152525200743234420230");
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss\n");

    public static void main(final String[] args) throws Exception {
        Undertow server = Undertow.builder()
                .addHttpListener(80, "<public dns>")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        String path = exchange.getRequestPath();
                        if ("/heartbeat".equals(path)) {
                            exchange.getResponseSender().send("Hello! I am alive!");
                            return;
                        }
                        if ("/q2".equals(path)) {
                            if (exchange == null || 
                                exchange.getQueryParameters().get("userid") == null ||
                                exchange.getQueryParameters().get("hashtag") == null) {
                                exchange.getResponseSender().send(team + "\n");
                                return;
                            }
                            String userid = exchange.getQueryParameters().get("userid").getFirst();
                            String hashtag = exchange.getQueryParameters().get("hashtag").getFirst();
                            if (userid == null || hashtag == null) {
                                exchange.getResponseSender().send(team + "\n");
                                return;
                            }
                            String info = userid + "_" + hashtag;
                            if (cache.containsKey(info)) {
                                exchange.getResponseSender().send(team + cache.get(info));
                            } else {
                                String queryRes = querysql.query(info);
                                cache.put(info, queryRes);
                                exchange.getResponseSender().send(team + queryRes);
                            }
                        } else {
                            if (exchange == null || 
                                exchange.getQueryParameters().get("key") == null ||
                                exchange.getQueryParameters().get("message") == null) {
                                exchange.getResponseSender().send(team + "\n");
                                return;
                            }
                            String key = exchange.getQueryParameters().get("key").getFirst();
                            String message = exchange.getQueryParameters().get("message").getFirst();
                            if (key == null || message == null) {
                                exchange.getResponseSender().send(team + "\n");
                                return;
                            }
                            exchange.getResponseSender().send(encrypt(key, message));
                        }
                    }
                }).build();
        server.start();
    }
    private String encrypt(String key, String message) {

        // Step 1
        // Get X and Y
        BigInteger Y = new BigInteger(key);

        // Compute Greatest Common Divisor
        BigInteger Z = X.gcd(Y);

        // Step 2
        // Compute K
        BigInteger K = Z.mod(new BigInteger("25")).add(new BigInteger("1"));
        int k = K.intValue();

        // message -> I
        char[] char_array = message.toCharArray();
        int len = char_array.length;
        int n = (int) Math.sqrt(len);
        for (int i = 0; i < len; i++) {
            char_array[i] -= k;
            if (char_array[i] < 'A') {
                char_array[i] += 26;
            }
        }

        // Step 3 - Spiral
        int index = 0;
        char[][] temp = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                temp[i][j] = char_array[index++];
            }
        }

        index = 0;
        int left = 0, top = 0, right = n - 1, bottom = n - 1;
        while (left <= right && top <= bottom) {
            for (int j = left; j <= right; j++) {
                char_array[index++] = temp[top][j];
            }
            top++;

            for (int i = top; i <= bottom; i++) {
                char_array[index++] = temp[i][right];
            }
            right--;

            for (int j = right; j >= left; j--) {
                char_array[index++] = temp[bottom][j];
            }
            bottom--;

            for (int i = bottom; i >= top; i--) {
                char_array[index++] = temp[i][left];
            }
            left++;
        }
        String I = new String(char_array);

        // Step 4
        TimeZone.setDefault(TimeZone.getTimeZone("EST"));
        Date date = new Date();

        StringBuilder final_String = new StringBuilder(team);
        final_String.append(dateFormat.format(date));
        final_String.append(I + "\n");

        return final_String.toString();
    }
}