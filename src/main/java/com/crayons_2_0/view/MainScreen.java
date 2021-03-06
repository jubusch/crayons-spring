package com.crayons_2_0.view;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.crayons_2_0.MyUI;
import com.crayons_2_0.authentication.CurrentUser;
import com.crayons_2_0.controller.Menu;
import com.crayons_2_0.service.LanguageService;
import com.crayons_2_0.service.database.UserService;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

/**
 * Content of the UI when the user is logged in.
 * 
 */
@SuppressWarnings("serial")
@ViewScope
@SpringView(name = MainScreen.VIEW_NAME)
public class MainScreen extends HorizontalLayout implements View {

	public static final String VIEW_NAME = "mainScreen";

	private Menu menu;

	private ResourceBundle lang = LanguageService.getInstance().getRes();

	@Autowired
	UserService userService;

	@Autowired
	Preferences preferences;

	@Autowired
	Authorlibrary authorlibrary;

	@Autowired
	UserlibraryView userlibraryView;

	@Autowired
	UnitUserView unitUserView;

	@Autowired
	Search search;

	@Autowired
	AdminView adminView;

	@Autowired
	CourseEditorView courseEditorView;

	@Autowired
	Uniteditor uniteditor;

	public MainScreen() {

	}

	@PostConstruct
	void init() {

	}

	// notify the view menu about view changes so that it can display which view
	// is currently active
	ViewChangeListener viewChangeListener = new ViewChangeListener() {

		/**
         * 
         */
		@Override
		public boolean beforeViewChange(ViewChangeEvent event) {
			return true;
		}

		@Override
		public void afterViewChange(ViewChangeEvent event) {
			menu.setActiveView(event.getViewName());
		}

	};

	public void setUI(MyUI myUI) {

	}

	@Override
	public void enter(ViewChangeEvent event) {
		CurrentUser.getInstance().setUser(
				userService.findByEMail(CurrentUser.getInstance().geteMail()));
		setStyleName("main-screen");

		CssLayout viewContainer = new CssLayout();
		viewContainer.addStyleName("valo-content");
		viewContainer.setSizeFull();

		final Navigator navigator = new Navigator(MyUI.get(), viewContainer);
		navigator.setErrorView(ErrorView.class);
		menu = new Menu(navigator);
		// menu.addView(aboutView, AboutView.VIEW_NAME,
		// lang.getString(AboutView.VIEW_NAME), FontAwesome.INFO_CIRCLE);
		if (userService.findByEMail(CurrentUser.getInstance().geteMail())
				.getPermission() < 2) {
			menu.addView(authorlibrary, Authorlibrary.VIEW_NAME,
					lang.getString(Authorlibrary.VIEW_NAME), FontAwesome.BOOK);
		}
		menu.addView(userlibraryView, UserlibraryView.VIEW_NAME,
				lang.getString(UserlibraryView.VIEW_NAME), FontAwesome.PENCIL);
		menu.addView(preferences, Preferences.VIEW_NAME,
				lang.getString(Preferences.VIEW_NAME), FontAwesome.GEAR);
		menu.addView(search, Search.VIEW_NAME,
				lang.getString(Search.VIEW_NAME), FontAwesome.SEARCH);
		if (userService.findByEMail(CurrentUser.getInstance().geteMail())
				.getPermission() < 1) {
			menu.addView(adminView, AdminView.VIEW_NAME,
					lang.getString(AdminView.VIEW_NAME), FontAwesome.USERS);
		}

		navigator.addView(CourseEditorView.VIEW_NAME, courseEditorView);

		navigator.addView(Uniteditor.VIEW_NAME, uniteditor);

		navigator.addViewChangeListener(viewChangeListener);

		navigator.addView(UnitUserView.VIEW_NAME, unitUserView);

		navigator.navigateTo(UserlibraryView.VIEW_NAME);

		addComponent(menu);

		addComponent(viewContainer);
		setExpandRatio(viewContainer, 1);
		setSizeFull();
	}
}
