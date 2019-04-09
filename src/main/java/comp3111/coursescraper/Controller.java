package comp3111.coursescraper;


import java.awt.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.scene.text.Font;

import java.util.Random;
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
    private static List<Section> enrolledSections = new Vector<Section>();

    @FXML
    /*
     *  Task 1: Search function for button "Search".
     */
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
    	
    	// display details of each course
    	for (Course c : courses) {
    		String newline = c.getTitle() + "\n";
    		int counter = 0;
    		for (int i = 0; i < c.getNumSections(); i++) {
    			Section s = c.getSection(i);
    			for (int j = 0; j < s.getNumSlots(); ++j) {
    				Slot t = s.getSlot(j);
    				newline += "Slot" + " " + (counter++) + ": " + s.getSectionTitle() + "\t" + t + "\n";
    			}
    		}
    		textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
    	}
    }
    
    /*
     *  Task 4: Update the timetable whenever the enrolled sections list is updated.
     */
    private static List<Label> labels = new Vector<Label>();
    private static int[] RGB = new int[3];
    // set random initial values for RGB
    static {
    	Random r = new Random();
    	for (int i = 0; i < 3; ++i) RGB[i] = r.nextInt(256);
    }
    /**
     *  update the RGB array, set a different value to each
     */
    private static void updateRGB() {
    	int[] increase = {47,73,107};
    	for (int i = 0; i < 3; ++i) 
    		RGB[i] = (RGB[i] + increase[i]) % 256;
    }
    public void updateTimetable() {
    	// first remove all existing labels
    	AnchorPane ap = (AnchorPane)tabTimetable.getContent();
    	ap.getChildren().removeAll(labels);
    	labels.clear();
    	
    	// then create new labels for each section
    	for (int i = 0; i < enrolledSections.size(); ++i) {
    		Section s = enrolledSections.get(i);
    		
    		// prepare the label name and the color, as these are shared by slots from the same section
    		String labelName = s.getParent().getSimplifiedTitle() + "\n" + s.getSimplifiedTitle();
    		updateRGB();
    		
    		// create a label for each slot, set attributes
    		for (int j = 0; j < s.getNumSlots(); ++j) {
    			Slot slot = s.getSlot(j);
    			Label label = new Label(labelName);
    			//label.setFont(new Font(7));
    			label.setBackground(new Background(new BackgroundFill(Color.rgb(RGB[0], RGB[1], RGB[2], 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
    	    	label.setLayoutX((slot.getDay()+1)*100);
    	    	label.setLayoutY((float)20*(slot.getStart().getHour()-9+(float)slot.getStart().getMinute()/60)+40);
    			label.setMinWidth(100.0);
    	    	label.setMaxWidth(100.0);
    	    	label.setMinHeight((float)20*((slot.getEnd().getHour()+(float)slot.getEnd().getMinute()/60)-
    	    									  (slot.getStart().getHour()+(float)slot.getStart().getMinute()/60)));
    	    	label.setMaxHeight((float)20*((slot.getEnd().getHour()+(float)slot.getEnd().getMinute()/60)-
						  		   (slot.getStart().getHour()+(float)slot.getStart().getMinute()/60)));
    			
    	    	// after finishing, add this label to the label list
    			labels.add(label);
    		}
    	}
    	
    	// add all labels from the label list to the anchor pane
    	ap.getChildren().addAll(labels);
    }
}