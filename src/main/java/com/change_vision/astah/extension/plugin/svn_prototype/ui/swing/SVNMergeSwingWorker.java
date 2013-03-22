package com.change_vision.astah.extension.plugin.svn_prototype.ui.swing;

import java.util.List;

import javax.swing.SwingWorker;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import com.change_vision.astah.extension.plugin.svn_prototype.task.SVNMergeTask;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class SVNMergeSwingWorker extends SwingWorker<List<Integer>, Integer> {
    private SVNMergeTask task;

    public SVNMergeSwingWorker(String pjPath, SVNWCClient wcClient, ProjectAccessor projectAccessor) {
        task = new SVNMergeTask(pjPath, wcClient, projectAccessor);
    }

    @Override
    protected List<Integer> doInBackground() throws Exception {
        List<Integer> list = task.doInBackground();
        setProgress(100);
        return list;
    }

    public void setLatestRevision(long revision) {
        task.setLatestRevision(revision);
    }

    public void setSVNInfo(SVNUtils utils) {
        task.setSVNInfo(utils);
    }

    public void setSelected(int selected) {
        task.setSelected(selected);
    }

    public int getSelected() {
        return task.getSelected();
    }

}
