
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.HTableInterface;
import java.io.IOException;

public class HTableManager {
    private static HTableManager instance;
    private static HTablePool hTablePool;

    private HTableManager() {
        Configuration config = HBaseConfiguration.create();
        hTablePool = new HTablePool(config, 1000);
    }

    public static HTableManager getInstance() {
        if (instance == null) {
            instance = new HTableManager();
        }
        return instance;
    }

    public synchronized HTableInterface getHTable(String tableName) {
        return hTablePool.getTable(tableName);
    }
}