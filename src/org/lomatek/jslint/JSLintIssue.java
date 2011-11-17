/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Undefined;

/**
 *
 * @author LORD
 */
public class JSLintIssue {
    
    private final int line;
    private final int character;
    private final String reason;
    private final String a;
    private final String b;
    private final int length;
    /*private final String c;
    private final String d;
    private final String evidence;
    private final String raw;*/
    
    public JSLintIssue(Scriptable error) {
	line = ((Number) error.get("line", null)).intValue();
	character = (int) ((Number) error.get("character", null)).intValue();
	reason = objectToString("reason", error);
	a = objectToString("a", error);
	b = objectToString("b", error);
	if (null != a && !"(space)".equals(a) && null == b) {
	    length = a.length();
	} else {
	    length = 1;
	}
    }
    
    private String objectToString(String name, Scriptable scope) {
	Object obj = scope.get(name, scope);
        return obj instanceof String ? (String) obj : null;
    }
    
    /**
     * @return A string of auxiliary information.
     */
    public int getLength() {
	return length;
    }
     /**
     * @return A string of auxiliary information.
     */
    public String getA() {
        return a;
    }

    /**
     * @return A string of auxiliary information.
     */
    public String getB() {
        return b;
    }

    /**
     * @return the position of the issue within the line. Starts at 0.
     */
    public int getCharacter() {
        return character;
    }

    /**
     * @return the number of the line on which this issue occurs.
     */
    public int getLine() {
        return line;
    }

    /**
     * @return a textual description of this issue.
     */
    public String getReason() {
        return reason;
    }
    
    
}
