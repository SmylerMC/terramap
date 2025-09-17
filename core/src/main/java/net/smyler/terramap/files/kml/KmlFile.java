package net.smyler.terramap.files.kml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import static net.smyler.terramap.Terramap.getTerramap;


@XmlRootElement(name="kml")
public class KmlFile {

    @XmlElement(name="Document")
    private final KmlDocument document = new KmlDocument();

    @XmlAttribute
    private final String XLMNS = "http://www.opengis.net/kml/2.2";

    @XmlTransient
    public KmlDocument getDocument() {
        return this.document;
    }

    public void save(File file, boolean compressed) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(KmlFile.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            try(OutputStream stream = Files.newOutputStream(file.toPath())) {
                if(compressed) {
                    try(ZipOutputStream compressedStream = new ZipOutputStream(stream)) {
                        ZipEntry entry = new ZipEntry("terramap.kml");
                        compressedStream.putNextEntry(entry);
                        marshaller.marshal(this, compressedStream);
                    }
                } else {
                    marshaller.marshal(this, stream);
                }
            }
        } catch(JAXBException e) {
            getTerramap().logger().error("Something went seriously wrong when saving a kml file. Save aborted.");
            getTerramap().logger().catching(e);
        }
    }

}
