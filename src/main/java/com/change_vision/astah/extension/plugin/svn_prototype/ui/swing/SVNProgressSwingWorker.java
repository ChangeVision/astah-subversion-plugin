package com.change_vision.astah.extension.plugin.svn_prototype.ui.swing;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import com.change_vision.astah.extension.plugin.svn_prototype.task.SVNProgressTask;

public class SVNProgressSwingWorker extends SwingWorker<List<Integer>, Integer> {
    private SVNProgressTask task;

    public SVNProgressSwingWorker(JFrame f) {
        task = new SVNProgressTask(f);
    }

    @Override
    protected List<Integer> doInBackground() throws Exception {
        return task.doInBackground();
    }

    public void finishProgress(){
        task.finishProgress();
    }
}
