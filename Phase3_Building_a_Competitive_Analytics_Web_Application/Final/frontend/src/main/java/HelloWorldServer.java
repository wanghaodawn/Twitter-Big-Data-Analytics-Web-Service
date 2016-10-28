import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.net.HttpURLConnection;
import java.net.URL;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jialingliu on 3/15/16.
 */
public class HelloWorldServer {
    private static String team = "elder,934442434066\n";
    private static QuerySqlV4 querysql = new QuerySqlV4();
    private static ConcurrentHashMap<Long, Short> map = new ConcurrentHashMap<>();
    private static final int flag = 0;
    private static final String[] dns = new String[6];
    private static final int[] port = new int[6];
    private static final String listenDNS = "";

    private static final BigInteger X = new BigInteger("64266330917908644872330635228106713310880186591609208114244758680898150367880703152525200743234420230");
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss\n");
    final static Lock lock = new ReentrantLock();
    final static Condition condition = lock.newCondition();

    private static void initilizeDNS() {
        dns[0] = "172.31.22.50";
        dns[1] = "172.31.22.51";
        dns[2] = "172.31.22.52";
        dns[3] = "172.31.22.53";
        dns[4] = "172.31.22.54";
        dns[5] = "172.31.22.55";
        port[0] = 23470;
        port[1] = 23471;
        port[2] = 23472;
        port[3] = 23473;
        port[4] = 23474;
        port[5] = 23475;

    }

    public static void main(final String[] args) throws Exception {
        initilizeDNS();
        Undertow server = Undertow.builder()
                .addHttpListener(80, listenDNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        String path = exchange.getRequestPath();
                        if ("/heartbeat".equals(path)) {
                            exchange.getResponseSender().send("Hello! I am alive! :)");
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
                            String queryRes = querysql.query(info);
                            exchange.getResponseSender().send(team + queryRes);
                        } else if ("/q1".equals(path)) {
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
                        } else if ("/q3".equals(path)){
                            if (exchange == null ||
                                    exchange.getQueryParameters().get("start_date") == null ||
                                    exchange.getQueryParameters().get("end_date") == null ||
                                    exchange.getQueryParameters().get("start_userid") == null ||
                                    exchange.getQueryParameters().get("end_userid") == null ||
                                    exchange.getQueryParameters().get("words") == null) {
                                exchange.getResponseSender().send(team + "\n");
                                return;
                            }
                            String start_date = getIntDate(exchange.getQueryParameters().get("start_date").getFirst());
                            String end_date = getIntDate(exchange.getQueryParameters().get("end_date").getFirst());
                            String start_userid = exchange.getQueryParameters().get("start_userid").getFirst();
                            String end_userid = exchange.getQueryParameters().get("end_userid").getFirst();
                            String words = exchange.getQueryParameters().get("words").getFirst();
                            if (start_date == null || end_date == null || start_userid == null || end_userid == null) {
                                exchange.getResponseSender().send(team + "\n");
                                return;
                            }
                            String queryRes = querysql.query(start_date, end_date, start_userid, end_userid, words);
                            exchange.getResponseSender().send(team + queryRes);
                        } else {
                            if (exchange == null || exchange.getQueryParameters().get("tweetid") == null ||
                                    exchange.getQueryParameters().get("op") == null || exchange.getQueryParameters().get("seq") == null
                                    || exchange.getQueryParameters().get("fields") == null ||
                                    exchange.getQueryParameters().get("payload") == null) {
                                exchange.getResponseSender().send(team + "\n");
                                return;
                            }
                            final String tweet_id = exchange.getQueryParameters().get("tweetid").getFirst();
                            final String op = exchange.getQueryParameters().get("op").getFirst();
                            final String seq_string = exchange.getQueryParameters().get("seq").getFirst();
                            final String fields = exchange.getQueryParameters().get("fields").getFirst();
                            final String payload = exchange.getQueryString().split("payload", -1)[1];

                            if (tweet_id == null || op == null || seq_string == null || fields == null) {
                                exchange.getResponseSender().send(team + "\n");
                                return;
                            }
                            final long id;
                            final short seq;
                            try {
                                id = Long.parseLong(tweet_id);
                                seq = Short.parseShort(seq_string);
                            }
                            catch (NumberFormatException e) {
                                exchange.getResponseSender().send(team);
                                return;
                            }
                            String queryString = exchange.getQueryString();
                            int newFlag = (int) ((id/100) % 6);
                            if (newFlag != flag) {
                                exchange.getResponseSender().send(forward(4, newFlag, queryString));
                                return;
                            }
                            if (!map.containsKey(id)) {
                                map.put(id, (short) 0);
                            } else if (map.containsKey(id) && map.get(id) >= seq) {
                                //todo reset database
                                map.put(id, (short)0);
                            }
                            if (op.equals("set")) {
                                exchange.getResponseSender().send(team + "success" + ";");
                                try {
                                    String a = parseQuery(id, seq, op, fields, payload);
                                } catch (Exception e) {}
                            } else if (op.equals("get")) {
                                String a = null;
                                try {
                                    a = parseQuery(id, seq, op, fields, payload);
                                } catch (Exception e) {
                                    exchange.getResponseSender().send(team);
                                    return;
                                }
                                if (a != null) {
                                    exchange.getResponseSender().send(team + a);
                                }
                                else {
                                    exchange.getResponseSender().send(team);
                                }
                            }
                            else {
                                exchange.getResponseSender().send(team);
                            }
                        }

                    }
                }).build();
        Undertow server0 = Undertow.builder()
                .addHttpListener(23470, listenDNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        if (exchange == null || exchange.getQueryParameters().get("tweetid") == null ||
                                exchange.getQueryParameters().get("op") == null || exchange.getQueryParameters().get("seq") == null
                                || exchange.getQueryParameters().get("fields") == null ||
                                exchange.getQueryParameters().get("payload") == null)
                        {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final String tweet_id = exchange.getQueryParameters().get("tweetid").getFirst();
                        final String op = exchange.getQueryParameters().get("op").getFirst();
                        final String seq_string = exchange.getQueryParameters().get("seq").getFirst();
                        final String fields = exchange.getQueryParameters().get("fields").getFirst();
                        final String payload = exchange.getQueryString().split("payload", -1)[1];

                        if (tweet_id == null || op == null || seq_string == null || fields == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final long id;
                        final short seq;
                        try {
                            id = Long.parseLong(tweet_id);
                            seq = Short.parseShort(seq_string);
                        }
                        catch (NumberFormatException e) {
                            //e.printStackTrace();
                            exchange.getResponseSender().send(team);
                            return;
                        }
                        String queryString = exchange.getQueryString();
                        int newFlag = (int) ((id/100) % 6);
                        if (newFlag != flag) {
                            exchange.getResponseSender().send(forward(4, newFlag, queryString));
                            return;
                        }
                        if (!map.containsKey(id)) {
                            map.put(id, (short) 0);
                        } else if (map.containsKey(id) && map.get(id) >= seq) {
                            //todo reset database
                            map.put(id, (short) 0);
                        }
                        if (op.equals("set")) {
                            exchange.getResponseSender().send(team + "success" + ";");
                            try {
                                String a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (op.equals("get")) {
                            String a = null;
                            try {
                                a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                exchange.getResponseSender().send(team);
                                e.printStackTrace();
                                return;
                            }
                            if (a != null) {
                                exchange.getResponseSender().send(team + a);
                            } else {
                                exchange.getResponseSender().send(team);
                            }
                        } else {
                            exchange.getResponseSender().send(team);
                        }
                    }
                }).build();
        Undertow server1 = Undertow.builder()
                .addHttpListener(23471, listenDNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        if (exchange == null || exchange.getQueryParameters().get("tweetid") == null ||
                                exchange.getQueryParameters().get("op") == null || exchange.getQueryParameters().get("seq") == null
                                || exchange.getQueryParameters().get("fields") == null ||
                                exchange.getQueryParameters().get("payload") == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final String tweet_id = exchange.getQueryParameters().get("tweetid").getFirst();
                        final String op = exchange.getQueryParameters().get("op").getFirst();
                        final String seq_string = exchange.getQueryParameters().get("seq").getFirst();
                        final String fields = exchange.getQueryParameters().get("fields").getFirst();
                        final String payload = exchange.getQueryString().split("payload", -1)[1];

                        if (tweet_id == null || op == null || seq_string == null || fields == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final long id;
                        final short seq;
                        try {
                            id = Long.parseLong(tweet_id);
                            seq = Short.parseShort(seq_string);
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                            exchange.getResponseSender().send(team);
                            return;
                        }
                        String queryString = exchange.getQueryString();
                        int newFlag = (int) ((id/100) % 6);
                        if (newFlag != flag) {
                            exchange.getResponseSender().send(forward(4, newFlag, queryString));
                            return;
                        }
                        if (!map.containsKey(id))
                            map.put(id, (short) 0);
                        else if (map.containsKey(id) && map.get(id) >= seq) {
                            //todo reset database
                            map.put(id, (short) 0);
                        }
                        if (op.equals("set")) {
                            exchange.getResponseSender().send(team + "success" + ";");
                            try {
                                String a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (op.equals("get")) {
                            String a = null;
                            try {
                                a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                exchange.getResponseSender().send(team);
                                return;
                            }
                            if (a != null) {
                                exchange.getResponseSender().send(team + a);
                            } else {
                                exchange.getResponseSender().send(team);
                            }
                        } else {
                            exchange.getResponseSender().send(team);
                        }
                    }
                }).build();
        Undertow server2 = Undertow.builder()
                .addHttpListener(23472, listenDNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        if (exchange == null || exchange.getQueryParameters().get("tweetid") == null ||
                                exchange.getQueryParameters().get("op") == null || exchange.getQueryParameters().get("seq") == null
                                || exchange.getQueryParameters().get("fields") == null ||
                                exchange.getQueryParameters().get("payload") == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final String tweet_id = exchange.getQueryParameters().get("tweetid").getFirst();
                        final String op = exchange.getQueryParameters().get("op").getFirst();
                        final String seq_string = exchange.getQueryParameters().get("seq").getFirst();
                        final String fields = exchange.getQueryParameters().get("fields").getFirst();
                        final String payload = exchange.getQueryString().split("payload", -1)[1];

                        if (tweet_id == null || op == null || seq_string == null || fields == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final long id;
                        final short seq;
                        try {
                            id = Long.parseLong(tweet_id);
                            seq = Short.parseShort(seq_string);
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                            exchange.getResponseSender().send(team);
                            return;
                        }
                        String queryString = exchange.getQueryString();
                        int newFlag = (int) ((id/100) % 6);
                        if (newFlag != flag) {
                            exchange.getResponseSender().send(forward(4, newFlag, queryString));
                            return;
                        }
                        if (!map.containsKey(id)) {
                            map.put(id, (short) 0);
                        } else if (map.containsKey(id) && map.get(id) >= seq) {
                            //todo reset database
                            map.put(id, (short) 0);
                        }
                        if (op.equals("set")) {
                            exchange.getResponseSender().send(team + "success" + ";");
                            try {
                                String a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else if (op.equals("get")) {
                            String a = null;
                            try {
                                a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                exchange.getResponseSender().send(team);
                                return;
                            }
                            if (a != null) {
                                exchange.getResponseSender().send(team + a);
                            } else {
                                exchange.getResponseSender().send(team);
                            }
                        } else {
                            exchange.getResponseSender().send(team);
                        }
                    }
                }).build();
        Undertow server3 = Undertow.builder()
                .addHttpListener(23473, listenDNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        if (exchange == null || exchange.getQueryParameters().get("tweetid") == null ||
                                exchange.getQueryParameters().get("op") == null || exchange.getQueryParameters().get("seq") == null
                                || exchange.getQueryParameters().get("fields") == null ||
                                exchange.getQueryParameters().get("payload") == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final String tweet_id = exchange.getQueryParameters().get("tweetid").getFirst();
                        final String op = exchange.getQueryParameters().get("op").getFirst();
                        final String seq_string = exchange.getQueryParameters().get("seq").getFirst();
                        final String fields = exchange.getQueryParameters().get("fields").getFirst();
                        final String payload = exchange.getQueryString().split("payload", -1)[1];

                        if (tweet_id == null || op == null || seq_string == null || fields == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final long id;
                        final short seq;
                        try {
                            id = Long.parseLong(tweet_id);
                            seq = Short.parseShort(seq_string);
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                            exchange.getResponseSender().send(team);
                            return;
                        }
                        String queryString = exchange.getQueryString();
                        int newFlag = (int) ((id/100) % 6);
                        if (newFlag != flag) {
                            exchange.getResponseSender().send(forward(4, newFlag, queryString));
                            return;
                        }
                        if (!map.containsKey(id)) {
                            map.put(id, (short) 0);
                        } else if (map.containsKey(id) && map.get(id) >= seq) {
                            //todo reset database
                            map.put(id, (short) 0);
                        }
                        if (op.equals("set")) {
                            exchange.getResponseSender().send(team + "success" + ";");
                            try {
                                String a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (op.equals("get")) {
                            String a = null;
                            try {
                                a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                exchange.getResponseSender().send(team);
                                return;
                            }
                            if (a != null) {
                                exchange.getResponseSender().send(team + a);
                            } else {
                                exchange.getResponseSender().send(team);
                            }
                        } else {
                            exchange.getResponseSender().send(team);
                        }
                    }
                }).build();
        Undertow server4 = Undertow.builder()
                .addHttpListener(23474, listenDNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        if (exchange == null || exchange.getQueryParameters().get("tweetid") == null ||
                                exchange.getQueryParameters().get("op") == null || exchange.getQueryParameters().get("seq") == null
                                || exchange.getQueryParameters().get("fields") == null ||
                                exchange.getQueryParameters().get("payload") == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final String tweet_id = exchange.getQueryParameters().get("tweetid").getFirst();
                        final String op = exchange.getQueryParameters().get("op").getFirst();
                        final String seq_string = exchange.getQueryParameters().get("seq").getFirst();
                        final String fields = exchange.getQueryParameters().get("fields").getFirst();
                        final String payload = exchange.getQueryString().split("payload", -1)[1];

                        if (tweet_id == null || op == null || seq_string == null || fields == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final long id;
                        final short seq;
                        try {
                            id = Long.parseLong(tweet_id);
                            seq = Short.parseShort(seq_string);
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                            exchange.getResponseSender().send(team);
                            return;
                        }
                        String queryString = exchange.getQueryString();
                        int newFlag = (int) ((id/100) % 6);
                        if (newFlag != flag) {
                            exchange.getResponseSender().send(forward(4, newFlag, queryString));
                            return;
                        }
                        if (!map.containsKey(id)) {
                            map.put(id, (short) 0);
                        } else if (map.containsKey(id) && map.get(id) >= seq) {
                            //todo reset database
                            map.put(id, (short) 0);
                        }
                        if (op.equals("set")) {
                            exchange.getResponseSender().send(team + "success" + ";");
                            try {
                                String a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (op.equals("get")) {
                            String a = null;
                            try {
                                a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                exchange.getResponseSender().send(team);
                                return;
                            }
                            if (a != null) {
                                exchange.getResponseSender().send(team + a);
                            } else {
                                exchange.getResponseSender().send(team);
                            }
                        } else {
                            exchange.getResponseSender().send(team);
                        }
                    }
                }).build();
        Undertow server5 = Undertow.builder()
                .addHttpListener(23475, listenDNS)
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        if (exchange == null || exchange.getQueryParameters().get("tweetid") == null ||
                                exchange.getQueryParameters().get("op") == null || exchange.getQueryParameters().get("seq") == null
                                || exchange.getQueryParameters().get("fields") == null ||
                                exchange.getQueryParameters().get("payload") == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final String tweet_id = exchange.getQueryParameters().get("tweetid").getFirst();
                        final String op = exchange.getQueryParameters().get("op").getFirst();
                        final String seq_string = exchange.getQueryParameters().get("seq").getFirst();
                        final String fields = exchange.getQueryParameters().get("fields").getFirst();
                        final String payload = exchange.getQueryString().split("payload", -1)[1];

                        if (tweet_id == null || op == null || seq_string == null || fields == null) {
                            exchange.getResponseSender().send(team + "\n");
                            return;
                        }
                        final long id;
                        final short seq;
                        try {
                            id = Long.parseLong(tweet_id);
                            seq = Short.parseShort(seq_string);
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                            exchange.getResponseSender().send(team);
                            return;
                        }
                        String queryString = exchange.getQueryString();
                        int newFlag = (int) ((id/100) % 6);
                        if (newFlag != flag) {
                            exchange.getResponseSender().send(forward(4, newFlag, queryString));
                            return;
                        }
                        if (!map.containsKey(id)) {
                            map.put(id, (short) 0);
                        } else if (map.containsKey(id) && map.get(id) >= seq) {
                            //todo reset database
                            map.put(id, (short) 0);
                        }
                        if (op.equals("set")) {
                            exchange.getResponseSender().send(team + "success" + ";");
                            try {
                                String a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (op.equals("get")) {
                            String a = null;
                            try {
                                a = parseQuery(id, seq, op, fields, payload);
                            } catch (Exception e) {
                                exchange.getResponseSender().send(team);
                                return;
                            }
                            if (a != null) {
                                exchange.getResponseSender().send(team + a);
                            } else {
                                exchange.getResponseSender().send(team);
                            }
                        } else {
                            exchange.getResponseSender().send(team);
                        }
                    }
                }).build();
        server.start();
        server0.start();
        server1.start();
        server2.start();
        server3.start();
        server4.start();
        server5.start();
    }

    private static String parseQuery(long id, short seq, String op, String fields, String payload) throws Exception {
        lock.lock();
        try {
            while (seq != map.get(id) + 1) {
                condition.await();
            }
            String result = QuerySqlV4.query4(id, seq, op, fields, payload);
            map.put(id, seq);
            condition.signalAll();
            return result;
        }
        finally {
            lock.unlock();
        }
    }

    private static String forward(int queryNumber, int index, String queryString) {
        String body = null;
        HttpURLConnection con = null;
        InputStream in = null;
        try {
            String link = "http://" + dns[index] + String.format(":%d/q%d?", port[flag], queryNumber) + queryString;
            URL url = new URL(link);
            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(3000);
            in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            body = new String(baos.toByteArray(), encoding);
        } catch (Exception e) {
            e.printStackTrace();
            return "timeout";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {}
            }
            if (con != null) {
                con.disconnect();
            }
        }
        return body;
    }

    private static String getIntDate(String date) {
        StringBuilder sb = new StringBuilder();
        for (String s: date.split("-")) {
            sb.append(s);
        }
        return sb.toString();
    }

    private static String encrypt(String key, String message) {

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
