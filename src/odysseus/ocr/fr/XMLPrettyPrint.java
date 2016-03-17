package odysseus.ocr.fr;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.w3c.tidy.Tidy;

public class XMLPrettyPrint {
	/* Un valideur et formatteur d'XML (JTidy) avec différents encodages*/
	public void prettyPrint(String content, String encoding, String path) throws IOException{
		Tidy tidy = new Tidy();
		tidy.setXmlTags(true);
		InputStream stream=IOUtils.toInputStream(content, encoding);
		tidy.setInputEncoding(encoding);
		tidy.setOutputEncoding(encoding);
		tidy.setMakeClean(true);
		tidy.setXmlTags(true);
		tidy.setXmlOut(true);
//		tidy.setXHTML(true);
		
		OutputStream out = new FileOutputStream(path);
		tidy.parse(stream, out);
	}
}
