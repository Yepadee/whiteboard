package org.microboard.whiteboard.service_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.microboard.whiteboard.model.user.UnitDirector;
import org.microboard.whiteboard.repositories.user.UnitDirectorRepository;
import org.microboard.whiteboard.services.user.UnitDirectorService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnitDirectorServiceTest {

	@Mock
	private UnitDirectorRepository unitDirectorRepository;
	
	private UnitDirectorService unitDirectorService;
	
	@Before
	public void setUp() {
		this.unitDirectorService = new UnitDirectorService(unitDirectorRepository);
	}
	
	@Test
	public void getUser_returnsUnitDirector() {
		UnitDirector mockUnitDirector = new UnitDirector();
		mockUnitDirector.setUserName("unitDirector");
		
		given(unitDirectorRepository.findByUserName("unitDirector")).willReturn(Optional.ofNullable(mockUnitDirector));
		
		Optional<UnitDirector> maybeUnitDirector = unitDirectorService.getByUserName("unitDirector");
		
		assertTrue(maybeUnitDirector.isPresent());
		assertThat(maybeUnitDirector.get().getUserName()).isEqualTo("unitDirector");
	}
	
	
	/** TODO:
	 * Test getting projects and all their components
	 * Test creating a project
	 */

}
