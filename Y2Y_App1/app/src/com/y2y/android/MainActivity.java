/*
 * Copyright (c) 2012-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.y2y.android;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.support.design.widget.NavigationView;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity implements ControlFragInterface{

    private RestClient client;
    private ArrayAdapter<String> listAdapter;

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mToggle;
	private Toolbar mToolbar;

	private String soql_temp = "empty";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create the toolbar and replace the original action bar with our own toolbar
		mToolbar = (Toolbar) findViewById(R.id.nav_action);
		setActionBar(mToolbar);

		// set up our drawer containing the options
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_opened, R.string.drawer_closed);

		mDrawerLayout.addDrawerListener(mToggle);
		mToggle.syncState();

		// set the hamburger to open up the drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	
	@Override
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.drawer_layout).setVisibility(View.INVISIBLE);

		super.onResume();
	}

	@Override
	public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;

		// display the home screen as a default
		displaySelectedScreen(R.id.nav_home);

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem item) {
						int id = item.getItemId();
						item.setChecked(true);
						mDrawerLayout.closeDrawers();
						displaySelectedScreen(id);
						return true;
					}
				}
		);

		// Show everything
		findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
	}


	public void onFetchDetailsStay(View v) throws UnsupportedEncodingException {
		sendRequest("SELECT Name, Major_Warnings__c, Minor_Warnings__c, Last_Date_of_Stay__c, Locker_Combination__c, Id FROM Contact WHERE (Name='Monica Chiu')");

	}

	// Essentially trying to get this soql from several soql queries:
	// sendRequest("SELECT Bed__c.Name FROM Bed__c WHERE Bed__c.Id IN (SELECT Bed_Assignment__c.Bed__c FROM Bed_Assignment__c WHERE Bed_Assignment__c.Guest__c='0031D00000AWSc7QAH')");
	public void onFetchBed(View v) throws UnsupportedEncodingException {
		sendBedSoqlRequest1("SELECT Contact.Id FROM Contact WHERE (Name='Monica Chiu')");
	}

	public void onFetchLottery(View v) throws UnsupportedEncodingException {
		sendRequest("SELECT Name, Lottery_Date__c, Type__c FROM Lottery__c WHERE (Lottery_Date__c=2018-04-10 AND Type__c != 'Last Call') ORDER BY Type__c ASC");
	}


	public void sendRequest(String soql) throws UnsupportedEncodingException {
		RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, final RestResponse result) {
				result.consumeQuietly(); // consume before going back to main thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONArray records = result.asJSONObject().getJSONArray("records");
							// FIND OUT HOW TO CHECK CURRENT FRAGMENT AND CREATE ONE BASED ON CURRENT FRAGMENT
							// Currently in home screen so display details of stay
							HomeFragment homeFragment = (HomeFragment) getFragmentManager().findFragmentByTag("HomeScreen");
							LotteryFragment lotteryFragment = (LotteryFragment) getFragmentManager().findFragmentByTag("LotteryScreen");
							if (homeFragment != null && homeFragment.isVisible()) {
								homeFragment.setDetailsStay(records);
							} else if (lotteryFragment != null && lotteryFragment.isVisible()) {
								lotteryFragment.setLottery(records);
							}

						} catch (Exception e) {
							onError(e);
						}
					}
				});
			}

			public void onError(final Exception exception) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						exception.printStackTrace();
					}
				});
			}


		});
	}

	public void sendBedSoqlRequest1(String soql) throws UnsupportedEncodingException {
		RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, final RestResponse result) {
				result.consumeQuietly(); // consume before going back to main thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONArray records = result.asJSONObject().getJSONArray("records");
							String guest_id = records.getJSONObject(0).getString("Id");
							String soql_the_bedName = "SELECT Bed__c.Name FROM Bed__c WHERE Bed__c.Id IN (SELECT Bed_Assignment__c.Bed__c FROM Bed_Assignment__c WHERE Bed_Assignment__c.Guest__c='" +
									guest_id + "')";
							sendBedSoqlRequest2(soql_the_bedName);
						} catch (Exception e) {
							onError(e);
						}
					}
				});
			}
			public void onError(final Exception exception) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						exception.printStackTrace();
					}
				});
			}
		});
	}

	public void sendBedSoqlRequest2(String soql) throws UnsupportedEncodingException {
		RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, final RestResponse result) {
				result.consumeQuietly(); // consume before going back to main thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONArray other_records = result.asJSONObject().getJSONArray("records");
							HomeFragment receivingFragment2 = (HomeFragment) getFragmentManager().findFragmentByTag("HomeScreen");
							receivingFragment2.setBed(other_records);
						} catch (Exception e) {
							onError(e);
						}
					}
				});
			}

			public void onError(final Exception exception) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						exception.printStackTrace();
					}
				});
			}

		});
	}


	private void displaySelectedScreen(int id) {
		Fragment fragment = null;
		String tag = null;
		// set the initialized fragment to the fragment with the corresponding id
		switch(id) {
			case R.id.nav_home:
				fragment = new HomeFragment();
				tag = "HomeScreen";
				break;
			case R.id.nav_lottery:
				fragment = new LotteryFragment();
				tag = "LotteryScreen";
				break;
			case R.id.nav_schedule:
				fragment = new ScheduleFragment();
				tag = "ScheduleScreen";
				break;
			case R.id.nav_handbook:
				fragment = new HandbookFragment();
				tag = "HandbookScreen";
				break;
		}

		// set the current screen to the fragment inputted as an argument
		if (fragment != null && tag != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.content_frame,fragment,tag);
			ft.replace(R.id.content_frame, fragment);
			ft.commit();
		}

		// close drawer
		mDrawerLayout.closeDrawer(GravityCompat.START);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mToggle.onOptionsItemSelected(item)) {
			Toast.makeText(this, "woah", Toast.LENGTH_SHORT).show();
			return true;
		}

		else {
			Toast.makeText(this, "haha", Toast.LENGTH_SHORT).show();
		}

		return super.onOptionsItemSelected(item);
	}


}
