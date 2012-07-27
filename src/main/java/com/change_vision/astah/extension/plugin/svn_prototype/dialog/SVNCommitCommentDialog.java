package com.change_vision.astah.extension.plugin.svn_prototype.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;

@SuppressWarnings("serial")
public class SVNCommitCommentDialog extends KeyDialog {

    private boolean runCommitFlg = false;
    private JTextArea textArea;
    private JScrollPane scrollpane = null;

    public SVNCommitCommentDialog(Frame frame) {
        super(frame, true);

        setTitle(Messages.getMessage("commit_comment_dialog.title"));
        setSize(new Dimension(410, 280));
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel headerPanel = new JPanel();
        JPanel contentPanel = new JPanel();
        JPanel footerPanel = new JPanel();

        JLabel lblHeader = new JLabel(Messages.getMessage("commit_comment_dialog.comment_title"));
        lblHeader.setPreferredSize(new Dimension(400, 15));
        headerPanel.add(lblHeader);

        textArea = new JTextArea(9, 55);
        textArea.setPreferredSize(new Dimension(390, 100));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent e){
                if (textArea.getLineCount() > textArea.getRows()){
                    textArea.setRows(textArea.getLineCount() + 2);
                    textArea.invalidate();
                    if (scrollpane != null){
                        scrollpane.updateUI();
                        (scrollpane.getVerticalScrollBar()).setValue(100);
                    }
                }
            }
        });

        scrollpane = new JScrollPane(textArea);
        scrollpane.setWheelScrollingEnabled(true);
        scrollpane.setPreferredSize(new Dimension(390, 180));
        contentPanel.add(scrollpane);

        // OKボタンの設定
        JButton okButton = new JButton(Messages.getMessage("ok"));
        okButton.setToolTipText(Messages.getMessage("ok"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommitFlg = true;

                // ウィンドウを閉じる処理
                dispose();
                return;
            }
        });
        footerPanel.add(okButton);

        // キャンセルボタンの設定
        JButton cancelButton = new JButton(Messages.getMessage("cancel"));
        cancelButton.setToolTipText(Messages.getMessage("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommitFlg = false;

                // ウィンドウを閉じる処理
                dispose();
                return;
            }
        });
        footerPanel.add(cancelButton);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        pack();

        if(frame != null) {
            setLocationRelativeTo(frame.getParent());
        }
    }

    public boolean getRunCommitFlg(){
        return runCommitFlg;
    }

    public String getCommitComment(){
        return textArea.getText();
    }
}
