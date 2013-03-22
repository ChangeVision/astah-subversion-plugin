package com.change_vision.astah.extension.plugin.svn_prototype.exception;

public class SVNConflictException extends Exception {

    private static final long serialVersionUID = -2852183361506789472L;

    public SVNConflictException() {
        super();
    }

    public SVNConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public SVNConflictException(String message) {
        super(message);
    }

    public SVNConflictException(Throwable cause) {
        super(cause);
    }

}
