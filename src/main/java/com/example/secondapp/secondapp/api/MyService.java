package com.example.secondapp.secondapp.api;


import com.example.secondapp.secondapp.dto.UserDTO;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

public interface MyService {
    UserDTO add(UserDTO userDTO) throws IOException;
    List<UserDTO> list() throws IOException;
    UserDTO get(long id) throws Exception;
    UserDTO update(long userId, UserDTO userDTO) throws IOException;
    void delete(long userId) throws IOException;
}
