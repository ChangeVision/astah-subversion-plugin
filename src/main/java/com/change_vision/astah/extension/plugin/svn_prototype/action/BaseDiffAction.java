package com.change_vision.astah.extension.plugin.svn_prototype.action;

import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNBaseDiffExecutor;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class BaseDiffAction implements IPluginActionDelegate {

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        try {
            new SVNBaseDiffExecutor(ProjectAccessorFactory.getProjectAccessor()).execute();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnExpectedException();
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
