package fr.thesmyler.terramap.files.kml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import fr.thesmyler.terramap.TerramapMod;

@XmlRootElement(name="kml")
public class KmlFile {

	@XmlElement(name="Document")
	private KmlDocument document = new KmlDocument();

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
			try(OutputStream stream = new FileOutputStream(file)) {
				if(compressed) {
					try(ZipArchiveOutputStream compressedStream = new ZipArchiveOutputStream(stream)) {
						ZipArchiveEntry entry = new ZipArchiveEntry("terramap.kml");
						compressedStream.putArchiveEntry(entry);
						marshaller.marshal(this, compressedStream);
						compressedStream.closeArchiveEntry();
					}
				} else {
					marshaller.marshal(this, stream);
				}
			}
		} catch(JAXBException e) {
			TerramapMod.logger.error("Something went seriously wrong when saving a kml file. Save aborted.");
			TerramapMod.logger.catching(e);
		}
	}

}
