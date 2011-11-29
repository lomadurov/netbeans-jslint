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

import java.io.File;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;

import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;

import java.util.List;
import javax.swing.text.StyledDocument;
/**
 *
 * @author Stanislav Lomadurov
 */
public class JSLintRunnable implements Runnable {
    static final String EXECUTABLE_KEY = "jslintExecutable";
    private final DataObject nodeData;
    
    public JSLintRunnable(DataObject nodeData, String commandLineArgs) {
	this.nodeData = nodeData;
    }
    
    @Override
    public void run() {
	try {
	    InputOutput io = IOProvider.getDefault().getIO("JSLint sample II", false);
	    //io.select(); //Tree tab is selected
	    OutputWriter writer = io.getOut();
	    //Clear Output
	    writer.reset();
	    
	    /**
	     * Init standart object
	     */
	    FileObject fileObject = nodeData.getPrimaryFile();
	    File file = FileUtil.toFile(fileObject);
	    LineCookie lc = (LineCookie) nodeData.getCookie(LineCookie.class);
	    EditorCookie edc = (EditorCookie) nodeData.getCookie(EditorCookie.class);
	    StyledDocument mydoc = edc.getDocument();
	    
	    /**/
	    JSLintIssueAnnotation.clear(nodeData);
	    List<JSLintIssue> errors = JSLintRun.getInstance().run(mydoc.getText(0, mydoc.getLength()));
	    for (JSLintIssue issue : errors) {
		JSLintIssueAnnotation.createAnnotation(nodeData, lc, issue.getReason(), issue.getLine(), issue.getCharacter(), issue.getLength());
	    }
	    /*
	    NativeArray errors = JSLint.getInstance().run(mydoc.getText(0, mydoc.getLength()));
	    // Чистим анотацию
	    JSLintAnnotation.clear();
	    
	    for (int i = 0; i < errors.getLength(); i++) {
		NativeObject error = (NativeObject) errors.get(i, null);
		if (null == error)
		    continue;
		Number lineNumber = (Number) error.get("line", null);
		Number columnNumber = (Number) error.get("character", null); 
		Object reason = error.get("reason", null);
		//Определям длину выделения
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
		JSLintAnnotation.createAnnotation(partLine, reason.toString(), lineNumber.intValue(), columnNumber.intValue());
	    }*/
	    
	    writer.close();
	} catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
}
