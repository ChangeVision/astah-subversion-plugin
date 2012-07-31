package com.change_vision.astah.extension.plugin.svn_prototype;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import com.change_vision.astah.extension.plugin.svn_prototype.action.UpdateAction;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNSelectMergeDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IWindow;

public class SVNMergeTask extends SwingWorker<List<Integer>, Integer> {

    private int selected = 0;

    private long revision = 0;

    private String pjPath   = null;

    private IWindow window = null;

    private SVNWCClient   wcClient = null;

    private ProjectAccessor projectAccessor = null;

    private SVNUtils utils = null;

    public SVNMergeTask(String pjPath, IWindow window, SVNWCClient wcClient, ProjectAccessor projectAccessor) throws SVNException{
        this.pjPath = pjPath;
        this.window = window;
        this.wcClient = wcClient;
        this.projectAccessor = projectAccessor;
    }

    public void setSelected(int selected){
        this.selected = selected;
    }

    public void setLatestRevision(long latest){
        this.revision = latest;
    }

    public void setSVNInfo(SVNUtils utils){
        this.utils    = utils;
    }

    @Override
    protected List<Integer> doInBackground() throws InvalidEditingException, SVNException,
                                                    LicenseNotFoundException, ProjectNotFoundException,
                                                    NonCompatibleException, IOException,
                                                    ClassNotFoundException, ProjectLockedException
    {
        // 開いているプロジェクトのパス、ファイル名を取得
        int markIndex = pjPath.lastIndexOf(File.separator);
        String fileName = pjPath.substring(markIndex + 1);
        String filePath = pjPath.substring(0, markIndex + 1);
        String workFile = filePath + "work." + fileName;
        String newFileName = pjPath + ".r" + revision + ".asta";

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
        UpdateAction.fileRenameAction(newFileName, pjPath + ".r" + revision);

        // 処理対象のファイルのファイルオブジェクト
        File pjFile = new File(pjPath);

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
            wcClient.doRevert(new File[]{pjFile}, SVNDepth.INFINITY, null);
            if (originRevision != revision) {
                // 「特定のリビジョンへ更新」処理
                UpdateAction.doUpdate(window, utils, originRevision, pjPath);
            }
        } else {
            // 競合解消のため、「元に戻す」処理を実行
            wcClient.doRevert(new File[]{pjFile}, SVNDepth.INFINITY, null);
        }

        if (selected == SVNSelectMergeDialog.GET_LATEST_PROJECT){
            File work = new File(workFile);
            work.delete();
        } else {
            if (!pjFile.delete()) {
                // 元のファイルをリネーム
                UpdateAction.fileRenameAction(pjPath, pjPath + ".old");
                File oldFile = new File(pjPath + ".old");
                oldFile.delete();
            }
            // マージしたプロジェクトのプロジェクト名を本来のファイル名に変更
            UpdateAction.fileRenameAction(workFile, pjPath);
        }
        // プロジェクトを開く
        projectAccessor.open(pjPath);

        setProgress(100);

        return null;
    }

    public int getSelected(){
    	return selected;
    }
}
