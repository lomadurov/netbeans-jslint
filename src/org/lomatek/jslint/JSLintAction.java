/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import org.openide.cookies.EditorCookie;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOColorLines;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

import org.openide.text.Line;
import org.openide.text.Line.ShowOpenType;
import org.openide.text.Line.ShowVisibilityType;

import org.openide.cookies.EditCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.ViewCookie;
import org.openide.loaders.DataObject;

import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Issue;
//import com.googlecode.jslint4java.

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.swing.text.StyledDocument;

@ActionID(category = "Build",
id = "org.lomatek.jslint.JSLintAction")
@ActionRegistration(displayName = "#CTL_JSLintAction")
@ActionReferences({
    @ActionReference(path = "Editors/text/javascript/Popup", position = 400)
})
//@Messages("CTL_JSLintAction=JSLint")
public final class JSLintAction implements ActionListener {
    
    private JSLint lint;
    private final JSLintBuilder lintBuilder = new JSLintBuilder();
    
    //DataObject EditorCookie
    private final EditorCookie context;
    
    //JSLint Scriptable
    private Context jscontext = null;
    private Scriptable scope = null;
    
    public JSLintAction(EditorCookie context) {
	this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
	try {
	    final InputOutput io = IOProvider.getDefault().getIO("JSLink", false);
            final OutputWriter out = io.getOut();
	    
            OutputWriter erout = io.getErr();
            io.select();
            io.setOutputVisible(true);
            out.reset();
	    IOColorLines.println(io, "Hello JSLint", Color.darkGray);

	    //Выводим 
	    StyledDocument mydoc = context.getDocument();
	    IOColorLines.println(io, mydoc.getText(0, mydoc.getLength()), Color.darkGray);
	    /**
	     * Старый метод
	     */
	    /*lint = makeLint();
	    
	    try {
		JSLintResult result = lint.lint("name", mydoc.getText(0, mydoc.getLength()));
		formatOutput(result, io);
		
	    } catch (Exception e) {
		IOColorLines.println(io, "No such file", Color.darkGray);
	    }*/
	    
	    /**
	     * Новый метод
	     */
	    
	    /**
	     * INIT CONTEXT
	     */
	    if (null == jscontext) {
		jscontext = Context.enter();
		jscontext.setLanguageVersion(Context.VERSION_1_6);
		scope = jscontext.initStandardObjects();

		Reader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
		    .getResourceAsStream("org/lomatek/jslint/resources/jslint.js"), Charset.forName("UTF-8")));
		jscontext.evaluateReader(scope, reader, "JSLint", 1, null);
	    }
	    
	    
	    String options = "/*jslint maxerr: 50 */";
	    scope.put("contents", scope, mydoc.getText(0, mydoc.getLength()));
	    scope.put("opts", scope, options);
	    
	    jscontext.evaluateString(scope, "results = JSLINT(contents);", "JSLint", 1, null);
	    Scriptable lint = (Scriptable) scope.get("JSLINT", scope);
	    NativeArray errors = (NativeArray) lint.get("errors", null);
	    for (int i = 0; i < errors.getLength(); i++) {
		NativeObject error = (NativeObject) errors.get(i, null);
		Double lineNo = (Double) error.get("line", null);
		Object reason = error.get("reason", null);
		IOColorLines.println(io, "Error: " + reason + lineNo, Color.darkGray);
	    }
	    
	} catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    /*private JSLint makeLint() {
        try {
                return lintBuilder.fromDefault();
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return null;
    }
    public void formatOutput(JSLintResult result, InputOutput io) {
        try {
	    for (Issue issue : result.getIssues()) {
		StringBuilder substr = new StringBuilder();
		substr.append(issue.getSystemId());
		substr.append(':');
		substr.append(issue.getLine());
		substr.append(':');
		substr.append(issue.getCharacter());
		// NB: space before reason to look like javac!
		substr.append(": ");
		substr.append(issue.getReason());
		substr.append(System.getProperty("line.separator"));
		String evidence = issue.getEvidence();
		if (evidence != null && !"".equals(evidence)) {
		    substr.append(evidence);
		    substr.append(System.getProperty("line.separator"));
		    // character is now one-based.
		    substr.append(spaces(issue.getCharacter() - 1));
		    substr.append("^");
		    substr.append(System.getProperty("line.separator"));
		}
		IOColorLines.println(io, substr.toString(), Color.RED);
		//IOColorLines.println(io, substr.toString(), new jsLintOutput(this.fo, issue.getLine()), true, Color.RED);
		//sb.append(outputOneIssue(issue));return sb.toString(); 
		substr = null;
	    }
	} catch (Exception ex) {
	    Exceptions.printStackTrace(ex);
	}
	//return sb.toString();        
    }
    
    protected String spaces(int howmany) {
        StringBuffer sb = new StringBuffer(howmany);
        for (int i = 0; i < howmany; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }*/
}
