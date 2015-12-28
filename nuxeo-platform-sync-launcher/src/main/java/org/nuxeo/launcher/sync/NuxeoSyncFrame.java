/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Sun Seng David TAN <stan@nuxeo.com>, jcarsique
 */
package org.nuxeo.launcher.sync;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.launcher.gui.NuxeoFrame;
import org.nuxeo.launcher.gui.NuxeoLauncherGUI;

/**
 * A NuxeoFrame with a custom tab: Synchronization
 * 
 * @author Sun Seng David TAN <stan@nuxeo.com>
 */
public class NuxeoSyncFrame extends NuxeoFrame {

    @SuppressWarnings("hiding")
    public static final Log log = LogFactory.getLog(NuxeoSyncFrame.class);

    private static final long serialVersionUID = 1L;

    protected JTextField syncServerIpTextField;

    protected JTextField syncPortTextField;

    protected JTextField syncLoginTextField;

    protected JPasswordField syncPasswordField;

    protected Action synchronizeAction;

    protected JButton syncButton;

    protected JPanel syncPanel;

    public NuxeoSyncFrame(NuxeoLauncherGUI controller) throws HeadlessException {
        super(controller);
    }

    @Override
    protected JComponent buildTabbedPanel() {
        // do not call super, hiding other tabbed panel (log and summary tab)
        tabbedPanel = new JTabbedPane(SwingConstants.TOP);
        tabbedPanel.addTab("Synchronization", buildSyncPanel());
        return tabbedPanel;
    }

    @Override
    public void updateSummary() {
        // summary not displayed
    }

    @Override
    protected JComponent buildHeader() {
        // override the default image with the new desktop client logo
        ImagePanel headerLogo = new ImagePanel(getImageIcon("img/nuxeo_desktop_client_logo.png"),
                getImageIcon("img/nuxeo_control_panel_bg.png"));
        headerLogo.setLayout(new GridBagLayout());
        // Main button (start/stop) (added to header)
        GridBagConstraints headerConstraints = new GridBagConstraints();
        headerConstraints.gridx = 0;
        headerLogo.add(buildMainButton(), headerConstraints);
        headerLogo.add(buildLaunchBrowserButton(), headerConstraints);
        return headerLogo;
    }

    protected Component buildSyncPanel() {
        syncPanel = new JPanel();
        syncPanel.setBackground(new Color(55, 55, 55));
        syncPanel.setForeground(Color.WHITE);
        syncPanel.setLayout(new GridBagLayout());

        JLabel l = new JLabel("Server IP", SwingConstants.TRAILING);
        l.setForeground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 0);
        syncPanel.add(l, c);
        syncServerIpTextField = new JTextField(10);
        l.setLabelFor(syncServerIpTextField);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;

        syncPanel.add(syncServerIpTextField, c);

        l = new JLabel("Port", SwingConstants.TRAILING);
        l.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;

        syncPanel.add(l, c);
        syncPortTextField = new JTextField(10);
        l.setLabelFor(syncPortTextField);
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
        syncPanel.add(syncPortTextField, c);

        l = new JLabel("Login", SwingConstants.TRAILING);
        l.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;

        syncPanel.add(l, c);
        syncLoginTextField = new JTextField(10);
        l.setLabelFor(syncLoginTextField);
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.WEST;

        syncPanel.add(syncLoginTextField, c);

        l = new JLabel("Password", SwingConstants.TRAILING);
        l.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.EAST;

        syncPanel.add(l, c);
        syncPasswordField = new JPasswordField(10);
        l.setLabelFor(syncPasswordField);
        c.gridx = 1;
        c.gridy = 4;
        c.anchor = GridBagConstraints.WEST;
        syncPanel.add(syncPasswordField, c);

        syncButton = new JButton();
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 0, 0);
        c.anchor = GridBagConstraints.CENTER;
        // set the action to the button
        synchronizeAction = createSyncAction();
        syncButton.setAction(synchronizeAction);
        syncButton.setText("Synchronize");

        updateSyncButton();

        syncPanel.add(syncButton, c);

        errorMessageLabel = new JLabel("", SwingConstants.TRAILING);
        errorMessageLabel.setForeground(Color.RED);
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        c.anchor = GridBagConstraints.CENTER;
        syncPanel.add(errorMessageLabel, c);
        return syncPanel;
    }

    protected Action createSyncAction() {
        return new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String serverip = syncServerIpTextField.getText();
                String port = syncPortTextField.getText();
                String login = syncLoginTextField.getText();
                char[] password = syncPasswordField.getPassword();

                HttpAutomationClient client = new HttpAutomationClient("http://localhost:8080/nuxeo/site/automation");
                // anonymous locally
                Session session;
                try {
                    session = client.getSession();
                    OperationRequest request;
                    request = session.newRequest("Synchronization.ClientSync");
                    request = request.set("host", serverip);
                    request = request.set("port", port);
                    request = request.set("username", login);
                    request = request.set("password", new String(password));
                    request.execute();
                    getErrorMessageLabel().setText("");
                } catch (Exception e) {
                    errorMessageLabel.setText("<html>" + " an error occurred while synchronizing" + "<br> ["
                            + e.getMessage() + "]" + "</html>");
                    syncPanel.updateUI();
                } finally {
                    client.shutdown();
                }
            }
        };
    }

    protected Action createLaunchBrowserAction() {
        return new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    Desktop.getDesktop().browse(java.net.URI.create(getController().getLauncher().getURL()));
                } catch (Exception e) {
                    setError("an error occurred while launching browser", e);
                }
            }
        };
    }

    protected void updateSyncButton() {
        if (syncButton != null) {
            syncButton.setEnabled(getController().getLauncher().isStarted());
        }
    }
}
