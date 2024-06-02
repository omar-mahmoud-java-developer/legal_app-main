package com.omar.legal_app.service;

import com.omar.legal_app.dto.UserDto;
import com.omar.legal_app.entity.User;

public interface  UserService {
    User save (UserDto userDto);
    

}
