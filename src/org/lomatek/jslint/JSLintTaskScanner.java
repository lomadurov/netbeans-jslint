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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.Collections;
import java.util.logging.Logger;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Stanislav Lomadurov 
 */
public class JSLintTaskScanner extends FileTaskScanner {

    private static final String GROUP_NAME = "logging-tasklist";
    private Callback callback = null;

    public JSLintTaskScanner(String name, String desc) {
	super(name, desc, null);
    }

    public static JSLintTaskScanner create() {
	String name = org.openide.util.NbBundle.getBundle(JSLintTaskScanner.class).getString("LBL_task");
	String desc = org.openide.util.NbBundle.getBundle(JSLintTaskScanner.class).getString("DESC_task");
	return new JSLintTaskScanner(name, desc);
    }

    @Override
    public List<? extends Task> scan(FileObject file) {
	// Если файл не JavaScript игнорируем его
	if ( ! "text/javascript".equals(file.getMIMEType()))
	    return Collections.<Task>emptyList();

	List<Task> tasks = new ArrayList<Task>();
	try {
	    String text = getContent(file);
	    
	    /* Ишем наш редактор */
	    DataObject dObj = DataObject.find(file);
	    LineCookie cLine = null;
	    StyledDocument currentDocument = null;
	    List<JSLintIssue> errors = JSLintRun.getInstance().run(text);
	    if (null != dObj) {
		EditorCookie cEditor = dObj.getCookie(EditorCookie.class);
		cLine = dObj.getCookie(LineCookie.class);
		currentDocument = cEditor.getDocument();
		//Чистим аннотацию
		JSLintIssueAnnotation.clear(dObj);
	    }
	    if (errors.isEmpty())
		return Collections.<Task>emptyList();
	    for (JSLintIssue issue : errors) {
		if (null != currentDocument) {
		    JSLintIssueAnnotation.createAnnotation(dObj, cLine, issue.getReason(), issue.getLine(), issue.getCharacter(), issue.getLength());
		}
		//Создаём задание
		Task task = Task.create(file, GROUP_NAME, issue.getReason(), issue.getLine());
		tasks.add(task);
	    }
	} catch (Exception e) {
	    Logger.getLogger(getClass().getName()).log(Level.WARNING, null, e);
	}
	return tasks;
    }

    private String getContent(FileObject file) throws IOException {
	//TODO: Add encoding
	return file.asText("UTF-8");
    }
    
    @Override
    public void attach(Callback callback) {
	this.callback = callback;
    }
}