package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.entities.User;

public class UserInsertDTO extends UserDTO {
	private static final long serialVersionUID = 1L;
	
	private String password;
	
	public UserInsertDTO() {
	}
	
	public UserInsertDTO(Long id, String firstName, String lastName, String email) {
		super();
	}

	public UserInsertDTO(User entity) {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
