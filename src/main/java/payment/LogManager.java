package payment;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogManager implements ILogManager {
    private static final String LOG_DIRECTORY = "logs";
    private static final String PAYMENTS_LOG_FILE = LOG_DIRECTORY + "/payments.jsonl";
    private static final String USAGE_LOG_FILE = LOG_DIRECTORY + "/usage.jsonl";

    private final Gson gson;

    public LogManager() {
        this.gson = new Gson();

        File dir = new File(LOG_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void savePaymentLog(Payment payment) {
        String logEntry = gson.toJson(payment);
        appendLogToFile(PAYMENTS_LOG_FILE, logEntry);
    }

    @Override
    public void saveUsageLog(UsageSession session) {
        String logEntry = gson.toJson(session);
        appendLogToFile(USAGE_LOG_FILE, logEntry);
    }

    private synchronized void appendLogToFile(String filePath, String logEntry) {
        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            File file = new File(filePath);
            if (file.length() > 0) {
                pw.println();
            }
            pw.print(logEntry);
        } catch (IOException e) {
            System.err.println("로그 파일 쓰기 실패: " + filePath);
            e.printStackTrace();
        }
    }
}
