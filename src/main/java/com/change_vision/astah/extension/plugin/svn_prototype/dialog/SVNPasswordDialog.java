package com.change_vision.astah.extension.plugin.svn_prototype.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;

public class SVNPasswordDialog extends KeyDialog {

    private static final long serialVersionUID = 1L;
    private static final int  LABEL_WIDTH  = 50;
    private static final int  LABEL_HEIGHT = 20;

    private String password = "";
    private JPasswordField passwordField;

    public SVNPasswordDialog(JFrame frame){
        super(frame, true);

        // ダイアログのタイトルを設定
        setTitle(Messages.getMessage("passsword_dialog.title"));
        // ダイアログの大きさを設定
        setSize(new Dimension(500, 100));
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel();
        JPanel footerPanel = new JPanel();

        // SVNパスワード
        JLabel lblPassword = new JLabel(Messages.getMessage("login_dialog.password_label"));
        lblPassword.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        passwordField = new JPasswordField(36);

        contentPanel.add(lblPassword);
        contentPanel.add(passwordField);

        // OKボタンの設定
        JButton okButton = new JButton(Messages.getMessage("ok"));
        okButton.setToolTipText(Messages.getMessage("ok"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                password = String.valueOf(passwordField.getPassword());
                if (SVNUtils.chkNullString(password)){
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_entered_password"));
                } else {
                    // ウィンドウを閉じる処理
                    dispose();
                }
                return;
            }
        });
        okButton.setPreferredSize(new Dimension(80, 25));
        footerPanel.add(okButton);

        // キャンセルボタン追加
        JButton cancelButton = new JButton(Messages.getMessage("cancel"));
        cancelButton.setToolTipText(Messages.getMessage("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ウィンドウを閉じる処理
                password = "";
                dispose();
                return;
            }
        });
        cancelButton.setPreferredSize(new Dimension(80, 25));
        footerPanel.add(cancelButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel);
        pack();

        if(frame != null) {
            setLocationRelativeTo(frame.getParent());
        }
    }

    public String getPassword(){
        return password;
    }
}
