package fr.thesmyler.terramap;

import org.junit.Test;

import fr.thesmyler.terramap.TerramapVersion.InvalidVersionString;
import net.daporkchop.lib.common.util.PValidation;

public class TerramapVersionTest {

	@Test
	public void testValidVersionString() throws InvalidVersionString {
		String[] validVersions = {
				"1.0.0-beta7.3-dev_1.12.2",
				"1.0.0-beta7.3-dev",
				"1.0.0-beta7.3_1.12.2",
				"1.0.0-dev_1.12.2",
				"1.0.0_1.12.2",
				"1.0.0-beta7.3-dev",
				"1.0.0-beta7.3-dev",
				"1.0.0-beta7.3",
				"1.0.0-dev",
				"1.0.0"
		};
		for(String versionString: validVersions) {
			TerramapVersion version = new TerramapVersion(versionString);
			String otherVersionString = version.toString();
			PValidation.checkState(otherVersionString.equals(versionString), versionString + " and " + otherVersionString + " do not match");
		}
	}
	
	@Test
	public void testInvalidVersionString()  {
		String[] validVersions = {
				"_1.12.2",
				"1",
				"1.0",
				"-dev_1.12.2",
				"1.0.0-1.12.2",
				"",
				"1.0.0--dev",
				"1.0.0__beta7.3",
				"1.0.",
				"1..0"
		};
		for(String versionString: validVersions) {
			try {
			new TerramapVersion(versionString);
			} catch(InvalidVersionString e) {
				continue;
			}
			throw new IllegalStateException("Version String " + versionString + " parsed successufly when it should have raised an exception");
		}
	}

}
