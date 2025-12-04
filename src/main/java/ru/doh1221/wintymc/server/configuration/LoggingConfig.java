package ru.doh1221.wintymc.server.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

/**
 * Formatter который печатает строки в формате:
 * [HH:mm:ss] (loggerName) message
 * <p>
 * ВАЖНО: вызываетe LoggingConfig.install() как можно раньше в main(),
 * до первого Logger.getLogger(...) вызова.
 * <p>
 * Поведение:
 * - Если сообщение содержит переносы строк, префикс добавляется перед каждой строкой.
 * - Если есть исключение (throwable), его stacktrace тоже печатается с тем же префиксом на каждой строке.
 */
public final class LoggingConfig {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LoggingConfig() { /* utility */ }

    public static void install() {
        Logger root = Logger.getLogger("");
        // Удаляем старые обработчики (чтобы не было дублирования)
        for (Handler h : root.getHandlers()) {
            try {
                root.removeHandler(h);
                h.close();
            } catch (Exception ignored) {
            }
        }

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                String time = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getMillis()),
                        ZoneId.systemDefault()).format(TIME_FMT);
                String loggerName = record.getLoggerName() == null ? "-" : record.getLoggerName();
                String prefix = String.format("[%s] (%s) ", time, loggerName);

                String message = formatMessage(record);
                // Разбиваем по любому разделителю строк, чтобы корректно обработать CR, LF и CRLF
                String[] msgLines = message.split("\\R", -1);

                StringBuilder sb = new StringBuilder(msgLines.length * 80);
                for (int i = 0; i < msgLines.length; i++) {
                    sb.append(prefix).append(msgLines[i]).append(System.lineSeparator());
                }

                // Добавляем stacktrace, если есть исключение
                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.flush();
                    String trace = sw.toString();
                    String[] traceLines = trace.split("\\R", -1);
                    for (String tl : traceLines) {
                        sb.append(prefix).append(tl).append(System.lineSeparator());
                    }
                }

                return sb.toString();
            }
        });

        try {
            ch.setEncoding("UTF-8");
        } catch (Exception ignored) {
        }

        root.addHandler(ch);
        root.setUseParentHandlers(false);
    }
}