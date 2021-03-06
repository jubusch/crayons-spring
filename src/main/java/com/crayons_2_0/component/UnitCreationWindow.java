package com.crayons_2_0.component;

import java.util.ResourceBundle;

import com.crayons_2_0.model.graph.Graph;
import com.crayons_2_0.model.graph.UnitNode;
import com.crayons_2_0.service.LanguageService;
import com.crayons_2_0.view.CourseEditorView;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings({ "serial" })
public final class UnitCreationWindow extends Window {

	// @DB

	private UnitNode parent;
	private String unitTitle;
	private UnitNode child;
	private static Graph graph;

	private ResourceBundle lang = LanguageService.getInstance().getRes();

	public UnitCreationWindow(Graph graphData) {
		setSizeFull();
		setModal(true);
		setResizable(false);
		setClosable(true);
		setHeight(50.0f, Unit.PERCENTAGE);
		setWidth(40.0f, Unit.PERCENTAGE);

		graph = graphData;

		VerticalLayout content = new VerticalLayout();
		content.setSizeFull();
		content.setMargin(true);
		setContent(content);

		Component title = buildTitle();
		content.addComponent(title);
		content.setComponentAlignment(title, Alignment.TOP_CENTER);

		TextField titleField = new TextField(lang.getString("UnitTitle"));
		titleField.setCaption(lang.getString("UnitTitle"));
		titleField.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				unitTitle = titleField.getValue();
			}
		});

		content.addComponent(titleField);

		content.setComponentAlignment(titleField, Alignment.MIDDLE_LEFT);

		/*
		 * Component unitTypeChoice = buildUnitTypeChoice();
		 * content.addComponent(unitTypeChoice);
		 * content.setComponentAlignment(unitTypeChoice, Alignment.MIDDLE_LEFT);
		 */

		Component connectedUnitsChoice = buildConnectedUnitsChoice();
		content.addComponent(connectedUnitsChoice);
		content.setComponentAlignment(connectedUnitsChoice,
				Alignment.MIDDLE_LEFT);

		Component footer = buildFooter();
		content.addComponent(footer);
		content.setComponentAlignment(footer, Alignment.BOTTOM_CENTER);
	}

	private Component buildConnectedUnitsChoice() {
		HorizontalLayout comboBoxes = new HorizontalLayout();
		comboBoxes.setMargin(true);
		comboBoxes.setSpacing(true);

		ComboBox selectPredecessor = new ComboBox(
				lang.getString("SelectThePreviousUnit"));
		selectPredecessor.setNullSelectionAllowed(false);
		comboBoxes.addComponent(selectPredecessor);
		// Set<Node> predecessors = new HashSet<Node>();
		// predecessors.add(new Node("Node 1"));
		// predecessors.add(new Node("Node 2"));
		// selectPredecessor.addItems(predecessors);
		for (UnitNode currentNode : graph.getUnitCollection()) {
			if (currentNode.getUnitNodeTitle() != "End")
				selectPredecessor.addItem(currentNode.getUnitNodeTitle());
		}
		selectPredecessor.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				parent = graph.getNodeByName(selectPredecessor.getValue()
						.toString());
			}
		});

		ComboBox selectSuccessor = new ComboBox(
				lang.getString("SelectTheNextUnit"));
		selectSuccessor.setNullSelectionAllowed(false);
		comboBoxes.addComponent(selectSuccessor);
		// Set<Node> successors = new HashSet<Node>();
		// successors.add(new Node("Node 3"));
		// successors.add(new Node("Node 4"));
		// selectSuccessor.addItems(successors);

		for (UnitNode currentNode : graph.getUnitCollection()) {
			selectSuccessor.addItem(currentNode.getUnitNodeTitle());
		}
		selectSuccessor.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				child = graph.getNodeByName(selectSuccessor.getValue()
						.toString());
			}
		});

		return comboBoxes;
	}

	public static void refreshData(Graph graphTmp) {
		graph = graphTmp;
	}

	private Component buildTitle() {
		Label title = new Label(lang.getString("CreateANewUnit"));
		title.addStyleName(ValoTheme.LABEL_H2);
		return title;
	}

	/*
	 * private Component buildUnitTypeChoice() { VerticalLayout unitTypeChoice =
	 * new VerticalLayout(); CheckBox learningUnit = new
	 * CheckBox(UnitType.LEARNING_UNIT.getTitle()); CheckBox testUnit = new
	 * CheckBox(UnitType.TEST_UNIT.getTitle());
	 * 
	 * learningUnit.addValueChangeListener(event -> // Java 8
	 * testUnit.setValue(!learningUnit.getValue()));
	 * 
	 * testUnit.addValueChangeListener(event -> // Java 8
	 * learningUnit.setValue(!testUnit.getValue()));
	 * 
	 * unitTypeChoice.addComponents(learningUnit, testUnit); return
	 * unitTypeChoice; }
	 */

	private Component buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
		footer.setSpacing(true);

		Button ok = new Button(lang.getString("Create"));
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (unitTitle == null || unitTitle.equals("")) {
					Notification fail = new Notification(lang
							.getString("UnitCreatedfailure"));
					fail.setDelayMsec(1000);
					fail.setStyleName("bar fail small");
					fail.setPosition(Position.BOTTOM_CENTER);
					fail.show(Page.getCurrent());

				} else if (parent == null || child == null) {
					Notification fail = new Notification(lang
							.getString("UnitCreatedfailure2"));
					fail.setDelayMsec(1000);
					fail.setStyleName("bar fail small");
					fail.setPosition(Position.BOTTOM_CENTER);
					fail.show(Page.getCurrent());

				} else {
					UnitNode newUnit = new UnitNode(unitTitle, parent, child,
							graph);
					graph.addUnit(newUnit, parent, child);
					CourseEditorView.refreshGraph(graph);
					close();
					Notification success = new Notification(lang
							.getString("UnitCreatedSuccessfully"));
					success.setDelayMsec(500);
					success.setStyleName("bar success small");
					success.setPosition(Position.BOTTOM_CENTER);
					Page.getCurrent().reload();

				}

			}
		});
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_CENTER);
		return footer;
	}
}
