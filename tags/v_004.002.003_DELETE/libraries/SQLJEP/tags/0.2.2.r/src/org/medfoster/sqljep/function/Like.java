/*****************************************************************************
      SQLJEP - Java SQL Expression Parser 0.2
      November 1 2006
         (c) Copyright 2006, Alexey Gaidukov
      SQLJEP Author: Alexey Gaidukov

      SQLJEP is based on JEP 2.24 (http://www.singularsys.com/jep/)
           (c) Copyright 2002, Nathan Funk
 
      See LICENSE.txt for license information.
 *****************************************************************************/

package org.medfoster.sqljep.function;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.medfoster.sqljep.ASTFunNode;
import org.medfoster.sqljep.JepRuntime;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.exceptions.WrongTypeException;

/**
 * Like algorithm. It supports two variants of syntax. The first is SQL syntax.
 * The second is JRE regexp syntax. By default SQL syntax is used. To use regexp
 * syntax put pattern into slashes. For example "/^10*.?/"
 */
public final class Like extends PostfixCommand {

    protected static final Integer ZERO_OR_MORE_CHARS = 0;
    protected static final Integer ONE_CHAR = 1;

    protected static Hashtable<String, Pattern> patterns = new Hashtable<String, Pattern>();

    @Override
    final public int getNumberOfParameters() {
        return 2;
    }

    @Override
    public void evaluate(ASTFunNode node, JepRuntime runtime) throws ParseException {
        node.childrenAccept(runtime.ev, null);
        Comparable<?> param2 = runtime.stack.pop();
        Comparable<?> param1 = runtime.stack.pop();
        runtime.stack.push(like(param1, param2));
    }

    public boolean like(Comparable<?> param1, Comparable<?> param2) throws ParseException {

        if (param1 == null || param2 == null) {
            return false;
        }

        if (validateParameters(param1, param2)) {

            String source = param1.toString();
            String match = param2.toString();
            Pattern pattern = patterns.get(match);

            if (pattern == null) {
                ArrayList<Object> p = compile(match);
                String regexp = toRegExp(p);
                pattern = Pattern.compile(regexp);
                patterns.put(match, pattern);
            }

            Matcher m = pattern.matcher(source);

            return m.find();

        }

        throw new WrongTypeException(getFunctionName(), param1, param2);
    }

    public boolean validateParameters(Comparable<?> param1, Comparable<?> param2) {
        return (param1 instanceof String) && (param2 instanceof String);
    }

    private ArrayList<Object> compile(String pattern) {

        ArrayList<Object> format = new ArrayList<Object>();
        StringBuilder fill = new StringBuilder();
        final int plen = pattern.length();
        Character lastSymbol = null;

        if (pattern.length() > 2 && pattern.charAt(0) == '/' && pattern.charAt(pattern.length() - 1) == '/') {
            format.add(pattern.substring(1, pattern.length() - 2));
        } else {

            for (int i = 0; i < plen; i++) {

                char c = pattern.charAt(i);

                if (lastSymbol != null && lastSymbol.charValue() == '\\') {
                    lastSymbol = null;
                    fill.append(c);
                } else {
                    if (c == '%') {
                        if (fill.length() > 0) {
                            format.add(fill.toString());
                            fill.setLength(0);
                        }
                        format.add(ZERO_OR_MORE_CHARS);
                    } else if (c == '_') {
                        if (fill.length() > 0) {
                            format.add(fill.toString());
                            fill.setLength(0);
                        }
                        format.add(ONE_CHAR);
                    } else {
                        fill.append(c);
                    }
                    lastSymbol = c;
                }
            }

            if (fill.length() > 0) {
                format.add(fill.toString());
            }
        }

        return format;
    }

    private String toRegExp(ArrayList<Object> pattern) {

        if (pattern != null) {

            StringBuilder str = new StringBuilder("^");
            for (Object o : pattern) {
                if (o == ZERO_OR_MORE_CHARS) {
                    str.append(".*?");
                } else if (o == ONE_CHAR) {
                    str.append(".?");
                } else {
                    str.append((String)o);
                }
            }

            return str.toString();
        } else {
            return null;
        }
    }
}
