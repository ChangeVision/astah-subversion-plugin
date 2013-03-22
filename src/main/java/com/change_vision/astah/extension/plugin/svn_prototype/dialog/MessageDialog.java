package com.change_vision.astah.extension.plugin.svn_prototype.dialog;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;

public class MessageDialog {
    private Component parent;

    public MessageDialog() {
        this.parent = null;
    }

    public MessageDialog(Component parent) {
        this.parent = parent;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(parent, message);
    }

    public void showKeyMessage(String key) {
        JOptionPane.showMessageDialog(parent, Messages.getMessage(key));
    }
}
