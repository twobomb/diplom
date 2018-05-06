package com.twobomb.ui.pages.error;


import com.twobomb.Utils.AppConst;
import com.twobomb.ui.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.*;

import javax.servlet.http.HttpServletResponse;

import static com.twobomb.Utils.AppConst.VIEWPORT;

@ParentLayout(MainView.class)
@PageTitle(AppConst.TITLE_NOT_FOUND)
@Tag("error-page")
@HtmlImport("templates/error/404ErrorPage.html")
public class CustomRouteNotFoundError extends RouteNotFoundError {

    public CustomRouteNotFoundError() {

    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
