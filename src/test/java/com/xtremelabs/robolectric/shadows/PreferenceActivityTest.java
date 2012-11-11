package com.xtremelabs.robolectric.shadows;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import android.preference.Preference;
import android.preference.PreferenceScreen;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.preference.PreferenceActivity;
import android.widget.ListView;

import com.xtremelabs.robolectric.R;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.WithTestDefaultsRunner;

@RunWith(WithTestDefaultsRunner.class)
public class PreferenceActivityTest {
	static final String PREF_KEY = "prefKey";

	private TestPreferenceActivity activity;
	private ShadowPreferenceActivity shadow;
	
    @Before
    public void setUp() throws Exception {
    	activity = new TestPreferenceActivity();
    	shadow = Robolectric.shadowOf(activity);
    }
    
    @Test
    public void shouldGetListView() {
    	shadow.setListView( new ListView( new Activity() ) );
    	assertThat( activity.getListView(), notNullValue() );    	
    }
    
	@Test
	public void shouldInheritFromListActivity() {
		assertThat(shadow, instanceOf(ShadowListActivity.class));
	}
	
	@Test
	public void shouldNotInitializePreferenceScreen() {
		assertThat(activity.getPreferenceScreen(), nullValue());
	}
    
	@Test
	public void shouldRecordPreferencesResourceId() {
		assertThat(shadow.getPreferencesResId(), equalTo(-1));
		activity.addPreferencesFromResource(R.xml.preferences);
		assertThat(shadow.getPreferencesResId(), equalTo(R.xml.preferences));		
	}
	
	@Test
	public void shouldLoadPreferenceScreen() {
		activity.addPreferencesFromResource(R.xml.preferences);
		assertThat( activity.getPreferenceScreen().getPreferenceCount(), equalTo(6));
	}

	@Test
	public void shouldSetKeyTitleAndSummaryFromResourcesWhilstInflatingXmlLayout() throws Exception {
		activity.addPreferencesFromResource(R.xml.preferences);
		final PreferenceScreen preferenceScreen = activity.getPreferenceScreen();
		final int referenceUsingPreference = 4;
		final Preference preference = preferenceScreen.getPreference(referenceUsingPreference);
		assertThat(preference, not(nullValue()));
		assertThat(preference.getKey().toString(), equalTo(PREF_KEY));
		assertThat(preference.getTitle().toString(), equalTo("prefTitle"));
		assertThat(preference.getSummary().toString(), equalTo("prefSummary"));
	}

	@Test
	public void shouldFindResourceByKey() {
		activity.addPreferencesFromResource(R.xml.preferences);

		Preference preference = activity.findPreference(PREF_KEY);
		assertThat(preference, not(nullValue()));
		assertThat(preference.getKey().toString(), equalTo(PREF_KEY));
	}

	@Test
	public void shouldFindPreferenceByKeyViaPrefScreenAndDirectly() throws Exception
	{
		activity.addPreferencesFromResource(R.xml.preferences);

		Preference preferenceDirectly = activity.findPreference(PREF_KEY);
		Preference preferenceViaScreen = activity.getPreferenceScreen().findPreference(PREF_KEY);
		assertSame(preferenceViaScreen, preferenceDirectly);
	}


	private static class TestPreferenceActivity extends PreferenceActivity {
	}
}
