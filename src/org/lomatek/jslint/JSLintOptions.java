/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

//import org.netbeans.modules.php.api.util.UiUtils;
import org.openide.util.NbPreferences;
import java.lang.Object;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.NativeArray;

import java.util.Iterator;
import java.util.Map;
/**
 *
 * @author LORD
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
    //private static Map options = null;
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
    /*public Map getOptions() {
	if (null != options)
	    return options;
	return setOptions();
    }
    public Map setOptions() {
	options = new HashMap();
	for (String key : OPTIONS) {
	    options.put(key, getOption(key));
	}
	if (0 != getOption("maxlen", 0 ))
	    options.put("maxlen", getOption("maxlen", 0 ));
	options.put("maxerr", getOption("maxerr", 50 ));
	options.put("indent", getOption("indent", 4 ));
	return options;
    }*/
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
	/*Iterator it = options.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry pairs = (Map.Entry)it.next();
	    opts.put((String) pairs.getKey(), opts, (Object)pairs.getValue());
	    //System.out.println(pairs.getKey() + " = " + pairs.getValue());
	}*/
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
