package com.change_vision.astah.extension.plugin.svn_prototype.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.SVNDiffTask;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNProgressDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class LatestDiffAction implements IPluginActionDelegate {

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
//        try {
            // 開いているプロジェクトのパスを取得
            String pjPath = getProjectPath();
//            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
//            String pjPath = projectAccessor.getProjectPath();
//
//            if (SVNUtils.chkNotSaveProject(pjPath)) {
//                return null;
//            }

            String fileName = getFileName(pjPath);
            String filePath = getFilePath(pjPath);
//            int markIndex = pjPath.lastIndexOf(File.separator);
//            String fileName = pjPath.substring(markIndex + 1);
//            String filePath = pjPath.substring(0, markIndex + 1);

            SVNUtils utils = initializeSVN();
//            // SVNKitの初期化
//            FSRepositoryFactory.setup( );
//
//            // 保存してあるSubversionログイン情報取得
//            SVNUtils utils = new SVNUtils();
//            if (!utils.getPreferencesInfo(Messages.getMessage("info_message.diff_cancel"))){
//                return null;
//            }

            SVNWCClient client = getSVNWCClient(utils);
//            SVNClientManager scm;
//            if (utils.loginKind == SVNUtils.LOGIN_KIND_SSH && SVNUtils.chkNullString(utils.password)){
//                scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.getAuthManager());
//            } else {
//                scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.user, utils.password);
//            }
//            SVNWCClient client = scm.getWCClient();

            // リポジトリから最新リビジョンのファイルを取得
            String workFile = getLatestFile(filePath, fileName, utils, client);
//            // 対象プロジェクトに対する最新リビジョンを取得
//            SVNDirEntry entry = (utils.repos).info(fileName, -1);
//            if (entry == null) {
//                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_commit"));
//                return null;
//            }
//            long revision = entry.getRevision(); 
//
//            // リポジトリから最新リビジョンのファイルを取得
//            String workFile = filePath + "latest." + fileName;
//            FileOutputStream latestFile = new FileOutputStream(workFile);
//            client.doGetFileContents(new File(pjPath), SVNRevision.COMMITTED, SVNRevision.create(revision), false, latestFile);
//            latestFile.close();

            // 差分表示
            displayDiff(pjPath, workFile);
//            // プログレスバー設定
//            final SVNProgressDialog diffDialog = new SVNProgressDialog((SVNUtils.getViewManager()).getMainFrame(),
//                                                                       Messages.getMessage("progress_diff_title"),
//                                                                       Messages.getMessage("progress_diff_message"));
//
//            // 差分表示
//            SVNDiffTask diffTask = new SVNDiffTask(pjPath, workFile, true);
//            diffTask.addPropertyChangeListener(new PropertyChangeListener() {
//                @Override
//                public void propertyChange(PropertyChangeEvent evt) {
//                    if("progress".equals(evt.getPropertyName()) && (Integer)evt.getNewValue() == 100){
//                        diffDialog.dispose();
//                    }
//                }
//            });
//
//            diffTask.execute();
//            diffDialog.setVisible(true);
//        } catch (SVNException se){
//            if (!SVNUtils.chkLoginError(se)){
//                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
//            }
//        } catch (ClassNotFoundException cnfe){
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_class_not_found"));
//        } catch (ProjectNotFoundException pnfe){
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
//        } catch (IOException ie){
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
//        }

        return null;
    }

    public String getProjectPath() {
        // 開いているプロジェクトのパスを取得
        String pjPath = null;
        ProjectAccessor projectAccessor;
        try {
            projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            pjPath = projectAccessor.getProjectPath();

            if (SVNUtils.chkNotSaveProject(pjPath)) {
                return null;
            }
        } catch (ProjectNotFoundException e) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
            return null;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_class_not_found"));
            return null;
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

    public SVNUtils initializeSVN() {

        // SVNKitの初期化
        FSRepositoryFactory.setup( );

        // 保存してあるSubversionログイン情報取得
        SVNUtils utils = new SVNUtils();
        if (!utils.getPreferencesInfo(Messages.getMessage("info_message.diff_cancel"))){
            return null;
        }
        return utils;
    }

    public SVNWCClient getSVNWCClient(SVNUtils utils) {
        SVNClientManager scm;

        // 引数チェック
        if (utils == null) {
            return null;
        }

        if (utils.loginKind == SVNUtils.LOGIN_KIND_SSH && SVNUtils.chkNullString(utils.password)){
            scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.getAuthManager());
        } else {
            scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.user, utils.password);
        }
        SVNWCClient client = scm.getWCClient();
        return client;
    }

    public String getLatestFile(String filePath, String fileName, SVNUtils utils, SVNWCClient client) {
        // 引数チェック
        if (client == null || utils == null
         || filePath == null || fileName == null
         || filePath.length() == 0 || fileName.length() == 0) {
            return null;
        }

        // 対象プロジェクトに対する最新リビジョンを取得
        String workFile = null;
        SVNDirEntry entry;
        try {
            entry = (utils.repos).info(fileName, -1);
            if (entry == null) {
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_commit"));
                return null;
            }
            long revision = entry.getRevision(); 

            // リポジトリから最新リビジョンのファイルを取得
            workFile = filePath + "latest." + fileName;
            FileOutputStream latestFile = null;
            latestFile = new FileOutputStream(workFile);
            client.doGetFileContents(new File(filePath + fileName), SVNRevision.COMMITTED, SVNRevision.create(revision), false, latestFile);
            latestFile.close();
        } catch (SVNException e) {
            if (!SVNUtils.chkLoginError(e)){
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
            }
            return null;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
            return null;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
            return null;
        }
        return workFile;
    }

    public void displayDiff(String pjPath, String workFile) {
        // 引数チェック
        if (pjPath == null || workFile == null
         || pjPath.length() == 0 || workFile.length() == 0) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_file_not_found"));
            throw new IllegalArgumentException();
        }

        // プログレスバー設定
        final SVNProgressDialog diffDialog = new SVNProgressDialog((SVNUtils.getViewManager()).getMainFrame(),
                                                                   Messages.getMessage("progress_diff_title"),
                                                                   Messages.getMessage("progress_diff_message"));

        // 差分表示
        SVNDiffTask diffTask = new SVNDiffTask(pjPath, workFile, true);
        diffTask.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if("progress".equals(evt.getPropertyName()) && (Integer)evt.getNewValue() == 100){
                    diffDialog.dispose();
                }
            }
        });

        diffTask.execute();
        diffDialog.setVisible(true);
    }
}
