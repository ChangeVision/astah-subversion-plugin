package com.change_vision.astah.extension.plugin.svn_prototype.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNUpdate;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class UpdateAction implements IPluginActionDelegate {

    private static final Logger logger = LoggerFactory.getLogger(UpdateAction.class);

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        try {
            new SVNUpdate().execute();
        } catch (SVNPluginException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showMessage(e.getMessage());
        } catch (SVNNotConfigurationException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showMessage(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("Error has occurred.", e);
            throw new UnExpectedException();
        } catch (ProjectNotFoundException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showKeyMessage("err_message.common_not_open_project");
        } catch (LicenseNotFoundException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showKeyMessage("err_message.common_license_not_found");
        } catch (NonCompatibleException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showKeyMessage("err_message.common_non_compatible");
        } catch (ProjectLockedException e) {
            logger.error("Error has occurred.", e);
            new MessageDialog().showKeyMessage("err_message.common_lock_project");
        }
        return null;
    }
}
