package com.change_vision.astah.extension.plugin.svn_prototype.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNProgressDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNSelectMergeDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNConflictException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.ui.swing.SVNDiffSwingWorker;
import com.change_vision.astah.extension.plugin.svn_prototype.ui.swing.SVNMergeSwingWorker;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNConflictResolverHandler;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNPreferences;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;

public class SVNUpdate {

    private static MessageDialog messageDialog;
    private SVNUtils utils;

    public SVNUpdate() {
        SVNUpdate.messageDialog = new MessageDialog();
        utils = new SVNUtils();
    }

    public void setUtils(SVNUtils utils) {
        this.utils = utils;
    }

    public static void setMessageDialog(MessageDialog messageDialog) {
        SVNUpdate.messageDialog = messageDialog;
    }

    public Object execute() throws UnExpectedException, SVNPluginException,
            SVNNotConfigurationException {
        try {
            // 保存してあるSubversionログイン情報取得
            utils.getPreferencesInfo(Messages.getMessage("info_message.update_cancel"));

            // 開いているプロジェクトのパスを取得
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            String pjPath = projectAccessor.getProjectPath();

            if (SVNUtils.chkNotSaveProject(pjPath)) {
                return null;
            }

            if (SVNUtils.chkEditingProject()) {
                messageDialog.showKeyMessage("confirm_save_dialog.message");
                // JOptionPane.showMessageDialog(null,
                // Messages.getMessage("confirm_save_dialog.message"));
                return null;
            }

            // SVNKitの初期化
            initializeSVNKit();

            projectAccessor.close();
            SVNClientManager scm = getSVNClientManager();
            // if (SVNUtils.chkNullString(utils.getPassword())){
            // scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
            // utils.getAuthManager());
            // } else {
            // scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
            // utils.getUser(), utils.getPassword());
            // }
            if (svnUpdateMerge(pjPath, scm)) {
                // プロジェクトを開き直す
                projectAccessor.open(pjPath);

                messageDialog.showKeyMessage("info_message.update_complete");
                // JOptionPane.showMessageDialog(null,
                // Messages.getMessage("info_message.update_complete"));
            }
        } catch (ProjectLockedException pe) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_lock_project"), pe);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_lock_project"));
        } catch (ClassNotFoundException cfe) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_class_not_found"),
                    cfe);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_class_not_found"));
        } catch (ProjectNotFoundException pnfe) {
            throw new SVNPluginException(
                    Messages.getMessage("err_message.common_not_open_project"), pnfe);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_not_open_project"));
        } catch (IOException ie) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), ie);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_io_error"));
        } catch (NonCompatibleException nce) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_non_compatible"),
                    nce);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_non_compatible"));
        } catch (LicenseNotFoundException lnfe) {
            throw new SVNPluginException(
                    Messages.getMessage("err_message.common_license_not_found"), lnfe);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_license_not_found"));
        }

        return null;
    }

    public void initializeSVNKit() {
        // SVNKitの初期化
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    public boolean doUpdate(String path) throws SVNException, SVNConflictException {
        return doUpdate(utils.getLatestRevision(), path);
    }

    public boolean doUpdate(Long revision, String path) throws SVNException,
            SVNConflictException {
//        SVNClientManager scm = getSVNClientManager();
//        SVNUpdateClient client = scm.getUpdateClient();
//
        SVNConflictResolverHandler handler = new SVNConflictResolverHandler(null, path);
//        DefaultSVNOptions options = (DefaultSVNOptions) client.getOptions();
//        options.setConflictHandler(handler);
        SVNUpdateClient client = getUpdateClient(handler);

        try {
            // Update処理
            client.doUpdate(new File(path), SVNRevision.create(revision), SVNDepth.INFINITY, true, true);
            return true;
        } catch (SVNException e) {
            if (handler.getMergeFlg()) {
                throw new SVNConflictException();
            }
            throw e;
        }
    }

    public SVNUpdateClient getUpdateClient(SVNConflictResolverHandler handler) {
        SVNClientManager scm = getSVNClientManager();
        SVNUpdateClient client = scm.getUpdateClient();

        DefaultSVNOptions options = (DefaultSVNOptions) client.getOptions();
//        SVNConflictResolverHandler handler = new SVNConflictResolverHandler(null, path);
        options.setConflictHandler(handler);

        return client;
    }

    public boolean svnUpdateMerge(String pjPath, SVNClientManager scm) throws SVNPluginException {
        String errFile = "";
        ProjectAccessor projectAccessor = null;
        try {
            JFrame parent = (SVNUtils.getViewManager()).getMainFrame();
            projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            // 開いているプロジェクトのパス、ファイル名を取得
            int markIndex = pjPath.lastIndexOf(File.separator);
            String fileName = pjPath.substring(markIndex + 1);
            String filePath = pjPath.substring(0, markIndex + 1);
            String workFile = filePath + "work." + fileName;

            // 対象プロジェクトに対する最新リビジョンを取得
            if (utils.getSVNDirEntry(fileName) == null) {
                // プロジェクトを開き直す
                projectAccessor.open(pjPath);
                messageDialog.showKeyMessage("err_message.common_not_commit");
                // JOptionPane.showMessageDialog(null,
                // Messages.getMessage("err_message.common_not_commit"));
                return false;
            }

            long revision = utils.getLatestRevision();

            // プロジェクトを開いていれば閉じる
            if (projectAccessor.hasProject()) {
                projectAccessor.close();
            }

            // プロジェクトのコピーを作成
            errFile = pjPath;
            FileInputStream inputStream = new FileInputStream(pjPath);
            errFile = workFile;
            FileOutputStream outputStream = new FileOutputStream(workFile);
            errFile = "";
            FileChannel srcChannel = inputStream.getChannel();
            FileChannel destChannel = outputStream.getChannel();
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
            inputStream.close();
            outputStream.close();

            // 更新処理実施
            try {
                doUpdate(pjPath);
            } catch (SVNConflictException e) {
                // 競合が発生した場合の処理

                // 競合時に発生した「～.asta.r…」ファイルのうち、最新リビジョンの方に拡張子「.asta」をつける
                String newFileName = pjPath + ".r" + revision + ".asta";
                fileRenameAction(pjPath + ".r" + revision, newFileName);

                final SVNProgressDialog diffDialog = new SVNProgressDialog(parent,
                        Messages.getMessage("progress_diff_title"),
                        Messages.getMessage("progress_diff_message"));

                SVNDiffSwingWorker diffTask = new SVNDiffSwingWorker(workFile, newFileName);
                diffTask.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())
                                && (Integer) evt.getNewValue() == 100) {
                            diffDialog.dispose();
                        }
                    }
                });

                diffTask.execute();
                diffDialog.setVisible(true);

                // マージを行うか選択してもらう
                SVNSelectMergeDialog dialog = new SVNSelectMergeDialog(parent);
                dialog.setResizable(false);
                dialog.setVisible(true);

                // Preferences のインスタンスを取得
                SVNPreferences.getInstace(dialog.getClass());
                Preferences preferences = SVNPreferences.getInstance();

                // マージ処理用のオブジェクト作成
                final SVNProgressDialog mergeDialog = new SVNProgressDialog(parent,
                        Messages.getMessage("progress_merge_title"),
                        Messages.getMessage("progress_merge_message"));

                SVNMergeSwingWorker mergeTask = new SVNMergeSwingWorker(pjPath, scm.getWCClient(),
                        projectAccessor);
                mergeTask.setLatestRevision(revision);
                mergeTask.setSVNInfo(utils);

                int selected = 0;
                String strMergeKind = "";

                strMergeKind = preferences.get(SVNPreferences.KEY_MERGE_KIND, null);
                if (strMergeKind != null) {
                    if (dialog.getSelectFlg()) {
                        selected = Integer.valueOf(strMergeKind);
                    } else {
                        selected = SVNSelectMergeDialog.NO_MERGE;
                    }

                    mergeTask.setSelected(selected);
                }

                if (selected == SVNSelectMergeDialog.NO_MERGE) {
                    mergeDialog.setTitle(Messages.getMessage("progress_merge_cancel_title"));
                    mergeDialog.setMessage(Messages.getMessage("progress_merge_cancel_message"));
                } else if (selected == SVNSelectMergeDialog.GET_LATEST_PROJECT) {
                    mergeDialog.setTitle(Messages.getMessage("progress_merge_update_title"));
                    mergeDialog.setMessage(Messages.getMessage("progress_merge_update_message"));
                } else if (selected == SVNSelectMergeDialog.GET_LATEST_REVISION) {
                    mergeDialog.setTitle(Messages.getMessage("progress_merge_revision_title"));
                    mergeDialog.setMessage(Messages.getMessage("progress_merge_revision_message"));
                }

                mergeTask.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())
                                && (Integer) evt.getNewValue() == 100) {
                            mergeDialog.dispose();
                        }
                    }
                });

                mergeTask.execute();
                mergeDialog.setVisible(true);

                if (mergeDialog.interruptFlg) {
                    mergeTask.cancel(false);
                    mergeTask.setSelected(SVNSelectMergeDialog.NO_MERGE);
                    mergeTask.execute();
                    mergeDialog.setTitle(Messages.getMessage("progress_merge_cancel_title"));
                    mergeDialog.setMessage(Messages.getMessage("progress_merge_cancel_message"));
                    mergeDialog.setVisible(true);
                    messageDialog.showKeyMessage("info_message.update_cancel");
                    // JOptionPane.showMessageDialog(null,
                    // Messages.getMessage("info_message.update_cancel"));
                    return false;
                }

                if (mergeTask.getSelected() == SVNSelectMergeDialog.NO_MERGE) {
                    messageDialog.showKeyMessage("info_message.update_cancel");
                    // JOptionPane.showMessageDialog(null,
                    // Messages.getMessage("info_message.update_cancel"));
                    return false;
                }
                // マージを行ったのでtrueを返す
                return true;
            }
            // 競合なしなので、workFileを削除して終了する
            File delFile = new File(workFile);
            delFile.delete();
            return true;
        } catch (ClassNotFoundException cfe) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_class_not_found"),
                    cfe);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_class_not_found"));
            // return false;
        } catch (FileNotFoundException fnfe) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_file_not_found")
                    + errFile, fnfe);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_file_not_found") + errFile);
            // return false;
        } catch (IOException ie) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), ie);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_io_error"));
            // return false;
        } catch (SVNException svne) {
            if (SVNUtils.chkLoginError(svne)) {
                try {
                    // プロジェクトを開き直す
                    if (projectAccessor != null) {
                        projectAccessor.open(pjPath);
                    }
                } catch (ClassNotFoundException cfe) {
                    throw new SVNPluginException(
                            Messages.getMessage("err_message.common_class_not_found"), cfe);
                    // JOptionPane.showMessageDialog(null,
                    // Messages.getMessage("err_message.common_class_not_found"));
                } catch (IOException ie) {
                    throw new SVNPluginException(
                            Messages.getMessage("err_message.common_io_error"), ie);
                    // JOptionPane.showMessageDialog(null,
                    // Messages.getMessage("err_message.common_io_error"));
                } catch (ProjectLockedException ple) {
                    throw new SVNPluginException(
                            Messages.getMessage("err_message.common_not_open_project"), ple);
                    // JOptionPane.showMessageDialog(null,
                    // Messages.getMessage("err_message.common_not_open_project"));
                } catch (NonCompatibleException nce) {
                    throw new SVNPluginException(
                            Messages.getMessage("err_message.common_non_compatible"), nce);
                    // JOptionPane.showMessageDialog(null,
                    // Messages.getMessage("err_message.common_non_compatible"));
                } catch (ProjectNotFoundException pnfe) {
                    throw new SVNPluginException(
                            Messages.getMessage("err_message.common_not_open_project"), pnfe);
                    // JOptionPane.showMessageDialog(null,
                    // Messages.getMessage("err_message.common_not_open_project"));
                } catch (LicenseNotFoundException lnfe) {
                    throw new SVNPluginException(
                            Messages.getMessage("err_message.common_license_not_found"), lnfe);
                    // JOptionPane.showMessageDialog(null,
                    // Messages.getMessage("err_message.common_license_not_found"));
                }
            } else {
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"),
                        svne);
                // JOptionPane.showMessageDialog(null,
                // Messages.getMessage("err_message.common_svn_error"));
            }
            return false;
        } catch (ProjectLockedException ple) {
            throw new SVNPluginException(
                    Messages.getMessage("err_message.common_not_open_project"), ple);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_not_open_project"));
            // return false;
        } catch (NonCompatibleException nce) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_non_compatible"),
                    nce);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_non_compatible"));
            // return false;
        } catch (ProjectNotFoundException pnfe) {
            throw new SVNPluginException(
                    Messages.getMessage("err_message.common_not_open_project"), pnfe);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_not_open_project"));
            // return false;
        } catch (LicenseNotFoundException lnfe) {
            throw new SVNPluginException(
                    Messages.getMessage("err_message.common_license_not_found"), lnfe);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_license_not_found"));
            // return false;
        }
    }

    public static boolean fileRenameAction(String fromFile, String toFile) {
        File file1 = new File(fromFile);
        File file2 = new File(toFile);
        if (!file1.renameTo(file2)) {
            // ファイル名変更失敗
            return false;
        }
        return true;
    }

    public String getProjectPath() throws SVNPluginException {
        // 開いているプロジェクトのパスを取得
        String pjPath;
        ProjectAccessor projectAccessor;
        try {
            projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            pjPath = projectAccessor.getProjectPath();

            if (SVNUtils.chkNotSaveProject(pjPath)) {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_class_not_found"),
                    e);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_class_not_found"));
            // return null;
        } catch (ProjectNotFoundException e) {
            throw new SVNPluginException(
                    Messages.getMessage("err_message.common_not_open_project"), e);
            // JOptionPane.showMessageDialog(null,
            // Messages.getMessage("err_message.common_not_open_project"));
            // return null;
        }
        return pjPath;
    }

    public SVNClientManager getSVNClientManager() {
        if (SVNUtils.chkNullString(utils.getPassword())) {
            return SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                    utils.getAuthManager());
        } else {
            return SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                    utils.getUser(), utils.getPassword());
        }
    }

}
