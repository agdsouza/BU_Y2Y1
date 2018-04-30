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
package com.mytrail.android;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity implements ControlFragInterface{

    private RestClient client;
    private ArrayAdapter<String> listAdapter;

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mToggle;
	private Toolbar mToolbar;

	private ArrayList<ActionItem> actionItems; // holds an array representing action item data

	private String soql_temp = "empty";
    private String current_screen = null;


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

		actionItems = new ArrayList<ActionItem>();

		// set the hamburger to open up the drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);

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


	public void onFetchDetailsStay(View v) throws UnsupportedEncodingException {
		sendRequest("SELECT Name, Major_Warnings__c, Minor_Warnings__c, Last_Date_of_Stay__c, Locker_Combination__c, Id FROM Contact WHERE (Name='Monica Chiu')");
	}

	// Essentially trying to get this soql from several soql queries:
	// sendRequest("SELECT Bed__c.Name FROM Bed__c WHERE Bed__c.Id IN (SELECT Bed_Assignment__c.Bed__c FROM Bed_Assignment__c WHERE Bed_Assignment__c.Guest__c='0031D00000AWSc7QAH')");
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

	/*
	 * Using a given first name and last name, makes a SOQL request to obtain the id of the user
	 */
	@Override
	public void onFetchActionItems(View v) throws UnsupportedEncodingException{
//		Toast.makeText(getApplicationContext(), "hey there 2", Toast.LENGTH_LONG).show();
		sendActionIdSoqlRequest1("SELECT Contact.Id FROM Contact WHERE (Name='Adam Adkinson')");
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

	/*
	 * Obtains the ID of the user, then makes a SOQL query to obtain the Case Management session ( this
	 * allows only the most recent action item updates to be displayed, instead of older versions
	 */
	public void sendActionIdSoqlRequest1(String soql) throws UnsupportedEncodingException {
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
							// soql request to get the case management session name, ordering it by date
							// to obtain the most recent one
							String soql_cms_name = "SELECT Case_Management_Session__c.Name FROM Case_Management_Session__c WHERE (Guest__c='" +
									guest_id + "') ORDER BY Date__c DESC";
							// sends the SOQL query
							sendActionCMSSoqlRequest(soql_cms_name);
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

	/*
	 * Obtains the most recent case management session name, and uses it to create a SOQL query that gets
	 * all the action items within that CMS
	 */
	public void sendActionCMSSoqlRequest(String soql) throws UnsupportedEncodingException {
		RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, final RestResponse result) {
				result.consumeQuietly(); // consume before going back to main thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONArray cms_records = result.asJSONObject().getJSONArray("records");
							String cms_name = cms_records.getJSONObject(0).getString("Name");
							// create soql request to obtain all action items for specific user in specific cms
							String soql_action_items = "SELECT Action_Item__c FROM Case_Management_Session_Touchpoint__c WHERE (Case_Management_Session__c='" +
									cms_name + "')";
							// send SOQL query to obtain action items
							sendActionInfoSoqlRequest(soql_action_items);
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

	/*
	 * Obtains all the action items names within a specific case management session, and uses those names to create
	 * a SOQL query that gets hte id, description, due date, status, type, and number of steps
	 */
	public void sendActionInfoSoqlRequest(String soql) throws UnsupportedEncodingException {
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
							actionItems.clear();
							for (int i = 0; i < records.length(); i++) {
								// gets all of the action items of a specific CMS
								String action_item_id = records.getJSONObject(i).getString("Action_Item__c");
								if (!action_item_id.equals("null") || action_item_id != null) { // checks if entry is really an action item
									String soql_action_info = "SELECT Name, Id, Action_Item_Narrative__c, Due_Date__c, Status__c," +
											" Type__c, Number_of_Steps__c FROM Action_Item__c WHERE (Id='" + action_item_id + "')";
									getActionInfoRequest(soql_action_info);
								}
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

	/*
	 * Obtains the action item information specified in the sendActionInfoSOQLRequest function, and populates
	 * an Arraylist with this information by storing it in an ActionItem object, as long as the action item is
	 * not completed or dropped. Then, for each action item, it created a SOQL query that gets information on the steps
	 * of each action item, such as the name, id, step number, status, and due date
	 */
	public void getActionInfoRequest(String soql) throws UnsupportedEncodingException {
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
							// parses information and stores fields in a class variable
							ActionItem actionItem = initializeActionItem(records);
							// only adds item if its type is an Action Item, and it is not already completed or dropped
							if (actionItem.getActionItemType().equals("Action Item") && actionItem.getActionItemStatus().equals("Planned")) {
								actionItems.add(actionItem);
							}
							// creates a SOQL query that gets all the steps of an action item and other information
							String soql_step = "SELECT Name, Id, Action_Item__c, Step_Number__c, CompletedCB__c, Due__c FROM Action_Item_Step__c WHERE (Action_Item__c='" + actionItem.getActionItemId() + "')";
							getActionStepSoqlRequest(soql_step);
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

	/*
	 * Obtains the step information from the query made in getActionInfoRequest, then creates an arraylist of
	 * steps that will be added to the corresponding action item. The function in the ActionFragment, setActionItems,
	 * is then called to put these items in a list adapter.
	 */
	public void getActionStepSoqlRequest(String soql) throws UnsupportedEncodingException {
		RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(this), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, final RestResponse result) {
				result.consumeQuietly(); // consume before going back to main thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONArray step_records = result.asJSONObject().getJSONArray("records");
							ArrayList<Step> steps = new ArrayList<>();
							// loop through every step and initialize a new Step object, then add to the arraylist
							for (int i = 0; i < step_records.length(); i++) {
								Step step = initializeStep(step_records, i);
								steps.add(step);
							}
							Collections.sort(steps); // sort steps by the step number, ascending

							// set action item's step attribute to the step array
							int actInd = actionItems.indexOf(findActionById(steps.get(0).getActionItemId()));
							actionItems.get(actInd).setActionItemSteps(steps);
							// create an instance of the ActionFragment and render the list adapter
							ActionFragment actionFragment = (ActionFragment) getFragmentManager().findFragmentByTag("ActionScreen");
							actionFragment.setActionItems(actionItems);
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

	/*
	 * Inserts the update of a completed action item into the database
	 */

	public void insertCompletedActionUpdate(int actionCompletedPosition) throws IOException {

		// Hash map of fields (string and object)
		Map<String, Object> createCompletedActionInfo = new HashMap();

		// populate hashmap with the new status of the action item (int this case, completed)
		createCompletedActionInfo.put("Status__c", "Completed");

		RestRequest restRequest = RestRequest.getRequestForUpdate(ApiVersionStrings.getVersionNumber(this), "Action_Item__c", actionItems.get(actionCompletedPosition).getActionItemId(), createCompletedActionInfo);

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

	/*
	 * Inserts the update of a dropped action item into the database
	 */
	public void insertDroppedActionUpdate(int actionDroppedPosition) throws IOException {

		// Hash map of fields (string and object)
		Map<String, Object> createDroppedActionInfo = new HashMap();

		// populate hashmap with the new status of the action item (int this case, dropped)
		createDroppedActionInfo.put("Status__c", "Dropped");

		RestRequest restRequest = RestRequest.getRequestForUpdate(ApiVersionStrings.getVersionNumber(this), "Action_Item__c", actionItems.get(actionDroppedPosition).getActionItemId(), createDroppedActionInfo);

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

	/*
	 * Inserts the update of a completed step into the database
	 */
	public void insertStepUpdate(int actionPosition, int stepPosition) throws IOException {

		// Hash map of fields (string and object)
		Map<String, Object> createStepUpdateInfo = new HashMap();

		//populate hashmap with a "true" status, as they have checked the "completed" box
		createStepUpdateInfo.put("CompletedCB__c", "true");

		RestRequest restRequest = RestRequest.getRequestForUpdate(ApiVersionStrings.getVersionNumber(this), "Action_Item_Step__c", actionItems.get(actionPosition).getActionItemSteps().get(stepPosition).getStepId(), createStepUpdateInfo);

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

	/*
	 * Inserts an update of a reason for either a completed update or a dropped update
	 */
	public void insertReasonUpdate(int actionPosition, String reason) throws IOException {

		// Hash map of fields (string and object)
		Map<String, Object> createReasonUpdateInfo = new HashMap();

		// add the value of Action_Item__c into the hashmap
		createReasonUpdateInfo.put("Action_Item__c", actionItems.get(actionPosition).getActionItemId());
		// add the value of today's date into the hashmap
		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
		String date = df.format(new Date());
		createReasonUpdateInfo.put("Date__c", date);
		// add the value of the reason for the update into the hashmap
		createReasonUpdateInfo.put("Update__c", reason);

		RestRequest restRequest = RestRequest.getRequestForCreate(ApiVersionStrings.getVersionNumber(this), "Action_Item_Update__c", createReasonUpdateInfo);

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


	/*
	 * Takes a JSONArray as input, and parses through it to obtain the name, id, narrative, status,
	 * due date, type, and number of steps, and stores that information into a ActionItem object, which
	 * is then returned.
	 */
	private ActionItem initializeActionItem(JSONArray records) throws JSONException{
		ActionItem actionItem;

		// parse through the JSON record to get the required fields
		String actionName = records.getJSONObject(0).getString("Name");
		String actionId = records.getJSONObject(0).getString("Id");
		String actionNarrative = records.getJSONObject(0).getString("Action_Item_Narrative__c");
		String actionStatus = records.getJSONObject(0).getString("Status__c");
		String actionDueDate = records.getJSONObject(0).getString("Due_Date__c");
		String actionType = records.getJSONObject(0).getString("Type__c");
		int numSteps = Double.valueOf(records.getJSONObject(0).getString("Number_of_Steps__c")).intValue();

		// instantiate an ActionItem object and store the database information
		actionItem = new ActionItem(actionName, actionId);
		actionItem.setActionItemNarrative(actionNarrative);
		actionItem.setActionItemStatus(actionStatus);
		actionItem.setActionItemDueDate(actionDueDate);
		actionItem.setActionItemType(actionType);
		actionItem.setActionItemNumSteps(numSteps);

		return actionItem;
	}

	/*
	 * Takes a JSONArray records as input, along with an index, and parses through the records to obtain
	 * the step name, associated action item id, step id, step number, status, and due date, and stores that
	 * information into a Step object, which is then returned.
	 */
	private Step initializeStep(JSONArray records, int i) throws JSONException {
		Step step;

		// parse through the JSON records to obtain the required fields at index i
		String stepName = records.getJSONObject(i).getString("Name");
		String actionItemId = records.getJSONObject(i).getString("Action_Item__c");
		String stepId = records.getJSONObject(i).getString("Id");
		int stepNum = Double.valueOf(records.getJSONObject(i).getString("Step_Number__c")).intValue();
		Boolean isCompleted = Boolean.parseBoolean(records.getJSONObject(i).getString("CompletedCB__c"));
		String stepDue = records.getJSONObject(i).getString("Due__c");

		// instantiate an new step object, and add the obtained information into the step object
		step = new Step(actionItemId, stepId);
		step.setStepName(stepName);
		step.setStepNum(stepNum);
		step.setCompleted(isCompleted);
		step.setStepDue(stepDue);

		return step;
	}

	/*
	 * Given an action item id, this returns the associated action item
	 */
	private ActionItem findActionById(String id) {
		for (ActionItem action : actionItems) { // iterate through all the action items
			if (action.getActionItemId().equals(id)) {
				return action;
			}
		}
		return null; // if not found, return null
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
            case R.id.nav_action:
                fragment = new ActionFragment();
                tag = "ActionScreen";
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
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


}
