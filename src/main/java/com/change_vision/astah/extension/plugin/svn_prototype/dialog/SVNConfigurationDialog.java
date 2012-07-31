package com.change_vision.astah.extension.plugin.svn_prototype.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.tmatesoft.svn.core.SVNException;
//import org.tmatesoft.svn.core.SVNURL;
//import org.tmatesoft.svn.core.wc.SVNClientManager;
//import org.tmatesoft.svn.core.wc.SVNStatus;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.KeyDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNPreferences;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class SVNConfigurationDialog extends KeyDialog {

    private static final long serialVersionUID = 1L;
    private static final int  LABEL_WIDTH      = 80;
    private static final int  LABEL_HEIGHT     = 20;

    private JTextField astah_home;
    private JTextField repository;
    private JTextField basicUser;
//    private JTextField sslUser;
    private JTextField sshUser;
    private JTextField noAuthUser;
    private JTextField key_file_path;

    private JPasswordField basicPassword;
//    private JPasswordField sslPassword;
    private JPasswordField sshPassword;

    private JCheckBox basicSavePw;
//    private JCheckBox sslSavePw;
    private JCheckBox sshSavePw;

    private JButton keyFileButton;

    private JRadioButton basicRadio;
//    private JRadioButton sslRadio;
    private JRadioButton sshRadio;
    private JRadioButton noAuthRadio;

    private Container c;
    private SVNConfigurationDialog parent;

    private ButtonGroup bGroup;

    private boolean errFlg = false;

    public SVNConfigurationDialog(JFrame frame) throws SVNException, ProjectNotFoundException, UnsupportedEncodingException, ClassNotFoundException {
        super(frame, true);
        getDialog(frame);
    }

    private void getDialog(JFrame frame) throws SVNException, ProjectNotFoundException, UnsupportedEncodingException, ClassNotFoundException {
        try {
            int selected = -1;
            String strSelected = null;

            parent = this;
            SVNPreferences.getInstace(this.getClass());

            // ラジオボタンのグループ化
            bGroup = new ButtonGroup();

            JPanel headerPanel     = new JPanel(new BorderLayout());
            JPanel contentPanel    = new JPanel();
            JPanel contentPanel1   = new JPanel();
            JPanel contentPanel2   = new JPanel();
            JPanel repositoryPanel = new JPanel(new BorderLayout());

            setLayout(new BorderLayout());

            contentPanel.setLayout(new BorderLayout());
            contentPanel1.setLayout(new BorderLayout());
            contentPanel2.setLayout(new BorderLayout());
            repositoryPanel.setLayout(new BorderLayout());

            BevelBorder border = new BevelBorder(BevelBorder.RAISED);

            c = getContentPane();
            c.setLayout(new BorderLayout());

            // ダイアログのタイトルを設定
            setTitle(Messages.getMessage("svn_setting_list_dialog.title"));

            // ダイアログの大きさを設定
            setSize(new Dimension(600, 400));
            setLocationRelativeTo(frame);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // Preferences のインスタンスを取得
            Preferences preferences = SVNPreferences.getInstance();

            // 前回選択した項目を取得
            strSelected = getDefaultString(SVNPreferences.KEY_LOGIN_KIND, preferences);
            if (!SVNUtils.chkNullString(strSelected)){
                selected = Integer.parseInt(strSelected);
            }

            // 各テキストフィールドを定義
            // astah home設定
            String homePath = getDefaultString(SVNPreferences.KEY_ASTAH_HOME, preferences);
            headerPanel.add(getHeaderPanel(homePath), BorderLayout.NORTH);

            // リポジトリURL
            String url = getDefaultString(SVNPreferences.KEY_REPOSITORY_URL, preferences);
            if (errFlg) {
                return;
            }

            JLabel lblRepository = new JLabel(" " + Messages.getMessage("login_dialog.repository_label") + " ");
            lblRepository.setSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
            if (SVNUtils.chkNullString(url)) {
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_entered_repository"));
                repository = new JTextField(50);
            } else {
                repository = new JTextField(url, 50);
            }
            repository.setEditable(false);
            repositoryPanel.add(lblRepository, BorderLayout.WEST);
            repositoryPanel.add(repository, BorderLayout.CENTER);
            repositoryPanel.add(new JLabel("  "), BorderLayout.EAST);
            headerPanel.add(repositoryPanel, BorderLayout.CENTER);
            headerPanel.add(new JLabel("  "), BorderLayout.SOUTH);

            // 画面に配置
            contentPanel.add(headerPanel, BorderLayout.NORTH);
            contentPanel1.add(getBasicAuthPanel(preferences, border, selected), BorderLayout.NORTH);
//            contentPanel1.add(getSSLAuthPanel(preferences, border, selected), BorderLayout.CENTER);
            contentPanel1.add(getSSHAuthPanel(preferences, border, selected), BorderLayout.SOUTH);
            contentPanel2.add(getNoAuthPanel(preferences, border, selected), BorderLayout.NORTH);
            contentPanel.add(contentPanel1, BorderLayout.CENTER);
            contentPanel.add(contentPanel2, BorderLayout.SOUTH);

            // 各パーツの選択設定
            controllContentEnabled(selected);

            add(contentPanel, BorderLayout.CENTER);
            add(new JLabel("  "), BorderLayout.EAST);
            add(new JLabel("  "), BorderLayout.WEST);

            // ボタンを配置
            add(getFooterPanel(), BorderLayout.SOUTH);

            pack();

            if(frame != null) {
                setLocationRelativeTo(frame.getParent());
            }
        } catch (UnsupportedEncodingException uee) {
            throw uee;
        } catch(ProjectNotFoundException pe) {
            throw pe;
        } catch (SVNException se){
            throw se;
        } catch (ClassNotFoundException cnfe){
            throw cnfe;
        }
    }

    private JPanel getHeaderPanel(String homePath){
        JPanel headerPanel = new JPanel();
        JLabel lblastah = new JLabel(Messages.getMessage("login_dialog.astah_home_label"));
        lblastah.setPreferredSize(new Dimension((int)(Math.round(LABEL_WIDTH * 2.0)), LABEL_HEIGHT));
        if (SVNUtils.chkNullString(homePath)) {
            astah_home = new JTextField(34);
        } else {
            astah_home = new JTextField(homePath, 34);
        }
//        astah_home.setEditable(false);

        // 参照ボタンの設定
        JButton fileButton = new JButton(Messages.getMessage("file"));
        fileButton.setToolTipText(Messages.getMessage("file"));
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (!SVNUtils.chkNullString(astah_home.getText())){
                    chooser.setCurrentDirectory(new File(astah_home.getText()));
                }

                int selected = chooser.showOpenDialog(parent);
                if (selected == JFileChooser.APPROVE_OPTION){
                    File file = chooser.getSelectedFile();
                    astah_home.setText(file.getPath());
                }else {
                    // キャンセルボタン押下時は何もしない
                }
            }
        });

        headerPanel.add(lblastah);
        headerPanel.add(astah_home);
        headerPanel.add(fileButton);

        return headerPanel;
    }

    private JPanel getFooterPanel(){
        JPanel footerPanel = new JPanel();

        // 保存ボタンの設定
        JButton saveButton = new JButton(Messages.getMessage("save"));
        saveButton.setToolTipText(Messages.getMessage("save"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Preferences のインスタンスを取得
                Preferences preferences = SVNPreferences.getInstance();
                int    loginKind = -1;
                String user      = "";
                String password  = "";
                String keyFile   = "";

                // ユーザー、パスワード保存処理
                try {
                    if (!SVNUtils.chkNullString(astah_home.getText())) {
                        preferences.put(SVNPreferences.KEY_ASTAH_HOME, astah_home.getText());
                    } else {
                        JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_choice_astah"));
                        return;
                    }
                    if (!SVNUtils.chkNullString(repository.getText())) {
                        preferences.put(SVNPreferences.KEY_REPOSITORY_URL, repository.getText());
                    } else {
                        JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_entered_repository"));
                        return;
                    }

                    if (basicRadio.isSelected()) {
                        user = basicUser.getText();

                        if (basicSavePw.isSelected()){
                            password = String.valueOf(basicPassword.getPassword());
                            if (SVNUtils.chkNullString(password)) {
                                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_entered_password"));
                                return;
                            }
                        }

                        loginKind = SVNUtils.LOGIN_KIND_BASIC;
                    } else if (sshRadio.isSelected()) {
                        user = sshUser.getText();

                        if (sshSavePw.isSelected()){
                            password = String.valueOf(sshPassword.getPassword());
                            if (SVNUtils.chkNullString(password)) {
                                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_entered_password"));
                                return;
                            }
                        }

                        loginKind = SVNUtils.LOGIN_KIND_SSH;
                        keyFile  = String.valueOf(key_file_path.getText());

                        if (!SVNUtils.chkNullString(keyFile)) {
                            preferences.put(SVNPreferences.KEY_KEYFILE_PATH, key_file_path.getText());
                        } else if (sshSavePw.isSelected()) {
                            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_entered_keyfile"));
                            return;
                        }
                    } else if (noAuthRadio.isSelected()){
                        user = noAuthUser.getText();
                        loginKind = SVNUtils.LOGIN_KIND_NOAUTH;
                    } else {
                        JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_selected_auth"));
                        return;
                    }

                    // ユーザーの保存
                    if (!SVNUtils.chkNullString(user)) {
                        preferences.put(SVNPreferences.KEY_USER_NAME, user);
                    } else {
                        JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_not_entered_user"));
                        return;
                    }

                    // パスワードの保存
                    if (!SVNUtils.chkNullString(password)) {
                        // パスワード暗号化
                        byte[] pwByte = SVNUtils.encript(password);
                        password = new String(pwByte, SVNUtils.SAVE_PASSWORD_CHARSET);
                    }
                    preferences.put(SVNPreferences.KEY_PASSWORD, password);

                    // 選択されたログインの種類を保存
                    preferences.put(SVNPreferences.KEY_LOGIN_KIND, String.valueOf(loginKind));
                    // SSHのキーファイルパスを保存
                    preferences.put(SVNPreferences.KEY_KEYFILE_PATH, keyFile);

                    preferences.flush();

                    // パスワード入力可能な場合のみ、保存した内容を元にログインチェック
                    if (basicSavePw.isSelected() || sshSavePw.isSelected()){
                        // 保存してあるSubversionログイン情報取得
                        SVNUtils utils = new SVNUtils();
                        if (utils.getPreferencesInfo(Messages.getMessage("err_message.common_svn_error"))){
                            // 保存した内容を使用して最新リビジョン番号を取得
                            (utils.repos).getLatestRevision();
                            // ログイン可能であればウィンドウを閉じる処理
                            dispose();
                        }
                    } else {
                        dispose();
                    }
                    return;
                } catch (BackingStoreException bse) {
                    bse.getCause().printStackTrace();
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                } catch (SVNException se) {
                    se.printStackTrace();
                    if (!SVNUtils.chkLoginError(se)){
                        // それ以外のSVN関連エラー
                        JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
                    }
                    return;
                }
            }
        });
        saveButton.setPreferredSize(new Dimension(80, 25));
        footerPanel.add(saveButton);

        // キャンセルボタン追加
        JButton cancelButton = new JButton(Messages.getMessage("cancel"));
        cancelButton.setToolTipText(Messages.getMessage("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ウィンドウを閉じる処理
                dispose();
                return;
            }
        });
        cancelButton.setPreferredSize(new Dimension(80, 25));
        footerPanel.add(cancelButton);

        return footerPanel;
    }

    private JPanel getBasicAuthPanel(Preferences preferences, BevelBorder border, int selected) throws SVNException, ProjectNotFoundException, ClassNotFoundException, UnsupportedEncodingException {
        JPanel basicAuthPanel = new JPanel();
        JPanel userPanel       = new JPanel();
        JPanel passwordPanel   = new JPanel();

        basicAuthPanel.setLayout(new BorderLayout());
        basicAuthPanel.setBorder(border);

        basicRadio = new JRadioButton(Messages.getMessage("login_dialog.basic_auth_label"), false);
        basicRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	controllContentEnabled(SVNUtils.LOGIN_KIND_BASIC);
            }
        });
    	basicRadio.setSelected(selected == SVNUtils.LOGIN_KIND_BASIC);
        basicAuthPanel.add(basicRadio, BorderLayout.NORTH);

        bGroup.add(basicRadio);

        // SVNユーザー
        String userName = getDefaultString(SVNPreferences.KEY_USER_NAME, preferences);
        JLabel lblUser = new JLabel(Messages.getMessage("login_dialog.user_label"));
        lblUser.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        if (SVNUtils.chkNullString(userName) || selected != SVNUtils.LOGIN_KIND_BASIC) {
            basicUser = new JTextField(50);
        } else {
        	basicUser = new JTextField(userName, 50);
        }
        userPanel.add(lblUser);
        userPanel.add(basicUser);

        // SVNパスワード
        String pw = getDefaultString(SVNPreferences.KEY_PASSWORD, preferences);
        JLabel lblPassword = new JLabel(Messages.getMessage("login_dialog.password_label"));
        lblPassword.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        if (SVNUtils.chkNullString(pw) || selected != SVNUtils.LOGIN_KIND_BASIC) {
            basicPassword = new JPasswordField(36);
            // チェックボックスをoffに設定
            basicSavePw = new JCheckBox(Messages.getMessage("login_dialog.save_password_label"), false);
            // パスワード入力欄を編集不可に設定
            basicPassword.setEnabled(false);
        } else {
            pw = SVNUtils.decript(pw.getBytes(SVNUtils.SAVE_PASSWORD_CHARSET));
            basicPassword = new JPasswordField(pw, 36);
            // チェックボックスをonに設定
            basicSavePw = new JCheckBox(Messages.getMessage("login_dialog.save_password_label"), true);
            // パスワード入力欄を編集可に設定
            basicPassword.setEnabled(true);
        }
        passwordPanel.add(lblPassword);
        passwordPanel.add(basicPassword);

        // SVNパスワードを保存する/しない を選択するチェックボックス
        basicSavePw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // クリックされたチェックボックスの状態を取得
                JCheckBox checkbox = (JCheckBox)e.getSource();
                if (checkbox.isSelected()) {
                	basicPassword.setEnabled(true);
                } else {
                	basicPassword.setEnabled(false);
                }

            }
        });
        passwordPanel.add(basicSavePw);

        basicAuthPanel.add(userPanel, BorderLayout.CENTER);
        basicAuthPanel.add(passwordPanel, BorderLayout.SOUTH);

        return basicAuthPanel;
    }

//    private JPanel getSSLAuthPanel(Preferences preferences, BevelBorder border, int selected) throws SVNException, ProjectNotFoundException, ClassNotFoundException, UnsupportedEncodingException {
//        JPanel sslAuthPanel = new JPanel();
//        JPanel userPanel       = new JPanel();
//        JPanel passwordPanel   = new JPanel();
//
//        sslAuthPanel.setLayout(new BorderLayout());
//        sslAuthPanel.setBorder(border);
//
//        sslRadio = new JRadioButton(Messages.getMessage("login_dialog.ssl_auth_label"), false);
//        sslRadio.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            	controllContentEnabled(SVNUtils.LOGIN_KIND_SSL);
//            }
//        });
//        sslRadio.setSelected(selected == SVNUtils.LOGIN_KIND_SSL);
//        sslAuthPanel.add(sslRadio, BorderLayout.NORTH);
//
//        bGroup.add(sslRadio);
//
//        // SVNユーザー
//        String userName = getDefaultString(SVNPreferences.KEY_USER_NAME, preferences);
//        JLabel lblUser = new JLabel(Messages.getMessage("login_dialog.user_label"));
//        lblUser.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
//        if (SVNUtils.chkNullString(userName) || selected != SVNUtils.LOGIN_KIND_SSL) {
//            sslUser = new JTextField(50);
//        } else {
//        	sslUser = new JTextField(userName, 50);
//        }
//        userPanel.add(lblUser);
//        userPanel.add(sslUser);
//
//        // SVNパスワード
//        String pw = getDefaultString(SVNPreferences.KEY_PASSWORD, preferences);
//        JLabel lblPassword = new JLabel(Messages.getMessage("login_dialog.password_label"));
//        lblPassword.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
//        if (SVNUtils.chkNullString(pw) || selected != SVNUtils.LOGIN_KIND_SSL) {
//            sslPassword = new JPasswordField(36);
//            // チェックボックスをoffに設定
//            sslSavePw = new JCheckBox(Messages.getMessage("login_dialog.save_password_label"), false);
//            // パスワード入力欄を編集不可に設定
//            sslPassword.setEnabled(false);
//        } else {
//            pw = SVNUtils.decript(pw.getBytes(SVNUtils.SAVE_PASSWORD_CHARSET));
//            sslPassword = new JPasswordField(pw, 36);
//            // チェックボックスをonに設定
//            sslSavePw = new JCheckBox(Messages.getMessage("login_dialog.save_password_label"), true);
//            // パスワード入力欄を編集可に設定
//            sslPassword.setEnabled(true);
//        }
//        passwordPanel.add(lblPassword);
//        passwordPanel.add(sslPassword);
//
//        // SVNパスワードを保存する/しない を選択するチェックボックス
//        sslSavePw.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // クリックされたチェックボックスの状態を取得
//                JCheckBox checkbox = (JCheckBox)e.getSource();
//                if (checkbox.isSelected()) {
//                	sslPassword.setEnabled(true);
//                } else {
//                	sslPassword.setEnabled(false);
//                }
//
//            }
//        });
//        passwordPanel.add(sslSavePw);
//
//        sslAuthPanel.add(userPanel, BorderLayout.CENTER);
//        sslAuthPanel.add(passwordPanel, BorderLayout.SOUTH);
//
//        return sslAuthPanel;
//    }

    private JPanel getSSHAuthPanel(Preferences preferences, BevelBorder border, int selected) throws SVNException, ProjectNotFoundException, ClassNotFoundException, UnsupportedEncodingException {
        JPanel sshAuthPanel  = new JPanel();
        JPanel userPanel     = new JPanel();
        JPanel passPanel     = new JPanel();
        JPanel passwordPanel = new JPanel();
        JPanel filePanel     = new JPanel();

        passPanel.setLayout(new BorderLayout());
        sshAuthPanel.setLayout(new BorderLayout());
        sshAuthPanel.setBorder(border);

        sshRadio = new JRadioButton(Messages.getMessage("login_dialog.ssh_auth_label"), false);
        sshRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	controllContentEnabled(SVNUtils.LOGIN_KIND_SSH);
            }
        });
        sshRadio.setSelected(selected == SVNUtils.LOGIN_KIND_SSH);
        sshAuthPanel.add(sshRadio, BorderLayout.NORTH);

        bGroup.add(sshRadio);

        // SVNユーザー
        String userName = getDefaultString(SVNPreferences.KEY_USER_NAME, preferences);
        JLabel lblUser = new JLabel(Messages.getMessage("login_dialog.user_label"));
        lblUser.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        if (SVNUtils.chkNullString(userName) || selected != SVNUtils.LOGIN_KIND_SSH) {
            sshUser = new JTextField(50);
        } else {
        	sshUser = new JTextField(userName, 50);
        }
        userPanel.add(lblUser);
        userPanel.add(sshUser);

        // SVNパスワード
        String pw = getDefaultString(SVNPreferences.KEY_PASSWORD, preferences);
        JLabel lblPassword = new JLabel(Messages.getMessage("login_dialog.password_label"));
        lblPassword.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        if (SVNUtils.chkNullString(pw) || selected != SVNUtils.LOGIN_KIND_SSH) {
            sshPassword = new JPasswordField(36);
            // チェックボックスをoffに設定
            sshSavePw = new JCheckBox(Messages.getMessage("login_dialog.save_password_label"), false);
            // パスワード入力欄を編集不可に設定
            sshPassword.setEnabled(false);
        } else {
            pw = SVNUtils.decript(pw.getBytes(SVNUtils.SAVE_PASSWORD_CHARSET));
            sshPassword = new JPasswordField(pw, 36);
            // チェックボックスをonに設定
            sshSavePw = new JCheckBox(Messages.getMessage("login_dialog.save_password_label"), true);
            // パスワード入力欄を編集可に設定
            sshPassword.setEnabled(true);
        }
        passwordPanel.add(lblPassword);
        passwordPanel.add(sshPassword);

        // SVNパスワードを保存する/しない を選択するチェックボックス
        sshSavePw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // クリックされたチェックボックスの状態を取得
                JCheckBox checkbox = (JCheckBox)e.getSource();
                if (checkbox.isSelected()) {
                	sshPassword.setEnabled(true);
                } else {
                	sshPassword.setEnabled(false);
                }

            }
        });
        passwordPanel.add(sshSavePw);

        // SSHキーファイル
        String  key = getDefaultString(SVNPreferences.KEY_KEYFILE_PATH, preferences);
        keyFileButton = new JButton(Messages.getMessage("file"));
        JLabel  lblSsh = new JLabel(Messages.getMessage("login_dialog.keyfile_path_label"));
        lblSsh.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        if (SVNUtils.chkNullString(key) || selected != SVNUtils.LOGIN_KIND_SSH) {
            key_file_path = new JTextField(44);
            // キーファイル設定ボタンを無効に設定
            keyFileButton.setEnabled(false);
        } else {
            key_file_path = new JTextField(key, 44);
            // キーファイル設定ボタンを有効に設定
            keyFileButton.setEnabled(true);
        }
        key_file_path.setEditable(false);

        // 参照ボタンの設定
        keyFileButton.setToolTipText(Messages.getMessage("file"));
        keyFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int selected = chooser.showOpenDialog(parent);
                if (selected == JFileChooser.APPROVE_OPTION){
                    File file = chooser.getSelectedFile();
                    key_file_path.setText(file.getPath());
                }else {
                    // キャンセルボタン押下時は何もしない
                }
            }
        });
        filePanel.add(lblSsh);
        filePanel.add(key_file_path);
        filePanel.add(keyFileButton);
        passPanel.add(passwordPanel, BorderLayout.CENTER);
        passPanel.add(filePanel, BorderLayout.SOUTH);

        sshAuthPanel.add(userPanel, BorderLayout.CENTER);
        sshAuthPanel.add(passPanel, BorderLayout.SOUTH);

        return sshAuthPanel;
    }

    private JPanel getNoAuthPanel(Preferences preferences, BevelBorder border, int selected) throws SVNException, ProjectNotFoundException, ClassNotFoundException, UnsupportedEncodingException {
        JPanel noAuthPanel = new JPanel();
        JPanel userPanel       = new JPanel();

        noAuthPanel.setLayout(new BorderLayout());
        noAuthPanel.setBorder(border);

        noAuthRadio = new JRadioButton(Messages.getMessage("login_dialog.no_auth_label"), false);
        noAuthRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	controllContentEnabled(SVNUtils.LOGIN_KIND_NOAUTH);
            }
        });
        noAuthRadio.setSelected(selected == SVNUtils.LOGIN_KIND_NOAUTH);
        noAuthPanel.add(noAuthRadio, BorderLayout.NORTH);

        bGroup.add(noAuthRadio);

        // SVNユーザー
        String userName = getDefaultString(SVNPreferences.KEY_USER_NAME, preferences);
        JLabel lblUser = new JLabel(Messages.getMessage("login_dialog.user_label"));
        lblUser.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        if (SVNUtils.chkNullString(userName) || selected != SVNUtils.LOGIN_KIND_NOAUTH) {
            noAuthUser = new JTextField(50);
        } else {
        	noAuthUser = new JTextField(userName, 50);
        }
        userPanel.add(lblUser);
        userPanel.add(noAuthUser);

        noAuthPanel.add(userPanel, BorderLayout.CENTER);

        return noAuthPanel;
    }


    private String getDefaultString(String key, Preferences preferences) throws SVNException, ProjectNotFoundException, ClassNotFoundException {
        String property = preferences.get(key, null);

        if (key.equals(SVNPreferences.KEY_REPOSITORY_URL)) {
            // 開いているプロジェクトのカレントディレクトリを取得
//            int markIndex = 0;
//            SVNURL url = null;
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            String pjPath = projectAccessor.getProjectPath();

            if (!SVNUtils.chkNotSaveProject(pjPath)) {
                try {
//                    SVNStatus status = SVNClientManager.newInstance().getStatusClient().doStatus(new File(pjPath), false);
//                    if (status != null) {
//                        url = status.getURL();
//                        if (url == null) {
//                            markIndex = pjPath.lastIndexOf(File.separator);
//                            String path = pjPath.substring(0, markIndex);
//                            status = SVNClientManager.newInstance().getStatusClient().doStatus(new File(path), false);
//                            if (status != null) {
//                                url = status.getURL();
//                                property = url.toDecodedString();
//                            }
//                        } else {
//                            property = url.toDecodedString();
//                            markIndex = property.lastIndexOf("/");
//                            property = property.substring(0, markIndex + 1);
//                        }
//                    }
                    String fileURL = SVNUtils.getDefaultRepositoryURL(pjPath);
                    if (!SVNUtils.chkNullString(fileURL)) {
                        property = fileURL;
                    }
                } catch (SVNException se) {
                    se.printStackTrace();
                    throw se;
                }
            } else {
                errFlg = true;
                property = null;
            }
        }

        return property;
    }

    private void controllContentEnabled(int selected){
    	basicUser.setEnabled(false);
//    	sslUser.setEnabled(false);
    	sshUser.setEnabled(false);
    	noAuthUser.setEnabled(false);
    	key_file_path.setEnabled(false);
    	basicPassword.setEnabled(false);
//    	sslPassword.setEnabled(false);
    	sshPassword.setEnabled(false);
    	basicSavePw.setEnabled(false);
//    	sslSavePw.setEnabled(false);
    	sshSavePw.setEnabled(false);
    	keyFileButton.setEnabled(false);

    	if (selected == SVNUtils.LOGIN_KIND_BASIC){
        	basicUser.setEnabled(true);
        	basicSavePw.setEnabled(true);
        	if (basicSavePw.isSelected()){
        	    basicPassword.setEnabled(true);
        	}
//    	} else if (selected == SVNUtils.LOGIN_KIND_SSL) {
//        	sslUser.setEnabled(true);
//        	sslSavePw.setEnabled(true);
//        	if (sslSavePw.isSelected()){
//        	    sslPassword.setEnabled(true);
//        	}
    	} else if (selected == SVNUtils.LOGIN_KIND_SSH) {
        	sshUser.setEnabled(true);
        	sshSavePw.setEnabled(true);
        	if (sshSavePw.isSelected()){
        	    sshPassword.setEnabled(true);
        	}
        	keyFileButton.setEnabled(true);
    	} else if (selected == SVNUtils.LOGIN_KIND_NOAUTH) {
        	noAuthUser.setEnabled(true);
    	}

    	return;
    }

    public boolean getErrFlg(){
        return errFlg;
    }
}
