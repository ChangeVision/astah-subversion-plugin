package com.change_vision.astah.extension.plugin.svn_prototype.action;

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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnExpectedException();
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            new MessageDialog().showKeyMessage("err_message.common_not_open_project");
        } catch (LicenseNotFoundException e) {
            e.printStackTrace();
            new MessageDialog().showKeyMessage("err_message.common_license_not_found");
        } catch (NonCompatibleException e) {
            e.printStackTrace();
            new MessageDialog().showKeyMessage("err_message.common_non_compatible");
        } catch (ProjectLockedException e) {
            e.printStackTrace();
            new MessageDialog().showKeyMessage("err_message.common_lock_project");
        }
        return null;
    }
}
