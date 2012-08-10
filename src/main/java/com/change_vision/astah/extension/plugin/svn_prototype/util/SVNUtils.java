package com.change_vision.astah.extension.plugin.svn_prototype.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNPasswordAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSHAuthentication;
import org.tmatesoft.svn.core.auth.SVNUserNameAuthentication;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNConfigurationDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNPasswordDialog;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.view.IViewManager;

public class SVNUtils {
    public  final String ERR_FILE_REGEX="%%%errFile%%%";
    public  static final String SAVE_PASSWORD_CHARSET = "ISO-8859-1";
    private static final String CIPHER_TYPE = "AES";

    public static final int LOGIN_KIND_BASIC  = 0;
    public static final int LOGIN_KIND_SSL    = 1;
    public static final int LOGIN_KIND_SSH    = 2;
    public static final int LOGIN_KIND_NOAUTH = 3;

    public  int    loginKind;
    public  String user;
    public  String password;
    public  String repository;
    public  String keyFilePath;
    public  SVNRepository repos;

    private ISVNAuthenticationManager authManager;

    public SVNUtils(){
        loginKind  = 0;
        repository = null;
        user       = null;
        password   = null;
        repository = null;
        repos      = null;
    }

    public boolean getPreferencesInfo(String cancelMessage){
        try{
            // レジストリに保存してある値を取得
        	File keyFile = null;
            Preferences preferences = SVNPreferences.getInstace(SVNConfigurationDialog.class);

            loginKind   = Integer.parseInt(preferences.get(SVNPreferences.KEY_LOGIN_KIND, null));
//            repository  = preferences.get(SVNPreferences.KEY_REPOSITORY_URL, null);
            repository  = getDefaultRepositoryURL();
            user        = preferences.get(SVNPreferences.KEY_USER_NAME, null);
            password    = preferences.get(SVNPreferences.KEY_PASSWORD, null);
            keyFilePath = preferences.get(SVNPreferences.KEY_KEYFILE_PATH, null);

            if (!chkNullString(keyFilePath)) {
                keyFile = new File(preferences.get(SVNPreferences.KEY_KEYFILE_PATH, null));
            }

            if (SVNUtils.chkNullString(repository)
             && SVNUtils.chkNullString(user)
             && SVNUtils.chkNullString(password)) {
                // Subversionの設定が未設定
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_config"));
                return false;
            }

            if (loginKind == LOGIN_KIND_BASIC){
                if (SVNUtils.chkNullString(password)){
                    SVNPasswordDialog pwDialog = new SVNPasswordDialog((SVNUtils.getViewManager()).getMainFrame());
                    pwDialog.setVisible(true);
                    password = pwDialog.getPassword();
                    if (SVNUtils.chkNullString(password) && SVNUtils.chkNullString(keyFilePath)){
                        JOptionPane.showMessageDialog(null, cancelMessage);
                        return false;
                    }
                } else {
                    password = SVNUtils.decript(password.getBytes(SAVE_PASSWORD_CHARSET));
                }

                if (!SVNUtils.chkNullString(repository)
                 && !SVNUtils.chkNullString(user)
                 && !SVNUtils.chkNullString(password)) {
                    // SVNRepositoryのインスタンスを取得
                    SVNURL surl = SVNURL.parseURIEncoded(repository);
                    repos = SVNRepositoryFactory.create(surl);

                    authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(user, false, surl, false),
                                                                                          new SVNPasswordAuthentication(user, password, false, surl, false)}
                                                                );
                    repos.setAuthenticationManager(authManager);
                } else {
                    JOptionPane.showMessageDialog(null, cancelMessage);
                    return false;
                }
            } else if (loginKind == LOGIN_KIND_SSH) {
                if (!SVNUtils.chkNullString(repository)
                 && !SVNUtils.chkNullString(user)
                 && !SVNUtils.chkNullString(keyFilePath)) {
                    // SVNRepositoryのインスタンスを取得
                    SVNURL surl = SVNURL.parseURIEncoded(repository);
                    repos = SVNRepositoryFactory.create(surl);

                    authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(user, false, surl, false),
                                                                                          new SVNSSHAuthentication(user, keyFile, null, -1, false, surl, false)}
                                                                                         );
                    repos.setAuthenticationManager(authManager);
                } else if (!SVNUtils.chkNullString(repository)
                        && !SVNUtils.chkNullString(user)) {

                    if (SVNUtils.chkNullString(password)){
                        SVNPasswordDialog pwDialog = new SVNPasswordDialog((SVNUtils.getViewManager()).getMainFrame());
                        pwDialog.setVisible(true);
                        password = pwDialog.getPassword();

                        if (SVNUtils.chkNullString(password) && SVNUtils.chkNullString(keyFilePath)){
                            JOptionPane.showMessageDialog(null, cancelMessage);
                            return false;
                        }
                    } else {
                        password = SVNUtils.decript(password.getBytes(SAVE_PASSWORD_CHARSET));
                    }

                    if (!SVNUtils.chkNullString(repository)
                     && !SVNUtils.chkNullString(user)
                     && !SVNUtils.chkNullString(password)) {
                        // SVNRepositoryのインスタンスを取得
                        SVNURL surl = SVNURL.parseURIEncoded(repository);
                        repos = SVNRepositoryFactory.create(surl);

                        authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(user, false, surl, false),
                                                                                              new SVNPasswordAuthentication(user, password, false, surl, false)}
                                                                                             );
                        repos.setAuthenticationManager(authManager);
                    } else {
                        JOptionPane.showMessageDialog(null, cancelMessage);
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, cancelMessage);
                    return false;
                }
            } else if (loginKind == LOGIN_KIND_NOAUTH) {
                if (!SVNUtils.chkNullString(repository)
                 && !SVNUtils.chkNullString(user)) {
                    // SVNRepositoryのインスタンスを取得
                    SVNURL surl = SVNURL.parseURIEncoded(repository);
                    repos = SVNRepositoryFactory.create(surl);

                    authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(user, false, surl, false)});
                    repos.setAuthenticationManager(authManager);
                }
            }
        } catch (SVNException se) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
            return false;
        } catch (UnsupportedEncodingException uee) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_incorrect_password"));
            return false;
        } catch(ProjectNotFoundException pe) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_open_project"));
            return false;
        } catch (ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_class_not_found"));
            return false;
        }
        return true;
    }

    public static boolean chkNotSaveProject(String pjPath) {
        if (pjPath == null || pjPath.equals("no_title")) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_not_save_project"));
            return true;
        }
        return false;
    }

    public static boolean chkNullString(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }

    public static byte[] readFileByte(String filePath) throws IOException {
        byte b[] = new byte[(int)((new File(filePath)).length())];
        FileInputStream fis = new FileInputStream(filePath);
        fis.read(b);
        fis.close();
        return b;
    }

    public static SVNRepository getRepos(String url, String userName, String password) throws SVNException {
        SVNRepository repos = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password);
        repos.setAuthenticationManager(authManager);

        return repos;
    }

    public static IViewManager getViewManager() {
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

    private static ProjectAccessor getProjectAccessor() {
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

    public static boolean chkLoginError(SVNException se){
        if ((se.getMessage()).matches("^svn: E170001: Authentication required for.*")){
            // ログイン失敗
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_login_error"));
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
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
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
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
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
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
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

    public ISVNAuthenticationManager getAuthManager() {
        return authManager;
    }

    public static String getDefaultRepositoryURL() throws SVNException, ClassNotFoundException, ProjectNotFoundException {
        ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
        String pjPath = projectAccessor.getProjectPath();
        return getDefaultRepositoryURL(pjPath);
    }

    public static String getDefaultRepositoryURL(String filePath) throws SVNException {
        int markIndex;
        String property = null;
        SVNURL url;
        SVNStatus status = SVNClientManager.newInstance().getStatusClient().doStatus(new File(filePath), false);
        if (status != null) {
            url = status.getURL();
            if (url == null) {
                markIndex = filePath.lastIndexOf(File.separator);
                String path = filePath.substring(0, markIndex);
                status = SVNClientManager.newInstance().getStatusClient().doStatus(new File(path), false);
                if (status != null) {
                    url = status.getURL();
                    property = url.toDecodedString();
                }
            } else {
                property = url.toDecodedString();
                markIndex = property.lastIndexOf("/");
                property = property.substring(0, markIndex + 1);
            }
        }
        return property;
    }

    public static boolean chkEditingProject(){
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

    public static String escapeSpaceForMac(String origin){
        String strEscape = null;
        strEscape = origin.replaceAll(" ", "\\ ");
        return strEscape;
    }
}
