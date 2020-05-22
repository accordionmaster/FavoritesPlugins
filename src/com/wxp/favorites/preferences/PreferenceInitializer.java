package com.wxp.favorites.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.wxp.favorites.FavoritesActivator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = FavoritesActivator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.FAVORITES_VIEW_NAME_COLUMN_VISIBLE, true);
	}

}
