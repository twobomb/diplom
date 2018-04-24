package com.twobomb.service;

import com.twobomb.entity.Role;
import com.twobomb.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service("RoleService")
@Repository
@Transactional
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    public RoleService() {
    }

    public String[] getAllRoles(){
        List<Role> roleList = roleRepository.findAll();
        String[] str = new String[roleList.size()];
        for(int i = 0; i < roleList.size();i++)
            str[i] = roleList.get(i).getRole();
        return str;
    }
    public Role findByRole(String role){
        return roleRepository.findByRole(role);
    }
}
