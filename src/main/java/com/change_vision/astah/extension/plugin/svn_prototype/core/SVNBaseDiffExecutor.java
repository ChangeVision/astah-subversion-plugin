package com.change_vision.astah.extension.plugin.svn_prototype.core;

import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNProgressDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.ui.swing.SVNDiffSwingWorker;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;

public class SVNBaseDiffExecutor {

    private ProjectAccessor projectAccessor;
    private MessageDialog messageDialog;
    private SVNUtils utils;

    public SVNBaseDiffExecutor(ProjectAccessor projectAccessor) {
        super();
        this.projectAccessor = projectAccessor;
        this.messageDialog = new MessageDialog();
        this.utils = new SVNUtils();
    }

    public void setMessageDialog(MessageDialog messageDialog) {
        this.messageDialog = messageDialog;
    }

    public Object execute() throws UnExpectedException, SVNPluginException, SVNNotConfigurationException {
        // 開いているプロジェクトのパスを取得
        String pjPath = getProjectPath();

        String fileName = getFileName(pjPath);
        String filePath = getFilePath(pjPath);

        // SVNKitの初期化
        initializeSVN();

        SVNWCClient client = getSVNWCClient( fileName);

        // リポジトリからベースリビジョンのファイルを取得
        String workFile = getBaseFile(filePath, fileName, client);

        // 差分表示
        displayDiff(pjPath, workFile);

        return null;
    }

    public String getProjectPath() throws SVNPluginException {
        // 開いているプロジェクトのパスを取得
        String pjPath = null;
        try {
            pjPath = projectAccessor.getProjectPath();

            if (SVNUtils.chkNotSaveProject(pjPath)) {
                throw new SVNPluginException();
            }
        } catch (ProjectNotFoundException pnfe) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_not_open_project"), pnfe);
//            JOptionPane.showMessageDialog(null,
//                    Messages.getMessage("err_message.common_not_open_project"));
//            return null;
        }
        return pjPath;
    }

    public String getFileName(String pjPath) {
        int markIndex = pjPath.lastIndexOf(File.separator);
        String fileName = pjPath.substring(markIndex + 1);
        return fileName;
    }

    public String getFilePath(String pjPath) {
        int markIndex = pjPath.lastIndexOf(File.separator);
        String filePath = pjPath.substring(0, markIndex + 1);
        return filePath;
    }

    public SVNUtils initializeSVN() throws SVNNotConfigurationException, SVNPluginException {

        // SVNKitの初期化
        FSRepositoryFactory.setup();

        // 保存してあるSubversionログイン情報取得
        if (!utils.getPreferencesInfo(Messages.getMessage("info_message.diff_cancel"))) {
            throw new SVNPluginException();
        }
        return utils;
    }

    public SVNWCClient getSVNWCClient(String fileName) throws SVNPluginException {
        SVNWCClient client = null;

        // 引数チェック
        if (fileName == null || fileName.length() == 0) {
            return null;
        }

        try {
            // リポジトリにファイルがあるかをチェック
            if (utils.getSVNDirEntry(fileName) == null) {
                messageDialog.showMessage("err_message.common_not_commit");
//                JOptionPane.showMessageDialog(null,
//                        Messages.getMessage("err_message.common_not_commit"));
                return null;
            }

            SVNClientManager scm;
            if (utils.getLoginKind() == SVNUtils.LOGIN_KIND_SSH
                    && SVNUtils.chkNullString(utils.getPassword())) {
                scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                        utils.getAuthManager());
            } else {
                scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                        utils.getUser(), utils.getPassword());
            }
            client = scm.getWCClient();
        } catch (HeadlessException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
//            JOptionPane
//                    .showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
//            return null;
        } catch (SVNException e) {
            if (!SVNUtils.chkLoginError(e)) {
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
//                JOptionPane.showMessageDialog(null,
//                        Messages.getMessage("err_message.common_svn_error"));
            }
            return null;
        }
        return client;
    }

    public String getBaseFile(String filePath, String fileName, SVNWCClient client) throws SVNPluginException {
        // 引数チェック
        if (client == null || filePath == null || fileName == null || filePath.length() == 0
                || fileName.length() == 0) {
            return null;
        }

        // リポジトリからベースリビジョンのファイルを取得
        String workFile = filePath + "base." + fileName;
        FileOutputStream baseFile;
        try {
            baseFile = new FileOutputStream(workFile);
            client.doGetFileContents(new File(filePath + fileName), SVNRevision.UNDEFINED, null,
                    false, baseFile);
            baseFile.close();
        } catch (FileNotFoundException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), e);
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
//            return null;
        } catch (SVNException e) {
            if (!SVNUtils.chkLoginError(e)) {
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
//                JOptionPane.showMessageDialog(null,
//                        Messages.getMessage("err_message.common_svn_error"));
            }
            return null;
        } catch (IOException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), e);
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
//            return null;
        }
        return workFile;
    }

    public void displayDiff(String pjPath, String workFile) throws SVNPluginException {
        // 引数チェック
        if (pjPath == null || workFile == null || pjPath.length() == 0 || workFile.length() == 0) {
//            JOptionPane.showMessageDialog(null,
//                    Messages.getMessage("err_message.common_file_not_found"));
            throw new SVNPluginException(Messages.getMessage("err_message.common_file_not_found"));
        }

        // プログレスバー設定
        final SVNProgressDialog diffDialog = new SVNProgressDialog(
                (SVNUtils.getViewManager()).getMainFrame(),
                Messages.getMessage("progress_diff_title"),
                Messages.getMessage("progress_diff_message"));

        // 差分表示
        SVNDiffSwingWorker diffTask = new SVNDiffSwingWorker(pjPath, workFile, true);
        diffTask.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName()) && (Integer) evt.getNewValue() == 100) {
                    diffDialog.dispose();
                }
            }
        });

        diffTask.execute();
        diffDialog.setVisible(true);
    }
}
