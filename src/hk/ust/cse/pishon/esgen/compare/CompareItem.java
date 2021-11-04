package hk.ust.cse.pishon.esgen.compare;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

public class CompareItem implements ITypedElement, IStreamContentAccessor, Serializable{

	private static final long serialVersionUID = 4341621671235407567L;
	private File f;
	private final String content;

	public CompareItem(String path){
		this.f = new File(path);
		FileReader fr = null;
		StringBuffer sb = new StringBuffer();
		try {
			fr = new FileReader(f);
			char[] cbuf = new char[200];
			int len = 0;
			while((len=fr.read(cbuf))>-1){
				sb.append(cbuf, 0, len);
			}
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} finally {
			if(fr != null){
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}
		content = sb.toString();
	}

	@Override
	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(content.getBytes());
	}

	@Override
	public String getName() {
		return f.getName();
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getType() {
		return "change";
	}
}
