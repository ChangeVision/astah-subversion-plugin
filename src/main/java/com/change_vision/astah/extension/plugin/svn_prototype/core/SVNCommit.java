package com.change_vision.astah.extension.plugin.svn_prototype.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNPasswordAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSHAuthentication;
import org.tmatesoft.svn.core.auth.SVNUserNameAuthentication;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNCommitCommentDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNCommitCancelException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNConflictException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;

public class SVNCommit {

    private final String SVN_SEPARATOR = "/";
    private final String BASE_REVISION_REGEX = "%%%baseRevision%%%";
    private final String LATEST_REVISION_REGEX = "%%%latestRevision%%%";

    private MessageDialog messageDialog;
    private SVNComment comment;
    private SVNUtils utils;

    public SVNCommit(SVNComment comment) {
        utils = new SVNUtils();
//        comment = new SVNCommitCommentDialog((SVNUtils.getViewManager()).getMainFrame());
        this.comment = comment;
        this.messageDialog = new MessageDialog(null);
    }

    public void setUtils(SVNUtils utils) {
        this.utils = utils;
    }

    public void setMessageDialog(MessageDialog messageDialog) {
        this.messageDialog = messageDialog;
    }

    public Object execute() throws UnExpectedException, SVNPluginException, SVNNotConfigurationException {
        try {
            // 保存してあるSubversionログイン情報取得
            utils.getPreferencesInfo(Messages.getMessage("info_message.commit_cancel"));

            // 開いているプロジェクトのパスを取得
            String pjPath = getOpenProjectPath();

            long latestRevision = 0;
            long baseRevision   = 0;

            String fileName = getFileName(pjPath);

            // SVNKitの初期化
            initializeSVNKit();

            latestRevision = getLatestRevision(fileName);
            baseRevision = getBaseRevision(new File(pjPath));

            // コミット対象のファイルが新規ではなく古い場合
            if (checkConflict(baseRevision, latestRevision)) {
                return null;
            }

            // コミットコメント
            String comment;
            try {
                comment = displayCommitComment();
            } catch (SVNCommitCancelException e) {
                // コミットがキャンセルされた場合
                return null;
            }

            // コミット本処理
            if (baseRevision < 0) {
                // 新規登録
            } else {
                // 登録済みファイルUPDATE コミット 
                // 古いコンテンツを取得する 
                byte[] oldData;
                byte[] newData;
                ByteArrayInputStream oldStream = null;
                ByteArrayInputStream newStream = null;

                String checksum;

                ISVNEditor editor = null;
                SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();

                newData = SVNUtils.readFileByte(pjPath);
                newStream = new ByteArrayInputStream(newData);

                if (utils.getSVNDirEntry(fileName) != null) {
                    ByteArrayOutputStream oldOut = new ByteArrayOutputStream();

                    (utils.getRepos()).getFile(fileName, -1, SVNProperties.wrap(Collections.EMPTY_MAP), oldOut);
                    editor = (utils.getRepos()).getCommitEditor(comment == null ? "" : comment, null, true, null);
                    editor.openRoot(-1);
                    oldData = oldOut.toByteArray();
                    oldStream = new ByteArrayInputStream(oldData);
                    editor.openFile(fileName, -1);
                    editor.applyTextDelta(fileName, null);
                    checksum = deltaGenerator.sendDelta(fileName, oldStream, 0, newStream, editor, true);
                } else {
                    editor = (utils.getRepos()).getCommitEditor(comment == null ? "" : comment, null, true, null);
                    editor.openRoot(-1);
                    editor.addFile(fileName, null,-1);
                    editor.applyTextDelta(fileName, null);
                    InputStream is = new FileInputStream(new File(pjPath));
                    checksum = deltaGenerator.sendDelta(fileName, is, editor, true);
                }

                editor.closeFile(fileName, checksum);
                editor.closeDir();
                editor.closeEdit();
                editor = null;
            }

            // ファイルを更新
            try {
                SVNUpdate sUpdate = new SVNUpdate();
                sUpdate.setUtils(utils);
                sUpdate.doUpdate(pjPath);
            } catch (SVNConflictException e) {
                // 競合した場合
                SVNClientManager scm;
                if (SVNUtils.chkNullString(utils.getPassword())){
                    scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.getAuthManager());
                } else {
                    scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), utils.getUser(), utils.getPassword());
                }
                // 競合解消のため、「元に戻す」処理を実行
                SVNWCClient wcClient = scm.getWCClient();
                wcClient.doRevert(new File[]{new File(pjPath)}, SVNDepth.INFINITY, null);
            }

//            JOptionPane.showMessageDialog(null, Messages.getMessage("info_message.commit_complete"));
            messageDialog.showKeyMessage("info_message.commit_complete");
        } catch (SVNException se){
            if (!SVNUtils.chkLoginError(se)){
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), se);
//                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
            }
        } catch (IOException ie) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), ie);
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
        }
        return null;
    }

    public long getBaseRevision(File destination) throws SVNException{
        SVNClientManager clientManager = SVNClientManager.newInstance();
        return clientManager.getStatusClient().doStatus(destination, false).getRevision().getNumber();
    }

    public String getOpenProjectPath() throws SVNPluginException {
        // 開いているプロジェクトのパスを取得
        ProjectAccessor projectAccessor;
        String pjPath = null;

        try {
            projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            pjPath = projectAccessor.getProjectPath();

            if (SVNUtils.chkNotSaveProject(pjPath)) {
                throw new SVNPluginException();
            }

            if (SVNUtils.chkEditingProject()) {
//                messageDialog.showKeyMessage("confirm_save_dialog.message");
//                JOptionPane.showMessageDialog(null, Messages.getMessage("confirm_save_dialog.message"));
                throw new SVNPluginException(Messages.getMessage("confirm_save_dialog.message"));
            }
        } catch (ClassNotFoundException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_class_not_found"), e);
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_class_not_found"));
//            return null;
        } catch (ProjectNotFoundException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_not_open_project"), e);
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
//            return null;
        }
        return pjPath;
    }

    public String getFileName(String pjPath) {
        int markIndex = pjPath.lastIndexOf(File.separator);
        String fileName = pjPath.substring(markIndex + 1);

        return fileName;
    }

    public void initializeSVNKit() {
        // SVNKitの初期化
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    public long getLatestRevision(String fileName) throws SVNPluginException {
        long latestRevision = 0;

        String fileURL = utils.getRepository() + SVN_SEPARATOR + fileName;
        if (fileURL.matches("^/.*")){
            fileURL = fileURL.substring(1);
        }
        SVNRepository latestRepos;
        try {
            latestRepos = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(fileURL));

            ISVNAuthenticationManager authManager;

            if (utils.getLoginKind() == SVNUtils.LOGIN_KIND_SSH && SVNUtils.chkNullString(utils.getPassword())){
                authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(utils.getUser(), false, SVNURL.parseURIEncoded(utils.getRepository()), false),
                                                                                      new SVNSSHAuthentication(utils.getUser(), new File(utils.getKeyFilePath()), null, -1, false, SVNURL.parseURIEncoded(utils.getRepository()), false)}
                                                            );
            } else {
                authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(utils.getUser(), false, SVNURL.parseURIEncoded(utils.getRepository()), false),
                                                                                      new SVNPasswordAuthentication(utils.getUser(), utils.getPassword(), false, SVNURL.parseURIEncoded(utils.getRepository()), false)}
                                                            );
            }
            latestRepos.setAuthenticationManager(authManager);

            latestRevision = latestRepos.getLatestRevision();
        } catch (SVNException e) {
            if (!SVNUtils.chkLoginError(e)){
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
//                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
            }
            return -1;
        }
        return latestRevision;
    }

    public boolean checkConflict(long baseRevision, long latestRevision) {
        // コミット対象のファイルが新規ではなく古い場合
        if (baseRevision > 0 && latestRevision != baseRevision){
            String conflictMessage = Messages.getMessage("info_message.commit_conflict");
            conflictMessage = conflictMessage.replace(BASE_REVISION_REGEX, String.valueOf(baseRevision));
            conflictMessage = conflictMessage.replace(LATEST_REVISION_REGEX, String.valueOf(latestRevision));
//            JOptionPane.showMessageDialog(null, conflictMessage);
            messageDialog.showMessage(conflictMessage);
            return true;
        }
        return false;
    }

    public String displayCommitComment() throws SVNCommitCancelException {
        if (!comment.isCommit()){
            messageDialog.showKeyMessage("info_message.commit_cancel");
            throw new SVNCommitCancelException();
        }

        return comment.getCommitComment();
    }

    public boolean newRegistration(String pjPath, String fileName, String comment) throws SVNPluginException {
        // 新規登録
        ISVNEditor editor;
        try {
            editor = (utils.getRepos()).getCommitEditor(comment, null, true, null);
            editor.openRoot(-1);
            editor.addFile(fileName, null,-1);
            editor.applyTextDelta(fileName, null);
            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            InputStream is;
            is = new FileInputStream(new File(pjPath));
            String checksum = deltaGenerator.sendDelta(fileName, is, editor, true);

            editor.closeFile(fileName, checksum);
            editor.closeDir();

            editor.closeEdit();
        } catch (FileNotFoundException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), e);
//            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_io_error"));
//            return false;
        } catch (SVNException e) {
            if (!SVNUtils.chkLoginError(e)){
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
//                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
            }
            return false;
        }
        return true;
    }

}
