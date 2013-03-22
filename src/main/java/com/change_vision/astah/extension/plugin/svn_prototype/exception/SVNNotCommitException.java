package com.change_vision.astah.extension.plugin.svn_prototype.exception;

public class SVNNotCommitException extends Exception {

    private static final long serialVersionUID = 7187627302481281752L;

    public SVNNotCommitException() {
        super();
    }

    public SVNNotCommitException(String message, Throwable cause) {
        super(message, cause);
    }

    public SVNNotCommitException(String message) {
        super(message);
    }

    public SVNNotCommitException(Throwable cause) {
        super(cause);
    }

}
