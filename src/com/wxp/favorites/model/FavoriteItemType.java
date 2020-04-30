package com.wxp.favorites.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.wxp.favorites.FavoritesLog;

public abstract class FavoriteItemType implements Comparable<FavoriteItemType> {

	private static final ISharedImages PALTFORM_IMAGES = PlatformUI.getWorkbench().getSharedImages();

	private final String id;
	private final String printName;
	private final int ordinal;

	public FavoriteItemType(String id, String name, int position) {
		this.id = id;
		this.printName = name;
		this.ordinal = position;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return printName;
	}

	public abstract Image getImage();

	public abstract IFavoriteItem newFavorite(Object obj);

	public abstract IFavoriteItem loadFavorite(String info);

	@Override
	public int compareTo(FavoriteItemType other) {
		return this.ordinal - other.ordinal;
	}

	public static final FavoriteItemType UNKNOW = new FavoriteItemType("Unknow", "Unknow", 0) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			return null;
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return null;
		}

		@Override
		public Image getImage() {
			return null;
		}
	};

	public static final FavoriteItemType WORKBENCH_FILE = new FavoriteItemType("WBFile", "Workbench File", 1) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IFile)) {
				return null;
			}
			return new FavoriteResource(this, (IFile)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteResource.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return PALTFORM_IMAGES.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);
		}
	};
	
	public static final FavoriteItemType WORKBENCH_FOLDER = new FavoriteItemType("WBFolder", "Workbench Foler", 2) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IFolder)) {
				return null;
			}
			return new FavoriteResource(this, (IFolder)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteResource.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return PALTFORM_IMAGES.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER);
		}
	};
	
	public static final FavoriteItemType WORKBENCH_PROJECT = new FavoriteItemType("WBProj", "WorkbenchProject", 3) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IProject)) {
				return null;
			}
			return new FavoriteResource(this, (IProject)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteResource.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return PALTFORM_IMAGES.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_PROJECT);
		}
	};
	
	public static final FavoriteItemType JAVA_PROJECT = new FavoriteItemType("JProj", "Java Project", 4) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IJavaProject)) {
				return null;
			}
			return new FavoriteJavaElement(this, (IJavaProject)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteJavaElement.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return PALTFORM_IMAGES.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_PROJECT);
		}
	};
	
	public static final FavoriteItemType JAVA_PACKAGE_ROOT = new FavoriteItemType("JPkgRoot", "Java Package Root", 5) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IPackageFragmentRoot)) {
				return null;
			}
			return new FavoriteJavaElement(this, (IPackageFragmentRoot)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteJavaElement.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return PALTFORM_IMAGES.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER);
		}
	};
	
	
	public static final FavoriteItemType JAVA_PACKAGE = new FavoriteItemType("JPkg", "Java Package", 6) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IPackageFragment)) {
				return null;
			}
			return new FavoriteJavaElement(this, (IPackageFragment)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteJavaElement.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
		}
	};
	
	
	public static final FavoriteItemType JAVA_CLASS_FILE = new FavoriteItemType("JClass", "Java Class File", 7) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IClassFile)) {
				return null;
			}
			return new FavoriteJavaElement(this, (IClassFile)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteJavaElement.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CFILE);
		}
	};
	
	
	public static final FavoriteItemType JAVA_COMP_UNIT = new FavoriteItemType("JCompUnit", "Java Compilation Unit", 8) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof ICompilationUnit)) {
				return null;
			}
			return new FavoriteJavaElement(this, (ICompilationUnit)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteJavaElement.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT);
		}
	};
	
	public static final FavoriteItemType JAVA_INTERFACE = new FavoriteItemType("JInterface", "Java Interface", 9) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IType)) {
				return null;
			}
			try {
				if (!((IType)obj).isInterface()) {
					return null;
				}
			} catch (JavaModelException e) {
				FavoritesLog.logError(e);
			}
			return new FavoriteJavaElement(this, (IType)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteJavaElement.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_INTERFACE);
		}
	};
	
	public static final FavoriteItemType JAVA_CLASS = new FavoriteItemType("JClass", "Java Class", 10) {

		@Override
		public IFavoriteItem newFavorite(Object obj) {
			if (!(obj instanceof IType)) {
				return null;
			}
			try {
				if (((IType)obj).isInterface()) {
					return null;
				}
			} catch (JavaModelException e) {
				FavoritesLog.logError(e);
			}
			return new FavoriteJavaElement(this, (IType)obj);
		}

		@Override
		public IFavoriteItem loadFavorite(String info) {
			return FavoriteJavaElement.loadFavorite(this, info);
		}

		@Override
		public Image getImage() {
			return org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);
		}
		
	};
	
	
	private static final FavoriteItemType[] TYPES = { UNKNOW, WORKBENCH_FILE,
			WORKBENCH_FOLDER, WORKBENCH_PROJECT, 
			JAVA_PROJECT, JAVA_PACKAGE_ROOT, JAVA_PACKAGE,
			JAVA_CLASS_FILE, JAVA_COMP_UNIT, JAVA_INTERFACE, JAVA_CLASS
	};
	
	public static FavoriteItemType[] geTypes() {
		return TYPES;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
