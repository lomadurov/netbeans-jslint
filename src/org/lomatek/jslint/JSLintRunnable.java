/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import org.openide.ErrorManager;
import org.openide.cookies.LineCookie;
import org.openide.cookies.SaveCookie;
//import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Mozzila
 */
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

/**/
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 *
 * @author LORD
 */
public class JSLintRunnable implements Runnable {
    static final String EXECUTABLE_KEY = "tidyExecutable";
    private Node[] nodes;
    private final DataObject nodeData;
    
    private String commandLineArgs;
    private String tidyExecutable = null;
    private boolean forceSave = false;
    
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
        this.commandLineArgs = commandLineArgs;
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
    
    void processJSLint(final OutputWriter writer, final DataObject dataObject)
            throws InterruptedException, IOException {
        FileObject fileObject = dataObject.getPrimaryFile();
        File file = FileUtil.toFile(fileObject);
	LineCookie lc = (LineCookie) dataObject.getCookie(LineCookie.class);
	
        writer.println("File: " + file + " - Size: " + file.length() + "...");
	/**
	 * Init ok
	 */
	jscontext = Context.enter();
	jscontext.setLanguageVersion(Context.VERSION_1_6);
	scope = jscontext.initStandardObjects();

	Reader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
	    .getResourceAsStream("org/lomatek/jslint/resources/jslint.js"), Charset.forName("UTF-8")));
	jscontext.evaluateReader(scope, reader, "JSLint", 1, null);

	String options = "/*jslint maxerr: 50 */";
	scope.put("contents", scope, fileObject.asText());
	scope.put("opts", scope, options);

	jscontext.evaluateString(scope, "results = JSLINT(contents);", "JSLint", 1, null);
	Scriptable lint = (Scriptable) scope.get("JSLINT", scope);
	NativeArray errors = (NativeArray) lint.get("errors", null);
	for (int i = 0; i < errors.getLength(); i++) {
	    NativeObject error = (NativeObject) errors.get(i, null);
	    Double lineNo = (Double) error.get("line", null);
	    Double columNo = (Double) error.get("character", null); 
	    Object reason = error.get("reason", null);
	    writer.println("Error: " + reason + lineNo);
	    JSLintAnnotation.createAnnotation(lc, reason.toString(), lineNo.intValue(), columNo.intValue());
	}
	/**
	 * Loaded ok
	 */
        writer.println("Exit: ");
        writer.flush();
    }
}
