package catserver.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CatServer {
    public static final Logger log = LogManager.getLogger("CatServer");
    private static final String native_version = "v1_7_R4";

    public static String getNativeVersion() {
        return native_version;
    }
}
