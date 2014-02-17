package com.change_vision.astah.extension.plugin.svn_prototype.action;


import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNConfigurationDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;

public class SVNConfigurationAction implements IPluginActionDelegate {

    private static final Logger logger = LoggerFactory.getLogger(SVNConfigurationAction.class);

    public Object run(IWindow window) throws UnExpectedException {
        try {
            SVNConfigurationDialog dialog = new SVNConfigurationDialog(((new SVNUtils()).getViewManager()).getMainFrame());
            dialog.setSVNKitUtils(new SVNKitUtils());
            dialog.getDialog();
            if (!dialog.getErrFlg()){
                dialog.setResizable(false);
                dialog.setVisible(true);
            }
        } catch (UnsupportedEncodingException uee) {
            logger.error("Error has occurred.", uee);
            new MessageDialog().showKeyMessage("err_message.common_incorrect_password");
        } catch(ProjectNotFoundException pe) {
            logger.error("Error has occurred.", pe);
            new MessageDialog().showKeyMessage("err_message.common_not_open_project");
        } catch (SVNException se){
            logger.error("Error has occurred.", se);
            if ((se.getMessage()).matches("^svn: E155007:.*")){
                // Subversion管理対象外
                new MessageDialog().showKeyMessage("err_message.config_not_entered_repository");
            } else {
                new MessageDialog().showKeyMessage("err_message.common_svn_error");
            }
        } catch (ClassNotFoundException cnfe){
            logger.error("Error has occurred.", cnfe);
            new MessageDialog().showKeyMessage("err_message.common_class_not_found");
        }
        return null;
    }
}
