/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lomatek.jslint;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.openide.loaders.DataObject;

import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

import org.openide.util.RequestProcessor;

@ActionID(category = "Build",
id = "org.lomatek.jslint.JSLintSample")
@ActionRegistration(displayName = "#CTL_JSLintSample")
@ActionReferences({
    @ActionReference(path = "Editors/text/javascript/Popup", position = 300)
})
@Messages("CTL_JSLintSample=JSLintSample")
public final class JSLintSample implements ActionListener {
    
    static RequestProcessor processor = null;

    private final DataObject context;

    public JSLintSample(DataObject context) {
	this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
	// TODO use context
	if (processor == null) {
            processor = new RequestProcessor("TidyErrorCheck", 1, true);
        }
        processor.post(new JSLintRunnableB(context, "-e"));
    }
}
