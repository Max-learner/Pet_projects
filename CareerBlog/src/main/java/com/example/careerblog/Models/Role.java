package com.example.careerblog.Models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
	USER, AUTHOR, ADMIN, MASTER;

	@Override
	public String getAuthority() {
		return name();
	}

}
