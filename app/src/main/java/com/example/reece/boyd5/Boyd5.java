package com.example.reece.boyd5;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/*
File name: Boyd5.java
Due date: 11/17/2016 @ 1:20PM
Author: Reece Boyd
Course: CSCI3033
Description: Main activity for an app that that searches words at dictionary.com and stores previous
searches in a list view so that they may be searched again later.
Input/Output: A string into an EditText and a click on the "SAVE" button will create a search in the
list view. The user can then click on the search to search the word at dictionary.com.
 */

public class Boyd5 extends AppCompatActivity {

    private ListAdapter listAdapter;        // The bridge between a ListView and the data that backs the list.
    private ListView listView;              // A view that shows items in a vertically scrolling list. The items come from the ListAdapter associated with this view.
    private Button saveButton;              // Buttons can be pressed, or clicked, by the user to perform an action. This one will save the user's search to listView.
    private EditText searchEditText;        // EditText is a thin veneer over TextView that configures itself to be editable. This is where the user will enter input to become a search upon hitting saveButton.
    private ArrayList<String> searches;     // Resizable-array implementation of the List interface. This will be used to store searches from searchEditText as Strings so that listAdapter can show them in listView.
    SharedPreferences preferences;          // Interface for accessing and modifying preference data. This is used to keep persistent storage of the user's searches.
    SharedPreferences.Editor editor;        // Interface used for modifying values in a SharedPreferences object. This allows us to edit the user's preferences.

    /*
    Purpose: Called when the activity is starting. This is where objects are initialized and the UI is inflated.
    Parameters: Bundle savedInstateState:  If the activity is being re-initialized after previously being shut down then
    this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
    Pre-condition: None.
    Post-condition: Objects are initalized and UI is inflated.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);         // initialized activity with appropriate data.
        setContentView(R.layout.activity_boyd5);    // inflates the UI.

        searches = new ArrayList<String>();         // allocates memory for an ArrayList of strings.
        listAdapter = new ArrayAdapter<String>(this, R.layout.boyd5_list_item, searches);       // creates our listAdapter using the activity's context, the list item layout, and our array of searches.
        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);     // grabs the preferences from the user's phone.
        editor = preferences.edit();    // allocates our preferences editor to alter the user's preferences.

        listView = (ListView) findViewById(R.id.boyd5_list_view);                       // finds our listView in the layout.
        saveButton = (Button) findViewById(R.id.boyd5_button_in_grid_layout);           // finds out saveButton in the layout.
        searchEditText = (EditText) findViewById(R.id.boyd5_edit_text_in_grid_layout);  // finds out searchEditText in the layout.

        searches.addAll(preferences.getAll().keySet());     // stores the contents of our user's preferences into our searches ArrayList.

        listView.setAdapter(listAdapter);                                           // links our adapter to listView so that it can keep it in sync searches.
        saveButton.setOnClickListener(new saveButtonOnClickListener());             // Adds custom onClickListening functionality to our saveButton. More details below.
        listView.setOnItemClickListener(new onBoyd5ItemClickListener());            // Adds custom onItemClickListening functionality to items in our listView. More details below.
        listView.setOnItemLongClickListener(new onBoyd5ItemLongClickListener());    // Adds custom onItemLongClickListening functionality to items in our listView. More details below.
    }

    /*
    Purpose: Updates the user's sharedPreferences with the most up-to-date search data.
    Parameters: None.
    Pre-condition: An object of type SharedPreferences.Editor and an object of type ArrayList<String> have been created.
    Post-condition: The user's sharedPreferences contain the most up-to-date search history.
    */
    private void updatePreferences() {
        editor.clear();
        for (String search : searches) {
            editor.putString(search, search);
        }
        editor.commit();
    }

    // Private inner class for implementation of onClickListening for our saveButton.
    private class saveButtonOnClickListener implements View.OnClickListener {
        /*
        Purpose: Create a search in our listView when the user provides valid input to searchEditText.
        Parameters: The view of saveButton.
        Pre-condition: searchEditText and listView have been found via ID from the layout. Additionally, an ArrayList<String> needs to have been allocated and connected to listView via a ListAdapter.
        Post-condition: Search will be saved to the ArrayList, added to the user's preferences, and shown to the user via the listView.
        */
        @Override
        public void onClick(View v) {
            if (searchEditText.getText().toString().length() > 0) {     // if there was valid input...
                searches.add(searchEditText.getText().toString());      // add the contents of editText to searches.
                updatePreferences();                                    // update the user's sharedPreferences since the searches have changed/
                listView.invalidateViews();                             // tells listView to invalidate all its child item views (redraw them).

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());  // Creates a builder for an alert dialog.
                builder.setMessage("Another fine search to your list!");                // Creates a message that will show to the user when the alert is shown.
                builder.setCancelable(true);                                            // Enable the ability for the user to dismiss the alert by clicking away from its view.

                final AlertDialog alert = builder.create(); // Create the AlertDialog with the contents of the builder.

                alert.show();                               // Show the alert to the user.

                final Timer t = new Timer();    // Create a timer object to automatically dismiss the alert after 1000ms or 1 second.
                t.schedule(new TimerTask() {
                    public void run() {
                        alert.dismiss();
                        t.cancel(); }}, 1000);
            } else {    // but if the input was not valid...
                AlertDialog.Builder alert = new AlertDialog.Builder(Boyd5.this);            // Creates a builder for an alert dialog.

                alert.setTitle("Give us something to work with here, geez!");               // Sternly tell the user that you don't appreciate invalid input.
                alert.setMessage("Please enter a valid word to search in the dictionary."); // Explain to them what the meaning of valid input really is.

                alert.setPositiveButton("Ok, I'll fix my act!", new DialogInterface.OnClickListener() { // Give them the option to change their ways. Everyone is capable of changin'.
                    public void onClick(DialogInterface dialog, int which) {}});
                alert.show();   // Show this alert to the user.
            }
        }
    }

    // Private inner class for implementation of onItemClickListening for list items.
    private class onBoyd5ItemClickListener implements AdapterView.OnItemClickListener {
        /*
        Purpose: Search the word from our stored searches at Dictionary.com.
        Parameters: The AdapterView where the click happened, the view within the AdapterView that was
        clicked, the position of the view in the adapter, and the row id of the item that was clicked.
        Pre-condition: searches contains a String which would also mean that said String is shown to
        the user via the listView and has this onItemClickListener attached to it.
        Post-condition: The user has been navigated to Dictionary.com's definition page for the word selected.
        */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String url = "http://www.dictionary.com/browse/" + searches.get(position);  // append the word to be searched to the appropriate URL.
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));             // Create a new Intent from this URL.
            startActivity(intent);                                                      // Activate that Intent and navigate the user to the URL.
        }
    }

    // Private inner class for implementation of onItemLongClickListening for list items.
    private class onBoyd5ItemLongClickListener implements AdapterView.OnItemLongClickListener {
        /*
        Purpose: Allow the user to edit the stored word or delete it entirely (or do neither).
        Parameters: The AdapterView where the click happened, the view within the AdapterView that was
        clicked, the position of the view in the adapter, and the row id of the item that was clicked.
        Pre-condition: searches contains a String which would also mean that said String is shown to
        the user via the listView and has this onItemLongClickListener attached to it.
        Post-condition: User has deleted or edited the word in their search history (or done neither).
        */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
            AlertDialog.Builder alert = new AlertDialog.Builder(Boyd5.this);    // Creates a builder for an alert dialog.

            final EditText editText = new EditText(Boyd5.this);                 // Create an EditText for the user to replace the text in the search if that is their wish.
            alert.setTitle("Do you want to change this search or delete it?");  // Inform the user with their options here.
            alert.setMessage("Enter the new search.");                          // Tell them they can enter a new search into the EditText.
            editText.setText(searches.get(position));                           // Initialize the text of the EditText to be the current word.
            alert.setView(editText);                                            // Set this EditText to be a part of the view of the AlertDialog.
            alert.setPositiveButton("Replace", new DialogInterface.OnClickListener() {  // Creates a 'Replace' button for the user.
                /*
                Purpose: To Replace the String in the current search to a new search.
                Parameters: The dialog that received the click and the button that was clicked or the position of the item clicked.
                Pre-condition: The AlertDialog has been shown to the user and they have selected the 'Replace' option.
                Post-condition: The text in the search has been changed to the String in the EditText within the AlertDialog.
                */
                public void onClick(DialogInterface dialog, int which) {
                    searches.remove(position);                              // remove the current word from searches.
                    searches.add(position, editText.getText().toString());  // add the new word from the EditText into searches.
                    updatePreferences();                                    // update the user's preferences with this change.
                    listView.invalidateViews();                             // tells listView to invalidate all its child item views (redraw them).

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());   // Creates a builder for an alert dialog.
                    builder.setMessage("Excellent changes good sir!");                          // Create a message to inform the user of their action.
                    builder.setCancelable(true);                                                // Allow this message to be dismissible if the user clicks away from the alert.

                    final AlertDialog alert = builder.create();  // Create the alert via the builder.

                    alert.show();                               // Show this alert to the user.

                    final Timer t = new Timer();    // Create a timer object to automatically dismiss the alert after 1000ms or 1 second.
                    t.schedule(new TimerTask() {
                        public void run() {
                            alert.dismiss();
                            t.cancel(); }}, 1000);
                }
            });

            alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                /*
                Purpose: To dismiss the AlertDialog without changing the search history.
                Parameters: The dialog that received the click and the button that was clicked or the position of the item clicked.
                Pre-condition: The AlertDialog has been shown to the user and they have selected the 'Cancel' option.
                Post-condition: The AlertDialog is dismissed and no changes have been made.
                */
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());   // Creates a builder for an alert dialog.
                    builder.setMessage("We thought they were good, too!");                      // Create a message to inform the user of their action.
                    builder.setCancelable(true);                                                // Allow this message to be dismissible if the user clicks away from the alert.

                    final AlertDialog alert = builder.create(); // Create the alert via the builder.

                    alert.show();                               // Show this alert to the user.

                    final Timer t = new Timer();    // Create a timer object to automatically dismiss the alert after 1000ms or 1 second.
                    t.schedule(new TimerTask() {
                        public void run() {
                            alert.dismiss();
                            t.cancel(); }}, 1000); }
            });

            alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                /*
                Purpose: To delete a search word in the listView and from the user's preferences.
                Parameters: The dialog that received the click and the button that was clicked or the position of the item clicked.
                Pre-condition: The AlertDialog has been shown to the user and they have selected the 'Delete' option.
                Post-condition: The search has been deleted from the listView and from the user's preferences.
                */
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    searches.remove(position);      // remove the word from searches.
                    updatePreferences();            // update the user's preferences with this change.
                    listView.invalidateViews();     // tells listView to invalidate all its child item views (redraw them).

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());   // Creates a builder for an alert dialog.
                    builder.setMessage("Aww, we liked that one.");                              // Create a message to inform the user of their action.
                    builder.setCancelable(true);                                                // Allow this message to be dismissible if the user clicks away from the alert.

                    final AlertDialog alert = builder.create(); // Create the alert via the builder.

                    alert.show();                               // Show this alert to the user.

                    final Timer t = new Timer();    // Create a timer object to automatically dismiss the alert after 1000ms or 1 second.
                    t.schedule(new TimerTask() {
                        public void run() {
                            alert.dismiss();
                            t.cancel(); }}, 1000);
                }
            });

            alert.show();   // Show this alert to the user.
            return true;    // returning true from longClickListener indicates that you don't want further processing. Meaning you don't want to call onClickListener right after onLongClickListener.
        }
    }
}

