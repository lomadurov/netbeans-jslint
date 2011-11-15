/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import java.io.BufferedReader;
import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.prefs.Preferences;
//import java.util.regex.Matcher;
import org.openide.ErrorManager;
//import org.openide.cookies.LineCookie;
//import org.openide.cookies.SaveCookie;
//import org.openide.execution.NbProcessDescriptor;
//import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
//import org.openide.loaders.DataObject;
//import org.openide.nodes.Node;
//import org.openide.util.NbBundle;
//import org.openide.windows.IOProvider;
//import org.openide.windows.InputOutput;
//import org.openide.windows.OutputWriter;

/**
 * Mozzila
 */
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;

/**/
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;


/***/

import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;


import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;

import org.openide.text.Line;

import org.lomatek.jslint.JSLintOptions;


//import java.util.concurrent.TimeUnit;

import javax.swing.text.StyledDocument;
/**
 *
 * @author LORD
 */
public class JSLintRunnable implements Runnable {
    static final String EXECUTABLE_KEY = "tidyExecutable";
    private Node[] nodes;
    private final DataObject nodeData;
    
    /*private String commandLineArgs;
    private String tidyExecutable = null;
    private boolean forceSave = false;*/
    
    private Context jscontext = null;
    private Scriptable scope = null;
    
    /**
     * Initialize HTML-Tidy runnable.
     * @param nodes List of nodes to process
     * @param commandLineArgs Command line argument for the execution
     */
    public JSLintRunnable(DataObject nodeData, String commandLineArgs) {
        //Load Settings
	//String defaultExecutablePath = NbBundle.getMessage(TidyErrorCheckAction.class, "AdvancedOption_Executable_Path_Default");
        /*tidyExecutable = Preferences.userNodeForPackage(
                TidyConfigurationPanel.class).get(EXECUTABLE_KEY, defaultExecutablePath);*/
	
	
        JSLintAnnotation.clear();
        this.nodeData = nodeData;
        //this.commandLineArgs = commandLineArgs;
        //this.forceSave = forceSave;
    }
    
    @Override
    public void run() {
	InputOutput io = IOProvider.getDefault().getIO("JSLint smpl", false);
        io.select(); //Tree tab is selected
        OutputWriter writer = io.getOut();

        try {
            writer.reset(); //clean the output window
            //writer.println("Selected " + nodes.length + " objects ...");
	    writer.println("Ok file");

            //if (new File(getTidyExecutable()).exists()) {// Check, if executable exits
                //for (int i = 0; i < nodes.length; i++) {
                    //DataObject dataObject = (DataObject) nodes[i].getCookie(DataObject.class);
                    processJSLint(writer, nodeData);
                //}
            /*} else {
                writer.println("Executable not found at " + getTidyExecutable() + ". Please select a valid path in the options panel.");
            }*/

        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        writer.close();
    }
    
    void processJSLint(final OutputWriter writer, final DataObject dataObject) {
	try {
	    FileObject fileObject = dataObject.getPrimaryFile();
	    File file = FileUtil.toFile(fileObject);
	    LineCookie lc = (LineCookie) dataObject.getCookie(LineCookie.class);
	    EditorCookie edc = (EditorCookie) dataObject.getCookie(EditorCookie.class);
	    StyledDocument mydoc = edc.getDocument();
	    //String options_my = JSLintOptions.getInstance().getOptions();

	    writer.println("File: " + file + " - Size: " + file.length() + "...");
	    //writer.println(options_my);
	    /**
	     * Init ok
	     */
	    jscontext = Context.enter();
	    jscontext.setLanguageVersion(Context.VERSION_1_6);
	    scope = jscontext.initStandardObjects();

	    //Read and evaluate JSLint
	    Reader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
		.getResourceAsStream("org/lomatek/jslint/resources/jslint.js"), Charset.forName("UTF-8")));
	    jscontext.evaluateReader(scope, reader, "JSLint", 1, null);
	    /**
	     * Init ok
	     */
	    String options = "/*jslint maxerr: 50 */";
	    scope.put("contents", scope, mydoc.getText(0, mydoc.getLength()));
	    scope.put("opts", scope, options);

	    jscontext.evaluateString(scope, "results = JSLINT(contents);", "JSLint", 1, null);
	    Scriptable lint = (Scriptable) scope.get("JSLINT", scope);
	    NativeArray errors = (NativeArray) lint.get("errors", null);
	    for (int i = 0; i < errors.getLength(); i++) {
		NativeObject error = (NativeObject) errors.get(i, null);
		if (null == error)
		    continue;
		Number lineNumber = (Number) error.get("line", null);
		Number columnNumber = (Number) error.get("character", null); 
		Object reason = error.get("reason", null);
		//Определям длину
		Object a = (Object) error.get("a", null);
		Object b = (Object) error.get("b", null);
		//if (a != "undefined") 
		//if ("undefined" != a && "undefined" == b)
		//Line line = lc.getLineSet().getOriginal(lineNumber.intValue()-1);
		Line line = lc.getLineSet().getCurrent(lineNumber.intValue()-1);
		Line.Part partLine = null;
		if (!(a instanceof Undefined) && !"(space)".equals(a.toString()) && b instanceof Undefined) {
		    /*Line line = lc.getLineSet().getOriginal(lineNumber.intValue()-1);
		    Line.Part partLine = null; //lc.getLineSet().getOriginal(lineNumber -1 );*/
		    partLine = line.createPart(columnNumber.intValue()-1, a.toString().length());
		    //writer.println("Select: " + a.toString());
		} else {
		    partLine = line.createPart(columnNumber.intValue()-1, 1);
		}
		
		
		writer.println("Error: " + reason + lineNumber.intValue() +':'+columnNumber.intValue());
		JSLintAnnotation.createAnnotation(partLine, reason.toString(), lineNumber.intValue(), columnNumber.intValue());
	    }
	    /**
	     * Loaded ok
	     */
	    writer.println("Exit: ");
	    writer.flush();
	} catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
