package com.datadoghq.datadog_lambda_java;

import com.google.gson.Gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

class DDLogger {
    public enum level {
        DEBUG,
        ERROR
    }

    private static level g_level;
    private level l_level;

    @NotNull
    @Contract(" -> new")
    public static DDLogger getLoggerImpl(){

        if (g_level != null) return new DDLogger();

        String env_level = System.getenv("DD_LOG_LEVEL");
        if (env_level == null) env_level = level.ERROR.toString();

        if (env_level.toUpperCase().equals(level.DEBUG.toString())){
            g_level = level.DEBUG;
        }

        return new DDLogger();
    }

    private DDLogger(){
        this.l_level = g_level;
    }

    public void debug(String logMessage, Object ... args){
        if (this.l_level == level.DEBUG){
            doLog(level.DEBUG, logMessage, args);
        }
    }

    public void error(String logMessage, Object... args){
        doLog(level.ERROR, logMessage, args);
    }

    private void doLog(level l, String logMessage, Object[] args){
        StringBuilder argsSB = new StringBuilder("datadog: ");
        argsSB.append(logMessage);
        if (args != null) {
            for (Object a : args) {
                argsSB.append(" ");
                argsSB.append(a);
            }
        }

        Map<String, String> structuredLog  = new HashMap<String, String>();
        structuredLog.put("level", l.toString());
        structuredLog.put("message", argsSB.toString());

        Gson g = new Gson();

        System.out.println(g.toJson(structuredLog));

    }

    public void setLevel(level l){
        this.l_level = l;
    }
}
