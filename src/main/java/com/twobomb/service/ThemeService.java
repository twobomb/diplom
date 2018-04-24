package com.twobomb.service;

import com.twobomb.entity.Person;
import com.twobomb.entity.Theme;
import com.twobomb.entity.User;
import com.twobomb.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
@Service("ThemeService")
@Repository
@Transactional
public class ThemeService {

    @Autowired
    ThemeRepository themeRepository;

    public List<Theme> getAll(){
        return themeRepository.findAll();
    }

}
