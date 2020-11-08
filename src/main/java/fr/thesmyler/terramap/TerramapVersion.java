package fr.thesmyler.terramap;

import scala.actors.threadpool.Arrays;

public class TerramapVersion {

	public final int majorTarget;
	public final int minorTarget;
	public final int buildTarget;

	public final ReleaseType releaseType;

	public final int build;
	public final int revision;

	public final boolean devBuild;
	public final boolean devRun;

	public TerramapVersion(String versionString) throws InvalidVersionString {
		if("${version}".equals(versionString)) {
			this.majorTarget = this.minorTarget = this.buildTarget = this.build = this.revision = 0;
			this.devBuild = false;
			this.devRun = true;
			this.releaseType = ReleaseType.DEV;
		} else {
			String[] parts = versionString.split("-");
			if(parts.length > 3) {
				throw new InvalidVersionString("Invalid version string " + versionString);
			}
			if(parts.length > 0) {
				if("dev".equals(parts[parts.length - 1])) {
					this.devBuild = true;
					parts = (String[]) Arrays.copyOfRange(parts, 0, parts.length - 1);
				} else {
					this.devBuild = false;
				}
				String[] target = parts[0].split("\\.");
				if(target.length != 3) throw new InvalidVersionString("Invalid target version " + parts[0]);
				devRun = false;
				try {
					this.majorTarget = Integer.parseInt(target[0]);
				} catch(NumberFormatException e) {
					throw new InvalidVersionString("Invalid target major version: " + target[0]);
				}
				try {
					this.minorTarget = Integer.parseInt(target[1]);
				} catch(NumberFormatException e) {
					throw new InvalidVersionString("Invalid target minor version: " + target[1]);
				}
				try {
					this.buildTarget = Integer.parseInt(target[2]);
				} catch(NumberFormatException e) {
					throw new InvalidVersionString("Invalid target build version: " + target[2]);
				}
				if(parts.length > 1) {
					for(ReleaseType type: ReleaseType.values()) {
						if(type.equals(ReleaseType.RELEASE)) continue;
						if(parts[1].startsWith(type.name)) {
							this.releaseType = type;
							parts[1] = parts[1].substring(type.name.length());
							String[] build = parts[1].split("\\.");
							if(build.length > 0) {
								try {
									this.build = Integer.parseInt(build[0]);
								} catch(NumberFormatException e) {
									throw new InvalidVersionString("Invalid build version: " + build[0]);
								}
								if(build.length > 1) {
									try {
										this.revision = Integer.parseInt(build[1]);
									} catch(NumberFormatException e) {
										throw new InvalidVersionString("Invalid revision version: " + build[1]);
									}
								} else {
									this.revision = 0;
								}
							} else {
								throw new InvalidVersionString("Invalid target version number: " + parts[1]);
							}
							return;
						}
					}
					throw new InvalidVersionString("Invalid target release type: " + parts[1]);
				} else {
					this.releaseType = ReleaseType.RELEASE;
					this.build = this.minorTarget;
					this.revision = this.buildTarget;
				}
			} else {
				throw new InvalidVersionString("Empty version string");
			}
		}
	}
	
	@Override
	public String toString() {
		if(this.devRun || this.releaseType.equals(ReleaseType.DEV)) {
			return "${version}";
		}
		String str = "";
		str += this.majorTarget;
		str += "." + this.minorTarget;
		str += "." + this.buildTarget;
		if(!this.releaseType.equals(ReleaseType.RELEASE)) {
			str += "-" + this.releaseType.name;
			str += this.build;
			if(this.revision != 0) {
				str += "." + this.revision;
			}
		}
		if(this.devBuild) {
			str += "-dev";
		}
		return str;
	}

	public enum ReleaseType {

		RELEASE(""),
		RELEASE_CANDIDATE("rc"),
		BETA("beta"),
		ALPHA("alpha"),
		DEV("dev"); // Running from dev environment, no actual version number

		public final String name;

		private ReleaseType(String name) {
			this.name = name;
		}

	}

	public class InvalidVersionString extends Exception {

		private static final long serialVersionUID = 1L;

		public InvalidVersionString(String message) {
			super(message);
		}

	}

}
