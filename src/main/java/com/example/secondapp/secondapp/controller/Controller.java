package com.example.secondapp.secondapp.controller;

import com.example.secondapp.secondapp.api.MyService;
import com.example.secondapp.secondapp.dto.UserDTO;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "Handles operations")
@RequiredArgsConstructor
public class Controller {

    private static final String LIST_API_OPERATION_VALUE = "Retrieves the list of existing users";
    private static final String GET_API_OPERATION_VALUE = "Retrieves a user";
    private static final String CODE_404_USER_DOES_NOT_EXISTS = "User does not exists";
    private static final String USER_DTO_INFORMATIONS = "The userDTO that has the informations what should be saved";
    private static final String ID_OF_USER = "Id of user";
    private static final String UPDATES_THE_USER = "Updates the user";
    private static final String DELETES_THE_USER = "Deletes the user";
    private static final String ADDS_A_NEW_USER = "Adds a new user";

    private final MyService myService;

    @PostMapping("")
    @ApiOperation(value = ADDS_A_NEW_USER, response = UserDTO.class)
    public ResponseEntity<UserDTO> add(@ApiParam(value = USER_DTO_INFORMATIONS, required = true) @RequestBody UserDTO userDTO) throws IOException {
        return ResponseEntity.ok(myService.add(userDTO));
    }

    @GetMapping("")
    @ApiOperation(value = LIST_API_OPERATION_VALUE, response = List.class)
    public ResponseEntity<List<UserDTO>> list() throws IOException {
        return ResponseEntity.ok(myService.list());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = GET_API_OPERATION_VALUE, response = UserDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = CODE_404_USER_DOES_NOT_EXISTS)})
    public ResponseEntity<UserDTO> get(@ApiParam(value = ID_OF_USER, required = true) @PathVariable(value = "id") long id) throws Exception {
        return ResponseEntity.ok(myService.get(id));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = UPDATES_THE_USER, response = UserDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = CODE_404_USER_DOES_NOT_EXISTS)})
    public ResponseEntity<UserDTO> update(@ApiParam(value = ID_OF_USER, required = true) @PathVariable(value = "id") long id, @ApiParam(value = USER_DTO_INFORMATIONS, required = true) @RequestBody UserDTO userDTO) throws IOException {
        return ResponseEntity.ok(myService.update(id, userDTO));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = DELETES_THE_USER)
    @ApiResponses(value = {@ApiResponse(code = 404, message = CODE_404_USER_DOES_NOT_EXISTS)})
    public void delete(@ApiParam(value = ID_OF_USER, required = true) @PathVariable(value = "id") long id) throws IOException {
        myService.delete(id);
    }
}
