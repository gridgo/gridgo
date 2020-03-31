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
        for (var line : lines) {
            sb.append(tabs).append(line).append("\n");
        }
        return sb.toString();
    }
}
