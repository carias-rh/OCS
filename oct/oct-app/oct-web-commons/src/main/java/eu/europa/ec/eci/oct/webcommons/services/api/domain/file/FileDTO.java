package eu.europa.ec.eci.oct.webcommons.services.api.domain.file;

import java.io.Serializable;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.springframework.stereotype.Component;

/**
 * User: franzmh
 * Date: 16/11/16
 */
@Component
public class FileDTO implements Serializable {
	
    private static final long serialVersionUID = -7055508070140029891L;

    private String filename;
    private String URL;
    private int weight;
    private int height;
    private String type;
    private MultiPart multiPart;

    public FileDTO(){}

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MultiPart getMultiPart() {
        return multiPart;
    }

    public void setMultiPart(MultiPart multiPart) {
        this.multiPart = multiPart;
    }
}
