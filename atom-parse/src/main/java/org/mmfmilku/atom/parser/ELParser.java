package org.mmfmilku.atom.parser;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.param.Param;
import org.mmfmilku.atom.param.operation.Copier;
import org.mmfmilku.atom.param.operation.Getter;
import org.mmfmilku.atom.param.operation.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ELParser<T extends Param> implements Parser<List<String>, Atom<T>> {

    private Setter<T, String, String> setter;
    private Getter<T, String, String> getter;
    private Copier<T, String> copier;

    public ELParser() {
        this((param, k, v) -> null,
                (param, k) -> "v",
                (param, source, target) -> {
                });
    }

    public ELParser(Setter<T, String, String> setter) {
        this(setter,
                (param, k) -> "v",
                (param, source, target) -> {
                });
    }

    public ELParser(Setter<T, String, String> setter, Getter<T, String, String> getter, Copier<T, String> copier) {
        this.setter = setter;
        this.getter = getter;
        this.copier = copier;
    }

    @Override
    public Atom<T> parse(List<String> expressions) {
        List<Consumer<T>> consumers = new ArrayList<>(expressions.size());
        expressions.forEach(s -> {
            s = s.trim();
            Matcher matcher = setPattern.matcher(s);
            if (matcher.matches()) {
                Matcher paramMatcher = twoParamPattern.matcher(s);
                if (paramMatcher.find()) {
                    String rawParam = paramMatcher.group();
                    String[] params = getParam(rawParam);
                    consumers.add(param -> setter.set(param, params[0], params[1]));
                }
                return;
            }
            matcher = copyPattern.matcher(s);
            if (matcher.matches()) {
                Matcher paramMatcher = twoParamPattern.matcher(s);
                if (paramMatcher.find()) {
                    String rawParam = paramMatcher.group();
                    String[] params = getParam(rawParam);
                    consumers.add(param -> copier.copy(param, params[0], params[1]));
                }
            }
        });
        return param -> {
            consumers.forEach(consumer -> consumer.accept(param));
            return true;
        };
    }

    private String[] getParam(String rawParam) {
        String[] split = rawParam.split(",");
        String p1 = split[0].trim().replaceAll("\"", "");
        String p2 = split[1].trim().replaceAll("\"", "");
        return new String[]{p1, p2};
    }

    // $SET("", "")
    private Pattern setPattern = Pattern.compile("\\$SET\\(\"\\w+\",\\s*\"\\w*\"\\)");
    // $COPY("", "")
    private Pattern copyPattern = Pattern.compile("\\$COPY\\(\"\\w+\",\\s*\"\\w*\"\\)");
    private Pattern twoParamPattern = Pattern.compile("\"\\w+\",\\s*\"\\w*\"");


}
