/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import java.io.File;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;

import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;

import org.openide.text.Line;
import javax.swing.text.StyledDocument;
/**
 *
 * @author LORD
 */
public class JSLintRunnableB implements Runnable {
    static final String EXECUTABLE_KEY = "tidyExecutable";
    private final DataObject nodeData;
    
    public JSLintRunnableB(DataObject nodeData, String commandLineArgs) {
	this.nodeData = nodeData;
    }
    
    @Override
    public void run() {
	try {
	    InputOutput io = IOProvider.getDefault().getIO("JSLint sample II", false);
	    io.select(); //Tree tab is selected
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
	    
	    
	    NativeArray errors = JSLint.getInstance().run(mydoc.getText(0, mydoc.getLength()));
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
	    }
	    
	    writer.close();
	} catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
}
