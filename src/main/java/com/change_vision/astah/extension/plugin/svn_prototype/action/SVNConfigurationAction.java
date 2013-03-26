package com.change_vision.astah.extension.plugin.svn_prototype.action;


import java.io.UnsupportedEncodingException;

import org.tmatesoft.svn.core.SVNException;

import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNConfigurationDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;

public class SVNConfigurationAction implements IPluginActionDelegate {

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
            uee.printStackTrace();
            new MessageDialog().showKeyMessage("err_message.common_incorrect_password");
        } catch(ProjectNotFoundException pe) {
            pe.printStackTrace();
            new MessageDialog().showKeyMessage("err_message.common_not_open_project");
        } catch (SVNException se){
            se.printStackTrace();
            if ((se.getMessage()).matches("^svn: E155007:.*")){
                // Subversion管理対象外
                new MessageDialog().showKeyMessage("err_message.config_not_entered_repository");
            } else {
                new MessageDialog().showKeyMessage("err_message.common_svn_error");
            }
        } catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
            new MessageDialog().showKeyMessage("err_message.common_class_not_found");
        }
        return null;
    }
}
