package com.spring.springjwt.models;

import java.util.List;

import lombok.Data;

@Data
public class AccessDetailsResponse {

	private Long id;

	private String username;

	private String email;

	private List<String> roleName;
}
