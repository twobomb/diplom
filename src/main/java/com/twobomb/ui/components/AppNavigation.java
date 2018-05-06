package com.twobomb.ui.components;

import com.twobomb.Utils.AppConst;
import com.twobomb.app.security.SecurityUtils;
import com.twobomb.entity.User;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Tag("app-navigation")
@HtmlImport("components/app-navigation.html")
public class AppNavigation extends PolymerTemplate<AppNavigation.Model> implements AfterNavigationObserver {

	@Id("tabs")
	private Tabs tabs;

	private List<String> hrefs = new ArrayList<>();
	private String logoutHref;
	private String defaultHref;
	private String currentHref;
	private String indexSegment;

	User currentUser;
	@Autowired
	public AppNavigation(User currentUser) {
		this.currentUser = currentUser;
	}


	public void init(List<PageInfo> pages, String defaultHref, String logoutHref) {
		getModel().setUsername(currentUser.getPerson().getLastname()+" "+currentUser.getPerson().getFirstname());
		getModel().setUserrole(currentUser.getRole().getRole_name());
		this.logoutHref = logoutHref;
		this.defaultHref = defaultHref;

		for (PageInfo page : pages) {
			Tab tab = new Tab(new IronIcon("vaadin", page.getIcon()), new Span(page.getTitle()));
			tab.getElement().setAttribute("theme", "icon-on-top");
			hrefs.add(page.getLink());
			tabs.add(tab);
		}

		tabs.addSelectedChangeListener(e -> navigate());


	}

	private void navigate() {
		int idx = tabs.getSelectedIndex();
		if (idx >= 0 && idx < hrefs.size()) {
			String href = hrefs.get(idx);
			if(indexSegment == null || indexSegment == "" && href.equals(AppConst.PAGE_THEMES)) {
				Notification.show("Выберите курсовую чтобы просмотреть темы",4000, Notification.Position.BOTTOM_START);
				UI.getCurrent().navigate(AppConst.PAGE_COURSEWORKS);
			}
			else if (href.equals(logoutHref)) {
				// The logout button is a 'normal' URL, not Flow-managed but
				// handled by Spring Security.
				UI.getCurrent().getPage().executeJavaScript("location.assign('logout')");
			}
			else if (!href.equals(currentHref)) {
				UI.getCurrent().navigate(href);
			}
		}
	}
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		String href = event.getLocation().getFirstSegment().isEmpty() ? defaultHref
				: event.getLocation().getFirstSegment();

		currentHref = href;
		if(event.getLocation().getSegments().size() >= 2)
			indexSegment = event.getLocation().getSegments().get(1);
		else
			indexSegment = "";

		tabs.setSelectedIndex(hrefs.indexOf(href));
	}

	public interface Model extends TemplateModel {
		void setUsername(String username);
		void setUserrole(String role);
	}
}
