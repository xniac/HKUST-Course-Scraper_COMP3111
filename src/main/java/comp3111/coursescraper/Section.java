package comp3111.coursescraper;

public class Section {
	private static final int DEFAULT_MAX_SLOT = 2;
	
	private String sectionTitle;
	private Slot [] slots;
	private int numSlots;
	private Course parent;
	
	public Section() {
		slots = new Slot[DEFAULT_MAX_SLOT];
		for (int i = 0; i < DEFAULT_MAX_SLOT; ++i) slots[i] = null;
		numSlots = 0;
		sectionTitle = "";
		parent = null;
	}
	
	
	/*
	 *  Title
	 */
	/**
	 * @param title the title to set
	 */
	public void setSectionTitle(String title) { sectionTitle = title; }
	/**
	 * @return the section title
	 */
	public String getSectionTitle() { return sectionTitle; }
	/**
	 * @return the simplified section title (no absolute id) (i.e. Type + Digit) (e.g. "L1")
	 */
	public String getSimplifiedTitle() {
		String [] arr = sectionTitle.split(" ");
		return arr[0];
	}
	
	
	/*
	 *  Parent
	 */
	/**
	 * @param c the parent course to set
	 */
	public void setParent(Course c) { parent = c; }
	/**
	 * @return the parent course
	 */
	public Course getParent() { return parent; }
	
	
	/*
	 *  Slots
	 */
	/**
	 * @return the number of slots
	 */
	public int getNumSlots() { return numSlots; }
	/**
	 * @param num the number of slots to set
	 */
	public void setNumSlots(int num) { numSlots = num; }
	/**
	 * @param i the index of the slot
	 * @return the slot specified by the index
	 */
	public Slot getSlot(int i) { return (i >= 0 && i < numSlots) ? slots[i] : null; }
	/**
	 * @param s the slot to add
	 */
	public void addSlot(Slot s) {
		if (numSlots >= DEFAULT_MAX_SLOT) 
			return;
		slots[numSlots++] = s;
		s.setParent(this);
	}
	
	
	/*
	 *  AM, PM and Weekday Boolean Test
	 */
	/**
	 * @return whether a section has slot(s) in AM
	 */
	public boolean hasAM() {
		for (int i = 0; i < numSlots; ++i) {
			if (slots[i].isAM())
				return true;
		}
		return false;
	}
	/**
	 * @return whether a section has slot(s) in PM
	 */
	public boolean hasPM() {
		for (int i = 0; i < numSlots; ++i) {
			if (slots[i].isPM())
				return true;
		}
		return false;
	}
	/**
	 * @param day the weekday, from 0 to 6
	 * @return whether a section has slot(s) on a specific weekday
	 */
	public boolean hasDay(int day) {
		for (int i = 0; i < numSlots; ++i) {
			if (slots[i].isOn(day))
				return true;
		}
		return false;
	}
	
	
	/*
	 *  Section Type Boolean Test
	 */
	/**
	 * @return whether a section is a lab or a tutorial
	 */
	public boolean isLabOrTuto() {
		if (sectionTitle.charAt(0) == 'T') {
			return true;
		} else if (sectionTitle.charAt(0) == 'L' && sectionTitle.charAt(1) == 'A') {
			return true;
		}
		return false;
	}
}
