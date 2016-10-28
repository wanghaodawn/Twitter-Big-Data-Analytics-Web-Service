
public private static String parseResult(String user_id, String hashtag) throws IOException {
	if (user_id == null || hashtag == null) {
		return "\n";
	}

	Get g = new Get(Bytes.toBytes(user_id));
    long time1 = System.currentTimeMillis();
    Result r = tweets.get(g);
    long time2 = System.currentTimeMillis();
    if (r == null)
        return "\n";
    byte[] value = r.getValue(Bytes.toBytes("data"), Bytes.toBytes("info"));
    if (value == null)
        return "\n";
    String s = Bytes.toString(value);
    String[] ss = s.split("!%\\^\\?!!!dawn!!!%\\^\\?");
    
    String a = "";
    for (int i = 0; i < ss.length; i++) {
    	if (ss[i] == null) {
    		continue;
    	}
    	if (!ss[i].contains("!%^?dawn!%^?")) {
    		if (ss[i].equals(hashtag) && i+1 < ss.length) {
    			a = ss[i+1];
    			break;
    		}
    	}
    }
	if (a == null) {
		return "\n";
	}
    a = a.replace("\\n", "\n").
            replace("\\t", "\t").
            replace("\\\"", "\"").
            replace("\\r", "\r").
            replace("!%^?dawn!%^?", ";") + ";;";
    if (time2 - time1 > 100)
        System.out.println(a);
    return a;
}