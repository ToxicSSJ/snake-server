package me.itoxic.snake.logging;

import com.github.tomaslanger.chalk.Chalk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static DateFormat dateFormat = new SimpleDateFormat("[mm-dd hh:mm:ss]");

    public static void net(String msg) {
        send(Chalk.on("[NET]").green().bgWhite().toString(), Chalk.on(msg).white().toString());
    }

    public static void debug(String msg) {
        send(Chalk.on("[DBG]").magenta().toString(), Chalk.on(msg).white().toString());
    }

    public static void info(String msg) {
        send(Chalk.on("[INF]").cyan().toString(), Chalk.on(msg).white().toString());
    }

    public static void warn(String msg) {
        send(Chalk.on("[WRN]").yellow().toString(), Chalk.on(msg).white().toString());
    }

    public static void error(String msg) {
        send(Chalk.on("[ERR]").red().toString(), Chalk.on(msg).white().toString());
    }

    private static void send(String prefix, String message) {
        System.out.println(time() + prefix + " :: " + message);
    }

    private static String time() {
        return Chalk.on(dateFormat.format(now())).yellow().toString();
    }

    private static Date now() {
        return new Date();
    }

}
