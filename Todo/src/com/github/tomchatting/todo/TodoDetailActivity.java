package com.github.tomchatting.todo;


import java.util.Date;

import com.github.tomchatting.todo.database.TodoDbAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

public class TodoDetailActivity extends Activity {
	private EditText mTitleText;
	private EditText mBodyText;
	private CheckBox mCheckBox;
	private Long mRowId;
	private Long mDate;
	private TodoDbAdapter mDbHelper;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mDbHelper = new TodoDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.edit);
		mTitleText = (EditText) findViewById(R.id.todo_edit_summary);
		mBodyText = (EditText) findViewById(R.id.todo_edit_description);
		mCheckBox = (CheckBox) findViewById(R.id.todo_edit_completed);

		mRowId = null;
		Bundle extras = getIntent().getExtras();
		mRowId = (bundle == null) ? null : (Long) bundle
				.getSerializable(TodoDbAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(TodoDbAdapter.KEY_ROWID);
		}
		populateFields();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case android.R.id.home:
	        // app icon in action bar clicked; go home
	        Intent intent = new Intent(this, TodoActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	        return true;
	    case R.id.todo_edit_confirm:
	    	setResult(RESULT_OK);
			finish();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
		}
	}
	
	private void populateFields() {
		if (mRowId != null) {
			Cursor todo = mDbHelper.fetchTodo(mRowId);
			startManagingCursor(todo);

			mTitleText.setText(todo.getString(todo
					.getColumnIndexOrThrow(TodoDbAdapter.KEY_SUMMARY)));
			mBodyText.setText(todo.getString(todo
					.getColumnIndexOrThrow(TodoDbAdapter.KEY_DESCRIPTION)));
			mCheckBox.setChecked((todo.getInt(todo
					.getColumnIndexOrThrow(TodoDbAdapter.KEY_COMPLETED)))==0? false:true);
			mDate = todo.getLong(todo
					.getColumnIndexOrThrow(TodoDbAdapter.KEY_DATE));
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(TodoDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String summary = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();
		Integer completed = 0;
		
		Boolean checkBoxValue = mCheckBox.isChecked();
		if(checkBoxValue==true){
			completed = 1;
		}
		 
		if (mRowId == null) {
			Date d = new Date();
			Long taskdate = d.getTime();
			long id = mDbHelper.createTodo(summary, description, taskdate, completed);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateTodo(mRowId, summary, description, mDate, completed);
		}
	}
}
