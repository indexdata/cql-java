// $Id: Utils.java,v 1.2 2002-11-06 00:05:58 mike Exp $

package org.z3950.zing.cql;


/**
 * Utility functions for the org.z3950.zing.cql package.
 * Not intended for use outside this package.
 *
 * @version	$Id: Utils.java,v 1.2 2002-11-06 00:05:58 mike Exp $
 */
class Utils {
    static String indent(int level) {
	String x = "";
	while (level-- > 0) {
	    x += "  ";
	}
	return x;
    }

    // XML Quote --
    //	s/&/&amp;/g;
    //	s/</&lt;/g;
    //	s/>/&gt;/g;
    static String xq(String str) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<str.length(); i++) {
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
	return sb.toString();
    }

}
