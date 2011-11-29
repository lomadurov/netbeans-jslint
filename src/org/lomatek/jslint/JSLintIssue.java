/*
 *  The MIT License
 * 
 *  Copyright (c) 2011 by Stanislav Lomadurov <lord.rojer@gmail.com>
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.lomatek.jslint;

import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Stanislav Lomadurov <lord.rojer@gmail.com>
 */
public class JSLintIssue {
    
    private final int line;
    private final int character;
    private final String reason;
    private final String a;
    private final String b;
    private final int length;
    
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