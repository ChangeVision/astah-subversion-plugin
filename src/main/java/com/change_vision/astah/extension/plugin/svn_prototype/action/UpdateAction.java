package com.change_vision.astah.extension.plugin.svn_prototype.action;

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
import javax.swing.JOptionPane;

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
import com.change_vision.astah.extension.plugin.svn_prototype.SVNDiffTask;
import com.change_vision.astah.extension.plugin.svn_prototype.SVNMergeTask;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNProgressDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNSelectMergeDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNConflictResolverHandler;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNPreferences;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class UpdateAction implements IPluginActionDelegate {

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        try {
            // 開いているプロジェクトのパスを取得
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            String pjPath = projectAccessor.getProjectPath();

            if (SVNUtils.chkNotSaveProject(pjPath)) {
                return null;
            }

            if (SVNUtils.chkEditingProject()){
                JOptionPane.showMessageDialog(null, Messages.getMessage("confirm_save_dialog.message"));
                return null;
            }

            // SVNKitの初期化
            DAVRepositoryFactory.setup();
            SVNRepositoryFactoryImpl.setup();
            FSRepositoryFactory.setup( );

            // 保存してあるSubversionログイン情報取得
            SVNUtils utils = new SVNUtils();
            if (!utils.getPreferencesInfo(Messages.getMessage("info_message.update_cancel"))){
                return null;
            }

            projectAccessor.close();
            SVNClientManager scm;
            if (SVNUtils.chkNullString(utils.password)){
                scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.getAuthManager());
            } else {
                scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.user, utils.password);
            }
            if (svnUpdateMerge(arg0, utils, pjPath, scm)) {
                // プロジェクトを開き直す
                projectAccessor.open(pjPath);

                JOptionPane.showMessageDialog(null, Messages.getMessage("info_message.update_complete"));
            }
        } catch (ProjectLockedException pe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_lock_project"));
        } catch (ClassNotFoundException cfe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_class_not_found"));
        } catch (ProjectNotFoundException pnfe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
        } catch (IOException ie) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
        } catch (NonCompatibleException nce) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_non_compatible"));
        } catch (LicenseNotFoundException lnfe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_license_not_found"));
        }

        return null;
    }

    public static boolean doUpdate(IWindow window, SVNUtils utils, String path) throws SVNException {
        return doUpdate(window, utils, (utils.repos).getLatestRevision(), path);
    }

    public static boolean doUpdate(IWindow window, SVNUtils utils, Long revision, String path) throws SVNException {
        SVNClientManager scm;
        if (utils.loginKind == SVNUtils.LOGIN_KIND_SSH && SVNUtils.chkNullString(utils.password)){
            scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.getAuthManager());
        } else {
            scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.user, utils.password);
        }

        SVNUpdateClient client = scm.getUpdateClient();

        DefaultSVNOptions options = (DefaultSVNOptions) client.getOptions();
        SVNConflictResolverHandler handler = new SVNConflictResolverHandler(window, path);
        options.setConflictHandler(handler);

        try {
            // Update処理
            client.doUpdate(new File(path), SVNRevision.create(revision), SVNDepth.INFINITY, true, true);
            return true;
        } catch (SVNException e) {
            if (handler.getMergeFlg()) {
                throw e;
            }
            return false;
        }
    }

    private static boolean svnUpdateMerge(IWindow arg0, SVNUtils utils, String pjPath, SVNClientManager scm) {
        String errFile = "";
        ProjectAccessor projectAccessor = null;
        try{
        	JFrame parent = (SVNUtils.getViewManager()).getMainFrame();
            projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            // 開いているプロジェクトのパス、ファイル名を取得
            int markIndex   = pjPath.lastIndexOf(File.separator);
            String fileName = pjPath.substring(markIndex + 1);
            String filePath = pjPath.substring(0, markIndex + 1);
            String workFile = filePath + "work." + fileName;

            // 対象プロジェクトに対する最新リビジョンを取得
            SVNDirEntry entry = (utils.repos).info(fileName, -1);
            if (entry == null) {
                // プロジェクトを開き直す
                projectAccessor.open(pjPath);
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_commit"));
                return false;
            }

            long revision = (utils.repos).getLatestRevision(); 

            // プロジェクトを開いていれば閉じる
            if (projectAccessor.hasProject()) {
                projectAccessor.close();
            }

            // プロジェクトのコピーを作成
            errFile = pjPath;
            FileInputStream  inputStream  = new FileInputStream(pjPath);
            errFile = workFile;
            FileOutputStream outputStream = new FileOutputStream(workFile);
            errFile = "";
            FileChannel srcChannel  = inputStream.getChannel();
            FileChannel destChannel = outputStream.getChannel();
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
            inputStream.close();
            outputStream.close();

            // 競合時に発生するファイル名を取得
            File originFile = new File(pjPath + ".r" + revision);

            // 更新処理実施
            doUpdate(arg0, utils, pjPath);
            if (!originFile.exists()) {
                // 競合なし
                File delFile = new File(workFile);
                delFile.delete();
                return true;
            }

            // 競合時に発生した「～.asta.r…」ファイルのうち、最新リビジョンの方に拡張子「.asta」をつける
            String newFileName = pjPath + ".r" + revision + ".asta";
            fileRenameAction(pjPath + ".r" + revision, newFileName);

            final SVNProgressDialog diffDialog = new SVNProgressDialog(parent,
                                                                       Messages.getMessage("progress_diff_title"),
                                                                       Messages.getMessage("progress_diff_message"));

            SVNDiffTask diffTask = new SVNDiffTask(workFile, newFileName);
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

            SVNMergeTask mergeTask = new SVNMergeTask(pjPath, arg0, scm.getWCClient(), projectAccessor);
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
                    if("progress".equals(evt.getPropertyName()) && (Integer)evt.getNewValue() == 100){
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
                JOptionPane.showMessageDialog(null, Messages.getMessage("info_message.update_cancel"));
                return false;
            }

            if (mergeTask.getSelected() == SVNSelectMergeDialog.NO_MERGE){
                JOptionPane.showMessageDialog(null, Messages.getMessage("info_message.update_cancel"));
                return false;
            }
        } catch (ClassNotFoundException cfe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_class_not_found"));
            return false;
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_file_not_found") + errFile);
            return false;
        } catch (IOException ie) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
            return false;
        } catch (SVNException svne) {
            if (SVNUtils.chkLoginError(svne)){
                try{
                    // プロジェクトを開き直す
                    if (projectAccessor != null) {
                        projectAccessor.open(pjPath);
                    }
                } catch (ClassNotFoundException cfe) {
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_class_not_found"));
                } catch (IOException ie) {
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
                } catch (ProjectLockedException ple) {
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
                } catch (NonCompatibleException nce) {
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_non_compatible"));
                } catch (ProjectNotFoundException pnfe) {
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
                } catch (LicenseNotFoundException lnfe) {
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_license_not_found"));
                }
            } else {
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
            }
            return false;
        } catch (ProjectLockedException ple) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
            return false;
        } catch (NonCompatibleException nce) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_non_compatible"));
            return false;
        } catch (ProjectNotFoundException pnfe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
            return false;
        } catch (LicenseNotFoundException lnfe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_license_not_found"));
            return false;
        }
        // マージを行ったのでtrueを返す
        return true;
    }

    public static boolean fileRenameAction(String fromFile, String toFile){
        File file1 = new File(fromFile);
        File file2 = new File(toFile);
        if(!file1.renameTo(file2)){
            //ファイル名変更失敗
            return false;
        }
        return true;
    }
}
