package com.twobomb.ui.models;

import com.twobomb.ui.datacontainers.IndexData;
import com.twobomb.ui.datacontainers.UserData;
import com.vaadin.flow.templatemodel.TemplateModel;

public interface MainModel extends TemplateModel {

    void setUserData(UserData d);
    void setIndexData(IndexData d);
}


