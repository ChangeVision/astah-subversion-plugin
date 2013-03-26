package com.change_vision.astah.extension.plugin.svn_prototype.core;

import java.io.File;
import java.io.IOException;

import org.tmatesoft.svn.core.SVNException;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNCommitCancelException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNConflictException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;

public class SVNCommit {
    private final String BASE_REVISION_REGEX = "%%%baseRevision%%%";
    private final String LATEST_REVISION_REGEX = "%%%latestRevision%%%";

    private MessageDialog messageDialog;
    private SVNComment comment;
    private SVNUtils utils;
    private ISVNKitUtils kitUtils;

    public SVNCommit(SVNComment comment, ISVNKitUtils kitUtils) {
        utils = new SVNUtils();
        this.comment = comment;
        this.messageDialog = new MessageDialog(null);
        this.kitUtils = kitUtils;
    }

    public void setUtils(SVNUtils utils) {
        this.utils = utils;
    }

    public void setMessageDialog(MessageDialog messageDialog) {
        this.messageDialog = messageDialog;
    }

    public Object execute() throws UnExpectedException, SVNPluginException, SVNNotConfigurationException, ProjectNotFoundException, ClassNotFoundException, SVNException {
        try {
            // 保存してあるSubversionログイン情報取得
            utils.getPreferencesInfo(Messages.getMessage("info_message.commit_cancel"));
            kitUtils.setSVNUtils(utils);

            // 開いているプロジェクトのパスを取得
            String pjPath = utils.getOpenProjectPath(ProjectAccessorFactory.getProjectAccessor());
            kitUtils.setProjectPath(pjPath);

            String fileName = SVNUtils.getFileName(pjPath);

            long latestRevision = kitUtils.getLatestRevision(fileName);
            long baseRevision = kitUtils.getBaseRevision(new File(pjPath));

            // コミット対象のファイルが新規ではなく古い場合
            if (checkConflict(baseRevision, latestRevision)) {
                return null;
            }

            // SVNKitの初期化
            kitUtils.initialize();

            // コミットコメント
            String comment;
            try {
                comment = displayCommitComment();
            } catch (SVNCommitCancelException e) {
                // コミットがキャンセルされた場合
                return null;
            }

            // コミット本処理
            kitUtils.doCommit(fileName, comment, baseRevision);
//            if (baseRevision < 0) {
//                // 新規登録
//            } else {
//                // 登録済みファイルUPDATE コミット 
//                // 古いコンテンツを取得する 
//                byte[] oldData;
//                byte[] newData;
//                ByteArrayInputStream oldStream = null;
//                ByteArrayInputStream newStream = null;
//
//                String checksum;
//
//                ISVNEditor editor = null;
//                SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
//
//                newData = SVNUtils.readFileByte(pjPath);
//                newStream = new ByteArrayInputStream(newData);
//
//                if (utils.getSVNDirEntry(fileName) != null) {
//                    ByteArrayOutputStream oldOut = new ByteArrayOutputStream();
//
//                    (utils.getRepos()).getFile(fileName, -1, SVNProperties.wrap(Collections.EMPTY_MAP), oldOut);
//                    editor = (utils.getRepos()).getCommitEditor(comment == null ? "" : comment, null, true, null);
//                    editor.openRoot(-1);
//                    oldData = oldOut.toByteArray();
//                    oldStream = new ByteArrayInputStream(oldData);
//                    editor.openFile(fileName, -1);
//                    editor.applyTextDelta(fileName, null);
//                    checksum = deltaGenerator.sendDelta(fileName, oldStream, 0, newStream, editor, true);
//                } else {
//                    editor = (utils.getRepos()).getCommitEditor(comment == null ? "" : comment, null, true, null);
//                    editor.openRoot(-1);
//                    editor.addFile(fileName, null,-1);
//                    editor.applyTextDelta(fileName, null);
//                    InputStream is = new FileInputStream(new File(pjPath));
//                    checksum = deltaGenerator.sendDelta(fileName, is, editor, true);
//                }
//
//                editor.closeFile(fileName, checksum);
//                editor.closeDir();
//                editor.closeEdit();
//                editor = null;
//            }

            // ファイルを更新
            try {
//                SVNUpdate sUpdate = new SVNUpdate();
//                sUpdate.setUtils(utils);
//                sUpdate.doUpdate(pjPath);
                kitUtils.doUpdate(pjPath);
            } catch (SVNConflictException e) {
                // 競合した場合
                // 競合解消のため、「元に戻す」処理を実行
                kitUtils.doRevert(pjPath);
//                (kitUtils.getSVNWCClient()).doRevert(new File[]{new File(pjPath)}, SVNDepth.INFINITY, null);
            }

            messageDialog.showKeyMessage("info_message.commit_complete");
        } catch (IOException ie) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), ie);
        }
        return null;
    }

    public boolean checkConflict(long baseRevision, long latestRevision) {
        // コミット対象のファイルが新規ではなく古い場合
        if (baseRevision > 0 && latestRevision != baseRevision){
            String conflictMessage = Messages.getMessage("info_message.commit_conflict");
            conflictMessage = conflictMessage.replace(BASE_REVISION_REGEX, String.valueOf(baseRevision));
            conflictMessage = conflictMessage.replace(LATEST_REVISION_REGEX, String.valueOf(latestRevision));
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
//
//    public boolean newRegistration(String pjPath, String fileName, String comment) throws SVNPluginException {
//        // 新規登録
//        ISVNEditor editor;
//        try {
//            editor = (utils.getRepos()).getCommitEditor(comment, null, true, null);
//            editor.openRoot(-1);
//            editor.addFile(fileName, null,-1);
//            editor.applyTextDelta(fileName, null);
//            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
//            InputStream is;
//            is = new FileInputStream(new File(pjPath));
//            String checksum = deltaGenerator.sendDelta(fileName, is, editor, true);
//
//            editor.closeFile(fileName, checksum);
//            editor.closeDir();
//
//            editor.closeEdit();
//        } catch (FileNotFoundException e) {
//            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), e);
//        } catch (SVNException e) {
//            if (!SVNUtils.isLoginError(e)){
//                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
//            }
//            return false;
//        }
//        return true;
//    }
//
}
