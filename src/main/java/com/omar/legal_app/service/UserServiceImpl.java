package com.omar.legal_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.omar.legal_app.dto.UserDto;
import com.omar.legal_app.entity.User;
import com.omar.legal_app.repository.UserRepository;
@Service
public class UserServiceImpl  implements UserService {
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }



  
  

	@Override
	public User save(UserDto userDto) {
		User user = new User(userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()) , userDto.getRole(), userDto.getFullname());
		return userRepository.save(user);
	}

    @Override
    public User findByEmail(String username) {
        return  userRepository.findByEmail(username);
    }

	@Override
	public User save(User user) {
	
		return userRepository.save(user);
	}

    @Override
    public User findByUsername(String username) {
       return userRepository.findByEmail(username);
    }




    
}
