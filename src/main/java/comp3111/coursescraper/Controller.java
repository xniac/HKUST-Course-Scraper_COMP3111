package comp3111.coursescraper;





//import java.awt.event.ActionEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;

import java.util.Random;

import java.awt.Checkbox;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
public class Controller {

    @FXML
    private Tab tabMain;

    @FXML
    private TextField textfieldTerm;

    @FXML
    private TextField textfieldSubject;

    @FXML
    private Button buttonSearch;

    @FXML
    private TextField textfieldURL;

    @FXML
    private Tab tabStatistic;

    @FXML
    private ComboBox<?> comboboxTimeSlot;

    @FXML
    private Tab tabFilter;
    
//A series of Checkbox obj [Task2]
    @FXML
    private CheckBox CheckboxAM;

    @FXML
    private CheckBox CheckboxPM;

    @FXML
    private CheckBox CheckboxMon;

    @FXML
    private CheckBox CheckboxTue;

    @FXML
    private CheckBox CheckboxWed;

    @FXML
    private CheckBox CheckboxThu;

    @FXML
    private CheckBox CheckboxFri;

    @FXML
    private CheckBox CheckboxSat;

    @FXML
    private CheckBox CheckboxCC;

    @FXML
    private CheckBox CheckboxNoEx;

    @FXML
    private CheckBox CheckboxWithLabs;
    
    @FXML
    private Button BtnSelectAll;
    
    @FXML
    private TableView<Section> SectionTable;
    
    @FXML
    private TableColumn<Section, String> CourseCodeColumn;

    @FXML
    private TableColumn<Section, String> SectionColumn;

    @FXML
    private TableColumn<Section, String> CourseNameColumn;

    @FXML
    private TableColumn<Section, String> InstructorColumn;

    @FXML
    private TableColumn<Section,CheckBox> EnrollColumn;
    
 //[Modified by nxy]

    @FXML
    private Tab tabList;

    @FXML
    private Tab tabTimetable;

    @FXML
    private Tab tabAllSubject;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private TextField textfieldSfqUrl;

    @FXML
    private Button buttonSfqEnrollCourse;

    @FXML
    private Button buttonInstructorSfq;

    @FXML
    private TextArea textAreaConsole;
    
    private Scraper scraper = new Scraper();
    
    @FXML
    void allSubjectSearch() {
    	
    }

    @FXML
    void findInstructorSfq() {
    	buttonInstructorSfq.setDisable(true);
    }

    @FXML
    void findSfqEnrollCourse() {

    }
    
    
    
    
    private static List<Course> courses = new Vector<Course>();
    private static List<Course> filteredCourses = new Vector<Course>();
    private static List<Section> filteredSections = new Vector<Section>();
    public static List<Section> enrolledSections = new Vector<Section>();

    @FXML
    void search() {
    	courses = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),textfieldSubject.getText());
    	
    	// Handle 404
    	if (courses == null) {
    		textAreaConsole.setText("Oops! 404 Not Found! Please check your input.\n");
    		return;
    	}
    	
    	// count and display # of courses and sections
    	int number_of_sections = 0,
    		number_of_courses  = 0;
    	for (Course c : courses) {
    		number_of_sections += c.getNumSections();
    		number_of_courses  += (c.hasValidSection()) ? 1 : 0;
    	}
    	textAreaConsole.setText("Total Number of difference sections in this search: " + number_of_courses +
    			                "\nTotal Number of Course in this search: " + number_of_sections + "\n");
    	
    	// find and display a list of instrutors who have teaching assignment but not at Tu 3:10PM
    	List<String> instructors = new Vector<String>();
    	for (Course c : courses) {
    		for (int i = 0; i < c.getNumSections(); ++i) {
    			Section s = c.getSection(i);
    			for (int j = 0; j < s.getNumSlots(); ++j) {
    				Slot slot = s.getSlot(j);
    				if ((!slot.isOn(1) || !slot.include(15, 10)) && !instructors.contains(slot.getInstName()))
    					instructors.add(slot.getInstName());
    			}
    		}
    	}
    	instructors.sort(null);
    	String names = "Instructors who has teaching assignment this term but does not need to teach at Tu 3:10pm: ";
    	for (String name : instructors) {
    		names += (name + ", ");
    	}
    	names += "\n";
    	textAreaConsole.setText(textAreaConsole.getText() + names);
    	
    	for (Course c : courses) {
    		String newline = c.getTitle() + "\n";
    		int counter = 1;
    		for (int i = 0; i < c.getNumSections(); i++) {
    			Section s = c.getSection(i);
    			for (int j = 0; j < s.getNumSlots(); ++j) {
    				Slot t = s.getSlot(j);
    				newline += "Slot" + i + ": " + s.getSectionTitle() + "\t" + t + "\n";
    			}
    		}
    		textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
    	}
    	
    	//Add a random block on Saturday
    	AnchorPane ap = (AnchorPane)tabTimetable.getContent();
    	Label randomLabel = new Label("COMP1022\nL1");
    	Random r = new Random();
    	double start = (r.nextInt(10) + 1) * 20 + 40;

    	randomLabel.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    	randomLabel.setLayoutX(600.0);
    	randomLabel.setLayoutY(start);
    	randomLabel.setMinWidth(100.0);
    	randomLabel.setMaxWidth(100.0);
    	randomLabel.setMinHeight(60);
    	randomLabel.setMaxHeight(60);
    
    	ap.getChildren().addAll(randomLabel); 	
    }
    
    // update timetable accordingly after enrolled course list is updated
    public void updateTimetable() {
    	System.out.println("hello");
    	AnchorPane ap = (AnchorPane)tabTimetable.getContent();
    	ap.getChildren().remove(0, enrolledSections.size());
    	Label[] labels = new Label[enrolledSections.size()];
    	for (int i = 0; i < enrolledSections.size(); ++i) {
    		System.out.println(enrolledSections.get(i).getCourseCode());
    		Section s = enrolledSections.get(i);
    		String labelName = s.getParent().getSimplifiedTitle() + "\n" + s.getSimplifiedTitle();
    		for (int j = 0; j < s.getNumSlots(); ++j) {
    			Slot slot = s.getSlot(j);
    			Label label = new Label(labelName);
    			
    		}
    	}
    }
    
    
    //Task2
    @FXML
    public void SelectDeselectAll() {
    	final CheckBox[] ListAll = {CheckboxAM, CheckboxPM,CheckboxMon,CheckboxTue,CheckboxWed,CheckboxThu,CheckboxFri,
    			CheckboxSat,CheckboxCC,CheckboxNoEx,CheckboxWithLabs};
    	if(BtnSelectAll.getText().contentEquals("Select All"))
    		{BtnSelectAll.setText("De-select All");
    		 for (int i=0; i<ListAll.length;i++) {
    			ListAll[i].selectedProperty().set(true);
    		 }
    		}
    	
    	else if(BtnSelectAll.getText().contentEquals("De-select All"))
    		{BtnSelectAll.setText("Select All");
    		 for (int i=0; i<ListAll.length;i++) {
			 ListAll[i].selectedProperty().set(false);
		     }
    		}
    	refreshCheckBox();
    }
    
    
    public void RefreshFilter() {
    	
    }
    public void refreshCheckBox() {
    	Vector<boolean[]> flags=new Vector<boolean[]>();
    	// For every section in the courses list, create a boolean array
    	for (Course item: courses) {
    		boolean [] innerflags= {false,false,false,false,false,false,false,false,false,false,false,true};
    		if(item.is4YCC()) innerflags[8]=true;
    		if(!item.hasExclusion()) innerflags[9]=true;
    		if(item.hasLabOrTuto()) innerflags[10]=true;
    		for (int i=0;i<item.getNumSections();i++) {
    			for(int j=0;j<item.getSection(i).getNumSlots();j++) {
    				if(item.getSection(i).getSlot(j).isAM()) innerflags[0]=true;
    				if(item.getSection(i).getSlot(j).isPM()) innerflags[1]=true;
    				for (int temp=0;temp<6;temp++)
    				{
    					if(item.getSection(i).getSlot(j).isOn(temp)) innerflags[2+temp]=true;
    				}
    				
    			}
    		}
    		flags.add(innerflags);
    	}
    	
    	
    	
    	//check whether the section satisfy the CheckBox, store boolean value in item[11]
    	final CheckBox[] ListAll = {CheckboxAM, CheckboxPM,CheckboxMon,CheckboxTue,CheckboxWed,CheckboxThu,CheckboxFri,
    			CheckboxSat,CheckboxCC,CheckboxNoEx,CheckboxWithLabs};
    	for (int i=0;i<ListAll.length;i++) {
    		if(ListAll[i].selectedProperty().get()) {
    			for (boolean[] item: flags) {
    				if(item[i]==false) item[11]=false; 
    			}
    		}
    	}
    	
    	//store the filtered Sections in the array filteredCourses
    	filteredCourses.clear();
    	for(int i=0; i<courses.size();i++){
    			if (flags.get(i)[11]) filteredCourses.add(courses.get(i));
    	}
    	
    	//print all the filtered Sections
    	textAreaConsole.clear();
    	for (Course c : filteredCourses) {
    		String newline = c.getTitle() + "\n";
    		int counter = 1;
    		for (int i = 0; i < c.getNumSections(); i++) {
    			Section s = c.getSection(i);
    			for (int j = 0; j < s.getNumSlots(); ++j) {
    				Slot t = s.getSlot(j);
    				newline += "Slot" + i + ": " + s.getSectionTitle() + "\t" + t + "\n";
    			}
    		}
    		textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
    	}
    }
    
    public void createTable() {
    	filteredSections.clear();
    	for(Course item: filteredCourses) {
    		for(int p=0;p<item.getNumSections();p++) {
    			filteredSections.add(item.getSection(p));
    		}
    	}
    	
    	SectionTable.getItems().clear();
    	
    	for (Section item:filteredSections) {
    		
    		CourseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("CourseCode"));
    		SectionColumn.setCellValueFactory(new PropertyValueFactory<>("sectionName"));
        	CourseNameColumn.setCellValueFactory(new PropertyValueFactory<>("CourseName"));
        	InstructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructorList"));
        	
        	
        	
        	EnrollColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Section, CheckBox>, ObservableValue<CheckBox>>() {

                @Override
                public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Section, CheckBox> arg0) {
                    Section se = arg0.getValue();
                  
                    CheckBox checkBox = new CheckBox();

                    checkBox.selectedProperty().setValue(se.getEnrolled());



                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {

                            se.setEnrolled(new_val);
                            if(new_val) enrolledSections.add(se);
                            else if(!new_val && enrolledSections.contains(se)) enrolledSections.remove(se);
                            textAreaConsole.clear();
                        	textAreaConsole.setText("The following sections are enrolled:"+'\n');
                        	for (Section item: enrolledSections) {
                        		String newline = item.getCourseCode();
                        		newline = newline + '\t'+ item.getCourseName() + '\t' +item.getSectionTitle();
                        		
                        		textAreaConsole.setText(textAreaConsole.getText()+'\n'+ newline);
                        	}

                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }

            });

        	
        	SectionTable.getItems().add(item);
    	}
    }
 
    public void PrintonConsole() {
    	textAreaConsole.clear();
    	textAreaConsole.setText("The following sections are enrolled:"+'\n');
    	for (Section item: enrolledSections) {
    		String newline = item.getCourseCode();
    		newline += item.getCourseName();
    		textAreaConsole.setText(textAreaConsole.getText()+'\n'+ newline);
    	}
    }
}

