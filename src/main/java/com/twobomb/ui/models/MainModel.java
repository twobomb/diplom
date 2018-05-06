package com.twobomb.ui.models;

import com.twobomb.ui.datacontainers.UserData;
import com.vaadin.flow.templatemodel.TemplateModel;

public interface MainModel extends TemplateModel {

    void setUserData(UserData d);
}


