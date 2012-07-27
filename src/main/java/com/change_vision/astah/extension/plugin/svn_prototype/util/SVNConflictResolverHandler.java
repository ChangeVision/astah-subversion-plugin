package com.change_vision.astah.extension.plugin.svn_prototype.util;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictReason;
import org.tmatesoft.svn.core.wc.SVNConflictResult;
import org.tmatesoft.svn.core.wc.SVNMergeFileSet;

import com.change_vision.jude.api.inf.ui.IWindow;

// 競合発生時の処理
public class SVNConflictResolverHandler implements ISVNConflictHandler {
    IWindow window   = null;
    String  pjPath   = "";
    boolean mergeFlg = true;

    // コンストラクタ
    public SVNConflictResolverHandler(IWindow arg0, String path) {
        super();
        window = arg0;
        pjPath = path;
    }

    @Override
    public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {
        SVNConflictReason reason = conflictDescription.getConflictReason();
        SVNMergeFileSet mergeFiles = conflictDescription.getMergeFiles();

        SVNConflictChoice choice = SVNConflictChoice.THEIRS_FULL;
        if (reason == SVNConflictReason.EDITED) {
            choice = SVNConflictChoice.POSTPONE;
            mergeFlg = false;
        }
        return new SVNConflictResult(choice, mergeFiles.getResultFile());
    }

    public boolean getMergeFlg() {
        return mergeFlg;
    }
}
