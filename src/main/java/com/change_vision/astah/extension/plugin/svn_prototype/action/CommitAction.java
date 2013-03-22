package com.change_vision.astah.extension.plugin.svn_prototype.action;

import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNCommit;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNCommitCommentDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class CommitAction implements IPluginActionDelegate {
    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        try {
            new SVNCommit(new SVNCommitCommentDialog((SVNUtils.getViewManager()).getMainFrame())).execute();
        } catch (SVNPluginException e) {
            e.printStackTrace();
            new MessageDialog().showMessage(e.getMessage());
//            JOptionPane.showMessageDialog(null,e.getMessage());
        } catch (SVNNotConfigurationException e) {
            e.printStackTrace();
            new MessageDialog().showMessage(e.getMessage());
        }
        return null;
    }
}
