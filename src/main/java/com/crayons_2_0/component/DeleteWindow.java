package com.crayons_2_0.component;

import com.crayons_2_0.service.graph.DeleteUnitListener;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

public final class DeleteWindow extends Window {
    public DeleteWindow() {
        setSizeFull();
        setModal(true);
        setResizable(false);
        setClosable(true); 
        setHeight(40.0f, Unit.PERCENTAGE);
        setWidth(40.0f, Unit.PERCENTAGE);
        
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        setContent(content);
        Component title = buildTitle();
        content.addComponent(title);
        content.setComponentAlignment(title, Alignment.TOP_CENTER);
        
        /*content.addComponent(buildDescription());
        
        Component unitChoise = buildUnitChoice();
        content.addComponent(unitChoise);
        content.setComponentAlignment(unitChoise, Alignment.MIDDLE_LEFT);*/
        
        content.addComponent(buildFooter());
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button ok = new Button("Delete");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new DeleteUnitListener());
        ok.focus();
        footer.addComponent(ok);
        footer.setComponentAlignment(ok, Alignment.TOP_CENTER);
        return footer;
    }

    /*private Component buildUnitChoice() {
        VerticalLayout unitTypeChoice = new VerticalLayout();
        return null;
    }*/

    private Component buildTitle() {
        Label title = new Label("Delete a unit");
        title.addStyleName(ValoTheme.LABEL_H2);
        return title;
    }
    
    /*private Component buildDescription() {
        return null;
    }*/
}
