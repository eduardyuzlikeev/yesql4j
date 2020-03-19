package com.yesql4j.generator;

import com.yesql4j.parser.SQLQueryDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ParamsUtils {

    // TODO: quoted cases
    public static String cleanupQuery(SQLQueryDefinition query) {
        String res = query.getQuery();
        List<String> params = new ArrayList<>(query.getParams());
        params.sort((s, t1) -> t1.length() - s.length());

        for (String param : params) {
            if (!param.equals("?"))
                res = res.replace(":" + param, "?");
        }
        return res;
    }

    public static List<String> getQueryParamsNames(SQLQueryDefinition query) {
        int lastQIndex = 0;
        ArrayList<String> res = new ArrayList<>();
        for (String param : query.getParams()) {
            if (param.equals("?")) {
                res.add("p" + lastQIndex);
                lastQIndex++;
            } else if (!res.contains(param)) {
                res.add(param);
            }
        }
        return res;
    }

    public static List<String> getQueryParamsBinding(SQLQueryDefinition query) {
        int lastQIndex = 0;
        ArrayList<String> res = new ArrayList<>();
        for (String param : query.getParams()) {
            if (param.equals("?")) {
                res.add("p" + lastQIndex);
                lastQIndex++;
            } else {
                res.add(param);
            }
        }
        return res;
    }

    private static final String[] searchPackages = {
            "java.lang",
            "java.util",
            "java.math"
    };

    public static String predictType(String type) {
        if (type.contains(".")) return type;
        for (String p : searchPackages) {
            try {
                Class.forName(p + "." + type);
                return p + "." + type;
            } catch (ClassNotFoundException e) {
                //do nothing
            }
        }
        return type;
    }

    public static String methodParams(SQLQueryDefinition queryDefinition) {
        return ParamsUtils.getQueryParamsNames(queryDefinition).stream()
                .map(el -> {
                    String type = queryDefinition.getParamsTypes().get(el);
                    if (type != null) {
                        return ParamsUtils.predictType(type) + " " + el;
                    }else {
                        return "Object " + el;
                    }
                })
                .collect(Collectors.joining(", "));
    }
}