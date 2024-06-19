
## So库动态加载

APK体积优化其中有一点涉及so库的动态加载。工作流程是在原始项目构建时不会把so库加入到apk文件中，而是在app运行过程中找到合适的时机,
通过网络把so库文件下载手机本地，从而减少apk的体积。随着现在app功能日益丰富，使用的三方so库增多，该流程的效果更加突出。
学习DEMO
运行DEMO方式:
1. push你手机架构的[So文件](/lib)到手机`/data/data/com.modi.dynamic/files/dynamic_so/`文件目录下




[文章详细介绍](https://wds1204.github.io/2024/06/19/%E5%85%B3%E4%BA%8ESo%E5%BA%93%E7%9A%84%E5%8A%A8%E6%80%81%E5%8A%A0%E8%BD%BD/)

### So库的加载流程
```java
public class System{
	public static void loadLibrary(String libname) {
		Runtime.getRuntime().loadLibrary0(Reflection.getCallerClass(), libname);
	}

	private synchronized void loadLibrary0(ClassLoader loader, Class<?> callerClass, String libname) {
		if (libname.indexOf((int)File.separatorChar) != -1) {
			throw new UnsatisfiedLinkError(
				"Directory separator should not appear in library name: " + libname);
		}
		String libraryName = libname;

		if (loader != null && !(loader instanceof BootClassLoader)) {
			String filename = loader.findLibrary(libraryName);//加载so库
            //...
			return;
		}

	}
}
```

```java
 public class BaseDexClassLoader{
    @UnsupportedAppUsage
    private final DexPathList pathList;

    //...
     
	@Override
    public String findLibrary(String name) {
        return pathList.findLibrary(name);
    }

    //...
}
```
DexPathList--API
```java
public class DexPathList{

    private final File[] nativeLibraryDirectories;
    //...

    public String findLibrary(String libraryName) {
        String fileName = System.mapLibraryName(libraryName);
        for (File directory : nativeLibraryDirectories) {
            File file = new File(directory, fileName);
            if (file.exists() && file.isFile() && file.canRead()) {
                return file.getPath();
            }
        }
        return null;
    }
    //...
}
```
不同的版本代码有一定的差别
系统的过程是通过classoader检查native目录是否存在so库然后进行加载，只要我们把自定义的path路径也加进来，是不是就可以了达到效果。这其实很classloader热修复差不多。