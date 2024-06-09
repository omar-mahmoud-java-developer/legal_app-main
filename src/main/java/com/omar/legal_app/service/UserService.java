package com.omar.legal_app.service;

import com.omar.legal_app.dto.UserDto;
import com.omar.legal_app.entity.User;

public interface  UserService {
    User findByUsername(String username);
    User findByEmail(String username);
    User save (UserDto userDto);
    User save (User user);
    

}
