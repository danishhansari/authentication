package com.authentication.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ProfileResponse {
private String userId;
private String name;
private String email;
private Boolean isAccountVerified;
private Timestamp createdAt;
private Timestamp updatedAt;
}
