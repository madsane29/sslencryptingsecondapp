package com.example.secondapp.secondapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class DataAndKeysDTO {
    @NonNull
    private String data;
    @NonNull
    private String keyUsedForEncrypting;
    @NonNull
    private String publicKey;

}
