package com.crayons_2_0.view;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.crayons.view.dagred3.Dagre;
import com.crayons_2_0.MyUI;
import com.crayons_2_0.authentication.CurrentCourses;
import com.crayons_2_0.component.DeleteVerification;
import com.crayons_2_0.component.UnitConnectionEditor;
import com.crayons_2_0.component.UnitCreationWindow;
import com.crayons_2_0.model.graph.Graph;
import com.crayons_2_0.model.graph.UnitNode;
import com.crayons_2_0.service.LanguageService;
import com.crayons_2_0.service.database.CourseService;
import com.crayons_2_0.service.database.UnitService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Course editor view consists of a learning graph and a set of buttons for it's
 * modification. It allows an author to modify the course by adding/removing
 * learning units and changing the dependencies between them and open unit
 * editor to modify a unit.
 *
 */
@SuppressWarnings("serial")
@SpringUI
@ViewScope
@SpringComponent
public class CourseEditorView extends VerticalLayout implements View {

	private @Autowired CourseService courseService;

	private @Autowired UnitService unitService;

	private static Graph graphData;

	public static final String VIEW_NAME = "Learning Graph";
	private final static Dagre graph = new Dagre();

	private static ResourceBundle lang = LanguageService.getInstance().getRes();
	private static ComboBox selectUnit = new ComboBox();

	@PostConstruct
	void init() {
		setSizeFull();
		VerticalLayout graphLayout = new VerticalLayout();
		graphLayout.setSizeFull();
		graphLayout.setHeight("1000");
		graphData = courseService.getDummyGraph();
		graph.setGraph(graphData.getNodeNameList(), graphData.getEdgeSequence());
		graph.setSizeFull();
		try {
			graphLayout.addComponent(graph);
		} catch (RuntimeException e) {
			getUI().getConnectorTracker().markAllConnectorsDirty();
			getUI().getConnectorTracker().markAllClientSidesUninitialized();
			MyUI.getCurrent().getPage().reload();
			System.out.println("test");
		}
		// addComponent( );
		addComponent(graphLayout);

		Component footer = buildFooter();
		addComponent(footer);
		footer.setSizeUndefined();
		setComponentAlignment(footer, Alignment.BOTTOM_CENTER);
		setSpacing(false);

	}

	public Dagre getGraph() {
		return graph;
	}

	public ResourceBundle getLang() {
		return lang;
	}

	/**
	 * general refresher
	 * 
	 * @param graphTmp
	 */
	public static void refreshGraph(Graph graphTmp) {

		for (String tmp : graphTmp.getNodeNameList()) {
			if (!tmp.equals("Start") && !tmp.equals("End")) {
				selectUnit.addItem(tmp);
			}
		}
		graph.setGraph(graphTmp.getNodeNameList(), graphTmp.getEdgeSequence());
		graphData = graphTmp;
	}

	/**
	 * Builds a footer which includes primary control buttons for the editor and
	 * the graph.
	 * 
	 * @return the footer
	 */
	private Component buildFooter() {
		CssLayout footer = new CssLayout();
		footer.addStyleName("courseeditor-footer");
		footer.setSizeFull();
		Component controlButtons = buildControlButtons();
		footer.addComponent(controlButtons);
		controlButtons.addStyleName("courseeditor-footer-left");
		Component editMenu = buildEditMenu();
		editMenu.addStyleName("courseeditor-footer-right");
		footer.addComponent(editMenu);
		return footer;
	}

	/**
	 * Builds control buttons which allows user to save the course and to return
	 * to the author library.
	 * 
	 * @return layout wit the control buttons
	 */
	private Component buildControlButtons() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		Button backButton = new Button(lang.getString("Back"),
				FontAwesome.ARROW_LEFT);
		layout.addComponent(backButton);
		backButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().addWindow(new UnsavedChangesWindow());
			}

		});
		Button save = new Button(lang.getString("Save"));
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		layout.addComponent(save);
		save.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				courseService.saveCourseData(graphData, CurrentCourses
						.getInstance().getTitle());
				Notification success = new Notification(lang
                        .getString("GraphSavedSuccessfully"));
                success.setDelayMsec(4000);
                success.setStyleName("barSuccessSmall");
                success.setPosition(Position.BOTTOM_CENTER);
                success.show(Page.getCurrent());
			}
		});
		return layout;
	}

	/**
	 * Builds a layout with control buttons for the graph
	 * 
	 * @return layout with control buttons
	 * 
	 */
	private class EditUnitWindow extends Window {
		/**
		 * Builds together several components of the window.
		 */
		public EditUnitWindow() {
			setSizeFull();
			setModal(true);
			setResizable(false);
			setClosable(true);
			setHeight(30.0f, Unit.PERCENTAGE);
			setWidth(35.0f, Unit.PERCENTAGE);

			VerticalLayout content = new VerticalLayout();
			content.setSizeFull();
			content.setMargin(true);
			setContent(content);
			selectUnit = new ComboBox();
			selectUnit.setCaption(lang.getString("PleaseSelectAUnit"));
			for (UnitNode tmp : graphData.getUnitCollection()) {
				if (!tmp.getUnitNodeTitle().equals("Start")
						&& !tmp.getUnitNodeTitle().equals("End")) {
					selectUnit.addItem(tmp.getUnitNodeTitle());
				}
			}
			selectUnit.setNullSelectionAllowed(false);
			Component title = buildTitle();
			content.addComponent(title);
			content.addComponent(selectUnit);
			content.setComponentAlignment(title, Alignment.TOP_CENTER);

			Component footer = buildFooter();
			content.addComponent(footer);
			content.setComponentAlignment(footer, Alignment.BOTTOM_CENTER);
			content.setExpandRatio(footer, 1);
		}

		/**
		 * Builds a footer which includes a modify button.
		 * 
		 * @return the footer component which will be placed on the bottom of
		 *         the window
		 */
		private Component buildFooter() {
			HorizontalLayout layout = new HorizontalLayout();
			layout.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
			layout.setWidth(100.0f, Unit.PERCENTAGE);
			layout.setSpacing(true);
			Button modify = new Button(lang.getString("OpenUnitEditor"));
			modify.addStyleName(ValoTheme.BUTTON_PRIMARY);
			modify.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (selectUnit.getValue() == null) {

					} else {
						CurrentCourses.getInstance().setUnitTitle(
								CurrentCourses.getInstance().getTitle() + "#"
										+ (String) selectUnit.getValue());
						unitService.newUnit();
						UI.getCurrent().getNavigator()
								.navigateTo(Uniteditor.VIEW_NAME);
					}
					close();
					selectUnit.removeAllItems();

				}
			});
			layout.addComponent(modify);
			modify.focus();
			layout.setSpacing(true);
			layout.setComponentAlignment(modify, Alignment.BOTTOM_CENTER);
			return layout;
		}

		/**
		 * Builds a title.
		 * 
		 * @return title of the window
		 */
		private Component buildTitle() {
			Label title = new Label(lang.getString("ModifyUnit"));
			title.addStyleName(ValoTheme.LABEL_H2);
			return title;
		}
	}

	private Component buildEditMenu() {
		HorizontalLayout editMenuLayout = new HorizontalLayout();
		editMenuLayout.setSpacing(true);
		editMenuLayout.setWidthUndefined();
		/*
		 * selectUnit = new ComboBox(); for (UnitNode tmp :
		 * graphData.getUnitCollection()) { if
		 * (!tmp.getUnitNodeTitle().equals("Start") &&
		 * !tmp.getUnitNodeTitle().equals("End")) {
		 * selectUnit.addItem(tmp.getUnitNodeTitle()); } }
		 * selectUnit.setNullSelectionAllowed(false);
		 */
		// editMenuLayout.addComponent(selectUnit);
		// create buttons with refresh data
		Button unitCreationButton = new Button(
				EditMenuButtonType.ADD_UNIT.getTitle(),
				EditMenuButtonType.ADD_UNIT.getIcon());
		editMenuLayout.addComponent(unitCreationButton);
		unitCreationButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				UnitCreationWindow.refreshData(graphData);
				UI.getCurrent().addWindow(new UnitCreationWindow(graphData));
			}
		});
		Button edit = new Button(EditMenuButtonType.EDIT_UNIT.getTitle(),
				EditMenuButtonType.EDIT_UNIT.getIcon());
		editMenuLayout.addComponent(edit);
		edit.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().addWindow(new EditUnitWindow());
			}
		});

		Button unitConnection = new Button(
				EditMenuButtonType.CONNECT_UNITS.getTitle(),
				EditMenuButtonType.CONNECT_UNITS.getIcon());
		editMenuLayout.addComponent(unitConnection);
		unitConnection.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				UnitConnectionEditor.refreshData(graphData);
				UI.getCurrent().addWindow(new UnitConnectionEditor(graphData));
			}
		});

		Button deleteUnit = new Button(
				EditMenuButtonType.DELETE_UNIT.getTitle(),
				EditMenuButtonType.DELETE_UNIT.getIcon());
		editMenuLayout.addComponent(deleteUnit);
		deleteUnit.addStyleName(ValoTheme.BUTTON_DANGER);
		deleteUnit.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
			    
				DeleteVerification.refreshData(graphData);
				UI.getCurrent().addWindow(new DeleteVerification(graphData));
			}
		});

		return editMenuLayout;
	}

	/**
	 * Defines title and icon for the edit menu buttons.
	 */
	public enum EditMenuButtonType {
		ADD_UNIT(lang.getString("AddUnit"), FontAwesome.PLUS), CONNECT_UNITS(
				lang.getString("ModifyConnections"), FontAwesome.LINK), DELETE_UNIT(
				lang.getString("DeleteUnit"), FontAwesome.TRASH), EDIT_UNIT(
				lang.getString("SelectUnit"), FontAwesome.PENCIL);

		private final String title;
		private final FontAwesome icon;

		EditMenuButtonType(final String title, final FontAwesome icon) {
			this.title = title;
			this.icon = icon;
		}

		public String getTitle() {
			return title;
		}

		public FontAwesome getIcon() {
			return icon;
		}
	}

	/**
	 * Dialog window which checks if the changes in the learning unit should be
	 * saved or not. Is called by a click on the back button.
	 */
	public class UnsavedChangesWindow extends Window {
		/**
		 * Builds together several components of the window.
		 */
		public UnsavedChangesWindow() {
			setSizeFull();
			setModal(true);
			setResizable(false);
			setClosable(false);
			setHeight(20.0f, Unit.PERCENTAGE);
			setWidth(40.0f, Unit.PERCENTAGE);

			VerticalLayout content = new VerticalLayout();
			content.setSizeFull();
			content.setMargin(true);
			setContent(content);

			Component title = buildTitle();
			title.setSizeFull();
			content.addComponent(title);
			content.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
			content.setExpandRatio(title, 2);

			Component footer = buildFooter();
			content.addComponent(footer);
			content.setComponentAlignment(footer, Alignment.BOTTOM_CENTER);
			content.setExpandRatio(footer, 1);
		}

		/**
		 * Builds a footer which includes yes, no and cancel buttons.
		 * 
		 * @return the footer component which will be placed on the bottom of
		 *         the window
		 */
		private Component buildFooter() {
			Button yesButton = new Button(lang.getString("Yes"));
			yesButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			yesButton.focus();
			yesButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					close();

					UI.getCurrent().getNavigator()
							.navigateTo(Authorlibrary.VIEW_NAME);
				}
			});

			Button noButton = new Button(lang.getString("No"));
			noButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					close();
					// TODO: discard changes
					UI.getCurrent().getNavigator()
							.navigateTo(Authorlibrary.VIEW_NAME);
				}
			});

			Button cancelButton = new Button(lang.getString("Cancel"));

			cancelButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					close();
				}
			});

			HorizontalLayout layout = new HorizontalLayout(yesButton, noButton,
					cancelButton);
			layout.setSpacing(true);
			return layout;
		}

		/**
		 * Builds a title.
		 * 
		 * @return title of the window
		 */
		private Component buildTitle() {
			Label title = new Label(lang.getString("GraphModified"));
			title.addStyleName(ValoTheme.LABEL_H3);
			HorizontalLayout layout = new HorizontalLayout(title);
			layout.setSizeUndefined();
			layout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
			return layout;
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}
}
