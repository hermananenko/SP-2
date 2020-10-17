package lib;

import java.util.ArrayList;
import java.util.List;

public class Variables {
    private final static List<String> variables;

    static {
        variables = new ArrayList<>();
    }

    public static boolean isExists(String name) {
        return variables.contains(name);
    }

    public static List<String> getVariables() {
        return variables;
    }

    public static int getIndex(String name) {
        return variables.indexOf(name);
    }

    public static void add(String name) {
        variables.add(name);
    }
}
