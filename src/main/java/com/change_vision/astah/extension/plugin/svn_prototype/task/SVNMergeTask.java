package com.change_vision.astah.extension.plugin.svn_prototype.task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

//import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNUpdate;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNSelectMergeDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNConflictException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class SVNMergeTask {

    private int selected = 0;

    private long revision = 0;

    private String pjPath   = null;

    private ProjectAccessor projectAccessor = null;

    private SVNUtils utils = null;

    private ISVNKitUtils kitUtils = null;

    public SVNMergeTask(String pjPath, ISVNKitUtils kitUtils, ProjectAccessor projectAccessor){
        this.pjPath = pjPath;
        this.kitUtils = kitUtils;
        this.projectAccessor = projectAccessor;
    }

    public void setSelected(int selected){
        this.selected = selected;
    }

    public int getSelected(){
        return selected;
    }

    public void setLatestRevision(long latest){
        this.revision = latest;
    }

    public void setSVNInfo(SVNUtils utils){
        this.utils    = utils;
    }

    public void setSVNKitUtils(ISVNKitUtils kitUtils) {
        this.kitUtils = kitUtils;
    }

    public List<Integer> doInBackground() throws InvalidEditingException,  SVNException,
                                                 LicenseNotFoundException, ProjectNotFoundException,
                                                 NonCompatibleException,   IOException,
                                                 ClassNotFoundException,   ProjectLockedException,
                                                 SVNConflictException, SVNPluginException
    {
        // 開いているプロジェクトのパス、ファイル名を取得
        String fileName = SVNUtils.getFileName(pjPath);
        String filePath = SVNUtils.getFilePath(pjPath);
        String workFile = filePath + "work." + fileName;
        String extension = utils.isSystemSafety() || utils.isSysML() ? ".axmz" : ".asta";
        String newFileName = pjPath + ".r" + revision + extension;

        if (selected == SVNSelectMergeDialog.CURRENT_PROJECT) {
            // 開いているプロジェクトの方を優先するマージ
            // easyMerge()を使用してマージ
            projectAccessor.open(workFile);
            projectAccessor.easyMerge(newFileName, true);
            // マージしたファイルを保存
            projectAccessor.save();
            projectAccessor.close();
        } else if (selected == SVNSelectMergeDialog.REPOSITORY_PROJECT) {
            // リポジトリにある方を優先するマージ
            // easyMerge()を使用してマージ
            projectAccessor.open(workFile);
            projectAccessor.easyMerge(newFileName, false);
            // マージしたファイルを保存
            projectAccessor.save();
            projectAccessor.close();
        } else {
            // マージを行わず元の状態に戻す
        }

        // 競合時に発生し、リネームした「～.asta.r…」ファイルのファイル名をもとに戻す
        utils.renameFile(newFileName, pjPath + ".r" + revision);

        if (selected == SVNSelectMergeDialog.NO_MERGE) {
            // 以前と同じ状態にするため、競合解消後、「特定のリビジョンへ更新」処理を実行

            // 以前のリビジョンを取得
            // とりあえず最新リビジョンを設定
            Long originRevision = revision;
            // カレントディレクトリにある、競合時に発生した「～.asta.r…」ファイルのファイル名を抽出// まずはFileオブジェクトでフォルダを指定して
            File dir = new File(filePath);
            // フォルダのファイル一覧を文字列配列で取得して
            String[] files = dir.list();
            // ファイルのパターン設定
            String fnamePtn = fileName + ".r";
            // パターンに適合するファイル名があれば処理をする
            for(String fname : files){
              if(fname.matches(fnamePtn + ".+")){
                originRevision = Long.valueOf(fname.substring(fnamePtn.length()));
                if (originRevision != revision) {
                    // 以前のリビジョンを取得できたらループを抜ける
                    break;
                }
              }
            }

            // 競合解消のため、「元に戻す」処理を実行
            kitUtils.doRevert(pjPath);
            if (originRevision != revision) {
                // 「特定のリビジョンへ更新」処理
                kitUtils.doUpdate(originRevision, pjPath);
            }
        } else {
            // 競合解消のため、「元に戻す」処理を実行
            kitUtils.doRevert(pjPath);
        }

        if (selected == SVNSelectMergeDialog.GET_LATEST_PROJECT){
            File work = new File(workFile);
            work.delete();
        } else {
            // 処理対象のファイルのファイルオブジェクト
            File pjFile = new File(pjPath);

            if (!pjFile.delete()) {
                // 元のファイルをリネーム
                utils.renameFile(pjPath, pjPath + ".old");
                File oldFile = new File(pjPath + ".old");
                oldFile.delete();
            }
            // マージしたプロジェクトのプロジェクト名を本来のファイル名に変更
            utils.renameFile(workFile, pjPath);
        }
        // プロジェクトを開く
        projectAccessor.open(pjPath);

        return null;
    }
}
