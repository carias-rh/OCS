package eu.europa.ec.eci.oct.webcommons.services.api.domain.file;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: franzmh
 * Date: 20/12/16
 */

@Component
@Scope("prototype")
public class ImageDTO {
    private String name;
    private byte[] data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
