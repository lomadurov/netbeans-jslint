/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Undefined;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import org.openide.util.Exceptions;

import org.lomatek.jslint.JSLintIssue;

/**
 *
 * @author LORD
 */
final public class JSLintRun {

    private static JSLintRun instance;
    /**
     * Rhino context
     * @url https://developer.mozilla.org/En/Rhino_documentation/Scopes_and_Contexts
     */
    private Context context = null;
    private Scriptable scope = null;

    JSLintRun() {
    }

    public static JSLintRun getInstance() {
	if (instance == null) {
	    instance = new JSLintRun();
	}
	return instance;
    }

    public List<JSLintIssue> run(String contents) {
	init();
	scope.put("contents", scope, contents);
	//Get options
	scope.put("opts", scope, JSLintOptions.getInstance().getOptions(context, scope));
	context.evaluateString(scope, "results = JSLINT(contents, opts);", "JSLint", 1, null);
	Scriptable lint = (Scriptable) scope.get("JSLINT", scope);
	//Выходим из контекста
	context.exit();
	//Собираем и обрабытываем ошибки
	NativeArray errors = (NativeArray) lint.get("errors", null);
	List<JSLintIssue> result = new ArrayList<JSLintIssue>();
	for (int i = 0; i < errors.getLength(); i++) {
	    NativeObject error = (NativeObject) errors.get(i, null);
	    if (null == error) {
		continue;
	    }
	    //Добавляем ошибку в результат
	    result.add(new JSLintIssue(error));
	}
	return result;//(NativeArray) lint.get("errors", null);
    }

    public void init() {
	try {
	    context = Context.enter();
	    context.setLanguageVersion(Context.VERSION_1_6);

	    if (null == scope) {
		scope = context.initStandardObjects();
		Reader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("org/lomatek/jslint/resources/jslint.js"), Charset.forName("UTF-8")));

		context.evaluateReader(scope, reader, "JSLint", 1, null);
	    }
	} catch (IOException ex) {
	    Exceptions.printStackTrace(ex);
	}
    }
}
