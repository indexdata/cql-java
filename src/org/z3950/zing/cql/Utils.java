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
    // This is hideously inefficient, but I just don't see a better
    // way using the standard JAVA library.
    //
    static String xq(String str) {
	str = replaceString(str, "&", "&amp;");
	str = replaceString(str, "<", "&lt;");
	str = replaceString(str, ">", "&gt;");
	return str;
    }

    // I can't _believe_ I have to write this by hand in 2002 ...
    static String replaceString(String str, String from, String to) {
	StringBuffer sb = new StringBuffer();
	int ix;			// index of next `from'
	int offset = 0;		// index of previous `from' + length(from)

	while ((ix = str.indexOf(from, offset)) != -1) {
	    sb.append(str.substring(offset, ix));
	    sb.append(to);
	    offset = ix + from.length();
	}

	// End of string: append last bit and we're done
	sb.append(str.substring(offset));
	return sb.toString();
    }
}
