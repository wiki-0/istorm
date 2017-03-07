package models;

import models.deadbolt.Role;

public class TRole implements Role {
	
	private String T_ROLE_NAME;

	public String T_ROLE_DESC;

	public TRole(String name){

        this.T_ROLE_NAME = name;
    }

	public String getRoleName() {
		return T_ROLE_NAME;
	}

}
