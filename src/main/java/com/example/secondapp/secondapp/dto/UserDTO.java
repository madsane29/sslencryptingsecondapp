package com.example.secondapp.secondapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long id;
    @NonNull
    private String name;
    @NonNull
    private int age;
    @NonNull
    private String email;
    private UUID uuid;
}
