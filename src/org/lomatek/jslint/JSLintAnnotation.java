/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.openide.cookies.LineCookie;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;

/**
 *
 * @author LORD
 */
public class JSLintAnnotation extends Annotation {
    private static List<Annotation> annotations = new ArrayList<Annotation>();
    private static String[] annoType = {
	//JSLintErrorAnnotation jslinterrorannotation
        "org-lomatek-jslint-jslinterrorannotation"
    };
    
    private String reason;
    private int column;
    private int severity = 0;

    public static JSLintAnnotation create(String severity, int column, String reason) {
        JSLintAnnotation annotation = new JSLintAnnotation(severity, column, reason);
        annotations.add(annotation);
        return annotation;
    }
    
    private JSLintAnnotation(String severity, int column, String reason) {
        this.severity = severity.contains("Err") ? 0 : 1;
        this.reason = reason;
        this.column = column;
    }
    
    public static void clear() {
        for (Annotation annotation : annotations) {
            annotation.detach();
        }
    }

    public static void remove(JSLintAnnotation annotation) {
        annotations.remove(annotation);
    }
    
    /**
     * Define the Tidy Annotation type
     *
     * @return Constant String "TidyErrorAnnotation"
     */
    public String getAnnotationType() {
        return annoType[severity];
    }

    /** Provide the Tidy error message as a description.
     * @return Annotation Reason*/
    public String getShortDescription() {
        return reason + " (" + "Column: " + column + ")";
    }
    /** Create an annotation for a line from match string*/
    public static void createAnnotation(final Line.Part partLine, final String reason, final int lineNumber, final int columnNumber/*, final Matcher matcher*/)
            throws IndexOutOfBoundsException, NumberFormatException {
        
	/*String lineNumberString = matcher.group(1);
        int lineNumber = Integer.parseInt(lineNumberString) - 1;
        String columnNumberString = matcher.group(2);
        int columnNumber = Integer.parseInt(columnNumberString) - 1;
        String severity = matcher.group(3);
        String reason = matcher.group(4);*/
	String severity = "Err";
	
        try {
            /*final Line line = lc.getLineSet().getOriginal(lineNumber-1);
	    Line.Part partLine = null; //lc.getLineSet().getOriginal(lineNumber -1 );
	    partLine = line.createPart(columnNumber-1, 1);*/
	    //final Line.Part w = lc.getLineSet().
	    
            final JSLintAnnotation annotation = JSLintAnnotation.create(severity, columnNumber, reason);

            annotation.attach(partLine);
            partLine.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent ev) {
                    String type = ev.getPropertyName();
                    if (type == null || type.equals(Annotatable.PROP_TEXT)) {
                        // User edited the line, assume error should be cleared.
                        partLine.removePropertyChangeListener(this);
                        annotation.detach();
                        JSLintAnnotation.remove(annotation);
                    }
                }
            });
        } catch (IndexOutOfBoundsException e) {
            // might happen, if state of file is not saved. ignore
        }

    }
    
}
