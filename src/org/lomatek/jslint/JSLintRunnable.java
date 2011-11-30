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
/*import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;*/
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
	    /*InputOutput io = IOProvider.getDefault().getIO("JSLint sample II", false);
	    OutputWriter writer = io.getOut();
	    //Clear Output
	    writer.reset();*/
	    
	    /**
	     * Init standart object
	     */
	    FileObject fileObject = nodeData.getPrimaryFile();
	    File file = FileUtil.toFile(fileObject);
	    LineCookie lc = nodeData.getCookie(LineCookie.class);
	    EditorCookie edc = nodeData.getCookie(EditorCookie.class);
	    StyledDocument mydoc = edc.getDocument();
	    
	    /**/
	    JSLintIssueAnnotation.clear(nodeData);
	    List<JSLintIssue> errors = JSLintRun.getInstance().run(mydoc.getText(0, mydoc.getLength()));
	    for (JSLintIssue issue : errors) {
		JSLintIssueAnnotation.createAnnotation(nodeData, lc, issue.getReason(), issue.getLine(), issue.getCharacter(), issue.getLength());
	    }
	    /*writer.close();*/
	} catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
}
