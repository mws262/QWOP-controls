package flashqwop;

import game.qwop.StateQWOP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tree.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FlashStateLogger implements IFlashStateListener {
    private File logFile;
    private PrintWriter writer;

    private static final Logger logger = LogManager.getLogger(FlashStateLogger.class);
    @Override
    public void stateReceived(int timestep, StateQWOP state) {
        if (timestep == 0) {
            logFile = new File("./src/main/resources/saved_data/" + Utility.generateFileName("flash_states", "txt"));
            logger.info("Made a new Flash QWOP state log at: " + logFile.getName());
            if (writer != null) {
                writer.close();
            }
            try {
                writer = new PrintWriter(logFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (logFile == null) {
            return;
        }

        writer.println(state.toFlatString());

        if (state.isFailed()) {
            writer.close();
        }
    }
}
