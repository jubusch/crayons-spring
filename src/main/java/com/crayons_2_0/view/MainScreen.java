package com.crayons_2_0.view;

import java.util.ResourceBundle; 

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import com.crayons_2_0.MyUI;
import com.crayons_2_0.controller.Menu;
import com.crayons_2_0.service.LanguageService;
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
 * 
 */
//@SpringUI
@ViewScope
@SpringView(name = MainScreen.VIEW_NAME)

public class MainScreen extends HorizontalLayout  implements View {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "MainScreen";
    
    /**
     * 
     */
    private Menu menu;
    
    private ResourceBundle lang = LanguageService.getInstance().getRes();
    
    private MyUI ui;
    
    @Autowired
    Preferences preferences;

    
    public MainScreen() {

        
    }

    @PostConstruct
    void init(){
        

        //UI ui = MyUI.get().getCurrent();          // Changed
        //MyUI ui2 = MyUI.get();
        setStyleName("main-screen");

        CssLayout viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();
        
        
        // HIER PROBLEM: UI.getCurrent() oder MyUI.get() oder ... liefern alle null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        final Navigator navigator = new Navigator(MyUI.get(), viewContainer);
        navigator.setErrorView(ErrorView.class);
        menu = new Menu(navigator);
        menu.addView(new AboutView(), AboutView.VIEW_NAME, lang.getString(AboutView.VIEW_NAME),
                FontAwesome.INFO_CIRCLE);
        menu.addView(new Authorlibrary(), Authorlibrary.VIEW_NAME, lang.getString(Authorlibrary.VIEW_NAME),
                FontAwesome.BOOK);
        menu.addView(new UserlibraryView(), UserlibraryView.VIEW_NAME, lang.getString(UserlibraryView.VIEW_NAME),
                FontAwesome.PENCIL);
        //menu.addView(new Preferences(),Preferences.VIEW_NAME, lang.getString(Preferences.VIEW_NAME),
        // FontAwesome.GEAR);
        
        menu.addView(preferences, Preferences.VIEW_NAME, lang.getString(Preferences.VIEW_NAME), FontAwesome.GEAR);
        
        // Adds Views to Navigator
        navigator.addView(CourseEditorView.VIEW_NAME, new CourseEditorView());
        //navigator.addView(UnitEditorView.VIEW_NAME, new UnitEditorView());
        navigator.addView(Uniteditor.VIEW_NAME, new Uniteditor());
        //navigator.addView(UnitUserView.VIEW_NAME, new UnitUserView("Test unit")); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        navigator.addViewChangeListener(viewChangeListener);
        
        addComponent(menu);
        
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();
    }
    // notify the view menu about view changes so that it can display which view
    // is currently active
    ViewChangeListener viewChangeListener = new ViewChangeListener() {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

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
		ui = myUI;
		
	}

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub
        
    }
}
