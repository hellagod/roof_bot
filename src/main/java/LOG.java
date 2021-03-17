import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LOG {
    private static Logger LOGGER;
    static {
        try(FileInputStream ins = new FileInputStream("G:\\roof_bot\\src\\main\\resources\\log.config")){
            LogManager.getLogManager().readConfiguration(ins);
            LOGGER = Logger.getLogger(Main.class.getName());
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
    }

    static void log(String s){
        LOGGER.log(Level.INFO,"---------------------------------   "+s);
    }
}
