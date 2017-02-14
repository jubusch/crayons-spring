package com.crayons_2_0.component;

import org.springframework.beans.factory.annotation.Autowired;

import com.crayons_2_0.model.Course;
import com.crayons_2_0.service.database.CourseService;
import com.crayons_2_0.view.Preferences;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SpringUI
@ViewScope
@SpringComponent
public class CourseModificationWindow extends Window {
    private TabSheet tabSheet;
    private Component tab;
	private Course course;
    @Autowired
	CourseService courseService;
    
    public CourseModificationWindow(Course course, Component tab, TabSheet tabSheet) {
        this.course = course;
        this.tab = tab;
        this.tabSheet = tabSheet;
        setModal(true);
        setResizable(false);
        setClosable(true);
        setHeight(40.0f, Unit.PERCENTAGE);
        setWidth(40.0f, Unit.PERCENTAGE);
        
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setSpacing(true);
        setContent(content);
        
        Component courseDescription = buildCourseDescription();
        content.addComponent(courseDescription);
    }
    
    private Component buildCourseDescription() {
        VerticalLayout courseDescription = new VerticalLayout();
        courseDescription.setSpacing(true);
        courseDescription.setMargin(true);
        
        HorizontalLayout course = new HorizontalLayout();
        course.setSpacing(true);
        Label courseTitleLabel = new Label("Kurstitel");
        TextField courseTitleField = new TextField(null, this.course.getTitle());
        course.addComponents(courseTitleLabel, courseTitleField);
        //courseTitleField.addValueChangeListener();
        courseDescription.addComponent(course);
        courseTitleField.setImmediate(true); 
        
        VerticalLayout couseDescription = new VerticalLayout();
        couseDescription.setSizeFull();
        Label couseDescriptionLabel = new Label("Kursbeschreibung");
        TextField couseDescriptionField = new TextField();
        couseDescriptionField.setSizeFull();
        
        couseDescriptionField.setValue(this.course.getDescription());
        couseDescription.addComponents(couseDescriptionLabel, couseDescriptionField);
        couseDescription.setSizeFull();
        courseDescription.addComponent(couseDescription);
        
        Component controlButtons = buildControlButtons(courseTitleField, couseDescriptionField);
        controlButtons.setSizeFull();
        courseDescription.addComponent(controlButtons);
        courseDescription.setComponentAlignment(controlButtons, Alignment.BOTTOM_CENTER);
        
        return courseDescription;
    }
    
    private Component buildControlButtons(TextField courseTitleField, TextField couseDescriptionField) {
        HorizontalLayout controlButtons = new HorizontalLayout();
        controlButtons.setMargin(true);
        controlButtons.setSpacing(true);
        
        Button saveCourse = new Button("Save");
        controlButtons.addComponent(saveCourse);
        controlButtons.setComponentAlignment(saveCourse, Alignment.BOTTOM_LEFT);
        saveCourse.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                //TODO: check why null at CourseDAO findAll()
                courseService.update(courseService.findCourseByTitle(course.getTitle()));
                tabSheet.getTab(tab).setCaption(courseTitleField.getValue());
                tabSheet.getTab(tab).setDescription(couseDescriptionField.getValue());
                close();
                Notification success = new Notification(
                        "Course is modified successfully");
                success.setDelayMsec(2000);
                success.setStyleName("bar success small");
                success.setPosition(Position.BOTTOM_CENTER);
                success.show(Page.getCurrent());
            }
            
        });
        
        Button deleteCourse = new Button("Delete course");
        controlButtons.addComponent(deleteCourse);
        controlButtons.setComponentAlignment(deleteCourse, Alignment.BOTTOM_RIGHT);
        deleteCourse.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            	courseService.deleteCourse(courseTitleField);
                /*tabSheet.removeComponent(tab);
                close();
                Notification success = new Notification(
                        "Course is deleted successfully");
                success.setDelayMsec(2000);
                success.setStyleName("bar success small");
                success.setPosition(Position.BOTTOM_CENTER);
                success.show(Page.getCurrent());*/
            }
            
        });
        
        return controlButtons;
    }
}