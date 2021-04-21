package eu.europa.ec.eci.oct.webcommons.services.security;

import java.io.File;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.crypto.CipherOperation;
import eu.europa.ec.eci.oct.crypto.Cryptography;
import eu.europa.ec.eci.oct.entities.admin.SystemState;
import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.AuthenticationException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.AuthenticationLockedException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/services-test.xml")
public class SecurityServiceImplTest extends ServicesTest {
	static final String USER = "admin";
	static final String PWD = "Qwerty123!";
	static final String PRIVATE_KEY_FILE = "/file/private.key";
	static final String SALT_FILE = "/file/crypto.salt";
	static final String TOKEN_PREFIX = "$6$ocs$";
	private final int iterations = 100000;

	@Autowired
	Lock lock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	// We test with a temporary key. Not the one from the database
	public void definePublicKey() throws Exception {
		String pubKey = "30820222300d06092a864886f70d01010105000382020f003082020a0282020100b38518df7db42316f7fcab565054db0cd2f561edd99d334be91913ff4fc32657a48a59d6d801911e0f1eddcc318bf8b355835dbdd58222e5a0d886bd71ace118f478e56bf0e1c79e9dfc23e799418097c1bf319f400b6821b1b16a037a146a9d51f6f1e92e4fc3a5f372f3509f261d79970fb3c1e0e6fea5a1444ca554b0d992f6d9b8e08f51c5669fe4751d1999e9cd753e7f96ec5a62a20e6daebaf67f57367897f173125ea2232ae38e2c27bfe7d613c1baeb156ba4d40b23eaa49fc902e105a8ce721200f869f2805207539bfb4ead0ea04ff93a80a2ede6543a2e0a4ae775faf865203c0ab72278f3827c31da7ad7e94633158de9f442da8f5ffdfcac5b0b171e18e8f100c3a8d1d7bae90d80dc5038b25968801ea820a3173f05ffe50a6ca591cdbb545c54f12093a6e702cc677bb72ff913108e314aa73ed1d039536cbb38fd8a9b9f045576492cfa55540401edd755f2838fcff1a29a4bfcc19c569c27660832628f1d3778ed00d9f397db361a69f12781f609a31efc6a568fb9ac207b0ab073cf2595f517624f72710289b5add4a653d935f264408d2ab0ba81cc94446cc507014694106695599d3edacb0080bffa61f9ce8d6d664425c3a4aa79e84ce3d3c0c8507501276e441931a8c2a7da5bd4a67ce44391f90a75e5d5d8341a02b33227f0e82a6874fee0529100d1b7fd4c09946a0e4d56e2b8ad5180779e350203010001";
		systemManager.getSystemPreferences().setPublicKey(pubKey);
		securityService.resetLock();
		testSystemPreferences.setState(SystemState.OPERATIONAL);
		lock = securityService.obtainLock("admin");
	}

	@After
	public void rollbackPublicKey() throws Exception {
		String pubKey = "308201a2300d06092a864886f70d01010105000382018f003082018a02820181008f2caa96d471331bcb1c936e0b366543f0463111f3d02819499b9da2d107ec78ffc53af67772650b619a2c6edb3c5b8ac81416e539ec4524e25244a0f2756122d26a16950a3569c0feb9570fa37c7666a979fe39db1e697b9135db1c2dcc5a131f17eaecf3d8cab5a51c1d32474e0a1f33629a71320f3acd9ee88211b10615ab5982547bd0d371aff67e3c5f1cc7bb07788d987b4d1e9900bbba04cd6b7b030b54dc30fd5add5bc0da243a1aa1248966a7074b3e167478ff25206a5b652f38e1866c50c9bcb09b9a4984c61369c6d81e92113f3bbcd43443bb0e419cac26cb691486ea51660b30dc57fb1bcf79ca97e51199a28a18d8b024050d51ae92a9a98a58bf9f9e5ff278dec6f3c8d96871a4b9357d6f697b23bf6bd0833a423a9a0ea9188894711532004140c85ea60cd89efbf9a104dc114d246df1b2450326e5843cf335ba84b31a6bdfd1533b054e9106d9ef98036c51445c1611f1ea0bc0176e9e4c8b814377d2bdf0eb6052c7622cd6c15bb447d155b9465422cb8457d81ebfb10203010001";
		systemManager.getSystemPreferences().setPublicKey(pubKey);
		testSystemPreferences.setState(SystemState.DEPLOYED);
	}

	@Test
	public void generatePhrase() {
		SecureRandom secureRandom = new SecureRandom();
		byte bytes[] = new byte[20];
		secureRandom.nextBytes(bytes);
	}

	@Test
	public void generateChallenge() throws Exception {
		assertTrue(securityService.generateChallenge().length() >= 768);
	}

	@Test
	public void validateChallenge() throws Exception {
		String challenge = securityService.generateChallenge();
		byte[] keyData = FileUtils
				.readFileToByteArray(new File(this.getClass().getResource(PRIVATE_KEY_FILE).getFile()));
		byte[] saltData = FileUtils.readFileToByteArray(new File(this.getClass().getResource(SALT_FILE).getFile()));
		byte[] encryptedKey = Hex.decodeHex(new String(keyData, "UTF-8").toCharArray());
		byte[] key = securityService.decrypt(encryptedKey, PWD.toCharArray(), saltData, iterations);
		Cryptography cryptography = new Cryptography(CipherOperation.DECRYPT, key);
		byte[] decryptedData = cryptography.perform(Hex.decodeHex(challenge.toCharArray()));
		String result = new String(decryptedData, "UTF-8");
		assertTrue(securityService.validateChallenge(result));
		result = "rzef4ze5f4dsqv3v4d3f5v434v3wx54c";
		assertFalse(securityService.validateChallenge(result));
	}

	@Test
	public void authenticate() throws Exception {
		String token = securityService.authenticate(USER, PWD, getChallengeResult());
		assertTrue(token.startsWith(TOKEN_PREFIX));
	}

	@Test
	public void authenticationLocked() throws Exception {
		String token = securityService.authenticate(USER, PWD, getChallengeResult());
		assertTrue(token.startsWith(TOKEN_PREFIX));

		lockAuthentication();

		thrown.expect(AuthenticationLockedException.class);
		securityService.authenticate(USER, "ERROR", getChallengeResult());
	}

	@Test
	public void authenticationUnlockedWithTimeout() throws Exception {
		String token = null;
		lock.setLockPeriod(1); // The lock period is express in minutes
		securityService.overrideAuthenticationLock(lock);
		lockAuthentication();
		try {
			token = securityService.authenticate(USER, PWD, getChallengeResult());
		} catch (AuthenticationLockedException ale) {
			assertNull(token);
		}
		TimeUnit.MINUTES.sleep(1);
		token = securityService.authenticate(USER, PWD, getChallengeResult());
		assertTrue(token.startsWith(TOKEN_PREFIX));
	}

	@Test
	public void authenticationNotLockedInOfflineMode() throws Exception {
		lockAuthentication();
		testSystemPreferences.setState(SystemState.DEPLOYED);
		thrown.expect(AuthenticationException.class);
		securityService.authenticate(USER, "ERROR", getChallengeResult());
	}

	@Test(expected = AuthenticationException.class)
	public void authenticateWrongUsername() throws Exception {
		securityService.authenticate("wrong user", PWD, getChallengeResult());
	}

	@Test(expected = AuthenticationException.class)
	public void authenticateWrongPassword() throws Exception {
		securityService.authenticate(USER, "wrong pwd", getChallengeResult());
	}

	@Test(expected = AuthenticationException.class)
	public void authenticateWrongChallenge() throws Exception {
		securityService.authenticate(USER, PWD, "wrong challenge result");
	}

	@Test
	public void generateToken() {
		String authToken = securityService.generateAuthToken();
		assertTrue(authToken.length() >= 93);
	}

	@Test
	public void buildAndStorePrincipal() {
		String authToken = securityService.generateAuthToken();
		Principal principal = securityService.buildAndStorePrincipal(authToken);
		assertNotNull(principal);
		principal.getToken().contains(TOKEN_PREFIX);
		assertEquals("ADMIN", principal.getRole());
	}

	@Test
	public void validateAuthToken() {
		String authToken = securityService.generateAuthToken();
		Principal principal1 = securityService.buildAndStorePrincipal(authToken);
		Principal principal2 = securityService.validateAuthToken(authToken);
		assertEquals(principal1.getToken(), principal2.getToken());
		assertEquals(principal1.getRole(), principal2.getRole());
		principal2 = securityService.validateAuthToken(authToken);
		assertEquals(principal1.getToken(), principal2.getToken()); // the token is not consumed
		principal2 = securityService.validateAuthToken(authToken);
		assertNotNull(principal2);
		principal2 = securityService.validateAuthToken("Fake-d4f5g5g");
		assertNull(principal2);
	}

	@Test
	public void isAuthenticated() throws Exception {
		String authToken = "fake_auth_token";
		assertFalse(securityService.isAuthenticated(authToken));
		authToken = securityService.authenticate(USER, PWD, getChallengeResult());
		assertTrue(securityService.isAuthenticated(authToken));
		assertFalse(securityService.isAuthenticated(authToken.substring(0, authToken.length() - 1)));
	}

	@Test
	public void logout() throws Exception {
		String token = securityService.authenticate(USER, PWD, getChallengeResult());
		assertTrue(token.startsWith(TOKEN_PREFIX));
		assertTrue(securityService.isAuthenticated(token));
		securityService.logout(token);
		assertFalse(securityService.isAuthenticated(token));
	}

	private String getChallengeResult() throws Exception {
		String challenge = securityService.generateChallenge();
		byte[] keyData = FileUtils
				.readFileToByteArray(new File(this.getClass().getResource(PRIVATE_KEY_FILE).getFile()));
		byte[] saltData = FileUtils.readFileToByteArray(new File(this.getClass().getResource(SALT_FILE).getFile()));
		byte[] encryptedKey = Hex.decodeHex(new String(keyData, "UTF-8").toCharArray());
		// byte[] key = securityService.decrypt(encryptedKey, PWD.toCharArray(),
		// saltData, 2500);
		byte[] key = securityService.decrypt(encryptedKey, PWD.toCharArray(), saltData, iterations);
		Cryptography cryptography = new Cryptography(CipherOperation.DECRYPT, key);
		byte[] decryptedData = cryptography.perform(Hex.decodeHex(challenge.toCharArray()));
		String result = new String(decryptedData, "UTF-8");
		return result;
	}

	private void lockAuthentication() throws Exception {
		int tentatives = 0;
		for (int i = 0; i < lock.getMaxAttempts(); i++) {
			try {
				securityService.authenticate(USER, "FAKE_PASSWORD", getChallengeResult());
			} catch (AuthenticationException ae) {
				tentatives++;
				continue;
			}
		}
		assertTrue(tentatives == lock.getMaxAttempts());
	}
}
