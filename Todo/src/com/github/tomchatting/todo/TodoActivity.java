package com.github.tomchatting.todo;

import com.github.tomchatting.todo.database.TodoDbAdapter;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class TodoActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		this.getListView().setDividerHeight(2);
		dbHelper = new TodoDbAdapter(this);
		dbHelper.open();
		Cursor myCur = null;

        myCur = dbHelper.fetchAllTodos();

        mListAdapter = new fillData(TodoActivity.this, myCur);
              
        setListAdapter(mListAdapter);
		registerForContextMenu(getListView());
		
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
			                                        long id, boolean checked) {
			    updateSubtitle(mode);
			}
			  
			private void updateSubtitle(ActionMode mode) {
			    mode.setSubtitle("("+listView.getCheckedItemCount()+")");
			}
		    @Override
		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		        // Respond to clicks on the actions in the CAB
		        switch (item.getItemId()) {
		            case R.id.todo_delete:
		                deleteSelectedItems();
		                mode.finish(); // Action picked, so close the CAB
		                return true;
		            default:
		                return false;
		        }
		    }

		    private void deleteSelectedItems() {
				// TODO Auto-generated method stub
				
			}

			@Override
		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		        // Inflate the menu for the CAB
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.edit_menu, menu);
		        return true;
		    }

		    @Override
		    public void onDestroyActionMode(ActionMode mode) {
		        // Here you can make any necessary updates to the activity when
		        // the CAB is removed. By default, selected items are deselected/unchecked.
		    }

		    @Override
		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		        // Here you can perform updates to the CAB due to
		        // an invalidate() request
		        return false;
		    }
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

	private TodoDbAdapter dbHelper;
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;

	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_task:
			createTodo();
			return true;
		case R.id.menu_refresh:
			Cursor myCur = null;

	        myCur = dbHelper.fetchAllTodos();

	        mListAdapter = new fillData(TodoActivity.this, myCur);
	              
	        setListAdapter(mListAdapter);
			return true;
		case R.id.menu_about:
			new AlertDialog.Builder( this )
				.setTitle( "Task App" )
				.setMessage( "Code available on Github at github.com/tomchatting/Todo\nVersion: "+getString(R.string.version))
				.setPositiveButton( "Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.d( "AlertDialog", "Positive" );
					}
				})
				.show();
			return true;
	    default:
	        return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			dbHelper.deleteTodo(info.id);
			Cursor myCur = null;

	        myCur = dbHelper.fetchAllTodos();

	        mListAdapter = new fillData(TodoActivity.this, myCur);
	              
	        setListAdapter(mListAdapter);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createTodo() {
		Intent i = new Intent(this, TodoDetailActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	// Opens the second activity if an entry is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, TodoDetailActivity.class);
		i.putExtra(TodoDbAdapter.KEY_ROWID, id);
		// Activity returns an result if called with startActivityForResult

		startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	
	// Called with the result of the other activity
	// requestCode was the origin request code send to the activity
	// resultCode is the return code, 0 is everything is ok
	// intend can be used to get data
	
	fillData mListAdapter;
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		Cursor myCur = null;

        myCur = dbHelper.fetchAllTodos();

        mListAdapter = new fillData(TodoActivity.this, myCur);
              
        setListAdapter(mListAdapter);
        
	}


	private class fillData extends ResourceCursorAdapter {
		public fillData(Context context, Cursor cur) {
			super(context, R.layout.main_row, cur);
		}
		  
	  @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.main_row, parent, false);
            
        }

	  
        @Override
        public void bindView(View view, Context context, Cursor cur) {
            TextView tvTopText = (TextView)view.findViewById(R.id.label);
            TextView tvBottomText = (TextView)view.findViewById(R.id.desc);
            TextView tvDateText = (TextView)view.findViewById(R.id.date);
            CheckBox cbListCheck = (CheckBox)view.findViewById(R.id.checkbox);
            LinearLayout gl1 = (LinearLayout)view.findViewById(R.id.linearLayout2);
            
            /* For future use */ //Integer id = cur.getInt(cur.getColumnIndex(TodoDbAdapter.KEY_ROWID));
            String summary = cur.getString(cur.getColumnIndex(TodoDbAdapter.KEY_SUMMARY));
            String description = cur.getString(cur.getColumnIndex(TodoDbAdapter.KEY_DESCRIPTION));
            Long date = cur.getLong(cur.getColumnIndex(TodoDbAdapter.KEY_DATE));
            Integer completed = cur.getInt(cur.getColumnIndex(TodoDbAdapter.KEY_COMPLETED));
            
            tvTopText.setText(summary);
            tvBottomText.setText(description);
            tvDateText.setText(DateUtils.getRelativeTimeSpanString(date));
            cbListCheck.setChecked((completed)==0? false:true);
            
        	cbListCheck.setEnabled(false);
        	if (completed==0) {
        		gl1.setBackgroundResource(R.color.white);
        		tvBottomText.setTypeface(null,Typeface.BOLD); 
        	} else {
        		tvTopText.setTextColor(R.color.darkGrey);
        	}
        		
        }
        
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHelper != null) {
			dbHelper.close();
		}
	}
}