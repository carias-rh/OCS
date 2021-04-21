package eu.europa.ec.eci.oct.validation;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.core.io.impl.FileSystemResource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.utils.KieHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opencsv.bean.CsvToBeanBuilder;

import eu.europa.ec.eci.oct.webcommons.services.enums.CountryEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.validation.RuleServiceImpl;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/drools-test.xml")
public class DroolsValidatorTest {

	protected Logger logger = LogManager.getLogger(DroolsValidatorTest.class);

	@Test // Austria
	public void test_at() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.AUSTRIA.getCode());
		executeByCountryCode(CountryEnum.AUSTRIA.getCode());
	}

	@Test // Belgium
	public void test_be() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.BELGIUM.getCode());
		executeByCountryCode(CountryEnum.BELGIUM.getCode());
	}

	@Test // Bulgaria
	public void test_bg() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.BULGARIA.getCode());
		executeByCountryCode(CountryEnum.BULGARIA.getCode());
	}

	@Test // Cyprus
	public void test_cy() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.CYPRUS.getCode());
		executeByCountryCode(CountryEnum.CYPRUS.getCode());
	}

	@Test // Czech Republic
	public void test_cz() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.CZECH_REPUBLIC.getCode());
	}

	@Test // Denmark
	public void test_dk() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.DENMARK.getCode());
	}

	@Test // Germany
	public void test_de() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.GERMANY.getCode());
	}

	@Test // Estonia
	public void test_ee() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.ESTONIA.getCode());
	}

	@Test // Greece
	public void test_el() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.GREECE.getCode());
	}

	@Test // Spain
	public void test_es() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.SPAIN.getCode());
	}

	@Test // Finland
	public void test_fi() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.FINLAND.getCode());
	}

	@Test // France
	public void test_fr() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.FRANCE.getCode());
	}

	@Test // Ireland
	public void test_ie() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.IRELAND.getCode());
	}

	@Test // Croatia
	public void test_hr() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.CROATIA.getCode());
	}

	@Test // Italy
	public void test_it() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.ITALY.getCode());
	}

	@Test // Lavtia
	public void test_lv() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.LATVIA.getCode());
	}

	@Test // Lithuania
	public void test_lt() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.LITHUANIA.getCode());
	}

	@Test // Lux
	public void test_lu() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.LUXEMBURG.getCode());
	}

	@Test // Hungary
	public void test_hu() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.HUNGARY.getCode());
	}

	@Test // Malta
	public void test_mt() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.MALTA.getCode());
	}

	@Test // Netherland
	public void test_nl() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.NETHERLANDS.getCode());
	}

	@Test // Slovakia
	public void test_sk() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.SLOVAKIA.getCode());
	}

	@Test // Poland
	public void test_pl() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.POLAND.getCode());
	}

	@Test // Portugal
	public void test_pt() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.PORTUGAL.getCode());
	}

	@Test // Romania
	public void test_ro() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.ROMANIA.getCode());
	}

	@Test // Sweden
	public void test_se() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.SWEDEN.getCode());
	}

	@Test // Slovenia
	public void test_si() throws FileNotFoundException, OCTException {
		executeByCountryCode(CountryEnum.SLOVENIA.getCode());
	}

	private void executeByCountryCode(String countryCode) throws FileNotFoundException, OCTException {

		String csvFile = this.getClass().getResource("/csv-rules-data/" + countryCode + ".csv").getFile();

		List<CSVSignatureTestBean> list = getCSVData(csvFile);
		// transformer
		for (CSVSignatureTestBean c : list) {
			ValidationBean v = new ValidationBean();
			// v.setNationality(c.getNationality());
			v.setNationality(countryCode);
			v.setProperties(c.getProperties());

			ValidationProperty vpCountry = null;
			ValidationProperty vpPostalCode = null;

			for (ValidationProperty vp : v.getProperties()) {
				if (vp.getKey().equalsIgnoreCase(PropertyEnum.RESIDENCE_POSTAL_CODE.getName())) {
					vpPostalCode = vp;
				}
				if (vp.getKey().equalsIgnoreCase(PropertyEnum.RESIDENCE_COUNTRY.getName())) {
					vpCountry = vp;
				}
			}

			if (vpCountry != null && vpPostalCode != null && vpCountry.getValue() != null
					&& !vpCountry.getValue().equals("")) {
				v.getProperties().add(new ValidationProperty(vpPostalCode.getKey() + "_" + vpCountry.getValue(),
						vpPostalCode.getValue()));
			}

			ValidationResult validationResult = validate(v, countryCode);
			List<ValidationError> errors = validationResult.getValidationErrors();
			String idMSG = "[" + countryCode + "-ID: " + c.getId() + "]>";
			logger.info(idMSG + c);
			printErrorFields(errors);
			if (c.getExpectedResult().equalsIgnoreCase("PASS")) {
				if (errors.size() != 0) {
					String msg = "\tExpected to succeed but is failing: test case id[" + c.getId() + "] "
							+ c.toString();
					logger.info(msg);
					fail(msg);
				}
			} else { // FAIL
				if (errors.size() == 0) {
					String msg = "\tExpected to fail but is passing: test case id[" + c.getId() + "] " + c.toString();
					logger.info(msg);
					fail(msg);
				}
			}
			logger.info(idMSG + "OK");
		}
	}

	public List<CSVSignatureTestBean> getCSVData(String csvFile) throws FileNotFoundException {
		List<CSVSignatureTestBean> list = new CsvToBeanBuilder(new FileReader(csvFile))
				.withType(CSVSignatureTestBean.class).withSeparator(';').build().parse();
		return list;
	}

	public void printErrorFields(List<ValidationError> errors) {
		for (ValidationError e : errors) {
			logger.info("\t" + e);
		}
	}

	private HashMap<String, KieBase> kieBaseStoreMap = new HashMap<>();

	public ValidationResult validate(ValidationBean validationBean, String countryOfReference) throws OCTException {
		ValidationResult validationResult = new ValidationResult();
		// String country = validationBean.getNationality();
		KieHelper kieHelper = new KieHelper();

		KieBase kieBase = kieBaseStoreMap.get(countryOfReference);
		if (kieBase == null) {
			kieHelper.addResource(new FileSystemResource(
					new File(RuleServiceImpl.class.getResource("/rules/common/common.drl").getFile())));
			kieHelper.addResource(new FileSystemResource(new File(RuleServiceImpl.class
					.getResource("/rules/" + countryOfReference + "/" + countryOfReference + ".drl").getFile())));

			// compiling to check of any errors;
			Results results = kieHelper.verify();

			if (results.hasMessages()) {
				logger.error("KIE compilation errors! check the rule files : COMMON and " + countryOfReference);
				for (Message err : results.getMessages()) {
					logger.error(err.toString());
				}
				throw new OCTException(
						"KIE compilation errors! check the rule files : COMMON and " + countryOfReference);
			}
			kieBase = kieHelper.build();
			kieBaseStoreMap.put(countryOfReference, kieBase);
		}

		StatelessKieSession kSession = kieBase.newStatelessKieSession();
		CommandFactory.newInsert(validationBean.getProperties());
		System.out.println(
				kieBaseStoreMap.hashCode() + " - " + kieBaseStoreMap.size() + " - " + kieBase.getKieSessions().size());

		List cmds = new ArrayList();
		cmds.add(CommandFactory.newSetGlobal("nationality", validationBean.getNationality(), true));
		cmds.add(CommandFactory.newSetGlobal("validationResult", validationResult, true));
		for (ValidationProperty vb : validationBean.getProperties()) {
			System.out.println(vb.toString());
			cmds.add(CommandFactory.newInsert(vb));
		}

		org.kie.api.runtime.ExecutionResults results = kSession.execute(CommandFactory.newBatchExecution(cmds));
		kSession = null;
		return validationResult;
	}

}
