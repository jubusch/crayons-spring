package com.crayons_2_0.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.maddon.ListContainer;

import com.crayons_2_0.service.CourseDisplay;
import com.crayons_2_0.service.database.CourseService;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringView(name = Search.VIEW_NAME)
@ViewScope
@SpringComponent
public final class Search extends VerticalLayout implements View {


    List<CourseDisplay> collection = new ArrayList<CourseDisplay>();
    public static final String VIEW_NAME = "Search";
    private Table table;
    private Button createSearch;
    
    @Autowired
    CourseService courseService;
    
    @PostConstruct
    public void init() {
        setSizeFull();
        addStyleName("courseDisplay");
        addComponent(buildToolbar());

        table = buildTable();
        addComponent(table);
        setExpandRatio(table, 1);
    }
    
    
    public Search() {

    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        createSearch = buildSearch();
        Component createFilter = buildFilter();
        HorizontalLayout tools = new HorizontalLayout(createFilter,
                createSearch);
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Button buildSearch() {
        final Button search = new Button("Search");
        search.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
		        collection.removeAll(collection);
		        collection.addAll(courseService.searchAll("algo"));
		        table.setContainerDataSource(new TempContainer(collection));
			}

        });
        return search;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        return filter;
    }

    @SuppressWarnings("deprecation")
	private Table buildTable() {
        final Table table = new Table();
        table.setSizeFull();
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        table.addStyleName(ValoTheme.TABLE_COMPACT);

        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        
        CourseDisplay trans = new CourseDisplay(null, "", "", "");
        collection.add(trans);
        table.setContainerDataSource(new TempContainer(collection));
        table.setSortContainerPropertyId("Release");
        table.setSortAscending(false);

        table.setVisibleColumns("release", "title", "author", "status");
        table.setColumnHeaders("Release", "Title", "Author", "Status");
        table.addListener(new ItemClickListener(){
			@Override
			public void itemClick(ItemClickEvent event) {
                Notification fail = new Notification("Course doesn't exist");
                fail.setDelayMsec(2000);
                fail.setStyleName("bar fail small");
                fail.setPosition(Position.BOTTOM_CENTER);
                fail.show(Page.getCurrent());
			}
        	
        });
        return table;
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

    private class TempContainer extends
            ListContainer<CourseDisplay> {

        public TempContainer(final Collection<CourseDisplay> collection) {
            super(collection);
        }

    }

}