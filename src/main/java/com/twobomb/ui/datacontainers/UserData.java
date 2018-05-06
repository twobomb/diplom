package com.twobomb.ui.datacontainers;

import com.twobomb.entity.User;

public class UserData {
    private boolean isAdmin;
    private boolean isTeacher;
    private boolean isStudent;

    public boolean getIsAdmin() {
        return isAdmin;
    }
    public static UserData getInstance(User currentUser){
        UserData userData = new UserData();
        userData.isAdmin = currentUser.getRole().isAdmin();
        userData.isStudent = currentUser.getRole().isStudent();
        userData.isTeacher = currentUser.getRole().isTeacher();
        return userData;
    }
    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    public boolean getIsStudent() {
        return isStudent;
    }

    public void setIsStudent(boolean student) {
        isStudent = student;
    }
}
