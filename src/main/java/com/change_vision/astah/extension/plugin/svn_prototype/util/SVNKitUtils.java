package com.change_vision.astah.extension.plugin.svn_prototype.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNPasswordAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSHAuthentication;
import org.tmatesoft.svn.core.auth.SVNUserNameAuthentication;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNConflictException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotCommitException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;

public class SVNKitUtils implements ISVNKitUtils {

    private final String SVN_SEPARATOR = "/";

    private String pjPath;
    private SVNUtils svnUtils;

    public SVNKitUtils() {
    }

    @Override
    public void setProjectPath(String pjPath) {
        this.pjPath = pjPath;
    }

    /* (non-Javadoc)
     * @see com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils#setSVNUtils(com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils)
     */
    @Override
    public void setSVNUtils(SVNUtils utils) {
        this.svnUtils = utils;
    }

    @Override
    public void initialize() {
        // SVNKitの初期化
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /* (non-Javadoc)
     * @see com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils#getSVNClientManager()
     */
    @Override
    public SVNClientManager getSVNClientManager() {
        if (SVNUtils.isNullString(svnUtils.getPassword())) {
            return SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                    svnUtils.getAuthManager());
        } else {
            return SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                    svnUtils.getUser(), svnUtils.getPassword());
        }
    }

    /* (non-Javadoc)
     * @see com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils#getSVNWCClient()
     */
    @Override
    public SVNWCClient getSVNWCClient() throws SVNException, SVNPluginException {
        SVNClientManager scm;

        if (svnUtils.getLoginKind() == SVNUtils.LOGIN_KIND_SSH
                && SVNUtils.isNullString(svnUtils.getPassword())) {
            scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                    svnUtils.getAuthManager());
        } else {
            scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                    svnUtils.getUser(), svnUtils.getPassword());
        }

        return scm.getWCClient();
    }

    /* (non-Javadoc)
     * @see com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils#getSVNWCClient()
     */
    @Override
    public SVNUpdateClient getSVNUpdateClient() throws SVNException, SVNPluginException {
        SVNClientManager scm;

        if (svnUtils.getLoginKind() == SVNUtils.LOGIN_KIND_SSH
                && SVNUtils.isNullString(svnUtils.getPassword())) {
            scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                    svnUtils.getAuthManager());
        } else {
            scm = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true),
                    svnUtils.getUser(), svnUtils.getPassword());
        }

        return scm.getUpdateClient();
    }

    @Override
    public long getBaseRevision(String destination) throws SVNException {
        return getBaseRevision(new File(destination));
    }

    /* (non-Javadoc)
     * @see com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils#getBaseRevision(java.io.File)
     */
    @Override
    public long getBaseRevision(File destination) throws SVNException{
        SVNClientManager clientManager = SVNClientManager.newInstance();
        return clientManager.getStatusClient().doStatus(destination, false).getRevision().getNumber();
    }

    /* (non-Javadoc)
     * @see com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils#getLatestRevision(java.lang.String)
     */
    @Override
    public long getLatestRevision(String fileName) throws SVNPluginException {
        long latestRevision = 0;

        String fileURL = svnUtils.getRepository() + SVN_SEPARATOR + fileName;
        if (fileURL.matches("^/.*")){
            fileURL = fileURL.substring(1);
        }
        SVNRepository latestRepos;
        try {
            latestRepos = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(fileURL));

            ISVNAuthenticationManager authManager;

            if (svnUtils.getLoginKind() == SVNUtils.LOGIN_KIND_SSH && SVNUtils.isNullString(svnUtils.getPassword())){
                authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(svnUtils.getUser(), false, SVNURL.parseURIEncoded(svnUtils.getRepository()), false),
                                                                                      new SVNSSHAuthentication(svnUtils.getUser(), new File(svnUtils.getKeyFilePath()), null, -1, false, SVNURL.parseURIEncoded(svnUtils.getRepository()), false)}
                                                            );
            } else {
                authManager = new BasicAuthenticationManager(new SVNAuthentication[] {new SVNUserNameAuthentication(svnUtils.getUser(), false, SVNURL.parseURIEncoded(svnUtils.getRepository()), false),
                                                                                      new SVNPasswordAuthentication(svnUtils.getUser(), svnUtils.getPassword(), false, SVNURL.parseURIEncoded(svnUtils.getRepository()), false)}
                                                            );
            }
            latestRepos.setAuthenticationManager(authManager);

            latestRevision = latestRepos.getLatestRevision();
        } catch (SVNException e) {
            if (!svnUtils.isLoginError(e)){
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
            }
            return -1;
        }
        return latestRevision;
    }

    @Override
    public void doCommit(String fileName, String comment, long baseRevision) throws SVNException, FileNotFoundException, IOException, SVNPluginException {

        // コミット本処理
        if (baseRevision < 0) {
            // 新規登録
            newRegistrationCommit(fileName, comment);
        } else {
            // 登録済みファイルUPDATE コミット 
            // 古いコンテンツを取得する 
            byte[] oldData;
            byte[] newData;
            ByteArrayInputStream oldStream = null;
            ByteArrayInputStream newStream = null;

            String checksum;

            ISVNEditor editor = null;
            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();

            newData = svnUtils.readFileByte(pjPath);
            newStream = new ByteArrayInputStream(newData);

            if (svnUtils.getSVNDirEntry(fileName) != null) {
                ByteArrayOutputStream oldOut = new ByteArrayOutputStream();

                (svnUtils.getRepos()).getFile(fileName, -1, SVNProperties.wrap(Collections.EMPTY_MAP), oldOut);
                editor = (svnUtils.getRepos()).getCommitEditor(comment == null ? "" : comment, null, true, null);
                editor.openRoot(-1);
                oldData = oldOut.toByteArray();
                oldStream = new ByteArrayInputStream(oldData);
                editor.openFile(fileName, -1);
                editor.applyTextDelta(fileName, null);
                checksum = deltaGenerator.sendDelta(fileName, oldStream, 0, newStream, editor, true);
            } else {
                editor = (svnUtils.getRepos()).getCommitEditor(comment == null ? "" : comment, null, true, null);
                editor.openRoot(-1);
                editor.addFile(fileName, null,-1);
                editor.applyTextDelta(fileName, null);
                InputStream is = new FileInputStream(new File(pjPath));
                checksum = deltaGenerator.sendDelta(fileName, is, editor, true);
            }

            editor.closeFile(fileName, checksum);
            editor.closeDir();
            editor.closeEdit();
            editor = null;
        }
    }

    public void newRegistrationCommit(String fileName, String comment) throws SVNException, FileNotFoundException {
        // 新規登録
        ISVNEditor editor;

        editor = (svnUtils.getRepos()).getCommitEditor(comment, null, true, null);
        editor.openRoot(-1);
        editor.addFile(fileName, null,-1);
        editor.applyTextDelta(fileName, null);
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        InputStream is;
        is = new FileInputStream(new File(pjPath));
        String checksum = deltaGenerator.sendDelta(fileName, is, editor, true);

        editor.closeFile(fileName, checksum);
        editor.closeDir();

        editor.closeEdit();
    }

    @Override
    public void doRevert(String fileName) throws SVNException, SVNPluginException {
        getSVNWCClient().doRevert(new File[]{new File(fileName)}, SVNDepth.INFINITY, null);
    }

    @Override
    public boolean doUpdate(String path) throws SVNException, SVNConflictException, SVNPluginException {
        return doUpdate(svnUtils.getLatestRevision(), path);
    }

    @Override
    public boolean doUpdate(Long revision, String path) throws SVNException, SVNConflictException,
                                                               SVNPluginException {
        SVNConflictResolverHandler handler = new SVNConflictResolverHandler(null, path);
//        SVNUpdateClient client = getUpdateClient(handler);
        SVNUpdateClient client = getSVNUpdateClient();

//        DefaultSVNOptions options = (DefaultSVNOptions) client.getOptions();
//        options.setConflictHandler(handler);
        ((DefaultSVNOptions) client.getOptions()).setConflictHandler(handler);

        try {
            // Update処理
            client.doUpdate(new File(path), SVNRevision.create(revision), SVNDepth.INFINITY, true, true);
            if (handler.isConflict()) {
                throw new SVNConflictException();
            }
            return true;
        } catch (SVNException e) {
            if (handler.getMergeFlg()) {
                throw new SVNConflictException();
            }
            throw e;
        }
    }

//    private SVNUpdateClient getUpdateClient(SVNConflictResolverHandler handler) {
////        SVNClientManager scm = getSVNClientManager();
//        SVNUpdateClient client = getSVNClientManager().getUpdateClient();
//
//        DefaultSVNOptions options = (DefaultSVNOptions) client.getOptions();
//        options.setConflictHandler(handler);
//
//        return client;
//    }

    public String getBaseFile(String filePath, String fileName) throws SVNPluginException, FileNotFoundException {
        // 引数チェック
        if (filePath == null || fileName == null || filePath.length() == 0
                || fileName.length() == 0) {
            return null;
        }

        // リポジトリからベースリビジョンのファイルを取得
        String workFile = filePath + "base." + fileName;
        FileOutputStream baseFile;
        try {
            baseFile = new FileOutputStream(workFile);
            getSVNWCClient().doGetFileContents(new File(filePath + fileName), SVNRevision.UNDEFINED, null,
                                               false, baseFile);
            baseFile.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (SVNException e) {
            if (!svnUtils.isLoginError(e)) {
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
            }
            return null;
        } catch (IOException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), e);
        }
        return workFile;
    }

    public String getLatestFile(String filePath, String fileName) throws SVNPluginException, SVNNotCommitException,
                                                                         FileNotFoundException {
        // 引数チェック
        if (SVNUtils.isNullString(filePath) || SVNUtils.isNullString(fileName)) {
            return null;
        }

        // 対象プロジェクトに対する最新リビジョンを取得
        String workFile = null;
//        SVNDirEntry entry;
        try {
//            entry = utils.getSVNDirEntry(fileName);
//            if (entry == null) {
//                throw new SVNNotCommitException(Messages.getMessage("err_message.common_not_commit"));
//            }
//            long revision = entry.getRevision();
            long revision = getLatestRevision(fileName);

            // リポジトリから最新リビジョンのファイルを取得
            workFile = filePath + "latest." + fileName;
            FileOutputStream latestFile = null;
            latestFile = new FileOutputStream(workFile);
            getSVNWCClient().doGetFileContents(new File(filePath + fileName), SVNRevision.COMMITTED,
                    SVNRevision.create(revision), false, latestFile);
            latestFile.close();
        } catch (SVNException e) {
            if (!svnUtils.isLoginError(e)) {
                throw new SVNPluginException(Messages.getMessage("err_message.common_svn_error"), e);
            }
            return null;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new SVNPluginException(Messages.getMessage("err_message.common_io_error"), e);
        }
        return workFile;
    }

    public SVNRepository getRepos(String url, String userName, String password) throws SVNException {
        SVNRepository repos = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password);
        repos.setAuthenticationManager(authManager);

        return repos;
    }

    @Override
    public String getDefaultRepositoryURL(String filePath) throws SVNException {
        int markIndex;
        String property = null;
        SVNStatus status = SVNClientManager.newInstance().getStatusClient().doStatus(new File(filePath), false);
        if (status != null) {
            SVNURL url = status.getURL();
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
}
