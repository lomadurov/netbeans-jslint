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

@ActionID(category = "Build",
id = "org.lomatek.jslint.JSLintSample")
@ActionRegistration(displayName = "#CTL_JSLintSample")
@ActionReferences({
    @ActionReference(path = "Editors/text/javascript/Popup", position = 300)
})
@Messages("CTL_JSLintSample=JSLintSample")
public final class JSLintSample implements ActionListener {

    private final DataObject context;

    public JSLintSample(DataObject context) {
	this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
	// TODO use context
    }
}
