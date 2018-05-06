package com.twobomb.ui.pages.error;


import com.twobomb.Utils.AppConst;
import com.twobomb.ui.MainView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;

import javax.servlet.http.HttpServletResponse;

@Tag("access-denied-view")
@HtmlImport("templates/error/AccessDeniedView.html")
@ParentLayout(MainView.class)
@PageTitle(AppConst.TITLE_ACCESS_DENIED)
public class AccessDeniedView extends PolymerTemplate<TemplateModel> implements HasErrorParameter<AccessDeniedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<AccessDeniedException> errorParameter) {
        return HttpServletResponse.SC_FORBIDDEN;
    }
}