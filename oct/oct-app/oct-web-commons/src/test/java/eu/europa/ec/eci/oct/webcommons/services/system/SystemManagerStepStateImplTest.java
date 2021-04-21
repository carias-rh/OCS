package eu.europa.ec.eci.oct.webcommons.services.system;

import org.junit.Test;

import eu.europa.ec.eci.oct.entities.admin.StepState;
import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

public class SystemManagerStepStateImplTest extends ServicesTest {

	
	@Test
	public void testDefaultInitialisationOfStepState() throws OCTException{
		StepState stepState = systemManager.getStepState();
		assertNotNull(stepState);
	}
	
	@Test
	public void testSetStructure() throws OCTException{
		systemManager.setStructureAsDone(true);

		StepState stepState = systemManager.getStepState();
		assertNotNull(stepState);
		assertTrue(stepState.getStructure());
		assertFalse(stepState.getPersonalise());
		assertFalse(stepState.getSocial());
		assertFalse(stepState.getLive());
	}
	
	
	@Test
	public void testSetPersonalise() throws OCTException{
		systemManager.setPersonaliseAsDone(true);

		StepState stepState = systemManager.getStepState();
		assertNotNull(stepState);
		assertFalse(stepState.getStructure());
		assertTrue(stepState.getPersonalise());
		assertFalse(stepState.getSocial());
		assertFalse(stepState.getLive());
	}


	@Test
	public void testSetSocialAsDone() throws OCTException{
		systemManager.setSocialAsDone(true);

		StepState stepState = systemManager.getStepState();
		assertNotNull(stepState);
		assertFalse(stepState.getStructure());
		assertFalse(stepState.getPersonalise());
		assertTrue(stepState.getSocial());
		assertFalse(stepState.getLive());
	}

	@Test
	public void testSetLive() throws OCTException{
		systemManager.setGoneLive();

		StepState stepState = systemManager.getStepState();
		assertNotNull(stepState);
		assertFalse(stepState.getStructure());
		assertFalse(stepState.getPersonalise());
		assertFalse(stepState.getSocial());
		assertTrue(stepState.getLive());
	}
	
	@Test
	public void testSetAllTrue() throws OCTException{
		systemManager.setStructureAsDone(true);
		systemManager.setPersonaliseAsDone(true);
		systemManager.setSocialAsDone(true);
		systemManager.setGoneLive();

		StepState stepState = systemManager.getStepState();
		assertNotNull(stepState);
		assertTrue(stepState.getStructure());
		assertTrue(stepState.getPersonalise());
		assertTrue(stepState.getSocial());
		assertTrue(stepState.getLive());
	}

	@Test
	public void testSetAllFalse() throws OCTException{
		systemManager.setStructureAsDone(false);
		systemManager.setPersonaliseAsDone(false);
		systemManager.setSocialAsDone(false);
		systemManager.setNotLive();

		StepState stepState = systemManager.getStepState();
		assertNotNull(stepState);
		assertFalse(stepState.getStructure());
		assertFalse(stepState.getPersonalise());
		assertFalse(stepState.getSocial());
		assertFalse(stepState.getLive());
	}
}
