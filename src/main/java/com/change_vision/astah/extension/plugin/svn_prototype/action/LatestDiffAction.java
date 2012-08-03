package com.change_vision.astah.extension.plugin.svn_prototype.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//import javax.swing.JFrame;
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
//import com.change_vision.astah.extension.plugin.svn_prototype.util.LockingGlassPane;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class LatestDiffAction implements IPluginActionDelegate {

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        try {
            // 開いているプロジェクトのパスを取得
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            String pjPath = projectAccessor.getProjectPath();

            if (SVNUtils.chkNotSaveProject(pjPath)) {
                return null;
            }

            int markIndex = pjPath.lastIndexOf(File.separator);
            String fileName = pjPath.substring(markIndex + 1);
            String filePath = pjPath.substring(0, markIndex + 1);

            // SVNKitの初期化
            FSRepositoryFactory.setup( );

            // 保存してあるSubversionログイン情報取得
            SVNUtils utils = new SVNUtils();
            if (!utils.getPreferencesInfo(Messages.getMessage("info_message.diff_cancel"))){
                return null;
            }

            SVNClientManager scm;
            if (utils.loginKind == SVNUtils.LOGIN_KIND_SSH && SVNUtils.chkNullString(utils.password)){
                scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.getAuthManager());
            } else {
                scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.user, utils.password);
            }
            SVNWCClient client = scm.getWCClient();

            // 対象プロジェクトに対する最新リビジョンを取得
            SVNDirEntry entry = (utils.repos).info(fileName, -1);
            if (entry == null) {
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_commit"));
                return null;
            }
            long revision = entry.getRevision(); 

            // リポジトリから最新リビジョンのファイルを取得
            String workFile = filePath + "latest." + fileName;
            FileOutputStream latestFile = new FileOutputStream(workFile);
            client.doGetFileContents(new File(pjPath), SVNRevision.COMMITTED, SVNRevision.create(revision), false, latestFile);
            latestFile.close();

            /** GlassPane 確認用 */
//            final JFrame frame = (SVNUtils.getViewManager()).getMainFrame();
//            frame.setGlassPane(new LockingGlassPane());
//            frame.getGlassPane().setVisible(false);
            /** GlassPane 確認用 */
            // プログレスバー設定
//            final SVNProgressDialog diffDialog = new SVNProgressDialog(frame,
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
                        /** GlassPane 確認用 */
//                        frame.getGlassPane().setVisible(false);
                        /** GlassPane 確認用 */
                    }
                }
            });

            diffTask.execute();
            /** GlassPane 確認用 */
//            frame.getGlassPane().setVisible(true);
            diffDialog.setVisible(true);
            /** GlassPane 確認用 */
        } catch (SVNException se){
            if (!SVNUtils.chkLoginError(se)){
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
            }
        } catch (ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_class_not_found"));
        } catch (ProjectNotFoundException pnfe){
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
        } catch (IOException ie){
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
        }

        return null;
    }
}
