package org.apache.yoko.util.cmsf;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.rmi.CORBA.Util;

public enum RepIds {
    ;

    public interface Query {
        public Query suffix(String suffix);
        public Query codebase(String codebase);
        public Class<?> toClass();
        public String toClassName();
    }

    private static final class QueryImpl implements Query {
        public final String repid;
        public final String suffix;
        public final String codebase;

        private QueryImpl(String repid) {
            this(Objects.requireNonNull(repid), "", null);
        }

        private QueryImpl(String repid, String suffix, String codebase) {
            this.repid = repid;
            this.suffix = suffix;
            this.codebase = codebase;
        }

        @Override
        public QueryImpl suffix(String suffix) {
            return new QueryImpl(repid, Objects.requireNonNull(suffix), codebase);
        }

        @Override
        public QueryImpl codebase(String codebase) {
            return new QueryImpl(repid, suffix, codebase);
        }

        @Override
        public Class<?> toClass() {
            return RepIds.toClass(this);
        }

        @Override
        public String toClassName() {
            return RepIds.toClassName(this);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(RepIds.class.getName());

    public static Query query(String repid) {
        return new QueryImpl(repid);
    }

    private static Class<?> toClass(final QueryImpl query) {
        final String repid = query.repid;
        final String suffix = query.suffix;
        final String codebase = query.codebase;
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine(String.format("Searching for class from repid \"%s\" using suffix \"%s\"", repid, suffix));
        Class<?> result = null;
        final String className = toClassName(query);

        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine(String.format("Class name from repid \"%s\" using suffix \"%s\" is \"%s\"", repid, suffix, className));

        if (className != null) {
            try {
                // get the appropriate class for the loading.
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                result = Util.loadClass(className, codebase, loader);
            } catch (ClassNotFoundException ex) {
                if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.fine(String.format("Class \"%s\" not found", className));
                // ignore
            }
        }

        return result;
    }

    private static String toClassName(QueryImpl query) {
        final String repid = query.repid;
        final String suffix = query.suffix;

        String result = null;

        if (repid.startsWith("IDL:")) {
            try {
                StringBuffer buf = new StringBuffer();

                int end = repid.lastIndexOf(':');
                String s;
                if (end < 0)
                    s = repid.substring(4);
                else
                    s = repid.substring(4, end);

                //
                // If the ID contains a prefix, then fix each of the
                // dotted components of the prefix
                //
                int slash = s.indexOf('/');
                if (slash > 0) {
                    String prefix = s.substring(0, slash);
                    String rest = s.substring(slash + 1);
                    java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(
                            prefix, ".");
                    while (tokenizer.hasMoreTokens()) {
                        String tok = tokenizer.nextToken();
                        buf.append(fixName(tok));
                        buf.append('.');
                    }
                    s = rest;
                }

                //
                // "Fix" the remainder of the ID
                //
                java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(
                        s, "/");
                while (tokenizer.hasMoreTokens()) {
                    String tok = tokenizer.nextToken();
                    buf.append(fixName(tok));
                    if (tokenizer.hasMoreTokens())
                        buf.append('.');
                }

                result = buf.toString() + suffix;
            } catch (IndexOutOfBoundsException ex) // if id has bad format
            {
                result = null;
            }
        }
        else if (repid.startsWith ("RMI:")) {
            int end = repid.indexOf (':', 4);
            result = end < 0
                ? repid.substring (4)
                : repid.substring (4, end);
        }
        if (result != null) {
            result = removeUnicodeEscapes(result);
        }
        return result;
    }

    private static String removeUnicodeEscapes(String in) {
        // if no escape sequences are in the string, this is easy
        int escape = in.indexOf("\\U");
        if (escape < 0) {
            return in;
        }

        // get a string buffer at least as long as the input string
        StringBuffer out = new StringBuffer(in.length());
        int start = 0;

        while (escape >= 0) {
            // add the next real segment to the buffer
            out.append(in.substring(start, escape));
            // step over the escape sequence
            escape += 2;

            int value = 0;
            for (int i=0; i<4; i++) {
                char ch = in.charAt(escape++);
                switch (ch) {
                  case '0':
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                     value = (value << 4) + ch - '0';
                     break;
                  case 'a':
                  case 'b':
                  case 'c':
                  case 'd':
                  case 'e':
                  case 'f':
                     value = (value << 4) + 10 + ch - 'a';
                     break;
                  case 'A':
                  case 'B':
                  case 'C':
                  case 'D':
                  case 'E':
                  case 'F':
                     value = (value << 4) + 10 + ch - 'A';
                     break;
                  default:
                      // not sure what to do here.  Just treat it as a 0 nibble
                      value = (value << 4);
                }
            }
            // now append this as a char value
            out.append((char)value);
            // now step and find the next one
            start = escape;
            escape = in.indexOf("\\U", escape);
        }
        // don't forget the trailing segment
        if (start < in.length()) {
            out.append(in.substring(start));
        }
        return out.toString();
    }

    private static String fixName(String name) {
        assert(name.indexOf('.') == -1); // Not for absolute names

        int nameLen = name.length();
        if (nameLen == 0)
            return name;

        final String[] kwds = {
        //
                // Note: This list must be sorted alphabetically
                //
                "abstract", "assert", "boolean", "break", "byte", "case",
                "catch", "char", "class", "clone", "const", "continue",
                "default", "do", "double", "else", "equals", "extends",
                "false", "final", "finalize", "finally", "float", "for",
                "getClass", "goto", "hashCode", "if", "implements", "import",
                "instanceof", "int", "interface", "long", "native", "new",
                "notify", "notifyAll", "null", "package", "private",
                "protected", "public", "return", "short", "static", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "toString", "transient", "true", "try", "void", "volatile",
                "wait", "while" };

        int l = 0;
        int r = kwds.length;

        while (l < r) {
            int m = (l + r) / 2;
            int res = kwds[m].compareTo(name);
            if (res == 0)
                return "_" + name;
            else if (res > 0)
                r = m;
            else
                l = m + 1;
        }

        //
        // Prepend an underscore for each of the reserved suffixes below
        //
        final String[] reserved = { "Helper", "Holder", "Operations", "POA",
                "POATie", "Package", "ValueFactory" };

        String result = name;
        String curr = name;

        boolean match;
        do {
            int currLen = curr.length();

            match = false;
            for (int i = 0; i < reserved.length; i++) {
                if (curr.endsWith(reserved[i])) {
                    //
                    // Prepend an underscore to result
                    //
                    result = "_" + result;

                    //
                    // Remove suffix from curr
                    //
                    int resLen = reserved[i].length();
                    if (currLen > resLen)
                        curr = curr.substring(0, currLen - resLen);
                    else
                        curr = "";

                    match = true;
                    break;
                }
            }
        } while (match);

        return result;
    }
}
