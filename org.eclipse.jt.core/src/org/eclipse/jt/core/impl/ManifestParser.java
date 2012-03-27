/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *manifest文件解析器
 * 
 * @author Jeff Tang
 * 
 */
final class ManifestParser {

	private final static Name bundle_name = new Name("Bundle-SymbolicName");
	private final static Name bundle_version = new Name("Bundle-Version");
	private final static Name export_package = new Name("Export-Package");
	private final static Name import_package = new Name("Import-Package");
	private final static Name fragment_host = new Name("Fragment-Host");
	private final static Name bundle_classptah = new Name("Bundle-ClassPath");
	private final static Name require_bundle = new Name("Require-Bundle");

	/**
	 * 解析插件名称
	 * 
	 * @param bundleNameString
	 * @return
	 */
	final static String parserBundleName(Attributes attrs) {
		String bundleNameString = attrs.getValue(bundle_name);
		int index = bundleNameString.indexOf(';');
		if (index == -1) {
			return bundleNameString;
		} else {
			return bundleNameString.substring(0, index);
		}
	}

	final static String parserFragmentHost(Attributes attrs) {
		String fragmentHostString = attrs.getValue(fragment_host);
		if (fragmentHostString == null || fragmentHostString.length() == 0) {
			return null;
		}
		int index = fragmentHostString.indexOf(';');
		if (index == -1) {
			return fragmentHostString;
		} else {
			return fragmentHostString.substring(0, index);
		}
	}

	/**
	 * 解析导出的包
	 * 
	 * @param expString
	 * @return
	 */
	final static MetaBaseContainerImpl<ExportPackage> parseExportPackage(
	        Attributes attrs, BundleInfo bundleInfo) {
		String expString = attrs.getValue(export_package);
		if (expString == null || expString.length() == 0) {
			return null;
		}
		MetaBaseContainerImpl<ExportPackage> container = new MetaBaseContainerImpl<ExportPackage>();
		String[] expArr = expString.split(",");
		for (int i = 0, l = expArr.length; i < l; i++) {
			String expstr = expArr[i];
			ExportPackage exp = new ExportPackage(parseName(expstr),
			        parseVersion(expstr), bundleInfo);
			container.add(exp);
		}
		return container;
	}

	/**
	 * 解析引入的包
	 * 
	 * @param impString
	 * @return
	 */
	final static MetaBaseContainerImpl<ImportPackage> parseImportPackage(
	        Attributes attrs) {
		String impString = attrs.getValue(import_package);
		if (impString == null || impString.length() == 0) {
			return null;
		}
		impString = impString.concat(",");
		Matcher m = pVersionRegionIn.matcher(impString);
		MetaBaseContainerImpl<ImportPackage> container = new MetaBaseContainerImpl<ImportPackage>();
		while (m.find()) {
			String find = m.group();
			container.add(new ImportPackage(parseName(find),
			        parseVersionRegion(find)));
		}
		return container;
	}

	/**
	 * 匹配版本或者版本区间的正则表达式
	 */
	private final static Pattern pVersion = Pattern
	        .compile("[1-9]+(\\.\\d+){2}(\\..*)?|[\\[\\(].*[\\]\\)]");
	/**
	 * 匹配一个含有版本区间的引入包或者依赖插件的正则表达式
	 */
	private final static Pattern pVersionRegionIn = Pattern
	        .compile("[a-z].*?\\D,");

	final static String parseName(String compstr) {
		int semiIndex = compstr.indexOf(';');
		String name;
		if (semiIndex == -1) {
			name = compstr.substring(0, compstr.length() - 1);// 去掉最后面的","
		} else {
			name = compstr.substring(0, semiIndex);
		}
		return name;
	}

	final static VersionRegion parseVersionRegion(String vrstr) {
		Matcher m = pVersion.matcher(vrstr);
		String vrString = m.group();
		if (vrString == null) {
			return VersionRegion.empty;
		}
		int j = vrString.indexOf(',');
		if (j > 0) {
			String lowVersion = vrString.substring(0, j);
			String highVersion = vrString.substring(j + 1);
			boolean includeL;
			boolean includeH;
			if (lowVersion.startsWith("[")) {
				includeL = true;
			} else {
				includeL = false;
			}
			if (highVersion.endsWith("]")) {
				includeH = true;
			} else {
				includeH = false;
			}
			lowVersion = lowVersion.substring(1, lowVersion.length());
			highVersion = highVersion.substring(0, highVersion.length() - 1);
			// TODO:
			String[] lvs = lowVersion.split("\\.");
			String[] hvs = highVersion.split("\\.");
			return new VersionRegion(Integer.parseInt(lvs[0]), Integer
			        .parseInt(lvs[1]), Integer.parseInt(lvs[2]), Integer
			        .parseInt(hvs[0]), Integer.parseInt(hvs[1]), Integer
			        .parseInt(hvs[2]), includeL, includeH);
		} else {
			String[] vs = vrString.split("\\.");
			int lv1 = Integer.parseInt(vs[0]);
			int lv2 = Integer.parseInt(vs[1]);
			int lv3 = Integer.parseInt(vs[2]);
			int hv1 = lv1;
			int hv2 = lv2;
			int hv3 = lv3;
			return new VersionRegion(lv1, lv2, lv3, hv1, hv2, hv3, true, true);
		}
	}

	/**
	 *解析依赖的插件
	 * 
	 * @param requiredBundleString
	 * @return
	 */
	final static MetaBaseContainerImpl<RequiredBundle> parseRequiredBundle(
	        Attributes attrs) {
		String rBundleString = attrs.getValue(require_bundle);
		if (rBundleString == null || rBundleString.length() == 0) {
			return null;
		}
		rBundleString = rBundleString.concat(",");
		Matcher m = pVersionRegionIn.matcher(rBundleString);
		MetaBaseContainerImpl<RequiredBundle> container = new MetaBaseContainerImpl<RequiredBundle>();
		while (m.find()) {
			String find = m.group();
			container.add(new RequiredBundle(parseName(find),
			        parseVersionRegion(find)));
		}
		return container;
	}

	/**
	 * 解析插件的classpath
	 * 
	 * @param classpathString
	 * @return
	 */
	final static MetaBaseContainerImpl<BundleClassPath> parseBundleClassPath(
	        Attributes attrs) {
		String bClassPathString = attrs.getValue(bundle_classptah);
		if (bClassPathString == null || bClassPathString.length() == 0) {
			return null;
		}
		String[] pathes = bClassPathString.split(",");
		MetaBaseContainerImpl<BundleClassPath> container = new MetaBaseContainerImpl<BundleClassPath>();
		for (int i = 0, l = pathes.length; i < l; i++) {
			String path = pathes[0];
			if ("\\.".equals(path)) {
				continue;
			}
			container.add(new BundleClassPath(path));
		}
		return container;
	}

	final static Version parseVersion(String versionstr) {
		Matcher m = pVersion.matcher(versionstr);
		if (!m.matches()) {
			return Version.empty;
		}
		String vrString = m.group();
		String[] versionArr = vrString.split("\\.");
		String qualifier = "";
		if (versionArr.length >= 4) {
			qualifier = versionArr[3];
		}
		return new Version(Integer.parseInt(versionArr[0]), Integer
		        .parseInt(versionArr[1]), Integer.parseInt(versionArr[2]),
		        qualifier);
	}

	final static Version parseVersion(Attributes attrs) {
		String versionString = attrs.getValue(bundle_version);
		return parseVersion(versionString);
	}

}
