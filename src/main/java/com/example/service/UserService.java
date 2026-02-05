package com.example.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.User;
import com.example.repository.UserRepository;

@Service
public class UserService {
    @Autowired private UserRepository repo;

    public List<User> getAll(){ return repo.findAll(); }
    public User create(User c){ return repo.save(c); }
}
