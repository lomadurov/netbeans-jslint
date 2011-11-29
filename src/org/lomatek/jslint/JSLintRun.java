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

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import org.openide.util.Exceptions;

/**
 *
 * @author Stanislav Lomadurov <lord.rojer@gmail.com>
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
