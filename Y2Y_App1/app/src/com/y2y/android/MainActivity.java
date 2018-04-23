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
import android.support.annotation.NonNull;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity implements ControlFragInterface{

    private RestClient client;

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mToggle;
	private Toolbar mToolbar;
	private ArrayList<String> lottery_names = new ArrayList<>();

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

		// set default screen to be home screen (details of stay)
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

		// Show everything
		findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
	}


	// Send soql query through sendRequest
    // Flow: onFetchDetailsStay -> sendRequest -> HomeFragment's method setDetailsStay to display the data
	public void onFetchDetailsStay(View v) throws UnsupportedEncodingException {
		sendRequest("SELECT Name, Major_Warnings__c, Minor_Warnings__c, Last_Date_of_Stay__c, Locker_Combination__c, Id FROM Contact WHERE (Name='Monica Chiu')");
	}

	// Need to sendRequest multiple times to get the result needed!
    // Our full SOQL query but split into a few parts as deeply nested queries are not allowed in SOQL.
	// sendRequest("SELECT Bed__c.Name FROM Bed__c WHERE Bed__c.Id IN (SELECT Bed_Assignment__c.Bed__c FROM Bed_Assignment__c WHERE Bed_Assignment__c.Guest__c='0031D00000AWSc7QAH')");
    // Flow: onFetchBed -> sendBedSoqlRequest1 -> sendBedSoqlRequest2 -> HomeFragment's method setBed to display the data
	public void onFetchBed(View v) throws UnsupportedEncodingException {
		sendBedSoqlRequest1("SELECT Contact.Id FROM Contact WHERE (Name='Monica Chiu')");
	}

	public void onFetchLottery(View v) throws UnsupportedEncodingException {
		// Get current date in expected format
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(Calendar.getInstance().getTime());
		String soqlquery = "SELECT Name, Lottery_Date__c, Type__c FROM Lottery__c WHERE (Lottery_Date__c=" + date + ") ORDER BY Type__c ASC";
//		sendRequest(soqlquery);
		sendRequest("SELECT Name, Lottery_Date__c, Type__c FROM Lottery__c WHERE (Lottery_Date__c=2018-04-10) ORDER BY Type__c ASC");
	}

	public void onFetchLotteryNumber(View v) throws UnsupportedEncodingException {
		// Get current date in expected format
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(Calendar.getInstance().getTime());
		String soqlquery = "SELECT Name FROM Lottery__c WHERE (Lottery_Date__c=" + date + ") ORDER BY Type__c ASC";
//		sendRequest(soqlquery);
		sendLotterySoqlRequest1("SELECT Name FROM Lottery__c WHERE (Lottery_Date__c=2018-04-10) ORDER BY Type__c ASC");
//		sendLotterySoqlRequest("SELECT Lottery_Number_Daily__c FROM Lottery_Entry__c WHERE (Lottery__c='" + a_lot_name + "')");
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
							// Create the fragments to traverse to
							HomeFragment homeFragment = (HomeFragment) getFragmentManager().findFragmentByTag("HomeScreen");
							LotteryFragment lotteryFragment = (LotteryFragment) getFragmentManager().findFragmentByTag("LotteryScreen");
                            // Currently in home screen so display details of stay
							if (homeFragment != null && homeFragment.isVisible()) {
								homeFragment.setDetailsStay(records);
							} else if (lotteryFragment != null && lotteryFragment.isVisible()) { // In lottery screen so display lottery info
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
							String soql_the_bedName = "SELECT Name FROM Bed__c WHERE Bed__c.Id IN (SELECT Bed_Assignment__c.Bed__c FROM Bed_Assignment__c WHERE Bed_Assignment__c.Guest__c='" +
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
							JSONArray records = result.asJSONObject().getJSONArray("records");
							HomeFragment receivingFragment2 = (HomeFragment) getFragmentManager().findFragmentByTag("HomeScreen");
							receivingFragment2.setBed(records);

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

	public void sendLotterySoqlRequest1(String soql) throws UnsupportedEncodingException {
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
							for (int i=0;i<records.length();i++) {
								String a_lot_name = records.getJSONObject(i).getString("Name");
								String soql_the_lotName = "SELECT Lottery__c, Lottery_Number_Daily__c FROM Lottery_Entry__c WHERE (Lottery__c='" + a_lot_name + "')";
								sendLotterySoqlRequest2(soql_the_lotName);
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

	public void sendLotterySoqlRequest2(String soql) throws UnsupportedEncodingException {
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
							LotteryFragment receivingFragment = (LotteryFragment) getFragmentManager().findFragmentByTag("LotteryScreen");
							receivingFragment.setLotteryNumber(records);

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

	public void insertFeedback(String feedback) throws IOException {

		// Hash map of fields (string and object)
		Map<String, Object> createFeedbackInfo = new HashMap();

		createFeedbackInfo.put("Guest__c", "0031D00000AWSc7QAH");
		createFeedbackInfo.put("CM_First_Name_and_Last_Initial__c", "Monica C.");
		createFeedbackInfo.put("Date_Taken__c", "2018-04-23");
		//RecordTypeId = Guest App Record
		createFeedbackInfo.put("RecordTypeId", "0121D0000001NIoQAM");
		createFeedbackInfo.put("Comments_about_Y2Y__c", feedback);

		RestRequest restRequest = RestRequest.getRequestForCreate(ApiVersionStrings.getVersionNumber(this), "Survey__c", createFeedbackInfo);

		client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, final RestResponse result) {
				result.consumeQuietly(); // consume before going back to main thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							Log.e("FeedbackTest","Success!");
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
			case R.id.nav_feedback:
				fragment = new FeedbackFragment();
				tag = "FeedbackScreen";
				break;
			case R.id.nav_survey:
				fragment = new SurveyFragment();
				tag = "SurveyScreen";
				break;
		}

		// set the current screen to the fragment inputted as an argument
		if (fragment != null && tag != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			// add to back trace so users can use back button to go to prev fragments
			ft.add(R.id.content_frame,fragment,tag).addToBackStack("frag");
			ft.replace(R.id.content_frame, fragment);
			ft.commit();
		}

		// close drawer
		mDrawerLayout.closeDrawer(GravityCompat.START);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


}
