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
import org.mozilla.javascript.Scriptable;
/**
 *
 * @author Stanislav Lomadurov <lord.rojer@gmail.com>
 */
public class JSLintOptions {
    
    private static JSLintOptions INSTANCE;
    
    private static final String NETTE_LOADER_PATH = "nette-loader-path";
    private static final String SANDBOX_PATH = "sandbox-path";
    
    private static final String[] OPTIONS = {"devel", "bitwise", "regexp", 
	"browser", "confusion", "undef", "node", "continue", "unparam", "rhino", 
	"debug", "sloppy", "widget", "eqeq", "sub", "windows", "es5", "vars", 
	"evil", "white", "passfail", "forin", "css", "newcap", "cap", "safe", 
	"nomen", "on", "adsafe", "plusplus", "fragment"};
    private static String directive = null;
    private static Scriptable options = null;
    
    public static JSLintOptions getInstance() {
	    if (INSTANCE == null) {
		    INSTANCE = new JSLintOptions();
	    }

	    return INSTANCE;
    }
    
    private JSLintOptions() {}
    
    public boolean getOption(String key) {
	return NbPreferences.forModule(JSLintOptions.class).getBoolean(key, false);
    }
    public int getOption(String key, int integer) {
	return NbPreferences.forModule(JSLintOptions.class).getInt(key, integer);
    }
    public void setOption(String key, boolean value) {
	NbPreferences.forModule(JSLintOptions.class).putBoolean(key, value);
    }
    public void setOption(String key, int value) {
	NbPreferences.forModule(JSLintOptions.class).putInt(key, value);
    }
    public Scriptable getOptions(Context context, Scriptable scope){
	if (null != options)
	    return options;
	options = context.newObject(scope);
	for (String key : OPTIONS) {
	    options.put(key, options, getOption(key));
	}
	if (0 != getOption("maxlen", 0 ))
	    options.put("maxlen", options, getOption("maxlen", 0 ));
	options.put("maxerr", options, getOption("maxerr", 50 ));
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
	    if (!"0".equals(getOption("maxlen", 0 )))
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
