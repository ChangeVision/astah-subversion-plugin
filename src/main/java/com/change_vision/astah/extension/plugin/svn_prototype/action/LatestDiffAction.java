package com.change_vision.astah.extension.plugin.svn_prototype.action;

import java.io.FileNotFoundException;

import org.tmatesoft.svn.core.SVNException;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNLatestDiffExecutor;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotCommitException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class LatestDiffAction implements IPluginActionDelegate {

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        try {
            new SVNLatestDiffExecutor(ProjectAccessorFactory.getProjectAccessor(), new SVNKitUtils()).execute();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnExpectedException();
        } catch (SVNPluginException e) {
            e.printStackTrace();
            new MessageDialog().showMessage(e.getMessage());
        } catch (SVNNotConfigurationException e) {
            e.printStackTrace();
            new MessageDialog().showMessage(e.getMessage());
        } catch (SVNNotCommitException e) {
            e.printStackTrace();
            new MessageDialog().showMessage(e.getMessage());
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            new MessageDialog().showKeyMessage("err_message.common_not_open_project");
        } catch (SVNException e) {
            e.printStackTrace();
            if (!(new SVNUtils()).isLoginError(e)){
                new MessageDialog().showMessage(Messages.getMessage("err_message.common_svn_error"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new MessageDialog().showMessage(Messages.getMessage("err_message.common_io_error"));
        }
        return null;
    }
}
