package com.change_vision.astah.extension.plugin.svn_prototype.task;

import java.util.List;

import javax.swing.JFrame;

import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNProgressDialog;

public class SVNProgressTask {

    private JFrame frame = null;
    private SVNProgressDialog dialog = null;

    public SVNProgressTask(JFrame f){
        frame = f;
    }

    public List<Integer> doInBackground() throws Exception {
        dialog = new SVNProgressDialog(frame);
        dialog.setVisible(true);
        return null;
    }

    public void finishProgress(){
        if (dialog != null){
            dialog.dispose();
        }
        return;
    }
}
