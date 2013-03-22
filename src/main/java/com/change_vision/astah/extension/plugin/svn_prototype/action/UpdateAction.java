package com.change_vision.astah.extension.plugin.svn_prototype.action;

import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNUpdate;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class UpdateAction implements IPluginActionDelegate {

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        try {
            new SVNUpdate().execute();
        } catch (SVNPluginException e) {
            e.printStackTrace();
            new MessageDialog().showMessage(e.getMessage());
        } catch (SVNNotConfigurationException e) {
            e.printStackTrace();
            new MessageDialog().showMessage(e.getMessage());
        }
        return null;
    }
}
