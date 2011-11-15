/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import java.io.IOException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.NativeArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import org.openide.util.Exceptions;
/**
 *
 * @author LORD
 */
final public class JSLint {
    private static JSLint instance;
    
    private Context context = null;
    private Scriptable scope = null;
    
    JSLint (){
	
    }
    public static JSLint getInstance() {
	if (instance == null) {
		instance = new JSLint();
	}
	return instance;
    }
    
    public NativeArray run(String contents) {
	init();
	scope.put("contents", scope, contents);
	//Get options
	scope.put("opts", scope, JSLintOptions.getInstance().getOptions(context, scope));
	context.evaluateString(scope, "results = JSLINT(contents, opts);", "JSLint", 1, null);
	Scriptable lint = (Scriptable) scope.get("JSLINT", scope);
	//Выходим из контекста
	context.exit();
	return (NativeArray) lint.get("errors", null);
    }
    public void init() {
	try {
	    context = Context.enter();
	    context.setLanguageVersion(Context.VERSION_1_6);
	    
	    if (null == scope) {
		scope = context.initStandardObjects();
		Reader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
			.getResourceAsStream("org/lomatek/jslint/resources/jslint.js"), Charset.forName("UTF-8")));

		context.evaluateReader(scope, reader, "JSLint", 1, null);
	    }
	} catch (IOException ex) {
	    Exceptions.printStackTrace(ex);
	}
    }
    
}
