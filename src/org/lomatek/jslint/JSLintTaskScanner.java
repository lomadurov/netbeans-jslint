/*
 *  The MIT License
 * 
 *  Copyright (c) 2010 Radek Ježdík <redhead@email.cz>, Ondřej Brejla <ondrej@brejla.cz>
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.PushTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.netbeans.spi.tasklist.TaskScanningScope;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;

import org.openide.util.NbPreferences;

import java.util.logging.Logger;

import org.openide.cookies.LineCookie;
import org.openide.cookies.EditorCookie;
import org.openide.text.Line;
import javax.swing.text.StyledDocument;
import org.openide.loaders.DataObject;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;

/**
 *
 * @author Radek Ježdík
 */
public class JSLintTaskScanner extends FileTaskScanner {

    private static final String GROUP_NAME = "logging-tasklist";
    private static final String[] TOKENS = {
	"System.out.println",
	"System.err.println",
	"printStackTrace"};
    private Pattern regexp = null;
    private Callback callback = null;

    public JSLintTaskScanner(String name, String desc) {
	super(name, desc, null);
    }

    public static JSLintTaskScanner create() {
	/*String name = org.openide.util.NbBundle.getBundle(JSLintTaskScanner.class).
	getString("LBL_loggingtask");
	String desc = NbBundle.getBundle(JSLintTaskScanner.class).
	getString("HINT_loggingtask");*/
	String name = "JSLint";
	String desc = "JSLint";
	return new JSLintTaskScanner(name, desc);
    }

    @Override
    public List<? extends Task> scan(FileObject file) {
	//List<Task> tasks = new LinkedList<Task>();
	if ( ! "text/javascript".equals(file.getMIMEType()))
	return null;// List<Task>.emtemptyList();
	List<Task> tasks = new ArrayList<Task>();
	try {
	    String text = getContent(file);
	    //file.
	    
	    Logger.getLogger( getClass().getName() ).log( Level.INFO, null);
	    //Logger.getAnonymousLogger().log(Level.WARNING, "Task start");
	    /*Task task = Task.create(file, GROUP_NAME, "Test Task", 4);
	    tasks.add(task);*/
	    NativeArray errors = JSLint.getInstance().run(text);
	    /* Чистим анотацию*/
	    JSLintAnnotation.clear();
	    /**/
	    for (int i = 0; i < errors.getLength(); i++) {
		NativeObject error = (NativeObject) errors.get(i, null);
		if (null == error)
		    continue;
		Number lineNumber = (Number) error.get("line", null);
		Number columnNumber = (Number) error.get("character", null); 
		Object reason = error.get("reason", null);
		Task task = Task.create(file, GROUP_NAME, reason.toString(), lineNumber.intValue());
		tasks.add(task);
		/*//Определям длину выделения
		Object a = (Object) error.get("a", null);
		Object b = (Object) error.get("b", null);
		Line line = lc.getLineSet().getCurrent(lineNumber.intValue()-1);
		Line.Part partLine = null;
		if (!(a instanceof Undefined) && !"(space)".equals(a.toString()) && b instanceof Undefined) {
		    partLine = line.createPart(columnNumber.intValue()-1, a.toString().length());
		} else {
		    partLine = line.createPart(columnNumber.intValue()-1, 1);
		}
		//Выводим ошибку
		writer.println("Error: " + reason + lineNumber.intValue() +':'+columnNumber.intValue());
		JSLintAnnotation.createAnnotation(partLine, reason.toString(), lineNumber.intValue(), columnNumber.intValue());*/
	    }
	    
	    
	    /*int index = 0;
	    int lineno = 1;
	    int len = text.length();
	    Matcher matcher = getScanRegexp().matcher(text);
	    while (index < len && matcher.find(index)) {
		int begin = matcher.start();
		int end = matcher.end();
		
		...
            String description = text.subSequence(begin, nonwhite + 1).toString();
		Task task = Task.create(file, GROUP_NAME, description, lineno);
		tasks.add(task);
	    }*/
	} catch (Exception e) {
	    //Logger.getLogger(getClass().getName()).info(e);
	}
	return tasks;
    }

    private String getContent(FileObject file) throws IOException {
	// extract the content from the file
	//LineCookie lc = (LineCookie) file.getCookie(LineCookie.class);
	//return null;
	return file.asText();
    }

    private Pattern getScanRegexp() {
	if (regexp == null) {
	    // create pattern for the tokens
	}
	return regexp;
    }

    public void attach(Callback callback) {
	if (callback == null && this.callback != null) {
	    regexp = null;
	}
	this.callback = callback;
    }

    @Override
    public void notifyPrepare() {
	getScanRegexp();
    }

    @Override
    public void notifyFinish() {
	regexp = null;
    }
}