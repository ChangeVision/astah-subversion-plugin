package com.change_vision.astah.extension.plugin.svn_prototype.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNPasswordAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSHAuthentication;
import org.tmatesoft.svn.core.auth.SVNUserNameAuthentication;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNConfigurationDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNPasswordDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.view.IViewManager;

public class SVNUtils {
    public  final static String ERR_FILE_REGEX="%%%errFile%%%";
    public  final static String SAVE_PASSWORD_CHARSET = "ISO-8859-1";
    private final static String CIPHER_TYPE = "AES";

    public final static int LOGIN_KIND_BASIC  = 0;
    public final static int LOGIN_KIND_SSL    = 1;
    public final static int LOGIN_KIND_SSH    = 2;
    public final static int LOGIN_KIND_NOAUTH = 3;

    private  int    loginKind;

    private  String user;
    private  String password;
    private  String repository;
    private  String keyFilePath;
    private  SVNRepository repos;

    private ISVNAuthenticationManager authManager;

    private ISVNKitUtils kitUtils;

    private static MessageDialog messageDialog;

    public SVNUtils(){
        loginKind  = 0;
        repository = null;
        user       = null;
        password   = null;
        repository = null;
        repos      = null;

        messageDialog = new MessageDialog();
    }

    public int getLoginKind() {
        return loginKind;
    }

    public void setLoginKind(int loginKind) {
        this.loginKind = loginKind;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getKeyFilePath() {
        return keyFilePath;
    }

    public void setKeyFilePath(String keyFilePath) {
        this.keyFilePath = keyFilePath;
    }

    public SVNRepository getRepos() {
        return repos;
    }

    public void setRepos(SVNRepository repos) {
        this.repos = repos;
    }

    public Long getLatestRevision() throws SVNException {
        return repos.getLatestRevision();
    }

    public SVNDirEntry getSVNDirEntry(String fileName) throws SVNException {
        return getRepos().info(fileName, -1);
    }

    public ISVNAuthenticationManager getAuthManager() {
        return authManager;
    }

    public void setSVNKitUtils(ISVNKitUtils kitUtils) {
        this.kitUtils = kitUtils;
    }

    public boolean getPreferencesInfo(String cancelMessage) throws SVNNotConfigurationException{
        try{
            // レジストリに保存してある値を取得
            File keyFile = null;
            Preferences preferences = SVNPreferences.getInstace(SVNConfigurationDialog.class);

            loginKind   = Integer.parseInt(preferences.get(SVNPreferences.KEY_LOGIN_KIND, null));
            repository  = getDefaultRepositoryURL();
            user        = preferences.get(SVNPreferences.KEY_USER_NAME, null);
            password    = preferences.get(SVNPreferences.KEY_PASSWORD, null);
            keyFilePath = preferences.get(SVNPreferences.KEY_KEYFILE_PATH, null);

            if (!isNullString(keyFilePath)) {
                keyFile = new File(keyFilePath);
            }

            if (SVNUtils.isNullString(repository)
             && SVNUtils.isNullString(user)
             && SVNUtils.isNullString(password)) {
                // Subversionの設定が未設定
                throw new SVNNotConfigurationException(Messages.getMessage("err_message.common_not_config"));
            }

            if (loginKind == LOGIN_KIND_BASIC){
                if (SVNUtils.isNullString(password)){
                    SVNPasswordDialog pwDialog = new SVNPasswordDialog((getViewManager()).getMainFrame());
                    pwDialog.setVisible(true);
                    password = pwDialog.getPassword();
                    if (SVNUtils.isNullString(password) && SVNUtils.isNullString(keyFilePath)){
                        messageDialog.showMessage(cancelMessage);
                        return false;
                    }
                } else {
                    password = SVNUtils.decript(password.getBytes(SAVE_PASSWORD_CHARSET));
                }

                if (!SVNUtils.isNullString(repository)
                 && !SVNUtils.isNullString(user)
                 && !SVNUtils.isNullString(password)) {
                    // SVNRepositoryのインスタンスを取得
                    SVNURL surl = SVNURL.parseURIEncoded(repository);
                    repos = SVNRepositoryFactory.create(surl);

                    authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(user, false, surl, false),
                                                                                          new SVNPasswordAuthentication(user, password, false, surl, false)}
                                                                );
                    repos.setAuthenticationManager(authManager);
                } else {
                    messageDialog.showMessage(cancelMessage);
                    return false;
                }
            } else if (loginKind == LOGIN_KIND_SSH) {
                if (!SVNUtils.isNullString(repository)
                 && !SVNUtils.isNullString(user)
                 && !SVNUtils.isNullString(keyFilePath)) {
                    // SVNRepositoryのインスタンスを取得
                    SVNURL surl = SVNURL.parseURIEncoded(repository);
                    repos = SVNRepositoryFactory.create(surl);

                    authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(user, false, surl, false),
                                                                                          new SVNSSHAuthentication(user, keyFile, null, -1, false, surl, false)}
                                                                                         );
                    repos.setAuthenticationManager(authManager);
                } else if (!SVNUtils.isNullString(repository)
                        && !SVNUtils.isNullString(user)) {

                    if (SVNUtils.isNullString(password)){
                        SVNPasswordDialog pwDialog = new SVNPasswordDialog((getViewManager()).getMainFrame());
                        pwDialog.setVisible(true);
                        password = pwDialog.getPassword();

                        if (SVNUtils.isNullString(password) && SVNUtils.isNullString(keyFilePath)){
                            messageDialog.showMessage(cancelMessage);
                            return false;
                        }
                    } else {
                        password = SVNUtils.decript(password.getBytes(SAVE_PASSWORD_CHARSET));
                    }

                    if (!SVNUtils.isNullString(repository)
                     && !SVNUtils.isNullString(user)
                     && !SVNUtils.isNullString(password)) {
                        // SVNRepositoryのインスタンスを取得
                        SVNURL surl = SVNURL.parseURIEncoded(repository);
                        repos = SVNRepositoryFactory.create(surl);

                        authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(user, false, surl, false),
                                                                                              new SVNPasswordAuthentication(user, password, false, surl, false)}
                                                                                             );
                        repos.setAuthenticationManager(authManager);
                    } else {
                        messageDialog.showMessage(cancelMessage);
                        return false;
                    }
                } else {
                    messageDialog.showMessage(cancelMessage);
                    return false;
                }
            } else if (loginKind == LOGIN_KIND_NOAUTH) {
                if (!SVNUtils.isNullString(repository)
                 && !SVNUtils.isNullString(user)) {
                    // SVNRepositoryのインスタンスを取得
                    SVNURL surl = SVNURL.parseURIEncoded(repository);
                    repos = SVNRepositoryFactory.create(surl);

                    authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(user, false, surl, false)});
                    repos.setAuthenticationManager(authManager);
                }
            }
        } catch (SVNException se) {
            messageDialog.showKeyMessage("err_message.common_svn_error");
            return false;
        } catch (UnsupportedEncodingException uee) {
            messageDialog.showKeyMessage("err_message.common_incorrect_password");
            return false;
        } catch(ProjectNotFoundException pe) {
            messageDialog.showKeyMessage("err_message.common_not_open_project");
            return false;
        } catch (ClassNotFoundException cnfe){
            messageDialog.showKeyMessage("err_message.common_class_not_found");
            return false;
        }
        return true;
    }

    public static void setMessageDialog(MessageDialog messageDialog) {
        SVNUtils.messageDialog = messageDialog;
    }

    public static boolean isSaveProject(String pjPath) {
        if (pjPath == null || pjPath.equals("no_title")) {
            messageDialog.showKeyMessage("err_message.common_not_save_project");
            return false;
        }
        return true;
    }

    public static boolean isNullString(String str) {
        return (str == null || str.equals(""));
    }

    public byte[] readFileByte(String filePath) throws IOException {
        byte b[] = new byte[(int)((new File(filePath)).length())];
        FileInputStream fis = new FileInputStream(filePath);
        fis.read(b);
        fis.close();
        return b;
    }

    public IViewManager getViewManager() {
        ProjectAccessor projectAccessor = getProjectAccessor();
        IViewManager viewManager = null;
        try {
            viewManager = projectAccessor.getViewManager();

            if(viewManager == null) {
                throw new IllegalStateException("ViewManager is null.");
            }

            return viewManager;
        } catch (InvalidUsingException e) {
            return null;
        }
    }

    private ProjectAccessor getProjectAccessor() {
        ProjectAccessor projectAccessor = null;

        try {
            projectAccessor = ProjectAccessorFactory.getProjectAccessor();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        if(projectAccessor == null) {
            throw new IllegalStateException("projectAccessor is null.");
        }

        return projectAccessor;
    }

    public boolean isLoginError(SVNException se){
        if ((se.getMessage()).matches("^svn: E170001: Authentication required for.*")){
            // ログイン失敗
            messageDialog.showKeyMessage("err_message.common_login_error");
            return true;
        }
        return false;
    }

    /**
    * 暗号化・復号化
    */
    public static byte[] encript(String encriptString) {
        byte[] bytes = null;
        try {
            bytes = createCipher(Cipher.ENCRYPT_MODE).doFinal(encriptString.getBytes());
        } catch (Exception e) {
            messageDialog.showMessage(e.getLocalizedMessage());
            return null;
        }
        return bytes;
    }

    public static String decript(byte[] decript) {
        String str = null;
        try {
            byte[] bytes = createCipher(Cipher.DECRYPT_MODE).doFinal(decript);
            str = new String(bytes);
        } catch (Exception e) {
            messageDialog.showMessage(e.getLocalizedMessage());
            return null;
        }
        return str;
    }

    public static Cipher createCipher(int cipherMode) {
        SecretKey key = new SecretKeySpec(create128bits(), CIPHER_TYPE);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(cipherMode, key);
        } catch (InvalidKeyException e) {
            messageDialog.showMessage(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            messageDialog.showMessage(e.getMessage());
        } catch (NoSuchPaddingException e) {
            messageDialog.showMessage(e.getMessage());
        }
        return cipher;
    }

    private static byte[] create128bits() {
        byte[] bytes = new byte[128 / 8];
        byte[] strBytes = (SVNUtils.class.getName()).getBytes();
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = strBytes[i];
        }
        return bytes;
    }

    public String getDefaultRepositoryURL() throws SVNException, ClassNotFoundException, ProjectNotFoundException {
        ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
        String pjPath = projectAccessor.getProjectPath();
        return kitUtils.getDefaultRepositoryURL(pjPath);
    }

    public boolean isEditingProject(){
        /* 
         * ********************************************************
         * APIに開いているプロジェクトが編集中かを取得する仕組みがないため、 
         * 暫定対応として編集中を示すマーカー「(*)」がついているかどうかで判別する
         * ********************************************************
         */
        JFrame frame = (getViewManager()).getMainFrame();
        String title = frame.getTitle();
        String marker = "(*)";

        if (title.endsWith(marker)){
            return true;
        }
        return false;
    }

    public String escapeSpaceForMac(String origin){
        String strEscape = null;
        strEscape = origin.replaceAll(" ", "\\ ");
        return strEscape;
    }

    // プロジェクトのパスからファイル名を取得する
    public static String getFileName(String pjPath) {
        int markIndex = pjPath.lastIndexOf(File.separator);
        String fileName = pjPath.substring(markIndex + 1);

        return fileName;
    }

    // プロジェクトのパスからファイル名を除いたパスを取得する
    public static String getFilePath(String pjPath) {
        int markIndex = pjPath.lastIndexOf(File.separator);
        String filePath = pjPath.substring(0, markIndex + 1);
        return filePath;
    }

    // 開いているプロジェクトのパスを取得
    public String getProjectPath(ProjectAccessor projectAccessor) throws SVNPluginException, ProjectNotFoundException {
        String pjPath = projectAccessor.getProjectPath();

        if (!isSaveProject(pjPath)) {
            throw new SVNPluginException();
        }
        return pjPath;
    }

    // 開いているプロジェクトのパスを取得
    // 編集中かどうかも判定する
    public String getOpenProjectPath(ProjectAccessor projectAccessor) throws SVNPluginException, ProjectNotFoundException {
        String pjPath = getProjectPath(projectAccessor);

        if (isEditingProject()) {
            throw new SVNPluginException(Messages.getMessage("confirm_save_dialog.message"));
        }
        return pjPath;
    }

    // ファイル名の変更
    public boolean renameFile(String fromFile, String toFile) {
        File file1 = new File(fromFile);
        File file2 = new File(toFile);
        if (!file1.renameTo(file2)) {
            // ファイル名変更失敗
            return false;
        }
        return true;
    }

    // ファイルコピー
    public void copyFile(String pjPath, String workFile) throws IOException, SVNPluginException {
        // プロジェクトのコピーを作成
        String errFile = "";
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            errFile = pjPath;
            inputStream = new FileInputStream(pjPath);

            errFile = workFile;
            outputStream = new FileOutputStream(workFile);

            errFile = "";
        } catch (FileNotFoundException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_file_not_found")
                    + errFile, e);
        }

        FileChannel srcChannel = inputStream.getChannel();
        FileChannel destChannel = outputStream.getChannel();

        srcChannel.transferTo(0, srcChannel.size(), destChannel);

        inputStream.close();
        outputStream.close();
    }

    public String getMacAstahPath() throws SVNPluginException, ClassNotFoundException {
        // TODO Java7 Macインストールパス対応 ここから
//        ProjectAccessor prjAccessor = (AstahAPI.getAstahAPI()).getProjectAccessor();
        // Java7 Macインストールパス対応 ここまで

        // Java7 Macインストールパス対応のastah 6.6版 ここから
        ProjectAccessor prjAccessor = ProjectAccessorFactory.getProjectAccessor();
        // Java7 Macインストールパス対応のastah 6.6版 ここまで

        String path = prjAccessor.getAstahInstallPath();

        // astahのバージョンを取得
        if (!path.endsWith(File.separator)){
            path = path + File.separator;
        }

        String filePath = path + "astah professional.app/Contents/Resources/Java/astah-pro.jar";
        if (!(new File(filePath).exists())) {
            filePath = path + "astah professional.app/Contents/Java/astah-pro.jar";
            if (!(new File(filePath).exists())) {
                throw new SVNPluginException(Messages.getMessage("err_message.common_file_not_found"));
            }
        }
        return filePath;
    }
}
