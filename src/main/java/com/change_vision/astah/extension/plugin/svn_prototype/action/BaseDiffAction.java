package com.change_vision.astah.extension.plugin.svn_prototype.action;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNBaseDiffExecutor;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class BaseDiffAction implements IPluginActionDelegate {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseDiffAction.class);

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        try {
            new SVNBaseDiffExecutor(ProjectAccessorFactory.getProjectAccessor(), new SVNKitUtils()).execute();
        } catch (ClassNotFoundException e) {
            logger.error("Error has occurred.", e);
            throw new UnExpectedException();
        } catch (SVNPluginException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showMessage(e.getMessage());
        } catch (SVNNotConfigurationException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showMessage(e.getMessage());
        } catch (ProjectNotFoundException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showKeyMessage("err_message.common_not_open_project");
        } catch (SVNException e) {
            logger.error("Error has occurred.", e);
            if (!(new SVNUtils()).isLoginError(e)){
                new MessageDialog().showMessage(Messages.getMessage("err_message.common_svn_error"));
            }
        } catch (FileNotFoundException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showMessage(Messages.getMessage("err_message.common_io_error"));
        }
        return null;
    }
}
