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
		String[] invalidVersions = {
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
		for(String versionString: invalidVersions) {
			try {
			new TerramapVersion(versionString);
			} catch(InvalidVersionString e) {
				continue;
			}
			throw new IllegalStateException("Version String " + versionString + " parsed successufly when it should have raised an exception");
		}
	}
	
	@Test
	public void comparisonTest() throws InvalidVersionString {
		String[] versions = {
				null,
				"0.0.0",
				"0.0.1",
				"0.1.0",
				"0.1.1",
				"1.0.0-alpha1",
				"1.0.0-alpha1.1",
				"1.0.0-beta0",
				"1.0.0-beta6",
				"1.0.0-beta6.6-dev",
				"1.0.0-rc1",
				"1.0.0-rc35",
				"1.0.0",
				"1.0.1",
				"1.1.0",
				"1.1.1",
				"2.0.0",
				"${version}",
		};
		for(int i=1; i<versions.length; i++) {
			TerramapVersion v1 = new TerramapVersion(versions[i]);
			for(int j=0; j<versions.length; j++) {
				TerramapVersion v2 = j==0 ? null: new TerramapVersion(versions[j]);
				PValidation.checkState(v1.isOlder(v2) == i < j, v1 + " isOlder " + v2 + " failed");
				PValidation.checkState(v1.isOlderOrSame(v2) == i <= j, v1 + " isOlderOrSame " + v2 + " failed");
				PValidation.checkState(v1.equals(v2) == (i == j), v1 + " equals " + v2 + " failed");
				PValidation.checkState(v1.isNewer(v2) == i > j, v1 + " isNewer " + v2 + " failed");
				PValidation.checkState(v1.isNewerOrSame(v2) == i >= j, v1 + " isNewerOrSame " + v2 + " failed");
			}
		}
	}

}
