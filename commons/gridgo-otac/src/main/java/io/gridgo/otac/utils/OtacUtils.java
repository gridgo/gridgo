package io.gridgo.otac.utils;

public class OtacUtils {

    public static String tabs(int numTabs) {
        var tabs = "";
        for (int i = 0; i < numTabs; i++)
            tabs += "    ";
        return tabs;
    }

    public static String tabs(int numTabs, String origin) {
        var tabs = tabs(numTabs);

        var lines = origin.split("\n");
        var sb = new StringBuilder();
        if (lines.length > 0) {
            sb.append(tabs).append(lines[0]);
            for (int i = 1; i < lines.length; i++)
                sb.append("\n").append(tabs).append(lines[i]);
        }
        return sb.toString();
    }
}
