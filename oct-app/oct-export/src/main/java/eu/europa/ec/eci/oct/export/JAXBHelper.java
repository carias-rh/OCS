package eu.europa.ec.eci.oct.export;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import eu.europa.ec.eci.oct.export.model.SupportForm;

/**
 * Helper class for marshalling and unmarshalling using JAXB. Implemented as a singleton because of the instantiating of
 * the {@link JAXBContext} which takes time.
 * 
 * @author chiridl
 * 
 */
public class JAXBHelper {

	private static JAXBHelper instance;

	/**
	 * The JAXB context is thread safe. As such, initialize it only once per instance
	 */
	private JAXBContext context;

	private JAXBHelper() throws DataException {
		try {
			context = JAXBContext.newInstance(SupportForm.class);
		} catch (JAXBException e) {
			throw new DataException("Exception while creating the JAXB context!", e);
		}
	}

	/**
	 * Returns the instance of the helper.
	 * 
	 * @return
	 * @throws DataException
	 */
	public static JAXBHelper getInstance() throws DataException {
		if (instance == null) {
			instance = new JAXBHelper();
		}

		return instance;
	}

	/**
	 * Performs the marshalling operation. It marshalls the given bean and writes the output to the given {@link Writer}
	 * .
	 * 
	 * @param bean
	 *            the bean to be marshalled
	 * @param writer
	 *            the writer to output the XML to
	 * @throws DataException
	 */
	public void marshall(SupportForm bean, Writer writer) throws DataException {
		try {
			Marshaller marshaller = context.createMarshaller();
			// TODO danic: this will increases file size; should we remove it?
			//marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(bean, writer);
		} catch (JAXBException e) {
			throw new DataException("Exception while marshalling data!", e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Unmarshalls the content of the given {@link Reader} and returns a {@link SupportForm} instance (if succesfull).
	 * 
	 * @param reader
	 *            the reader to get the XML data from
	 * @return the bean
	 * @throws DataException
	 */
	public SupportForm unmarshal(Reader reader) throws DataException {
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			SupportForm export = (SupportForm) unmarshaller.unmarshal(reader);

			return export;
		} catch (JAXBException e) {
			throw new DataException("Exception while unmarshalling data!", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
