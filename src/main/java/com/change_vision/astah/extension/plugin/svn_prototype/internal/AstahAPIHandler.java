package com.change_vision.astah.extension.plugin.svn_prototype.internal;

import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class AstahAPIHandler {

	public ProjectAccessor getProjectAccessor() {
		ProjectAccessor projectAccessor = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
		} catch (ClassNotFoundException e) {
	        throw new IllegalStateException(e);
		}
		if(projectAccessor == null) throw new IllegalStateException("projectAccessor is null.");
		return projectAccessor;
	}

	public String getEdition() {
		return getProjectAccessor().getAstahEdition();
	}
}
