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

import org.openide.util.NbPreferences;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
/**
 *
 * @author Stanislav Lomadurov <lord.rojer@gmail.com>
 */
public class JSLintOptions {
    
    private static JSLintOptions INSTANCE;
    
    private static final String[] OPTIONS = {"option", "ass", "bitwise", 
        "browser", "closure", "continue", "couch", "debug", "devel", "eqeq", 
        "evil", "forin", "newcap","node", "nomen", "passfail", "plusplus", 
        "predef", "regexp", "rhino", "sloppy", "stupid", "sub", "todo", 
        "unparam", "vars", "white"};
    private static String directive = null;
    private static Scriptable options = null;
    
    public static JSLintOptions getInstance() {
	    if (INSTANCE == null) {
		    INSTANCE = new JSLintOptions();
	    }

	    return INSTANCE;
    }
    
    private JSLintOptions() {}
    
    // Get option as boolean
    public boolean getOption(String key) {
	return NbPreferences.forModule(JSLintOptions.class).getBoolean(key, false);
    }
    // Get option as integer
    public int getOption(String key, int integer) {
	return NbPreferences.forModule(JSLintOptions.class).getInt(key, integer);
    }
    // Get option as string
    public String getOption(String key, String str){
        return NbPreferences.forModule(JSLintOptions.class).get(key, str);
    }
    // Set option as boolean
    public void setOption(String key, boolean value) {
	NbPreferences.forModule(JSLintOptions.class).putBoolean(key, value);
    }
    // Set option as integer
    public void setOption(String key, int value) {
	NbPreferences.forModule(JSLintOptions.class).putInt(key, value);
    }
    // Set option as string
    public void setOption(String key, String value) {
	NbPreferences.forModule(JSLintOptions.class).put(key, value);
    }
    // Get the Rhino context contains options for JSLint
    public Scriptable getOptions(Context context, Scriptable scope){
	if (null != options)
	    return options;
	options = context.newObject(scope);
	for (String key : OPTIONS) {
	    options.put(key, options, getOption(key));
	}
	// Predefined
	if ( ! "".equals(getOption("predef", ""))) {
	    Object[] opts = getOption("predef", "").replaceAll("\\s+", "").split(",");
	    options.put("predef", options, new NativeArray(opts));
	}
	// Maximum line length
	if (0 != getOption("maxlen", 0 ))
	    options.put("maxlen", options, getOption("maxlen", 0 ));
	// Maximum number of errors
	options.put("maxerr", options, getOption("maxerr", 50 ));
	// Indentation
	options.put("indent", options, getOption("indent", 4 ));
	return options;
    }
    public void removeOptions() {
	options = null;
    }
    /**
     * Get options by string
     * 
     * @param bool
     * @return 
     */
    public String getOptions(boolean bool) {
	int i = 0;
	if (null == directive) {
	    StringBuilder str = new StringBuilder();
	    str.append("/*jslint ");
	    for (String key : OPTIONS) {
		str.append(key);
		str.append(": ");
		if (getOption(key)) 
		    str.append("true, ");
		else
		    str.append("false, ");
	    }
	    if (!"".equals(getOption("predef", ""))) {
		str.append("predef:[");
		str.append(getOption("predef", ""));
		str.append("]");
	    }
	    if (0 != getOption("maxlen", 0))
		str.append("maxlen: ")
			.append(getOption("maxlen", 0 ))
			.append(", ");
	    str.append("maxerr: ")
		    .append(getOption("maxerr", 50))
		    .append(",  indent: ")
		    .append(getOption("indent", 4))
		    .append(" */");
	    
	    directive = str.toString();
	}
	return directive; 
    }
}
