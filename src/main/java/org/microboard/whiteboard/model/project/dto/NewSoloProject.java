package org.microboard.whiteboard.model.project.dto;

import java.util.ArrayList;
import java.util.List;

import org.microboard.whiteboard.model.user.Unit;

public class NewSoloProject {
	private String name;
	private String description;
	private List<NewSoloAssessment> assessments = new ArrayList<>();
	private Unit unit;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<NewSoloAssessment> getAssessments() {
		return assessments;
	}
	public void setAssessments(List<NewSoloAssessment> assessments) {
		this.assessments = assessments;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
}