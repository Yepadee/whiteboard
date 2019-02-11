package org.microboard.whiteboard.controllers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.microboard.whiteboard.dto.assessment.NewSoloAssessment;
import org.microboard.whiteboard.dto.project.NewSoloProject;
import org.microboard.whiteboard.dto.user.MarkerUserDto;
import org.microboard.whiteboard.model.assessment.SoloAssessment;
import org.microboard.whiteboard.model.project.SoloProject;
import org.microboard.whiteboard.model.user.Assessor;
import org.microboard.whiteboard.model.user.Unit;
import org.microboard.whiteboard.model.user.UnitDirector;
import org.microboard.whiteboard.model.user.User;
import org.microboard.whiteboard.pojos.ProjectEditApplyer;
import org.microboard.whiteboard.pojos.ProjectTemplateMaker;
import org.microboard.whiteboard.services.project.GroupProjectService;
import org.microboard.whiteboard.services.project.ProjectService;
import org.microboard.whiteboard.services.project.SoloProjectService;
import org.microboard.whiteboard.services.user.AssessorService;
import org.microboard.whiteboard.services.user.UnitDirectorService;
import org.microboard.whiteboard.services.user.UnitService;
import org.microboard.whiteboard.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/unit_director")
public class UnitDirectorController {
	
	private Logger logger = LoggerFactory.getLogger(UnitDirectorController.class);
	
	private String newProjectForm = "unit_director/new_project";
	private String newSoloProjectPath = "unit_director/new_solo_project";
	private String editSoloProjectPath = "unit_director/edit_solo_project/";
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AssessorService assessorService;
	
	@Autowired
	private UnitDirectorService unitDirectorService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private SoloProjectService soloProjectService;
	
	@Autowired
	private GroupProjectService groupProjectService;
	
	private void createSoloProjectUploadFolders(SoloProject project) {
		String path = System.getProperty("user.dir") + "/uploads/";
		path += Long.toString(project.getUnit().getId())+"/";
		List<User> users = project.getUnit().getCohort();
		for (User user : users) {
			String userPath = path + user.getUserName() + "/";
			userPath += project.getName() + "/";
			for (SoloAssessment assessment : project.getAssessments()) {
				//System.out.println(userPath + assessment.getName() + "/");
				new File(userPath + assessment.getName() + "/feedback/").mkdirs();
			}
		}
	}
	
	@GetMapping("/edit_solo_project/{id}")
	public String editSoloProject(Model model, @PathVariable Long id) {
		SoloProject soloProject = soloProjectService.getProject(id).get();
		ProjectTemplateMaker templateMaker = new ProjectTemplateMaker();
		NewSoloProject editProject = templateMaker.getTemplate(soloProject);
		model.addAttribute("newSoloProject", editProject);
		model.addAttribute("path", editSoloProjectPath + id);
		return newSoloProjectPath;
	}
	
	@GetMapping("/new_solo_project")
	public String getNewSoloProjectPage(Model model) {
		NewSoloProject project = new NewSoloProject();
		model.addAttribute("newSoloProject", project);
		//model.addAttribute("cohort", new ArrayList<UserDto>());
		return newSoloProjectPath;
	}

	@PostMapping(value="/new_solo_project", params={"setUnit"})
	public String setUnit(Model model, NewSoloProject project) {
		long unitId = project.getUnit().getId();
		Unit unit = unitService.getUnit(unitId).get();
		project.setUnit(unit);
		for (NewSoloAssessment assessment : project.getAssessments()) {
			for (MarkerUserDto markerDto : assessment.getMarkerDtos()) {
				markerDto.setToMark(new ArrayList<>());
			}
		}
	    return newSoloProjectPath;
	}
	
	@PostMapping(value="/new_solo_project", params={"addAssessment"})
	public String addAssessment(Model model, NewSoloProject project) {
		project.getAssessments().add(new NewSoloAssessment());
	    return newSoloProjectPath;
	}
	
	@PostMapping(value="/new_solo_project", params={"removeAssessment"})
	public String removeAssessment(Model model, NewSoloProject project, @RequestParam("removeAssessment") int index) {
		project.getAssessments().remove(index);
	    return newSoloProjectPath;
	}
	
	@PostMapping(value="/new_solo_project", params={"addMarker"})
	public String addMarker(Model model, NewSoloProject project, @RequestParam("addMarker") int addMarker) {
		int assessmentIndex = addMarker;
		project.getAssessments().get(assessmentIndex).getMarkerDtos().add(new MarkerUserDto());
	    return newSoloProjectPath;
	}

	@PostMapping(value="/new_solo_project", params={"removeMarker"})
	public String addMarker(Model model, NewSoloProject project, @RequestParam("removeMarker") List<Integer> removeMarker) {
		int assessmentIndex = removeMarker.get(0);
		int markerIndex = removeMarker.get(1);
		project.getAssessments().get(assessmentIndex).getMarkerDtos().remove(markerIndex);
	    return newSoloProjectPath;
	}
	
	@PostMapping(value="/new_solo_project", params= {"addProject"})
	public String addProject(Model model, NewSoloProject project) {
		if (! project.validate()) {
			model.addAttribute("error", project.getErrorMsg());
		} else {
			ProjectEditApplyer editApplyer = new ProjectEditApplyer();
			
			SoloProject newProject = new SoloProject();
			SoloProject soloProject = editApplyer.applyEdits(newProject, project);
			
			UnitDirector creator = unitDirectorService.getLoggedInUser();
			creator.addProject(soloProject);
			
			createSoloProjectUploadFolders(soloProject);
			
			
			projectService.addProject(soloProject);
			unitDirectorService.updateUser(creator); 
		}
		return newSoloProjectPath;
		
	}
	
	@PostMapping(value="/new_solo_project", params= {"editProject"})
	public String editProject(Model model, NewSoloProject edits) {
		if (! edits.validate()) {
			model.addAttribute("error", edits.getErrorMsg());
		} else {
			ProjectEditApplyer editApplyer = new ProjectEditApplyer();
			
			SoloProject project = soloProjectService.getProject(edits.getId()).get();
			SoloProject soloProject = editApplyer.applyEdits(project, edits);
			
			UnitDirector creator = unitDirectorService.getLoggedInUser();
			for (User u : project.getUnit().getCohort()) {
				userService.updateUser(u);
			}
			projectService.addProject(soloProject);
			unitDirectorService.updateUser(creator); 
		}
		return "redirect:/unit_director/edit_solo_project/" + edits.getId();
	}
	
	@GetMapping("/projects")
	public String viewProjectsPage(Model model) {
		UnitDirector unitDirector = unitDirectorService.getLoggedInUser();
		model.addAttribute("soloProjects", soloProjectService.getByCreator(unitDirector));
		model.addAttribute("groupProjects", groupProjectService.getByCreator(unitDirector));
		return "unit_director/view_projects";
	}
	
	@GetMapping("/manage_permissions")
	public String managePermissionsPage() {
		return "manage_permissions";
	}
	
	
	@ModelAttribute("user")
	public UnitDirector getUnitDirector() {
	   return unitDirectorService.getLoggedInUser();
	}
	
	@ModelAttribute("unitList")
	public List<Unit> getUnitList() {
	   return unitService.getAllUnits();
	}
	
	@ModelAttribute("assessorList")
	public List<Assessor> getAssessorList() {
	   return assessorService.getAllUsers();
	}
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");   
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, null,  new CustomDateEditor(dateFormat, true));
	}
	
}
