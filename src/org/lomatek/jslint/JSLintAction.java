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
/**
 *
 * @author Stanislav Lomadurov <lord.rojer@gmail.com>
 */
package org.lomatek.jslint;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.openide.loaders.DataObject;

import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;

import org.openide.util.RequestProcessor;

@ActionID(category = "Build",
id = "org.lomatek.jslint.JSLintSample")
@ActionRegistration(displayName = "#CTL_JSLintAction")
@ActionReferences({
    @ActionReference(path = "Editors/text/javascript/Popup", position = 400, separatorAfter = 450)
})
//@Messages("CTL_JSLintSample=JSLintSample")
public final class JSLintAction implements ActionListener {
    
    static RequestProcessor processor = null;

    private final DataObject context;

    public JSLintAction(DataObject context) {
	this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
	// Start JSLintRunnable
	if (processor == null) {
            processor = new RequestProcessor("JSLintErrorCheck", 1, true);
        }
        processor.post(new JSLintRunnable(context, "-e"));
    }
}
