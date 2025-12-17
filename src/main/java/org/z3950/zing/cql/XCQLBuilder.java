package org.z3950.zing.cql;

/**
 *
 * @author jakub
 */
class XCQLBuilder {
    private StringBuilder sb;

    XCQLBuilder(StringBuilder sb) {
        this.sb = sb;
    }

    XCQLBuilder indent(int level) {
        while (level-- > 0) {
            sb.append("  ");
        }
        return this;
    }

    XCQLBuilder xq(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return this;
    }

    XCQLBuilder append(String str) {
        sb.append(str);
        return this;
    }

    public String toString() {
        return sb.toString();
    }
}
